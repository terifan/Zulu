package org.terifan.zulu.math;


public class Tuple4f
{
	public float x, y, z, w;


	public Tuple4f()
	{
	}


	public Tuple4f(Tuple4f aTuple)
	{
		set(aTuple);
	}


	public Tuple4f(float x, float y, float z, float w)
	{
		set(x, y, z, w);
	}


	public void set(Tuple4f aTuple)
	{
		set(aTuple.x, aTuple.y, aTuple.z, aTuple.w);
	}


	public void set(float x, float y, float z, float w)
	{
		this.x = x;
		this.y = y;
		this.z = z;
		this.w = w;
	}


	public void add(float x, float y, float z, float w)
	{
		this.x += x;
		this.y += y;
		this.z += z;
		this.w += w;
	}


	public void add(Tuple4f aTuple)
	{
		this.x += aTuple.x;
		this.y += aTuple.y;
		this.z += aTuple.z;
		this.w += aTuple.w;
	}


	public void sub(float x, float y, float z, float w)
	{
		this.x -= x;
		this.y -= y;
		this.z -= z;
		this.w -= w;
	}


	public void sub(Tuple4f aTuple)
	{
		this.x -= aTuple.x;
		this.y -= aTuple.y;
		this.z -= aTuple.z;
		this.w -= aTuple.w;
	}


	public float dot(float x, float y, float z, float w)
	{
		return this.x * x + this.y * y + this.z * z + this.w * w;
	}


	public float dot(Tuple4f aTuple)
	{
		return this.x * aTuple.x + this.y * aTuple.y + this.z * aTuple.z + this.w * aTuple.w;
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
		if (w > 1 || w < -1)
		{
			w -= (int) w;
		}
	}


	@Override
	public String toString()
	{
		return ("[x="+x+", y="+y+", z="+z+", w="+w+"]").replace(".0,", ",").replace(".0]", "]");
	}
}