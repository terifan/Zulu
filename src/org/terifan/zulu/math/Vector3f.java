package org.terifan.zulu.math;


public class Vector3f extends Tuple3f
{
	public Vector3f()
	{
	}


	public Vector3f(float x, float y, float z)
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
			x -= (int) x;
		}
		if (y > 1 || y < -1)
		{
			y -= (int) y;
		}
		if (z > 1 || z < -1)
		{
			z -= (int) z;
		}
	}
}
