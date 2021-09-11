package org.terifan.zulu;

import org.terifan.vecmath.Vec3d;
import org.terifan.vecmath.Vec4d;
import org.terifan.zulu.core.GeometryBuffer;


public class Transform3D
{
	protected boolean mInverse;
	protected Vec3d mTranslate;
	protected Vec3d mRotation;
	protected Vec4d mForward;
	protected Vec4d mInverted;


	public Transform3D()
	{
		mTranslate = new Vec3d();
		mForward = new Vec4d();
		mInverted = new Vec4d();
		mRotation = new Vec3d();

		initialize();
	}


	public Transform3D setTranslation(Vec3d aTuple)
	{
		mTranslate.set(aTuple);
		return this;
	}


	public Transform3D setTranslation(double x, double y, double z)
	{
		mTranslate.set(x, y, z);
		return this;
	}


	public Transform3D translate(Vec3d aTuple)
	{
		mTranslate.add(aTuple);
		return this;
	}


	public Transform3D translate(double x, double y, double z)
	{
		mTranslate.add(x, y, z);
		return this;
	}


	public Transform3D setRotation(Vec3d aTuple)
	{
		mRotation.set(aTuple);
		initialize();
		return this;
	}


	public Transform3D setRotation(double x, double y, double z)
	{
		mRotation.set(x, y, z);
		initialize();
		return this;
	}


	public Transform3D rotate(Vec3d aTuple)
	{
		mRotation.add(aTuple);
		initialize();
		return this;
	}


	public Transform3D rotate(double x, double y, double z)
	{
		mRotation.add(x, y, z);
		initialize();
		return this;
	}


	public Transform3D setInverse(boolean aState)
	{
		mInverse = aState;
		initialize();
		return this;
	}


	public boolean isInverse()
	{
		return mInverse;
	}


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
				float x = vertexData[vertexIndex+0];
				float y = vertexData[vertexIndex+1];
				float z = vertexData[vertexIndex+2];

				float aw = - fx * x - fy * y - fz * z;
				float ax =   fw * x + fy * z - fz * y;
				float ay =   fw * y + fz * x - fx * z;
				float az =   fw * z + fx * y - fy * x;

				vertexData[vertexIndex+0] = aw * ix + ax * iw + ay * iz - az * iy + tx;
				vertexData[vertexIndex+1] = aw * iy + ay * iw + az * ix - ax * iz + ty;
				vertexData[vertexIndex+2] = aw * iz + az * iw + ax * iy - ay * ix + tz;

				vertexIndex += fieldCount;
			}
		}
	}


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

		float x = (float)aTuple.x;
		float y = (float)aTuple.y;
		float z = (float)aTuple.z;

		float aw = - fx * x - fy * y - fz * z;
		float ax =   fw * x + fy * z - fz * y;
		float ay =   fw * y + fz * x - fx * z;
		float az =   fw * z + fx * y - fy * x;

		aTuple.x = aw * ix + ax * iw + ay * iz - az * iy + (float)mTranslate.x;
		aTuple.y = aw * iy + ay * iw + az * ix - ax * iz + (float)mTranslate.y;
		aTuple.z = aw * iz + az * iw + ax * iy - ay * ix + (float)mTranslate.z;
	}


	@Override
	public String toString()
	{
		return "transate={"+mTranslate+"}, rotate={"+mRotation+"}";
	}




	private void initialize()
	{
		if (mInverse)
		{
			doInitializeInverse();
		}
		else
		{
			doInitialize();
		}

		mRotation.limit();

		double scale = 1.0 / mForward.dot(mForward);

		mInverted.w =  mForward.w * scale;
		mInverted.x = -mForward.x * scale;
		mInverted.y = -mForward.y * scale;
		mInverted.z = -mForward.z * scale;
	}


	private void multiply(double aAngle, boolean aVectorX, boolean aVectorY, boolean aVectorZ)
	{
		aAngle *= Math.PI;

		double tmp = Math.sin(aAngle);

		double qx = aVectorX ? tmp : 0,
		       qy = aVectorY ? tmp : 0,
		       qz = aVectorZ ? tmp : 0,
		       qw = Math.cos(aAngle);

		double fx = mForward.x;
		double fy = mForward.y;
		double fz = mForward.z;
		double fw = mForward.w;

		double rw = fw * qw - fx * qx - fy * qy - fz * qz;
		double rx = fw * qx + fx * qw + fy * qz - fz * qy;
		double ry = fw * qy + fy * qw + fz * qx - fx * qz;
		double rz = fw * qz + fz * qw + fx * qy - fy * qx;

		mForward.set(rx, ry, rz, rw);
	}


	private void doInitialize()
	{
		double qy, qz, qw, rx = 0, ry = 0, rz = 0, rw = 0;

		double fx = mForward.x;
		double fy = mForward.y;
		double fz = mForward.z;
		double fw = mForward.w;

		if (mRotation.x != 0)
		{
			double angle = mRotation.x * Math.PI;
			fx = Math.sin(angle);
			fw = Math.cos(angle);
		}
		else
		{
			fx = 0;
			fw = 1;
		}

		if (mRotation.y != 0)
		{
			double angle = mRotation.y * Math.PI;
			qy = Math.sin(angle);
			qw = Math.cos(angle);

			if (mRotation.z != 0)
			{
				rx = fx * qw;
				ry = fw * qy;
				rz = fx * qy;
				rw = fw * qw;
			}
		}
		else
		{
			qy = 0;
			qw = 1;

			if (mRotation.z != 0)
			{
				rx = fx;
				ry = 0;
				rz = 0;
				rw = fw;
			}
		}

		if (mRotation.z != 0)
		{
			double angle = mRotation.z * Math.PI;
			qz = Math.sin(angle);
			qw = Math.cos(angle);

			fw = rw * qw - rz * qz;
			fx = rx * qw + ry * qz;
			fy = ry * qw - rx * qz;
			fz = rw * qz + rz * qw;
		}
		else
		{
			fz = fx * qy;
			fy = fw * qy;
			fw *= qw;
			fx *= qw;
		}

		mForward.set(fx, fy, fz, fw);
	}


	private void doInitializeInverse()
	{
		mForward.set(0,0,0,1);

		multiply(mRotation.z, false, false, true);
		multiply(mRotation.y, false, true, false);
		multiply(mRotation.x, true, false, false);
	};
}