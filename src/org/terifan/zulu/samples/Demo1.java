package org.terifan.zulu.samples;

import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.ArrayList;
import javax.swing.JFrame;
import javax.swing.JPanel;
import org.terifan.zulu.Material;
import org.terifan.zulu.Texture2D;
import org.terifan.zulu.Transform3D;
import org.terifan.zulu.View;
import org.terifan.zulu.core.RenderingStateListener;
import org.terifan.zulu.core.VertexFormat;
import org.terifan.zulu.geometry.IndexedGeometry;
import org.terifan.zulu.loaders.PackagedResource;
import org.terifan.zulu.loaders.Resource;
import org.terifan.zulu.scenegraph.BranchGroup;
import org.terifan.zulu.scenegraph.Group;
import org.terifan.zulu.scenegraph.SceneGraph;
import org.terifan.zulu.scenegraph.Shape3D;
import org.terifan.zulu.util.vecmath.Color4f;
import org.terifan.zulu.util.vecmath.Point3f;
import org.terifan.zulu.util.vecmath.TexCoord2f;


public class Demo1
{
	public static void main(String ... args)
	{
		try
		{
			new Demo1();
		}
		catch (Exception e)
		{
			e.printStackTrace(System.out);
		}
	}


	public Demo1() throws Exception
	{
		test1();
	}


	private void test1() throws IOException
	{
		View view1 = new View(2*512, 2*384, "view1");
		view1.getViewTransform().translate(0,50,-15-150);
		view1.addRenderingStateListener(new MyRenderingStateListener());

		SceneGraph scene = new SceneGraph(3); // 30.9 58.3 78.2
		scene.setPrintPerformanceData(true);
		scene.addView(view1);

		scene.addChild(new BranchGroup(loadModel("resources/truck.obj")));

		JPanel panel = new JPanel();
		panel.add(view1);

		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.add(panel);
		frame.pack();
		//frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
		frame.setLocation(585,0);
		frame.addWindowListener(new WindowAdapter()
		{
			@Override
			public void windowClosing(WindowEvent aEvent)
			{
				System.exit(0);
			}
		});
		frame.setVisible(true);

		scene.startRendering();
	}


	private void test2() throws IOException
	{
		View view1 = new View(512, 384, "view1");
		View view2 = new View(512, 384, "view2");
		View view3 = new View(512, 384, "view3");
		View view4 = new View(512, 384, "view4");
		view1.getViewTransform().translate(0,50,-15-150);
		view2.getViewTransform().translate(0,50,0);
		view3.getViewTransform().translate(0,50,-15-150);
		view4.getViewTransform().translate(0,50,-10-150).rotate(0,0,0);
		view1.addRenderingStateListener(new MyRenderingStateListener());
		view2.addRenderingStateListener(new MyRenderingStateListener());
		view3.addRenderingStateListener(new MyRenderingStateListener());
		view4.addRenderingStateListener(new MyRenderingStateListener());

		SceneGraph scene = new SceneGraph(3); // 18 29 38
		scene.setPrintPerformanceData(!false);
		scene.addView(view1);
		scene.addView(view2);
		scene.addView(view3);
		scene.addView(view4);

		scene.addChild(new BranchGroup(loadModel("resources/truck.obj")));

		JPanel panel = new JPanel(new GridLayout(2,2));
		panel.add(view1);
		panel.add(view2);
		panel.add(view3);
		panel.add(view4);

		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.add(panel);
		frame.pack();
		//frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
		frame.setLocation(585,0);
		frame.addWindowListener(new WindowAdapter()
		{
			@Override
			public void windowClosing(WindowEvent aEvent)
			{
				System.exit(0);
			}
		});
		frame.setVisible(true);

		scene.startRendering();
	}


	private class MyRenderingStateListener implements RenderingStateListener
	{
		Transform3D rotate1 = new Transform3D();
		Transform3D rotate2 = new Transform3D();
		Transform3D rotate3 = new Transform3D();
		Transform3D translate = new Transform3D().translate(0,0,10);
		@Override
		public void renderingStarted(View aView)
		{
			aView.getTransformStack().push(rotate1);
			aView.getTransformStack().push(translate);
			if (aView.getIdentity().equals("view1")) aView.getTransformStack().push(rotate2);
			if (aView.getIdentity().equals("view4")) aView.getTransformStack().push(rotate3);
			rotate1.rotate(0, 0.0005, 0);
			rotate2.rotate(0.0005, 0.0004, 0.0003);
			rotate3.rotate(0, 0, 0.0005);
		}
		@Override
		public void renderingFinished(View aView)
		{
		}
	}


	private Group loadModel(String aPath) throws IOException
	{
		ArrayList<IndexedGeometry> geometries = new ArrayList<IndexedGeometry>();
		ArrayList<Material> materials = new ArrayList<Material>();

		LineNumberReader reader = new LineNumberReader(new InputStreamReader(Demo1.class.getResourceAsStream(aPath)));

		Point3f [] coords = null;
		Point3f [] normals = null;
		TexCoord2f [] texCoords = null;
		Color4f [] colors = null;

		int [] coordIndices = null;
		int [] normalIndices = null;
		int [] texCoordIndices = null;
		int [] colorIndices = null;

		for (String s; (s = reader.readLine()) != null;)
		{
			if (s.startsWith("begin"))
			{
			}
			if (s.startsWith("material"))
			{
				s = s.substring(8).trim();
				Resource shader = new PackagedResource(this, "demo.zps");
				Resource texture = new PackagedResource(this, "resources/"+s);
				Material material = new Material(shader, new Texture2D(texture));
				materials.add(material);
			}
			else if (s.startsWith("coords"))
			{
				coords = new Point3f[Integer.parseInt(s.substring(7))];
			}
			else if (s.startsWith("texcoords"))
			{
				texCoords = new TexCoord2f[Integer.parseInt(s.substring(10))];
			}
			else if (s.startsWith("colors"))
			{
				colors = new Color4f[Integer.parseInt(s.substring(7))];
			}
			else if (s.startsWith("normals"))
			{
				normals = new Point3f[Integer.parseInt(s.substring(8))];
			}
			else if (s.startsWith("vertices"))
			{
				coordIndices = new int[Integer.parseInt(s.substring(9))];
				normalIndices = new int[Integer.parseInt(s.substring(9))];
				texCoordIndices = new int[Integer.parseInt(s.substring(9))];
				colorIndices = new int[Integer.parseInt(s.substring(9))];
			}
			else if (s.startsWith("coord"))
			{
				s = s.substring(6);
				int i = Integer.parseInt(s.substring(0, s.indexOf(" ")));
				s = s.substring(s.indexOf(" ")+1).trim();
				String [] t = s.split(" ");
				coords[i] = new Point3f(Float.parseFloat(t[0]), Float.parseFloat(t[1]), Float.parseFloat(t[2]));
			}
			else if (s.startsWith("texcoord"))
			{
				s = s.substring(9);
				int i = Integer.parseInt(s.substring(0, s.indexOf(" ")));
				s = s.substring(s.indexOf(" ")+1).trim();
				String [] t = s.split(" ");
				texCoords[i] = new TexCoord2f(Float.parseFloat(t[0]), Float.parseFloat(t[1]));
			}
			else if (s.startsWith("color"))
			{
				s = s.substring(6);
				int i = Integer.parseInt(s.substring(0, s.indexOf(" ")));
				s = s.substring(s.indexOf(" ")+1).trim();
				String [] t = s.split(" ");
				colors[i] = new Color4f(Float.parseFloat(t[0]), Float.parseFloat(t[1]), Float.parseFloat(t[2]), Float.parseFloat(t[3]));
			}
			else if (s.startsWith("normal"))
			{
				s = s.substring(7);
				int i = Integer.parseInt(s.substring(0, s.indexOf(" ")));
				s = s.substring(s.indexOf(" ")+1).trim();
				String [] t = s.split(" ");
				normals[i] = new Point3f(Float.parseFloat(t[0]), Float.parseFloat(t[1]), Float.parseFloat(t[2]));
			}
			else if (s.startsWith("vertex"))
			{
				s = s.substring(7);
				int i = Integer.parseInt(s.substring(0, s.indexOf(" ")));
				s = s.substring(s.indexOf(" ")+1).trim();
				String [] t = s.split(" ");
				coordIndices[i] = Integer.parseInt(t[0]);
				texCoordIndices[i] = Integer.parseInt(t[1]);
				colorIndices[i] = Integer.parseInt(t[2]);
				normalIndices[i] = Integer.parseInt(t[3]);
			}
			if (s.startsWith("end"))
			{
				VertexFormat vertexFormat = new VertexFormat(1, 0*0x4, 1, 0x2, 0*1, 0);
				IndexedGeometry geometry = new IndexedGeometry(vertexFormat, coordIndices.length/3, 3, coords.length);

				float [] vertex = new float[3+0*4+2+0*3];

				for (int i = 0; i < coords.length; i++)
				{
					vertex[0] = coords[i].x;
					vertex[1] = coords[i].y;
					vertex[2] = coords[i].z;
//					vertex[3] = colors[i].x;
//					vertex[4] = colors[i].y;
//					vertex[5] = colors[i].z;
//					vertex[6] = colors[i].w;
					vertex[3+0*7] = texCoords[i].x;
					vertex[4+0*8] = texCoords[i].y;
//					vertex[9] = normals[i].x;
//					vertex[10] = normals[i].y;
//					vertex[11] = normals[i].z;

					geometry.setVertex(i, vertex);
				}

				for (int i = 0, j = 0; i < coordIndices.length; i+=3)
				{
					geometry.setIndices(j++, coordIndices[i+0], coordIndices[i+1], coordIndices[i+2]);
				}

				geometries.add(geometry);
				geometry.setTextures(materials.get(materials.size()-1).getTexture(0));
			}
		}

		reader.close();

		Group group = new Group();

		for (int i = 0; i < geometries.size(); i++)
		{
			Shape3D shape = new Shape3D();
			shape.addGeometry(geometries.get(i));
			shape.setMaterial(new Material(new PackagedResource(this, "demo.zps"))); //materials.get(i));
			group.addChild(shape);
		}

		return group;
	}
}