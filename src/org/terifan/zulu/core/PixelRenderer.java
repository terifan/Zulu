package org.terifan.zulu.core;

import org.terifan.zulu.Material;


public interface PixelRenderer
{
	public void initEdgeA(GeometryBuffer aGeometryBuffer, int aHeaderIndex, int aTopVertex, int aBottomVertex, int aSkip);

	public void initEdgeB(GeometryBuffer aGeometryBuffer, int aHeaderIndex, int aTopVertex, int aBottomVertex, int aSkip);

	public void initShader(GeometryBuffer aGeometryBuffer, int aTextureOffset, Material aMaterial, int [] aFrameBuffer, int [] aDepthBuffer);

	public void renderScanline(int aBufferOffsetStart);
}