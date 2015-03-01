package org.terifan.zulu.core;

import org.terifan.zulu.View;


public class RenderState
{
	public final static int VOLATILE_BUFFER_SIZE = 1024*1024;

	private Kernel mKernel;
	private View mView;
	private GeometryBuffer mGeometryBuffer;
	private float [] mVolatileBuffer;


	public RenderState(Kernel aKernel, View aView)
	{
		mKernel = aKernel;
		mView = aView;
		mGeometryBuffer = new GeometryBuffer();
		mVolatileBuffer = new float[VOLATILE_BUFFER_SIZE];
	}


	public View getView()
	{
		return mView;
	}


	public Kernel getKernel()
	{
		return mKernel;
	}


	public GeometryBuffer getGeometryBuffer(int aMinimumPrimitives, int aMinimumVertexData, int aMinimumTextures)
	{
		if (!mGeometryBuffer.hasCapacity(aMinimumPrimitives, aMinimumVertexData, aMinimumTextures))
		{
			commitRendering();
		}

		return mGeometryBuffer;
	}


	public void commitRendering()
	{
		mKernel.renderGeometry(mGeometryBuffer);

		// TODO: use caching
		mGeometryBuffer = new GeometryBuffer();
	}


	public float [] getVolatileBuffer()
	{
		return mVolatileBuffer;
	}
}