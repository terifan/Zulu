package org.terifan.zulu.core;

import org.terifan.zulu.math.Vector3d;


public class Plane
{
	public Vector3d normal;
	public double distance;
	public boolean intersectable;


	public Plane(Vector3d aNormal, double aDistance)
	{
		this(aNormal, aDistance, true);
	}

	public Plane(Vector3d aNormal, double aDistance, boolean aIntersectable)
	{
		normal = aNormal;
		distance = aDistance;
		intersectable = aIntersectable;
	}


	public boolean pointInsidePlane(Vector3d aPoint)
	{
		return normal.dot(aPoint) > distance;
	}
}