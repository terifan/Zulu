package org.terifan.zulu.core;

import org.terifan.zulu.View;


public interface GeometricProjection
{
	public void init(View aView);


	/**
	 * Performs projection (3D to 2D conversion) of the coordinates provided.
	 */
	public void project(float [] aVertexData, int aVertexIndex, int aVertexCount, int aFieldCount);
}