package org.terifan.zulu.samples;

import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import org.terifan.util.StopWatch;
import org.terifan.zulu.Material;
import org.terifan.zulu.Texture2D;
import org.terifan.zulu.Transform3D;
import org.terifan.zulu.View;
import org.terifan.zulu.core.Geometry;
import org.terifan.zulu.core.VertexFormat;
import org.terifan.zulu.core.GeometryBuffer;
import org.terifan.zulu.core.RenderState;
import org.terifan.zulu.core.RenderingStateListener;
import org.terifan.zulu.loaders.PackagedResource;
import org.terifan.zulu.scenegraph.BranchGroup;
import org.terifan.zulu.scenegraph.SceneGraph;
import org.terifan.zulu.scenegraph.Shape3D;


public class Demo
{
	public static void main(String ... args)
	{
		try
		{
			new Demo();
		}
		catch (Exception e)
		{
			e.printStackTrace(System.out);
		}
	}


	public Demo() throws Exception
	{
		demo1();
//		demo2();
		//transPerformance();
	}


	private void demo2()
	{
		View view1 = new View(512, 384, "view1");
		View view2 = new View(512, 384, "view2");
		View view3 = new View(512, 384, "view3");
		View view4 = new View(512, 384, "view4");
		view1.getViewTransform().translate(0,0,-15);
		view2.getViewTransform().translate(0,0,-25);
		view3.getViewTransform().translate(0,0,-15);
		view4.getViewTransform().translate(0,-5,-10).rotate(0.12,0,0);
		view1.addRenderingStateListener(new MyRenderingStateListener());
		view2.addRenderingStateListener(new MyRenderingStateListener());
		view3.addRenderingStateListener(new MyRenderingStateListener());
		view4.addRenderingStateListener(new MyRenderingStateListener());

		SceneGraph scene = new SceneGraph(3);
		scene.setPrintPerformanceData(!false);
		scene.addView(view1);
		scene.addView(view2);
		scene.addView(view3);
		scene.addView(view4);

		buildSceneGraph(scene);

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


	private void demo1()
	{
		View view = new View(1024, 768, "view3");
		view.getViewTransform().translate(0,0,-15);
		view.addRenderingStateListener(new MyRenderingStateListener());

		SceneGraph scene = new SceneGraph(3);
		scene.setPrintPerformanceData(!false);
		scene.addView(view);

		buildSceneGraph(scene);

		JFrame frame = new JFrame();

		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.add(view);
		frame.pack();
		//frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
		frame.setLocation(595,0);
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
		Transform3D rotate = new Transform3D();
		Transform3D rotate2 = new Transform3D();
		Transform3D translate = new Transform3D().translate(0,0,10);
		public void renderingStarted(View aView)
		{
			aView.getTransformStack().push(rotate);
			aView.getTransformStack().push(translate);
			if (aView.getIdentity().equals("view3")) aView.getTransformStack().push(rotate2);
			rotate.rotate(0, 0.0005, 0);
			rotate2.rotate(0.0005, 0.0005, 0.0005);
		}
		public void renderingFinished(View aView)
		{
		}
	}


	private void buildSceneGraph(SceneGraph aScene)
	{
		final Texture2D tex1 = new Texture2D(new PackagedResource(this, "resources/011.64713876.DXT1.png"));
		final Texture2D tex2 = new Texture2D(new PackagedResource(this, "resources/011.77599217.DXT1.png"));

		Geometry geometry = new Geometry()
		{
			VertexFormat vertexFormat = new VertexFormat(1, 0x3, 4, 0x3222, 0, 1);

			float[][] data = new float[][]{
				{
					 6f, 6f, 6f,  255f,  0f,  0f,  0f,0f,  0f,0f,  0f,0f,  1f,1f,0.5f,  47f,
					 6f,-6f, 6f,    0f,255f,  0f,  0f,1f,  0f,1f,  0f,1f,  0f,1f,0.5f,  18f,
					-6f,-6f, 6f,    0f,  0f,255f,  1f,1f,  1f,1f,  1f,1f,  1f,0f,0.5f,  31f,
					-6f, 6f, 6f,  255f,  0f,255f,  1f,0f,  1f,0f,  1f,0f,  1f,0f,0.5f,  10f,
				},{
					 6f, 6f,-6f,  255f,  0f,  0f,  0f,0f,  0f,0f,  0f,0f,  1f,1f,0.5f,  47f,
					 6f,-6f,-6f,    0f,255f,  0f,  0f,1f,  0f,1f,  0f,1f,  0f,1f,0.5f,  18f,
					-6f,-6f,-6f,    0f,  0f,255f,  1f,1f,  1f,1f,  1f,1f,  1f,0f,0.5f,  31f,
					-6f, 6f,-6f,  255f,  0f,255f,  1f,0f,  1f,0f,  1f,0f,  1f,0f,0.5f,  10f,
				},{
					-6f, 6f, 6f,  255f,  0f,  0f,  0f,0f,  0f,0f,  0f,0f,  1f,1f,0.5f,  47f,
					-6f,-6f, 6f,    0f,255f,  0f,  0f,1f,  0f,1f,  0f,1f,  0f,1f,0.5f,  18f,
					-6f,-6f,-6f,    0f,  0f,255f,  1f,1f,  1f,1f,  1f,1f,  1f,0f,0.5f,  31f,
					-6f, 6f,-6f,  255f,  0f,255f,  1f,0f,  1f,0f,  1f,0f,  1f,0f,0.5f,  10f,
				},{
					 6f, 6f, 6f,  255f,  0f,  0f,  0f,0f,  0f,0f,  0f,0f,  1f,1f,0.5f,  47f,
					 6f,-6f, 6f,    0f,255f,  0f,  0f,1f,  0f,1f,  0f,1f,  0f,1f,0.5f,  18f,
					 6f,-6f,-6f,    0f,  0f,255f,  1f,1f,  1f,1f,  1f,1f,  1f,0f,0.5f,  31f,
					 6f, 6f,-6f,  255f,  0f,255f,  1f,0f,  1f,0f,  1f,0f,  1f,0f,0.5f,  10f,
				},{
					 6f,-6f, 6f,  255f,  0f,  0f,  0f,0f,  0f,0f,  0f,0f,  1f,1f,0.5f,  47f,
					 6f,-6f,-6f,    0f,255f,  0f,  0f,1f,  0f,1f,  0f,1f,  0f,1f,0.5f,  18f,
					-6f,-6f,-6f,    0f,  0f,255f,  1f,1f,  1f,1f,  1f,1f,  1f,0f,0.5f,  31f,
					-6f,-6f, 6f,  255f,  0f,255f,  1f,0f,  1f,0f,  1f,0f,  1f,0f,0.5f,  10f,
				},{
					 6f, 6f, 6f,  255f,  0f,  0f,  0f,0f,  0f,0f,  0f,0f,  1f,1f,0.5f,  47f,
					 6f, 6f,-6f,    0f,255f,  0f,  0f,1f,  0f,1f,  0f,1f,  0f,1f,0.5f,  18f,
					-6f, 6f,-6f,    0f,  0f,255f,  1f,1f,  1f,1f,  1f,1f,  1f,0f,0.5f,  31f,
					-6f, 6f, 6f,  255f,  0f,255f,  1f,0f,  1f,0f,  1f,0f,  1f,0f,0.5f,  10f
				}
			};

			public void render(RenderState aRenderState, Material aMaterial)
			{
				//aRenderState.getView().getTransformStack().transform(aGeometryBuffer);

				for (int i = 0; i < 6; i++)
				{
					GeometryBuffer buf = aRenderState.getGeometryBuffer(1, data[i].length, 2);
					buf.writePrimitive(4, vertexFormat, aMaterial, 2);
					buf.writeTexture(aMaterial.getTexture(0));
					buf.writeTexture(tex1);
					buf.writeVertexData(data[i]);
				}
			}
		};
/*

		IndexedGeometry geometry = new IndexedGeometry(new VertexFormat(1, 0x3, 1, 0x2, 0, 0), 6, 4, 8);

		geometry.setVertex(0,  6f,-6f, 6f,  255f,  0f,  0f,  0f,1f);
		geometry.setVertex(1,  6f,-6f,-6f,    0f,255f,  0f,  0f,0f);
		geometry.setVertex(2, -6f,-6f,-6f,    0f,  0f,255f,  1f,0f);
		geometry.setVertex(3, -6f,-6f, 6f,  255f,  0f,255f,  1f,1f);
		geometry.setVertex(4,  6f, 6f, 6f,  255f,  0f,  0f,  1f,0f);
		geometry.setVertex(5,  6f, 6f,-6f,    0f,255f,  0f,  1f,1f);
		geometry.setVertex(6, -6f, 6f,-6f,    0f,  0f,255f,  0f,1f);
		geometry.setVertex(7, -6f, 6f, 6f,  255f,  0f,255f,  0f,0f);

		geometry.setIndices(0, 0, 1, 2, 3);
		geometry.setIndices(1, 4, 5, 6, 7);
		geometry.setIndices(2, 4, 0, 3, 7);
		geometry.setIndices(3, 5, 1, 2, 6);
		geometry.setIndices(4, 7, 3, 2, 6);
		geometry.setIndices(5, 4, 0, 1, 5);

		geometry.setTextures(tex2);
*/
		Material material = new Material(new PackagedResource(this, "demo.zps"), tex2);
		aScene.addChild(new BranchGroup(new Shape3D(geometry, material)));
	}


	private void transPerformance()
	{
		VertexFormat vertexFormat = new VertexFormat(0, 0, 0, 0, 0, 0);

		float[][] data = new float[][]{
			{
			6f, 6f, 6f,6f,-6f, 6f, -6f,-6f, 6f,-6f, 6f, 6f,
			},{
			6f, 6f,-6f,6f,-6f,-6f, -6f,-6f,-6f,-6f, 6f,-6f,
			},{
			-6f, 6f, 6f,-6f,-6f, 6f, -6f,-6f,-6f,-6f, 6f,-6f,
			},{
			6f, 6f, 6f,6f,-6f, 6f, 6f,-6f,-6f,6f, 6f,-6f,
			},{
			6f,-6f, 6f,6f,-6f,-6f, -6f,-6f,-6f,-6f,-6f, 6f,
			},{
			6f, 6f, 6f,6f, 6f,-6f, -6f, 6f,-6f,-6f, 6f, 6f
			}
		};

		GeometryBuffer buf = new GeometryBuffer();
		for (int j = 0; j < 16; j++)
		{
			for (int i = 0; i < 6; i++)
			{
				buf.writePrimitive(4, vertexFormat, new Material(), 0);
				buf.writeVertexData(data[i]);
			}
		}

		Transform3D tr = new Transform3D();

		StopWatch timer = new StopWatch();

		for (int test = 0; test < 100; test++)
		{
			timer.start();
			for (int i = 0; i < 1; i++)
			{
				tr.rotate(0.1, 0.1, 0.1);
				for (int j = 0; j < 1000000; j++)
				{
					tr.transform(buf);
				}
			}
			System.out.print((1000000*16*24/timer.getMillisTime())+"\t");
		}
	}

/*
	private static Shape3D loadModel(String aPath) throws IOException
	{
		ArrayList<IndexedTriangleArray> geometries = new ArrayList<IndexedTriangleArray>();
		ArrayList<Appearance> appearances = new ArrayList<Appearance>();

		LineNumberReader reader = new LineNumberReader(new FileReader(aPath));

		Vector [] coords = null;
		Vector [] normals = null;
		TexCoord [] texCoords = null;
		Color [] colors = null;

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
				Appearance app = new Appearance();
				app.mTexture = Texture2D.read(Zulu.class.getResource(s).getPath(), true);
				appearances.add(app);
			}
			else if (s.startsWith("coords"))
			{
				coords = new Vector[Integer.parseInt(s.substring(7))];
			}
			else if (s.startsWith("texcoords"))
			{
				texCoords = new TexCoord[Integer.parseInt(s.substring(10))];
			}
			else if (s.startsWith("colors"))
			{
				colors = new Color[Integer.parseInt(s.substring(7))];
			}
			else if (s.startsWith("normals"))
			{
				normals = new Vector[Integer.parseInt(s.substring(8))];
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
				coords[i] = new Vector(Double.parseDouble(t[0]), Double.parseDouble(t[1]), Double.parseDouble(t[2]));
			}
			else if (s.startsWith("texcoord"))
			{
				s = s.substring(9);
				int i = Integer.parseInt(s.substring(0, s.indexOf(" ")));
				s = s.substring(s.indexOf(" ")+1).trim();
				String [] t = s.split(" ");
				texCoords[i] = new TexCoord(Double.parseDouble(t[0]), Double.parseDouble(t[1]));
			}
			else if (s.startsWith("color"))
			{
				s = s.substring(6);
				int i = Integer.parseInt(s.substring(0, s.indexOf(" ")));
				s = s.substring(s.indexOf(" ")+1).trim();
				String [] t = s.split(" ");
				colors[i] = new Color(Double.parseDouble(t[0]), Double.parseDouble(t[1]), Double.parseDouble(t[2]), Double.parseDouble(t[3]));
			}
			else if (s.startsWith("normal"))
			{
				s = s.substring(7);
				int i = Integer.parseInt(s.substring(0, s.indexOf(" ")));
				s = s.substring(s.indexOf(" ")+1).trim();
				String [] t = s.split(" ");
				normals[i] = new Vector(Double.parseDouble(t[0]), Double.parseDouble(t[1]), Double.parseDouble(t[2]));
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
				IndexedTriangleArray geometry = new IndexedTriangleArray();
				geometry.setCoordinates(coords);
				geometry.setCoordinateIndices(coordIndices);
				geometry.setTexCoords(texCoords);
				geometry.setTexCoordIndices(texCoordIndices);
//				geometry.setColors(colors);
//				geometry.setColorIndices(colorIndices);

				geometries.add(geometry);
			}
		}

		reader.close();

		Shape3D shape = new Shape3D(geometries.size());

		for (int i = 0; i < geometries.size(); i++)
		{
			shape.setGeometry(i, geometries.get(i));
			shape.setAppearance(i, appearances.get(i));
		}

		return shape;
	}
*/
}