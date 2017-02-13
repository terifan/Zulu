package org.terifan.zulu.core;

import java.awt.Color;
import java.awt.Graphics;
import org.terifan.zulu.Material;
import org.terifan.zulu.View;


class GeometryRenderer
{
	private View mView;
	private int [] mFrameBuffer = new int[0];
	private int [] mDepthBuffer = new int[0];
	private int mSliceY;
	private int mSliceHeight;
	private int mThreadIndex;
	private boolean mTopRenderer;
	private boolean mBottomRenderer;
	private int mPerfRenderedPolygonCount;
	private int mPerfRenderedPrimitiveCount;


	public GeometryRenderer(int aThreadIndex, boolean aTopRenderer, boolean aBottomRenderer)
	{
		mThreadIndex = aThreadIndex;
		mTopRenderer = aTopRenderer;
		mBottomRenderer = aBottomRenderer;
	}


	public boolean intersect(float y)
	{
		return (mTopRenderer || y >= mSliceY) && (mBottomRenderer || y < mSliceY+mSliceHeight);
	}


	public void printPerformanceData(Graphics aGraphics)
	{
		aGraphics.setColor(Color.WHITE);
		aGraphics.drawLine(0, mSliceY, mView.getWidth(), mSliceY);
		aGraphics.drawString("polys="+mPerfRenderedPolygonCount, 2, mSliceY+12);
		aGraphics.drawString("prims="+mPerfRenderedPrimitiveCount, 2, mSliceY+24);
	}


	public void initialize(View aView, int aSliceY, int aSliceHeight)
	{
		mView = aView;
		mSliceY = aSliceY;
		mSliceHeight = aSliceHeight;

		int sz = mView.getWidth() * mSliceHeight;
		if (sz > mFrameBuffer.length)
		{
			mFrameBuffer = new int[sz];
			mDepthBuffer = new int[sz];
		}

//		if (mView.getClearFrameBuffer())
//		int clearColor = mView.getClearColor();
		for (int i = 0; i < sz; i++)
		{
			mFrameBuffer[i] = 0;
		}

//		int clearDepth = mView.getClearDepth();
		for (int i = 0; i < sz; i++)
		{
			mDepthBuffer[i] = 0x7fffffff;
		}

		mPerfRenderedPolygonCount = 0;
		mPerfRenderedPrimitiveCount = 0;
	}


	public void postProcess()
	{
		int [] src = mFrameBuffer;
		int [] dst = mView.getFrameBuffer();
		int w = mView.getWidth();

		for (int i = 0, j = w * mSliceY, sz = w * mSliceHeight; i < sz; i++, j++)
		{
			//TODO: gamma correction
			dst[j] = src[i];
		}
	}


	public void render(GeometryBuffer aGeometryBuffer)
	{
		//float [] vertexData = aGeometryBuffer.getVertexData();
		//Texture2D [] textures = aGeometryBuffer.getTextures();

		for (int primitiveIndex = 0, vertexIndex = 0, textureIndex = 0, szPC = aGeometryBuffer.getPrimitiveCount(); primitiveIndex < szPC; primitiveIndex++)
		{
			VertexFormat vertexFormat = aGeometryBuffer.getVertexFormat(primitiveIndex);
			int vertexCount = aGeometryBuffer.getVertexCount(primitiveIndex);
			int textureCount = aGeometryBuffer.getTextureCount(primitiveIndex);
			Material material = aGeometryBuffer.getMaterial(primitiveIndex);

			assert material != null : "Material is not defined for primitive " + primitiveIndex;

			PixelRenderer renderer = material.getRenderer(mThreadIndex);

			renderer.initShader(aGeometryBuffer, textureIndex, material, mFrameBuffer, mDepthBuffer);

			int fieldCount = vertexFormat.mFieldCount;

			for (int i = 2; i < vertexCount; i++)
			{
				int vertexA = vertexIndex;
				int vertexB = vertexIndex + (i-1) * fieldCount;
				int vertexC = vertexIndex + (i  ) * fieldCount;

				renderTriangle(renderer, aGeometryBuffer, primitiveIndex, vertexA, vertexB, vertexC);

				mPerfRenderedPrimitiveCount++;
			}

			vertexIndex += vertexCount * fieldCount;
			textureIndex += textureCount;

			mPerfRenderedPolygonCount++;
		}
	}


	private void renderTriangle(PixelRenderer aRenderer, GeometryBuffer aGeometryBuffer, int aHeaderOffset, int aVertexA, int aVertexB, int aVertexC)
	{
		int viewWidth = mView.getWidth();
		int viewBottom = mSliceY+mSliceHeight;
		float [] buffer = aGeometryBuffer.getVertexData();

		// Sort vertices with respect to their y-coordinate
		if (buffer[aVertexB+1] < buffer[aVertexA+1]) { int temp = aVertexA; aVertexA = aVertexB; aVertexB = temp; }
		if (buffer[aVertexC+1] < buffer[aVertexA+1]) { int temp = aVertexA; aVertexA = aVertexC; aVertexC = temp; }
		if (buffer[aVertexC+1] < buffer[aVertexB+1]) { int temp = aVertexB; aVertexB = aVertexC; aVertexC = temp; }

		int ay = (int)Math.ceil(buffer[aVertexA+1]);
		int by = (int)Math.ceil(buffer[aVertexB+1]);
		int cy = (int)Math.ceil(buffer[aVertexC+1]);

		if (ay > by || by > cy || ay > cy) throw new IllegalStateException();

		// Render upper part of triangle
		int skip = ay >= mSliceY ? 0 : mSliceY - ay;

		aRenderer.initEdgeB(aGeometryBuffer, aHeaderOffset, aVertexA, aVertexC, skip);

		if (by - ay > skip)
		{
			aRenderer.initEdgeA(aGeometryBuffer, aHeaderOffset, aVertexA, aVertexB, skip);

			for (int y=ay+skip, offset=viewWidth*(y-mSliceY), endY=Math.min(by,viewBottom); y < endY; y++, offset+=viewWidth)
			{
				aRenderer.renderScanline(offset);
			}
		}

		// Render lower part of triangle
		if (by < viewBottom)
		{
			skip = by >= mSliceY ? 0 : mSliceY - by;

			if (cy - by > skip)
			{
				aRenderer.initEdgeA(aGeometryBuffer, aHeaderOffset, aVertexB, aVertexC, skip);

				for (int y=by+skip, offset=viewWidth*(y-mSliceY), endY=Math.min(cy,viewBottom); y < endY; y++, offset+=viewWidth)
				{
					aRenderer.renderScanline(offset);
				}
			}
		}
	}
}