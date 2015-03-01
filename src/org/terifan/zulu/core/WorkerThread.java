package org.terifan.zulu.core;

import java.awt.Graphics;
import java.util.concurrent.ConcurrentLinkedQueue;
import org.terifan.zulu.View;


class WorkerThread extends Thread
{
	private final Kernel mKernel;
	private final int mThreadIndex;
	private final boolean mTraverserThread;
	private final boolean mLastThread;
	private final GeometryRenderer mGeometryRenderer;

	private WorkerData mWorkerData;
	private ConcurrentLinkedQueue<GeometryBuffer> mGeometryQueue = new ConcurrentLinkedQueue<GeometryBuffer>();
	private GeometryBuffer mGeometryBuffer;
	private boolean mInitialized;


	public WorkerThread(Kernel aKernel, int aThreadIndex, boolean aLastThread)
	{
		setDaemon(true);

		mKernel = aKernel;
		mThreadIndex = aThreadIndex;
		mTraverserThread = aThreadIndex == 0;
		mLastThread = aLastThread;
		mGeometryRenderer = new GeometryRenderer(mThreadIndex, aThreadIndex == 0, aThreadIndex == mKernel.mWorkerThreads.length-1);
		mGeometryBuffer = new GeometryBuffer();
	}


	public void initialize(View aView)
	{
		// initialize worker
		mWorkerData = aView.getWorkerData(mThreadIndex);

		if (!mWorkerData.mInitialized)
		{
			// TODO: slice y/height must account for uneven view heights!!!

			mWorkerData.mSliceHeight = aView.getHeight() / mKernel.mWorkerThreads.length;
			mWorkerData.mSliceY = mWorkerData.mSliceHeight * mThreadIndex;
			mWorkerData.mInitialized = true;
		}

		// initialize renderer
		mGeometryRenderer.initialize(aView, mWorkerData.mSliceY, mWorkerData.mSliceHeight);

		mInitialized = true;
	}


	@Override
	public void run()
	{
		// worker threads wait here for the Kernel class to call the initialize 
		// method of each work
		synchronized (mKernel.WORKERTHREAD_LOCK)
		{
			if (!mInitialized)
			{
				try
				{
					mKernel.WORKERTHREAD_LOCK.wait();
				}
				catch (InterruptedException e)
				{
				}
			}
		}

		try
		{
			for (;;)
			{
				// the traverser thread will traverse the entire Kernel and feed all worker threads with GeometryBuffers
				if (mTraverserThread)
				{
					mKernel.beginFrame();
				}

				// the last thead will output the previous fram to screen
				if (mLastThread)
				{
					mKernel.drawFrame();
				}

				// render scene elements
				for (;;)
				{
					// block until opaque geometry arrives

					// TODO: improve
					while (mKernel.mTraversing && mGeometryQueue.isEmpty())
					{
					}

					// render opaque element
					for (GeometryBuffer buffer; (buffer = mGeometryQueue.poll()) != null;)
					{
						mGeometryRenderer.render(buffer);
					}

					// break if all done
					if (!mKernel.mTraversing && mGeometryQueue.isEmpty())
					{
						break;
					}
				}

				// post processing and copy frame buffer
				mGeometryRenderer.postProcess();

				mGeometryQueue.clear();

				mWorkerData.mSyncTime = System.nanoTime();

				// synchronize all workers
				synchronized (mKernel.WORKERTHREAD_LOCK)
				{
					if (--mKernel.mBusyWorkerThreadCount == 0)
					{
						// publish frame
						mKernel.finishFrame();
					}
					else
					{
						// wait for other workers to finish
						try
						{
							mKernel.WORKERTHREAD_LOCK.wait();
						}
						catch (InterruptedException e)
						{
						}
					}
				}

				//System.out.print(" "+(System.nanoTime()-mWorkerData.mSyncTime)/1000000);

				// TODO: the paused and shutdown flags must be synchronized some how so that all workers do the same thing!!!
	
				if (mKernel.mPaused)
				{
					//...
				}
				if (mKernel.mShutdown)
				{
					break;
				}
			}
			
			mKernel.workerStopped(this);
		}
		catch (Throwable e)
		{
			e.printStackTrace(System.out);
		}
	}


	/**
	 * The GeometryClipper adds geometry using this method.
	 */
	public GeometryBuffer getGeometryBuffer(int aMinimumPrimitives, int aMinimumVertexData, int aMinimumTextures)
	{
		if (!mGeometryBuffer.hasCapacity(aMinimumPrimitives, aMinimumVertexData, aMinimumTextures))
		{
			commitGeometryBuffers();
		}

		return mGeometryBuffer;
	}


	void commitGeometryBuffers()
	{
		if (mGeometryBuffer.getPrimitiveCount() > 0)
		{
			mGeometryQueue.add(mGeometryBuffer);
			// TODO: use caching
			mGeometryBuffer = new GeometryBuffer();
		}
	}

	
	GeometryRenderer getGeometryRenderer()
	{
		return mGeometryRenderer;
	}

	
	WorkerData getWorkerData()
	{
		return mWorkerData;
	}


	public void printPerformanceData(Graphics aGraphics)
	{
		mGeometryRenderer.printPerformanceData(aGraphics);
	}
}