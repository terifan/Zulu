package org.terifan.zulu.math;


public class Vector2d extends Tuple2d
{
	public Vector2d()
	{
	}


	public Vector2d(double x, double y)
	{
		super(x, y);
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
	}
}
