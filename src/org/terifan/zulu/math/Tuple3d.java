package org.terifan.zulu.math;


public class Tuple3d
{
	public double x, y, z;


	public Tuple3d()
	{
	}


	public Tuple3d(Tuple3d aTuple)
	{
		set(aTuple);
	}


	public Tuple3d(double x, double y, double z)
	{
		set(x, y, z);
	}


	public void set(Tuple3d aTuple)
	{
		this.x = aTuple.x;
		this.y = aTuple.y;
		this.z = aTuple.z;
	}


	public void set(double v)
	{
		this.x = v;
		this.y = v;
		this.z = v;
	}


	public void set(double x, double y, double z)
	{
		this.x = x;
		this.y = y;
		this.z = z;
	}


	public void set(double [] aValues, int aIndex)
	{
		this.x = aValues[aIndex  ];
		this.y = aValues[aIndex+1];
		this.z = aValues[aIndex+2];
	}


	public void set(float [] aValues, int aIndex)
	{
		this.x = aValues[aIndex  ];
		this.y = aValues[aIndex+1];
		this.z = aValues[aIndex+2];
	}


	public void add(double v)
	{
		this.x += v;
		this.y += v;
		this.z += v;
	}


	public void add(double x, double y, double z)
	{
		this.x += x;
		this.y += y;
		this.z += z;
	}


	public void add(Tuple3d aTuple)
	{
		this.x += aTuple.x;
		this.y += aTuple.y;
		this.z += aTuple.z;
	}


	public void sub(double v)
	{
		this.x -= v;
		this.y -= v;
		this.z -= v;
	}


	public void sub(double x, double y, double z)
	{
		this.x -= x;
		this.y -= y;
		this.z -= z;
	}


	public void sub(Tuple3d aTuple)
	{
		this.x -= aTuple.x;
		this.y -= aTuple.y;
		this.z -= aTuple.z;
	}


	public void scale(double v)
	{
		this.x *= v;
		this.y *= v;
		this.z *= v;
	}


	public void scale(double x, double y, double z)
	{
		this.x *= x;
		this.y *= y;
		this.z *= z;
	}


	public void scale(Tuple3d aTuple)
	{
		this.x *= aTuple.x;
		this.y *= aTuple.y;
		this.z *= aTuple.z;
	}


	public double dot(double x, double y, double z)
	{
		return this.x * x + this.y * y + this.z * z;
	}


	public double dot(Tuple3d aTuple)
	{
		return this.x * aTuple.x + this.y * aTuple.y + this.z * aTuple.z;
	}


	public Tuple3d cross(double x, double y, double z)
	{
		double tx = this.y * z - this.z * y;
		double ty = this.z * x - this.x * z;
		double tz = this.x * y - this.y * x;

		this.x = tx;
        this.y = ty;
        this.z = tz;

		return this;
	}


	@Override
	public String toString()
	{
		return ("{x="+x+", y="+y+", z="+z+"}").replace(".0,", ",").replace(".0]", "]");
	}
}