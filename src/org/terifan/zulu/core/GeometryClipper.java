package org.terifan.zulu.core;

import java.awt.Graphics;
import org.terifan.geometry.Plane;
import org.terifan.zulu.Material;
import org.terifan.zulu.Texture2D;
import org.terifan.zulu.View;


class GeometryClipper
{
	private final static int [] FILL_GAPS = new int[1 << Kernel.MAX_WORKER_THREADS];

	static
	{
		for (int i = 0; i < FILL_GAPS.length; i++)
		{
			int high = ((Integer.highestOneBit(i) - 1) << 1) + 1;
			int low  = Integer.lowestOneBit(i)  - 1;
			FILL_GAPS[i] = high & (~low);
		}
	}

	private float [][] mClipBuffers = new float[2][1000];
	private int mPerfHiddenSurfaceCount;
	private int mPerfClipPlaneCount;


	public GeometryClipper()
	{
	}


	public void printPerformanceData(Graphics aGraphics)
	{
		aGraphics.drawString("hidden="+mPerfHiddenSurfaceCount, 102, 12);
		aGraphics.drawString("clip="+mPerfClipPlaneCount, 102, 24);

		mPerfHiddenSurfaceCount = 0;
		mPerfClipPlaneCount = 0;
	}


	public void clip(Kernel aKernel, View aView, GeometryBuffer aGeometryBuffer)
	{
		float [] vertexData = aGeometryBuffer.getVertexData();
		Texture2D [] textures = aGeometryBuffer.getTextures();
		Plane [] clipPlanes = aView.getClipPlanes();

		for (int primitiveIndex = 0, vertexIndex = 0, textureIndex = 0, szPC = aGeometryBuffer.getPrimitiveCount(); primitiveIndex < szPC; primitiveIndex++)
		{
			VertexFormat vertexFormat = aGeometryBuffer.getVertexFormat(primitiveIndex);
			Material material = aGeometryBuffer.getMaterial(primitiveIndex);
			int vertexCount = aGeometryBuffer.getVertexCount(primitiveIndex);
			int textureCount = aGeometryBuffer.getTextureCount(primitiveIndex);
			int fieldCount = vertexFormat.mFieldCount;

			float [] newVertexData = vertexData;
			int newVertexIndex = vertexIndex;
			int newVertexCount = vertexCount;

			int clipSet = 0;
			boolean hiddenSurface = false;

			for (int planeIndex = 0; planeIndex < clipPlanes.length; planeIndex++)
			{
				Plane plane = clipPlanes[planeIndex];
				boolean requireClipping = false;
				hiddenSurface = true;

				for (int tmpIndex = 0, fieldIndex = newVertexIndex; tmpIndex < newVertexCount; tmpIndex++, fieldIndex += fieldCount)
				{
					double dot = plane.getNormal().dot(newVertexData[fieldIndex+0], newVertexData[fieldIndex+1], newVertexData[fieldIndex+2]);

					if (dot < plane.getDistance())
					{
						requireClipping = true;
					}
					else
					{
						hiddenSurface = false;

						if (requireClipping)
						{
							break;
						}
					}
				}

				if (hiddenSurface)
				{
					break;
				}

				if (requireClipping)
				{
					mPerfClipPlaneCount += newVertexCount;

					newVertexCount = clipPolygon(plane, newVertexData, mClipBuffers[clipSet], newVertexIndex, newVertexCount, fieldCount);
					newVertexData = mClipBuffers[clipSet];
					newVertexIndex = 0;

					clipSet = 1-clipSet;
				}
			}

			if (hiddenSurface)
			{
				mPerfHiddenSurfaceCount++;
			}
			else
			{
				GeometricProjection geometricProjection = aView.getGeometricProjection();
				geometricProjection.project(newVertexData, newVertexIndex, newVertexCount, fieldCount);

				WorkerThread [] threads = aKernel.mWorkerThreads;

				// find all slices this polygon intersects

				int bits = 0;

				for (int threadIndex = 0; threadIndex < threads.length; threadIndex++)
				{
					GeometryRenderer renderer = threads[threadIndex].getGeometryRenderer();

					for (int i = 0; i < newVertexCount; i++)
					{
						if (renderer.intersect(newVertexData[i*fieldCount+newVertexIndex+1]))
						{
							bits |= 1 << threadIndex;
							break;
						}
					}
				}

				bits = FILL_GAPS[bits];

				// write the vertex data to all slices that the primitive intersects

				for (int threadIndex = 0; threadIndex < threads.length; threadIndex++)
				{
					if ((bits & (1 << threadIndex)) != 0)
					{
						GeometryBuffer output = threads[threadIndex].getGeometryBuffer(1, vertexCount*fieldCount, textureCount);

						output.writePrimitive(newVertexCount, vertexFormat, material, textureCount);
						output.writeTexture(textures, textureIndex, textureCount);
						output.writeVertexData(newVertexData, newVertexIndex, newVertexCount*fieldCount);
					}
				}
			}

			// advance to the next polygon

			vertexIndex += vertexCount * fieldCount;
			textureIndex += textureCount;
		}
	}


	private int clipPolygon(Plane aPlane, float [] aSrcBuffer, float [] aDstBuffer, int aVertexIndex, int aVertexCount, int aFieldCount)
	{
		int clippedVertexCount = 0;

		double endDot = aPlane.getNormal().dot(aSrcBuffer[aVertexIndex+0], aSrcBuffer[aVertexIndex+1], aSrcBuffer[aVertexIndex+2]);

		double planeDistance = aPlane.getDistance();

		for (int startVertexIndex = 0; startVertexIndex < aVertexCount; startVertexIndex++)
		{
			int endVertexIndex = (startVertexIndex + 1) % aVertexCount;
			int fieldOffset = aFieldCount * endVertexIndex;

			double startDot = endDot;
			endDot = aPlane.getNormal().dot(aSrcBuffer[aVertexIndex+fieldOffset+0], aSrcBuffer[aVertexIndex+fieldOffset+1], aSrcBuffer[aVertexIndex+fieldOffset+2]);

			boolean startInside = startDot >= planeDistance;
			boolean endInside = endDot >= planeDistance;

			if (startInside)
			{
				System.arraycopy(aSrcBuffer, aVertexIndex+aFieldCount*startVertexIndex, aDstBuffer, aFieldCount*clippedVertexCount, aFieldCount);
				clippedVertexCount++;
			}

			if (startInside != endInside)
			{
				double scale = (planeDistance - startDot) / (endDot - startDot);

				for (int i = 0; i < aFieldCount; i++)
				{
					float s = aSrcBuffer[aVertexIndex+i+aFieldCount*startVertexIndex];
					float e = aSrcBuffer[aVertexIndex+i+aFieldCount*endVertexIndex];
					aDstBuffer[i+aFieldCount*clippedVertexCount] = (float)(s+scale*(e-s));
				}

				clippedVertexCount++;
			}
		}

		return clippedVertexCount;
	}
}