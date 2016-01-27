package org.terifan.zulu.math;


public class Vector3d extends Tuple3d
{
	public Vector3d()
	{
	}


	public Vector3d(double x, double y, double z)
	{
		super(x, y, z);
	}


	/**
	 * Removes the integer portion of each component.
	 */
	public void limit()
	{
		if (x > 1 || x < -1)
		{
			x -= (long) x;
		}
		if (y > 1 || y < -1)
		{
			y -= (long) y;
		}
		if (z > 1 || z < -1)
		{
			z -= (long) z;
		}
	}
}
