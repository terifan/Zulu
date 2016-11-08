package org.terifan.zulu.core;

import java.awt.Color;
import java.awt.Graphics;
import java.lang.management.ManagementFactory;
import org.terifan.util.log.Log;
import org.terifan.zulu.View;


public abstract class Kernel
{
	public final static int MAX_WORKER_THREADS = 16;

	final Object WORKERTHREAD_LOCK = new String("WORKERTHREAD_LOCK");

	private View mView;
	private boolean mStarted;
	private long mPublishTime;
	private int mWorkerThreadCount;
	private int mRenderedFrameIndex;
	private GeometryClipper mGeometryClipper;

	WorkerThread [] mWorkerThreads;
	boolean mShutdown;
	boolean mPaused;
	boolean mTraversing;
	int mBusyWorkerThreadCount;

	private boolean mPrintPerformanceData;
	private long mPerformanceStart;
	private String mPerformanceTextA = "";
	private String mPerformanceTextB = "";
	private String mPerformanceTextC = "";
	private long mPerformanceUpdate;
	private int mPerformanceCount;
	private long mPerformanceTotalRuntime;


	/**
	 * Constructs a Kernel which use a specified percent of available processors.
	 *
	 * @param aWorkerThreadPercent
	 *   a value between 0.0 and 1.0 specifying how many percent of available
	 *   processors should be used. This method don't allow more threads than
	 *   available CPU count to be created.
	 */
	protected Kernel(double aWorkerThreadPercent)
	{
		this(Math.max(1, Math.min(getAvailableCPU(), (int)Math.round(aWorkerThreadPercent * getAvailableCPU()))));
	}


	/**
	 * Constructs a Kernel which use a specified number processors.
	 *
	 * @param aWorkerThreadCount
	 *   specifies how many processors should be used.
	 */
	protected Kernel(int aWorkerThreadCount)
	{
		mWorkerThreadCount = aWorkerThreadCount;
		mGeometryClipper = new GeometryClipper();
	}


	private static int getAvailableCPU()
	{
		return ManagementFactory.getOperatingSystemMXBean().getAvailableProcessors();
	}
	
	
	public void startRendering()
	{
		// TODO:
		if (mStarted && mShutdown)
		{
			throw new RuntimeException("Restarting a Kernel is currently not supported!!");
		}

		if (mStarted)
		{
			throw new RuntimeException("Scene is already started.");
		}
		if (mView == null)
		{
			throw new RuntimeException("Inherited mView variable must be initialized with a target View before rendering can be started.");
		}

		mWorkerThreads = new WorkerThread[mWorkerThreadCount];

		for (int i = 0; i < mWorkerThreadCount; i++)
		{
			boolean lastThread = i == mWorkerThreadCount-1;
			mWorkerThreads[i] = new WorkerThread(this, i, lastThread);
		}
		for (int i = 0; i < mWorkerThreadCount; i++)
		{
			mWorkerThreads[i].start();
		}
		for (int i = 0; i < mWorkerThreadCount; i++)
		{
			mWorkerThreads[i].initialize(mView);
		}

		// wake all worker threads
		synchronized (WORKERTHREAD_LOCK)
		{
			WORKERTHREAD_LOCK.notifyAll();
		}
	}
	
	
	void workerStopped(WorkerThread aWorkerThread)
	{
	}


	public void stopRendering()
	{
		if (!mStarted)
		{
			throw new RuntimeException("Scene is not started.");
		}
		mShutdown = true;
	}


	public void pauseRendering()
	{
		if (!mStarted)
		{
			throw new RuntimeException("Scene is not started.");
		}
		if (mPaused)
		{
			throw new RuntimeException("Scene is already paused.");
		}
		mPaused = true;
	}


	public void resumeRendering()
	{
		if (!mStarted)
		{
			throw new RuntimeException("Scene is not started.");
		}
		if (!mPaused)
		{
			throw new RuntimeException("Scene is not paused.");
		}
		mPaused = false;
	}


	public boolean isStarted()
	{
		return mStarted;
	}


	public boolean isPaused()
	{
		return mPaused;
	}


	/**
	 * One of the WorkerThread threads calls this method once for each 
	 * View. This method calls the initializeScene for the first View and then 
	 * the renderScene method.
	 */
	protected void beginFrame()
	{
		mBusyWorkerThreadCount = mWorkerThreads.length;

		mView.getTransformStack().clear();
		mView.getTransformStack().push(mView.getViewTransform());

		for (RenderingStateListener listener : mView.getRenderingStateListeners())
		{
			listener.renderingStarted(mView);
		}

		RenderState renderState = new RenderState(this, mView);

		renderScene(renderState);

		renderState.commitRendering();

		for (int i = 0; i < mWorkerThreads.length; i++)
		{
			mWorkerThreads[i].commitGeometryBuffers();
		}

		// unblock worker threads
		mTraversing = false;
	}


	/**
	 * The last of the WorkerThread threads to finish will call this method.
	 * This method finalizes current frame and initializes the worker threads 
	 * for the next frame.
	 *
	 * When this method is call, all worker threads are in a waiting state.
	 */
	protected void finishFrame()
	{
		mPublishTime = System.nanoTime();
		mRenderedFrameIndex++;

		for (RenderingStateListener listener : mView.getRenderingStateListeners())
		{
			listener.renderingFinished(mView);
		}

		if (mPrintPerformanceData)
		{
			printPerformanceData();
		}

		mTraversing = true;

		balanceSliceHeights();

		finishView();

		for (int i = 0; i < mWorkerThreads.length; i++)
		{
			mWorkerThreads[i].initialize(mView);
		}

		// wake all work threads
		synchronized (WORKERTHREAD_LOCK)
		{
			WORKERTHREAD_LOCK.notifyAll();
		}
	}


	protected void drawFrame()
	{
		mView.publishFrame();
	}
	
	
	/**
	 * The finishFrame method calls this method after the current frame has been 
	 * finalized and before the worker threads are initialized for the next 
	 * frame. This method implementation is emtpy.
	 */
	protected void finishView()
	{
	}


	private void balanceSliceHeights()
	{
		int stepSize = 1;

		for (int i = 0; i < mWorkerThreads.length-1; i++)
		{
			WorkerData wd0 = mWorkerThreads[i  ].getWorkerData();
			WorkerData wd1 = mWorkerThreads[i+1].getWorkerData();

			long delay0 = mPublishTime - wd0.mSyncTime;
			long delay1 = mPublishTime - wd1.mSyncTime;

			if (delay0 > delay1)
			{
				if (wd1.mSliceHeight > 10)
				{
					wd0.mSliceHeight += stepSize;
					wd1.mSliceHeight -= stepSize;
					wd1.mSliceY += stepSize;
				}
			}
			else
			{
				if (wd0.mSliceHeight > 10)
				{
					wd0.mSliceHeight -= stepSize;
					wd1.mSliceHeight += stepSize;
					wd1.mSliceY -= stepSize;
				}
			}
		}
	}


	protected void printPerformanceData()
	{
		Graphics g = mView.getRenderedGraphics();

		mPerformanceCount++;
		long currenTime = System.nanoTime();
		if (currenTime > mPerformanceUpdate)
		{
			if (mPerformanceTotalRuntime == 0 && mRenderedFrameIndex >= 100)
			{
				mPerformanceTotalRuntime = currenTime;
			}

			if (mPerformanceUpdate > 0)
			{
				mPerformanceTextA = ""+mRenderedFrameIndex;
				mPerformanceTextB = ""+(int)((mRenderedFrameIndex-100)/((currenTime-mPerformanceTotalRuntime)/1000000000000.0))/1000f;
				mPerformanceTextC = ""+(int)(mPerformanceCount/((currenTime-mPerformanceStart)/10000000000.0))/10f;
			}
			mPerformanceStart = currenTime;
			mPerformanceUpdate = mPerformanceStart+500000000L; // update the performance counter twice per second
			mPerformanceCount = 0;
		}

		int w = mView.getWidth();

		g.setColor(Color.WHITE);
		g.drawString(mPerformanceTextC, w-35, 12);
		g.drawString(mPerformanceTextB, w-135, 12);
		g.drawString(mPerformanceTextA, w-235, 12);

		mGeometryClipper.printPerformanceData(g);

		for (int i = 0; i < mWorkerThreads.length; i++)
		{
			mWorkerThreads[i].printPerformanceData(g);
		}
	}


	protected void setTargetView(View aView)
	{
		mView = aView;
	}


	public void setPrintPerformanceData(boolean aState)
	{
		mPrintPerformanceData = aState;
	}


	public boolean getPrintPerformanceData()
	{
		return mPrintPerformanceData;
	}


	/**
	 * The renderScene implementation calls this method. This method transforms 
	 * the geometry using the Views TransformStack and then clips it using a 
	 * GeometryClipper.
	 */
	protected void renderGeometry(GeometryBuffer aGeometryBuffer)
	{
		mView.getTransformStack().transform(aGeometryBuffer);

		mGeometryClipper.clip(this, mView, aGeometryBuffer);
	}


	protected abstract void renderScene(RenderState aRenderState);
}