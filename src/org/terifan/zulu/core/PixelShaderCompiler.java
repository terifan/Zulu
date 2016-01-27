package org.terifan.zulu.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import org.terifan.zeus.util.script.Identifier;
import org.terifan.zeus.util.script.JavaFormatter;
import org.terifan.zeus.util.script.LanguageElement;
import org.terifan.zeus.util.script.Node;
import org.terifan.zeus.util.script.ParserException;
import org.terifan.zeus.util.script.Primitive;
import org.terifan.zeus.util.script.Script;
import org.terifan.zulu.Material;
import org.terifan.zulu.loaders.Resource;


public class PixelShaderCompiler
{
	private final static boolean HIGH_QUALITY_GENERATOR = false;
	private final static PixelShaderLanguage PSL = PixelShaderLanguage.instance;
	private static String mTemplate;
    private static RendererClassLoader mRendererClassLoader = new RendererClassLoader();

	private ArrayList<Node> mUsingList;
	private StringBuilder mGeneratedCode;
	private StringBuilder mPendingCode;
	private StringBuilder mInsertionCode;
	private HashMap<String,Integer> mTempVars;


	public PixelShaderCompiler()
	{
		if (mTemplate == null)
		{
			try
			{
				File f = new File(PixelShaderCompiler.class.getResource("PixelRenderer.zst").getPath());
				char [] buf = new char[(int)f.length()];
				try (FileReader fr = new FileReader(f))
				{
					fr.read(buf);
				}
				mTemplate = new String(buf);
			}
			catch (Throwable e)
			{
				throw new RuntimeException(e);
			}
		}
	}


	public PixelRenderer [] compile(Material aMaterial, Resource aResource)
	{
		if (aResource == null)
		{
			throw new IllegalArgumentException("Resource is null.");
		}

		try
		{
			Script script;

			try (InputStream in = aResource.getStream())
			{
				if (in == null)
				{
					throw new IllegalArgumentException("Resource stream is null: resource: " + aResource.getName());
				}

				script = new Script(PSL, in);
			}

			String [] generatedCode = interpret(script);

			generatedCode[0] = new JavaFormatter().format(generatedCode[0]);
			generatedCode[1] = new JavaFormatter().format(generatedCode[1]);

			String source = mTemplate;
			source = source.replace("/**value-of PIXEL_SHADER_MAIN*/", generatedCode[0]);
			//source = source.replace("/**value-of PIXEL_SHADER_SUB*/", generatedCode[1]);

			for (;;)
			{
				int startOffset = source.indexOf("/**for-each EDGE_INDEX*/");
				if (startOffset == -1) break;
				int endOffset = source.indexOf("/**next EDGE_INDEX*/", startOffset);

				String chunk = source.substring(startOffset+24, endOffset);
				String replace = "";

				for (int i = 0; i < 2; i++)
				{
					replace += chunk.replace("#EDGE",""+(i==0?"A":"B")).replace("#INVEDGE",""+(i==0?"B":"A"));
				}

				source = source.substring(0,startOffset) + replace + source.substring(endOffset+20);
			}


			for (;;)
			{
				int startOffset = source.indexOf("/**for-each TEXTURE*/");
				if (startOffset == -1) break;
				int endOffset = source.indexOf("/**next TEXTURE*/", startOffset);

				String chunk = source.substring(startOffset+21, endOffset);
				String replace = "";

				for (Node using : mUsingList)
				{
					if (using.toString().startsWith("TEXTURE"))
					{
						String id = using.toString().substring(7);
						String fields = using.getNext().getNext().toString();
						replace += chunk.replace("#ID",id);
					}
				}

				source = source.substring(0,startOffset) + replace + source.substring(endOffset+17);
			}


			for (;;)
			{
				int startOffset = source.indexOf("/**for-each TEXTURE_COORDINATE_COMPONENT*/");
				if (startOffset == -1) break;
				int endOffset = source.indexOf("/**next TEXTURE_COORDINATE_COMPONENT*/", startOffset);

				String chunk = source.substring(startOffset+42, endOffset);
				String replace = "";

				int index = 0;
				for (Node using : mUsingList)
				{
					if (using.toString().startsWith("TEXCOORD"))
					{
						String id = using.toString().substring(8);
						String fields = using.getNext().getNext().toString();
						for (int i = 0; i < fields.length(); i++)
						{
							String comp = fields.substring(i,i+1).toUpperCase();
							String prop = comp.equals("U") ? "width" : comp.equals("V") ? "height" : "depth";
							replace += chunk.replace("#ID",id).replace("#COMP",comp).replace("#INDEX",""+index).replace("#PROP",prop);

							index++;
						}
					}
				}

				source = source.substring(0,startOffset) + replace + source.substring(endOffset+38);
			}

			for (int i = 0; i < 10; i++) source = source.replace("\t\r\n","\r\n");
			source = source.replace("\r\n\r\n\r\n","\r\n\r\n");

			// TODO: handle bad class names
			String className = aResource.getName();
			if (className.indexOf(".") != -1)
			{
				className = className.substring(0, className.indexOf("."));
			}

			source = source.replace("/**value-of SHADER_NAME*/", className);

			File filePath = new File(getTemporaryDirectory() + className + ".java");

			return buildJavaClass(className, filePath, source);
		}
		catch (IOException e)
		{
			throw new RuntimeException(e);
		}
	}


	private PixelRenderer [] buildJavaClass(String aClassName, File aFilePath, String aSource)
	{
		try
		{
			try (FileWriter fileWriter = new FileWriter(aFilePath))
			{
				fileWriter.write(aSource);
			}

			JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
			DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();
			StandardJavaFileManager fileManager = compiler.getStandardFileManager(diagnostics, null, null);

			Iterable<? extends JavaFileObject> compilationUnits1 = fileManager.getJavaFileObjectsFromFiles(Arrays.asList(new File[]{aFilePath}));
			compiler.getTask(null, fileManager, diagnostics, null, null, compilationUnits1).call();

			boolean b = true;

			for (Diagnostic diagnostic : diagnostics.getDiagnostics())
			{
				b = false;
				System.out.println();
				System.out.println(diagnostic);
			}

			fileManager.close();

			if (!b)
			{
				return null;
			}

			Class clazz = mRendererClassLoader.loadClass(aClassName);

			PixelRenderer [] renderers = new PixelRenderer[Kernel.MAX_WORKER_THREADS];
			for (int i = 0; i < renderers.length; i++)
			{
				renderers[i] = (PixelRenderer)clazz.newInstance();
			}

			return renderers;
		}
		catch (IOException e)
		{
			throw new RuntimeException(e);
		}
		catch (IllegalAccessException e)
		{
			throw new RuntimeException(e);
		}
		catch (ClassNotFoundException e)
		{
			throw new RuntimeException(e);
		}
		catch (InstantiationException e)
		{
			throw new RuntimeException(e);
		}
	}


	private String [] interpret(Script aScript) throws ParserException
	{
		mUsingList = new ArrayList<>();
		mGeneratedCode = new StringBuilder();
		mPendingCode = new StringBuilder();
		mInsertionCode = new StringBuilder();
		mTempVars = new HashMap<>();

		String mainCode = "";
		String subCode = "";

		Node node = aScript.getRoot().getChild();

		while (node != null)
		{
			if (node.getElement() == PSL.USING)
			{
				mUsingList.add(node.getNext());
				while (node.getElement() != PSL.OP_SEMI) node = node.getNext();
			}
			else if (node.getElement() == PSL.VOID || node.getElement() instanceof Primitive)
			{
				mGeneratedCode.setLength(0);
				mPendingCode.setLength(0);
				mInsertionCode.setLength(0);

				if (node.getNext().toString().equals("main"))
				{
					interpret(node.getNext().getNext().getNext().getNext().getChild());
					mGeneratedCode.append(mPendingCode.toString());
					mainCode = mGeneratedCode.toString();
				}
				else
				{
					// TODO: prettify
					subCode += node.toString() + " " + node.getNext().toString() + "(";
					for (Node n = node.getNext().getNext().getChild(); n != null; n = n.getNext())
					{
						subCode += n.toString()+" ";
					}
					subCode += "){";
					interpret(node.getNext().getNext().getNext().getNext().getChild());
					mGeneratedCode.append(mPendingCode.toString());
					subCode += mGeneratedCode.toString();
					subCode += "}";
				}

				node = node.getNext().getNext().getNext().getNext().getNext();
			}
			else
			{
				throw new ParserException("Unsupported element at root level: " + node);
			}

			if (node != null) node = node.getNext();
		}

		return new String[]{mainCode, subCode};
	}


	private void interpret(Node aParentNode) throws ParserException
	{
		Node node = aParentNode;

		while (node != null)
		{
			LanguageElement element = node.getElement();

			if (element == PSL.TEXTURE2D)
			{
				ArrayList<ParamNode> args = getArgumentList(node.getNext().getChild(), PSL.OP_COMMA);
				String textureName = args.get(0).getNode(0).toString();
				String coordName = args.get(1).getNode(0).toString();

				int ti = -1; // texture index
				int ci = -1; // coordinate index
				for (Node arg : mUsingList)
				{
					if (arg.toString().startsWith("TEXTURE") && arg.getNext().getNext().getNext().toString().equals(textureName))
					{
						ti = Integer.parseInt(arg.toString().substring(7));
					}
					if (arg.toString().startsWith("TEXCOORD") && arg.getNext().getNext().getNext().toString().equals(coordName))
					{
						ci = Integer.parseInt(arg.toString().substring(8));
					}
				}

				if (ti == -1)
				{
					throw new ParserException("Texture not declared with name: " + textureName);
				}

				if (false)
				{
					String cr1 = createTmp("cr");
					mGeneratedCode.append("int "+cr1+" = texMap" + ti + "[((texU"+ci+" & texMaskU"+ti+") >> texShiftU"+ti+") + ((texV"+ci+" & texMaskV"+ti+") >> texShiftV"+ti+")];\n");
					mPendingCode.append(cr1);
				}
				else
				{
					String cr1 = createTmp("cr");
					String cr2 = createTmp("cr");
					String cr3 = createTmp("cr");
					String cr4 = createTmp("cr");

					if (ci == -1)
					{
						throw new ParserException("Unsupported, only use declared texcoords");
					}
					else
					{
						String uc1 = createTmp("uc");
						String vc1 = createTmp("vc");
						String uc2 = createTmp("uc");
						String vc2 = createTmp("vc");

						mGeneratedCode.append("int "+uc1+" = ( (texU"+ci+"        & texMaskU"+ti+") >> texShiftU"+ti+");\n");
						mGeneratedCode.append("int "+vc1+" = ( (texV"+ci+"        & texMaskV"+ti+") >> texShiftV"+ti+");\n");
						mGeneratedCode.append("int "+uc2+" = (((texU"+ci+"+65536) & texMaskU"+ti+") >> texShiftU"+ti+");\n");
						mGeneratedCode.append("int "+vc2+" = (((texV"+ci+"+65536) & texMaskV"+ti+") >> texShiftV"+ti+");\n");

						mGeneratedCode.append("int "+cr1+" = texMap" + ti + "["+uc1+" + "+vc1+"];\n");
						mGeneratedCode.append("int "+cr2+" = texMap" + ti + "["+uc2+" + "+vc1+"];\n");
						mGeneratedCode.append("int "+cr3+" = texMap" + ti + "["+uc2+" + "+vc2+"];\n");
						mGeneratedCode.append("int "+cr4+" = texMap" + ti + "["+uc1+" + "+vc2+"];\n");

//						mGeneratedCode.append("int "+cr1+" = texMap" + ti + "[( (texU"+ci+"        & texMaskU"+ti+") >> texShiftU"+ti+") + ( (texV"+ci+"        & texMaskV"+ti+") >> texShiftV"+ti+")];\n");
//						mGeneratedCode.append("int "+cr2+" = texMap" + ti + "[(((texU"+ci+"+65536) & texMaskU"+ti+") >> texShiftU"+ti+") + ( (texV"+ci+"        & texMaskV"+ti+") >> texShiftV"+ti+")];\n");
//						mGeneratedCode.append("int "+cr3+" = texMap" + ti + "[(((texU"+ci+"+65536) & texMaskU"+ti+") >> texShiftU"+ti+") + (((texV"+ci+"+65536) & texMaskV"+ti+") >> texShiftV"+ti+")];\n");
//						mGeneratedCode.append("int "+cr4+" = texMap" + ti + "[( (texU"+ci+"        & texMaskU"+ti+") >> texShiftU"+ti+") + (((texV"+ci+"+65536) & texMaskV"+ti+") >> texShiftV"+ti+")];\n");
					}

					String fu = createTmp("fu");
					String fv = createTmp("fv");
					String fa = createTmp("fa");
					String fb = createTmp("fb");
					String fc = createTmp("fc");
					String fd = createTmp("fd");

					if (HIGH_QUALITY_GENERATOR)
					{
						mGeneratedCode.append("int "+fu+" = ((texU"+ci+">>texLevel"+ti+") & 0xFF00) / 255;\n");
						mGeneratedCode.append("int "+fv+" = ((texV"+ci+">>texLevel"+ti+") & 0xFF00) / 255;\n");
						mGeneratedCode.append("int "+fa+" = (256 - "+fu+") * (256 - "+fv+");\n");
						mGeneratedCode.append("int "+fb+" = (      "+fu+") * (256 - "+fv+");\n");
						mGeneratedCode.append("int "+fc+" = (      "+fu+") * (      "+fv+");\n");
						mGeneratedCode.append("int "+fd+" = 65536 - "+fa+" - "+fb+" - "+fc+";\n");
					}
					else
					{
						mGeneratedCode.append("int "+fu+" = ((texU"+ci+">>texLevel"+ti+") & 0xFF00) >> 8;\n");
						mGeneratedCode.append("int "+fv+" = ((texV"+ci+">>texLevel"+ti+") & 0xFF00) >> 8;\n");
						mGeneratedCode.append("int "+fa+" = (255 - "+fu+") * (255 - "+fv+");\n");
						mGeneratedCode.append("int "+fb+" = (      "+fu+") * (255 - "+fv+");\n");
						mGeneratedCode.append("int "+fc+" = (      "+fu+") * (      "+fv+");\n");
						mGeneratedCode.append("int "+fd+" = (255 - "+fu+") * (      "+fv+");\n");
//						mGeneratedCode.append("int "+fd+" = 65536 - "+fa+" - "+fb+" - "+fc+";\n");
					}

					String A = createTmp("A");
					String R = createTmp("R");
					String G = createTmp("G");
					String B = createTmp("B");

					mGeneratedCode.append("int "+A+" = 0x00ff0000 & (( ("+cr1+" >>> 24)             * "+fa+") + ( ("+cr2+" >>> 24)             * "+fb+") + ( ("+cr3+" >>> 24)             * "+fc+") + ( ("+cr4+" >>> 24)             * "+fd+"));\n");
					mGeneratedCode.append("int "+R+" = 0x00ff0000 & (((("+cr1+" >> 16)  & 0x0000ff) * "+fa+") + ((("+cr2+" >> 16)  & 0x0000ff) * "+fb+") + ((("+cr3+" >> 16)  & 0x0000ff) * "+fc+") + ((("+cr4+" >> 16)  & 0x0000ff) * "+fd+"));\n");
					mGeneratedCode.append("int "+G+" = 0xff000000 & (( ("+cr1+"         & 0x00ff00) * "+fa+") + ( ("+cr2+"         & 0x00ff00) * "+fb+") + ( ("+cr3+"         & 0x00ff00) * "+fc+") + ( ("+cr4+"         & 0x00ff00) * "+fd+"));\n");
					mGeneratedCode.append("int "+B+" = 0x00ff0000 & (( ("+cr1+"         & 0x0000ff) * "+fa+") + ( ("+cr2+"         & 0x0000ff) * "+fb+") + ( ("+cr3+"         & 0x0000ff) * "+fc+") + ( ("+cr4+"         & 0x0000ff) * "+fd+"));\n");

					mPendingCode.append("(("+A+"<<8)+"+R+"+(("+G+"+"+B+")>>>16))");

//const float textureSize = 512.0; //size of the texture
//const float texelSize = 1.0 / textureSize; //size of one texel
//
//vec4 texture2DBilinear( sampler2D textureSampler, vec2 uv )
//{
//    // in vertex shaders you should use texture2DLod instead of texture2D
//    vec4 tl = texture2D(textureSampler, uv);
//    vec4 tr = texture2D(textureSampler, uv + vec2(texelSize, 0));
//    vec4 bl = texture2D(textureSampler, uv + vec2(0, texelSize));
//    vec4 br = texture2D(textureSampler, uv + vec2(texelSize , texelSize));
//    vec2 f = fract( uv.xy * textureSize ); // get the decimal part
//    vec4 tA = mix( tl, tr, f.x ); // will interpolate the red dot in the image
//    vec4 tB = mix( bl, br, f.x ); // will interpolate the blue dot in the image
//    return mix( tA, tB, f.y ); // will interpolate the green dot in the image
//}


				}

				node = node.getNext().getNext();
			}
			else if (element == PSL.TEXCOORD)
			{
				ArrayList<ParamNode> args = getArgumentList(node.getNext().getChild(), PSL.OP_COMMA);
				String textureName = args.get(0).getNode(0).toString();
				String coordName = args.get(1).getNode(0).toString();

				int textureIndex = -1;
				int coordIndex = -1;
				for (Node arg : mUsingList)
				{
					if (arg.toString().startsWith("TEXTURE") && arg.getNext().toString().equals(textureName))
					{
						textureIndex = Integer.parseInt(arg.toString().substring(7));
					}
					if (arg.toString().startsWith("TEXCOORD") && arg.getNext().getNext().getNext().toString().equals(coordName))
					{
						coordIndex = Integer.parseInt(arg.toString().substring(8));
					}
				}

				mPendingCode.append("(((U"+coordIndex+" & texMaskU"+textureIndex+") >> texShiftU"+textureIndex+") + ((texV"+coordIndex+" & texMaskV"+textureIndex+") >> texShiftV"+textureIndex+"))");

				node = node.getNext().getNext();
			}
/**			else if (element == PSL.MIX)
			{
				ArrayList<ParamNode> args = getArgumentList(node.getNext().getChild(), PSL.OP_COMMA);

//				***********

				String textureName = args.get(0);
				String coordName = args.get(1);

				int textureIndex = -1;
				int coordIndex = -1;
				for (Node arg : mUsingList)
				{
					if (arg.toString().startsWith("TEXTURE") && arg.getNext().toString().equals(textureName))
					{
						textureIndex = Integer.parseInt(arg.toString().substring(7));
					}
					if (arg.toString().startsWith("TEXCOORD") && arg.getNext().getNext().getNext().toString().equals(coordName))
					{
						coordIndex = Integer.parseInt(arg.toString().substring(8));
					}
				}

				mPendingCode.append("(((U"+coordIndex+" & texMaskU"+textureIndex+") >> texShiftU"+textureIndex+") + ((texV"+coordIndex+" & texMaskV"+textureIndex+") >> texShiftV"+textureIndex+"))");

				node = node.getNext().getNext();
			}*/
			else if (element == PSL.FRAGDEPTH)
			{
				mPendingCode.append("mDepthBuffer[bufferOffset]");
			}
			else if (element == PSL.FRAGCOLOR)
			{
				mPendingCode.append("mFrameBuffer[bufferOffset]");
			}
			else if (element == PSL.FRAGCOORD)
			{
				String component = node.getNext().getNext().getElement().toString();

				switch (component)
				{
					case "x":
						throw new RuntimeException("not implemented");
					case "y":
						throw new RuntimeException("not implemented");
					case "z":
						mPendingCode.append("fragZ");
						break;
				}
				node = node.getNext().getNext();
			}
			else if (element != null && node.getNext() != null && node.getNext().getElement() != null && (element instanceof Identifier) && node.getNext().getElement() == PSL.OP_DOT)
			{
				if (node.getNext().getNext().getNext() != null && node.getNext().getNext().getNext().getElement() == PSL.OP_EQ) // assignment
				{
					String name = node.toString();
					String components = node.getNext().getNext().toString();

					int mask = 0;

					if (components.indexOf("a") != -1) mask |= 0xFF000000;
					if (components.indexOf("r") != -1) mask |= 0x00FF0000;
					if (components.indexOf("g") != -1) mask |= 0x0000FF00;
					if (components.indexOf("b") != -1) mask |= 0x000000FF;

					String v = createTmp("v");

					mPendingCode.append("int "+v+" = ");

					if (components.equals("rgb"))
					{
						mInsertionCode.append(";"+name+" = ("+name+" & 0xFF000000) + (");
						mInsertionCode.append(v+" & 0x00FFFFFF");
						mInsertionCode.append(")");
					}
					else
					{
						mInsertionCode.append(";"+name+" = ("+(~mask)+" & " + name+") + (");
						for (int i = 0; i < components.length(); i++)
						{
							if (i > 0) mInsertionCode.append("+");
							int j = components.length()-1-i;
							String shift = j > 0 ? " >> "+(8*j) : "";

							if (components.charAt(i) == 'a') mInsertionCode.append("((255 & ("+v+shift+")) << 24)");
							if (components.charAt(i) == 'r') mInsertionCode.append("((255 & ("+v+shift+")) << 16)");
							if (components.charAt(i) == 'g') mInsertionCode.append("((255 & ("+v+shift+")) <<  8)");
							if (components.charAt(i) == 'b') mInsertionCode.append("((255 & ("+v+shift+"))      )");
						}
						mInsertionCode.append(")");
					}

					node = node.getNext().getNext().getNext();
				}
				else // extraction
				{
					String name = node.toString();
					String components = node.getNext().getNext().toString();

					mPendingCode.append("(");

					switch (components)
					{
						case "rgb":
							mPendingCode.append(name+" & 0x00FFFFFF");
							break;
						case "argb":
							mPendingCode.append(name+" & 0xFFFFFFFF");
							break;
						default:
							for (int i = 0; i < components.length(); i++)
							{
								int j = components.length()-1-i;
								if (i > 0) mPendingCode.append("+");
								String shift = j > 0 ? " << "+(8*j) : "";

								if (components.charAt(i) == 'r') mPendingCode.append("((255&("+name+">>16))"+shift+")");
								else if (components.charAt(i) == 'g') mPendingCode.append("((255&("+name+">> 8))"+shift+")");
								else if (components.charAt(i) == 'b') mPendingCode.append("((255&("+name+"    ))"+shift+")");
								else if (components.charAt(i) == 'a') mPendingCode.append("((    ("+name+">>24))"+shift+")");
							}	break;
					}
					mPendingCode.append(")");
					node = node.getNext().getNext();
				}
			}
			else if (node.getElement() == PSL.OP_SEMI || node.getElement() == PSL.LBRACE)
			{
				if (node.getElement() != PSL.OP_SEMI || node.getParent().getPrevious().getElement() != PSL.FOR)
				{
					mInsertionCode.append(element.toString());
					mInsertionCode.append(" ");

					mGeneratedCode.append(mPendingCode.toString());
					mGeneratedCode.append(mInsertionCode.toString());
					mPendingCode.setLength(0);
					mInsertionCode.setLength(0);
				}
			}
			else
			{
				mPendingCode.append(element.toString());
				mPendingCode.append(" ");
			}

			interpret(node.getChild());

			node = node.getNext();
		}
	}


	private static class ParamNode
	{
		private Node mRoot;
		private int mSize;
		public ParamNode(Node aRoot, LanguageElement aSeparator)
		{
			mRoot = aRoot;
			for (Node node=mRoot;node!=null&&node.getElement()!=aSeparator;mSize++)
			{
				node = node.getNext();
			}
		}
		public Node getNode(int aIndex)
		{
			Node node = mRoot;
			while (aIndex-- > 0)
			{
				node = node.getNext();
			}
			return node;
		}
		public int size()
		{
			return mSize;
		}
	}


	private ArrayList<ParamNode> getArgumentList(Node aNode, LanguageElement aSeparator)
	{
		ArrayList<ParamNode> list = new ArrayList<>();

		if (aNode.getElement() != aSeparator)
		{
			list.add(new ParamNode(aNode, aSeparator));
		}

		while (aNode != null)
		{
			if (aNode.getElement() == aSeparator)
			{
				if (aNode.getNext() != null)
				{
					list.add(new ParamNode(aNode.getNext(), aSeparator));
				}
			}

			aNode = aNode.getNext();
		}
/*
System.out.println("---------------");
for(ParamNode pn:list)
{
	System.out.println("...............");
	for (int i = 0; i < pn.size(); i++)
	{
		System.out.println(pn.getNode(i));
	}
}
*/
		return list;
	}


	private String createTmp(String aName)
	{
		Integer i = mTempVars.get(aName);
		if (i == null)
		{
			i = 0;
		}
		i++;
		mTempVars.put(aName, i);
		return "_"+aName+i;
	}


	private static class RendererClassLoader extends ClassLoader
    {
        @Override
        protected Class<?> findClass(String name) throws ClassNotFoundException
        {
            try
            {
                System.out.println("Loading shader: " + name);

                File file = new File(getTemporaryDirectory() + name + ".class");
                byte [] buf = new byte[(int)file.length()];
				try (FileInputStream in = new FileInputStream(file))
				{
					in.read(buf);
				}
                return defineClass(name, buf, 0, buf.length);
            }
            catch (IOException | ClassFormatError e)
            {
                throw new ClassNotFoundException(e.toString());
            }
        }
    }


	private static String getTemporaryDirectory()
    {
        File tempDir = new File(System.getProperty("java.io.tmpdir"));
        if (!tempDir.exists())
        {
            tempDir = new File(System.getProperty("user.home"));
        }
        if (!tempDir.exists())
        {
            throw new RuntimeException("No temporary directory nor home directory exists in this environment. Define a user.home or java.io.tmpdir system property and then restart.");
        }

        tempDir = new File(tempDir.getAbsolutePath()+"/Zulu Temporary Files");
        tempDir.mkdir();

        if (!tempDir.exists())
        {
            throw new RuntimeException("Failed to create temporary directory at " + tempDir.getAbsolutePath());
        }

        return tempDir.getAbsolutePath() + "/";
    }
}