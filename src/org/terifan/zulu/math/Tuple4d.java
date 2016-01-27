package org.terifan.zulu.math;


public class Tuple4d
{
	public double x, y, z, w;


	public Tuple4d()
	{
	}


	public Tuple4d(Tuple4d aTuple)
	{
		set(aTuple);
	}


	public Tuple4d(double x, double y, double z, double w)
	{
		set(x, y, z, w);
	}


	public void set(Tuple4d aTuple)
	{
		set(aTuple.x, aTuple.y, aTuple.z, aTuple.w);
	}


	public void set(double x, double y, double z, double w)
	{
		this.x = x;
		this.y = y;
		this.z = z;
		this.w = w;
	}


	public void add(double x, double y, double z, double w)
	{
		this.x += x;
		this.y += y;
		this.z += z;
		this.w += w;
	}


	public void add(Tuple4d aTuple)
	{
		this.x += aTuple.x;
		this.y += aTuple.y;
		this.z += aTuple.z;
		this.w += aTuple.w;
	}


	public void sub(double x, double y, double z, double w)
	{
		this.x -= x;
		this.y -= y;
		this.z -= z;
		this.w -= w;
	}


	public void sub(Tuple4d aTuple)
	{
		this.x -= aTuple.x;
		this.y -= aTuple.y;
		this.z -= aTuple.z;
		this.w -= aTuple.w;
	}


	public double dot(double x, double y, double z, double w)
	{
		return this.x * x + this.y * y + this.z * z + this.w * w;
	}


	public double dot(Tuple4d aTuple)
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