package org.terifan.zulu.core;

import org.terifan.zulu.View;


public class PerspectiveProjection implements GeometricProjection
{
	private float mProjectionScale;
	private float mCenterX;
	private float mCenterY;


	@Override
	public void init(View aView)
	{
		mCenterX = 0.5f * aView.getWidth();
		mCenterY = 0.5f * aView.getHeight();
		mProjectionScale = (float)(mCenterX / Math.tan(0.5 * Math.toRadians(aView.getFovDegrees())));
view = aView;
	}
View view;

	@Override
	public void project(float [] aVertexData, int aVertexIndex, int aVertexCount, int aFieldCount)
	{
		float cx = mCenterX;
		float cy = mCenterY;

		for (int i = 0; i < aVertexCount; i++)
		{
			{
			float iz = 1f / aVertexData[aVertexIndex+2];
			float piz = mProjectionScale * iz;

			aVertexData[aVertexIndex  ] = cx + piz * aVertexData[aVertexIndex  ];
			aVertexData[aVertexIndex+1] = cy - piz * aVertexData[aVertexIndex+1];
			aVertexData[aVertexIndex+2] = iz;
			}
//
//			float x = aVertexData[aVertexIndex  ];
//			float y = aVertexData[aVertexIndex+1];
//			float z = aVertexData[aVertexIndex+2];
//
//
//			double znear = view.getFrustumPlanes()[0].distance;
//			double zfar = view.getFrustumPlanes()[5].distance;
////			double q = zfar / (zfar - znear);
////			double w = 2 * znear / view.getWidth();
////			double h = 2 * znear / view.getHeight();
//
//double fov=60.0f;
//double aspect=1.3333f;
//
//double xymax = znear * Math.tan(0.5 * Math.toRadians(fov));
//double ymin = -xymax;
//double xmin = -xymax;
//
//double width = xymax - xmin;
//double height = xymax - ymin;
//
//double depth = zfar - znear;
//double q = -(zfar + znear) / depth;
//double qn = -2 * (zfar * znear) / depth;
//
//double w = 2 * znear / width;
//w = w / aspect;
//double h = 2 * znear / height;
//
//double [][] m = new double[4][4];
//
//m[0][0]  = w;
//m[0][1]  = 0;
//m[0][2]  = 0;
//m[0][3]  = 0;
//
//m[1][0]  = 0;
//m[1][1]  = h;
//m[1][2]  = 0;
//m[1][3]  = 0;
//
//m[2][0]  = 0;
//m[2][1]  = 0;
//m[2][2] = q;
//m[2][3] = -1;
//
//m[3][0] = 0;
//m[3][1] = 0;
//m[3][2] = qn;
//m[3][3] = 0;
//
//
//			Matrix m1 = new Matrix(m);
//
//			Matrix m2 = new Matrix(new double[][]{{1,0,0,-x},{0,1,0,-y},{0,0,1,0},{0,0,1/z,0}});
//
//			Matrix m3 = new Matrix(new double[][]{{}});
//
//			Matrix m3 = m1.mul(m2);
//
////			aVertexData[aVertexIndex  ] = (float)m3.data[0][0];
////			aVertexData[aVertexIndex+1] = (float)m3.data[1][1];
////			aVertexData[aVertexIndex+2] = (float)m3.data[2][2];

			aVertexIndex += aFieldCount;
		}
	}
}