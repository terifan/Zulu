package org.terifan.zulu.core;

import org.terifan.zulu.View;


public final class WorkerData
{
	View mView;
	boolean mInitialized;
	int mSliceY;
	int mSliceHeight;
	long mSyncTime;

	public WorkerData(View aView)
	{
		mView = aView;
	}
}