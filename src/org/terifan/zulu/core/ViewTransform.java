package org.terifan.zulu.core;

import org.terifan.vecmath.Vec3d;
import org.terifan.zulu.Transform3D;


public class ViewTransform extends Transform3D
{
	public ViewTransform()
	{
		super();

		setInverse(true);
	}


	@Override
	public void transform(GeometryBuffer aGeometryBuffer)
	{
		float fx = (float)mForward.x;
		float fy = (float)mForward.y;
		float fz = (float)mForward.z;
		float fw = (float)mForward.w;
		float ix = (float)mInverted.x;
		float iy = (float)mInverted.y;
		float iz = (float)mInverted.z;
		float iw = (float)mInverted.w;
		float tx = (float)mTranslate.x;
		float ty = (float)mTranslate.y;
        float tz = (float)mTranslate.z;

		float [] vertexData = aGeometryBuffer.getVertexData();

		for (int primitiveIndex = 0, vertexIndex = 0, szPC = aGeometryBuffer.getPrimitiveCount(); primitiveIndex < szPC; primitiveIndex++)
		{
			int fieldCount = aGeometryBuffer.getVertexFormat(primitiveIndex).mFieldCount;

			for (int vi = 0, szVC = aGeometryBuffer.getVertexCount(primitiveIndex); vi < szVC; vi++)
			{
				float x = vertexData[vertexIndex+0] - tx;
				float y = vertexData[vertexIndex+1] - ty;
				float z = vertexData[vertexIndex+2] - tz;
		
				float aw = - fx * x - fy * y - fz * z;
				float ax =   fw * x + fy * z - fz * y;
				float ay =   fw * y + fz * x - fx * z;
				float az =   fw * z + fx * y - fy * x;

				vertexData[vertexIndex+0] = aw * ix + ax * iw + ay * iz - az * iy;
				vertexData[vertexIndex+1] = aw * iy + ay * iw + az * ix - ax * iz;
				vertexData[vertexIndex+2] = aw * iz + az * iw + ax * iy - ay * ix;

				vertexIndex += fieldCount;
			}
		}
	}


	@Override
	public void transform(Vec3d aTuple)
	{
		float fx = (float)mForward.x;
		float fy = (float)mForward.y;
		float fz = (float)mForward.z;
		float fw = (float)mForward.w;
		float ix = (float)mInverted.x;
		float iy = (float)mInverted.y;
		float iz = (float)mInverted.z;
		float iw = (float)mInverted.w;

		float x = (float)aTuple.x - (float)mTranslate.x;
		float y = (float)aTuple.y - (float)mTranslate.y;
		float z = (float)aTuple.z - (float)mTranslate.z;

		float aw = - fx * x - fy * y - fz * z;
		float ax =   fw * x + fy * z - fz * y;
		float ay =   fw * y + fz * x - fx * z;
		float az =   fw * z + fx * y - fy * x;

		aTuple.x = aw * ix + ax * iw + ay * iz - az * iy;
		aTuple.y = aw * iy + ay * iw + az * ix - ax * iz;
		aTuple.z = aw * iz + az * iw + ax * iy - ay * ix;
	}
}