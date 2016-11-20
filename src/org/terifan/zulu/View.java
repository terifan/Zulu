package org.terifan.zulu;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import javax.swing.JComponent;
import java.util.ArrayList;
import org.terifan.algebra.Plane;
import org.terifan.algebra.Vec3d;
import org.terifan.zulu.core.GeometricProjection;
import org.terifan.zulu.core.PerspectiveProjection;
import org.terifan.zulu.core.RenderingStateListener;
import org.terifan.zulu.core.TransformStack;
import org.terifan.zulu.core.ViewTransform;
import org.terifan.zulu.core.WorkerData;


public class View extends JComponent
{
	private String mIdentity;
	private double mFovDegrees;
	private int mWidth;
	private int mHeight;
	private int [] mDepthBuffer;
	private int [] mFrameBuffer;
	private Plane [] mClipPlanes;
	private Plane [] mFrustumPlanes;
	private BufferedImage mRenderedImage;
	private GeometricProjection mGeometricProjection;
	private ViewTransform mViewTransform;
	private ArrayList<RenderingStateListener> mRenderingStateListeners;
	private TransformStack mTransformStack;
	private Graphics mRenderedGraphics;
	private WorkerData [] mWorkerData;


	public View(int aWidth, int aHeight)
	{
		this(aWidth, aHeight, "");
	}


	public View(int aWidth, int aHeight, String aIdentity)
	{
		super.setFocusable(true);
		super.requestFocus();

		setIdentity(aIdentity);
		setFovDegrees(90);
		setGeometricProjection(new PerspectiveProjection());

		mWidth = aWidth;
		mHeight = aHeight;
		mTransformStack = new TransformStack();
		mRenderingStateListeners = new ArrayList<RenderingStateListener>();
		mViewTransform = new ViewTransform();

		mRenderedImage = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration().createCompatibleImage(mWidth, mHeight);
		mFrameBuffer = ((DataBufferInt)mRenderedImage.getRaster().getDataBuffer()).getData();
		mDepthBuffer = new int[mWidth * mHeight];
		mRenderedGraphics = mRenderedImage.getGraphics();

		mWorkerData = new WorkerData[Scene.MAX_WORKER_THREADS];
		for (int i = 0; i < mWorkerData.length; i++)
		{
			mWorkerData[i] = new WorkerData(this);
		}

		// setup frustum
		double f = Math.toRadians(mFovDegrees * 0.5);
		double ch = Math.cos(f);
		double sh = Math.sin(f);
		double cv = Math.cos(f);
		double sv = Math.sin(f);

		mFrustumPlanes = new Plane[]
		{
			new Plane(new Vec3d(  0,   0,  1),     5), //, true),
			new Plane(new Vec3d( ch,   0, sh),     0), //, true),
			new Plane(new Vec3d(-ch,   0, sh),     0), //, true),
			new Plane(new Vec3d(  0, -cv, sv),     0), //, false),
			new Plane(new Vec3d(  0,  cv, sv),     0), //, false),
			new Plane(new Vec3d(  0,   0, -1), -2000), //, false)
		};

		mClipPlanes = new Plane[]{
			mFrustumPlanes[0],
			mFrustumPlanes[1],
			mFrustumPlanes[2]
		};

		mGeometricProjection.init(this); // depends on width, height and FOV
	}


	public ArrayList<RenderingStateListener> getRenderingStateListeners()
	{
		return mRenderingStateListeners;
	}


	public void publishFrame()
	{
		Graphics graphics = super.getGraphics();
		graphics.drawImage(mRenderedImage, 0, 0, null);
		graphics.dispose();
	}


	public Plane [] getFrustumPlanes()
	{
		return mFrustumPlanes;
	}


	public Plane [] getClipPlanes()
	{
		return mClipPlanes;
	}


	public int [] getFrameBuffer()
	{
		return mFrameBuffer;
	}


	public int [] getDepthBuffer()
	{
		return mDepthBuffer;
	}


	public GeometricProjection getGeometricProjection()
	{
		return mGeometricProjection;
	}


	public void setGeometricProjection(GeometricProjection aGeometricProjection)
	{
		mGeometricProjection = aGeometricProjection;
	}


	public double getFovDegrees()
	{
		return mFovDegrees;
	}


	public void setFovDegrees(double aFovDegrees)
	{
		mFovDegrees = aFovDegrees;
	}


	public Transform3D getViewTransform()
	{
		return mViewTransform;
	}


	@Override
	public Dimension getPreferredSize()
	{
		return new Dimension(mWidth,mHeight);
	}


	@Override
	protected void paintComponent(Graphics aGraphics)
	{
		aGraphics.drawImage(mRenderedImage, 0, 0, null);
	}


	@Override
	public int getWidth()
	{
		return mWidth;
	}


	@Override
	public int getHeight()
	{
		return mHeight;
	}


	/**
	 * Adds a RenderingStateListener to this View.
	 *
	 * @param aRenderingStateListener
	 *   the RenderingStateListener to add.
	 */
	public void addRenderingStateListener(RenderingStateListener aRenderingStateListener)
	{
		mRenderingStateListeners.add(aRenderingStateListener);
	}


	/**
	 * Removes a RenderingStateListener from this View.
	 *
	 * @param aRenderingStateListener
	 *   the RenderingStateListener to remove.
	 * @return
	 *   true if a RenderingStateListener was removed.
	 */
	public boolean removeRenderingStateListener(RenderingStateListener aRenderingStateListener)
	{
		return mRenderingStateListeners.remove(aRenderingStateListener);
	}


	public TransformStack getTransformStack()
	{
		return mTransformStack;
	}


	public Graphics getRenderedGraphics()
	{
		return mRenderedGraphics;
	}


	public String getIdentity()
	{
		return mIdentity;
	}


	public void setIdentity(String aIdentity)
	{
		mIdentity = aIdentity;
	}


	public WorkerData getWorkerData(int aWorkerIndex)
	{
		return mWorkerData[aWorkerIndex];
	}
}