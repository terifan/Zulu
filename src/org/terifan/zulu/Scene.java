package org.terifan.zulu;

import org.terifan.zulu.core.Kernel;
import org.terifan.zulu.core.RenderState;
import org.terifan.zulu.core.WorkerData;


public abstract class Scene extends Kernel
{
	private View [] mViews = new View[0];
	private int mTargetViewIndex;
	private long mRenderedFrameIndex;
	private WorkerData [] mWorkerData;


	/**
	 * Constructs a Scene which use a specified percent of available processors.
	 *
	 * @param aWorkerThreadPercent
	 *   a value between 0.0 and 1.0 specifying how many percent of available
	 *   processors should be used. This method don't allow more threads than
	 *   available CPU count to be created.
	 * @param aViews
	 *   an optional list of Views to be attached to this Scene.
	 */
	public Scene(double aWorkerThreadPercent, View ... aViews)
	{
		super(aWorkerThreadPercent);

		addView(aViews);
	}


	/**
	 * Constructs a Scene which use a specified number processors.
	 *
	 * @param aWorkerThreadCount
	 *   specifices how many processors should be used.
	 * @param aViews
	 *   an optional list of Views to be attached to this Scene.
	 */
	public Scene(int aWorkerThreadCount, View ... aViews)
	{
		super(aWorkerThreadCount);

		addView(aViews);
	}


	public void addView(View ... aViews)
	{
		if (aViews.length > 0)
		{	
			View [] t = new View[mViews.length + aViews.length];
			System.arraycopy(mViews, 0, t, 0, mViews.length);
			System.arraycopy(aViews, 0, t, mViews.length, aViews.length);

			if (mViews.length == 0)
			{
				// initialize Kernel to render to a View
				super.setTargetView(t[0]);
			}

			mViews = t;
		}
	}


	@Override
	protected void beginFrame()
	{
		// initialize scene if this is the first view in the current frame
		if (mTargetViewIndex == 0)
		{
			initializeScene();
		}

		super.beginFrame();
	}


	@Override
	protected void finishView()
	{
		mTargetViewIndex++;

		if (mTargetViewIndex == mViews.length)
		{
			mTargetViewIndex = 0;
			mRenderedFrameIndex++;
		}

		super.setTargetView(mViews[mTargetViewIndex]);
	}


	protected abstract void initializeScene();


	protected abstract void renderScene(RenderState aRenderState);
}