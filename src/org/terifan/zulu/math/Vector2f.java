package org.terifan.zulu.math;


public class Vector2f extends Tuple2f
{
	public Vector2f()
	{
	}


	public Vector2f(float x, float y)
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
			x -= (int) x;
		}
		if (y > 1 || y < -1)
		{
			y -= (int) y;
		}
	}
}
