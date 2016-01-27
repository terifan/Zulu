package org.terifan.zulu.math;


public class Vector4d extends Tuple4d
{
	public Vector4d()
	{
	}


	public Vector4d(double x, double y, double z, double w)
	{
		super(x, y, z, w);
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
		if (w > 1 || w < -1)
		{
			w -= (long) w;
		}
	}
}
