package org.terifan.zulu.math;


public class Tuple3f
{
	public float x, y, z;


	public Tuple3f()
	{
	}


	public Tuple3f(Tuple3f aTuple)
	{
		set(aTuple);
	}


	public Tuple3f(float x, float y, float z)
	{
		set(x, y, z);
	}


	public void set(Tuple3f aTuple)
	{
		this.x = aTuple.x;
		this.y = aTuple.y;
		this.z = aTuple.z;
	}


	public void set(float x, float y, float z)
	{
		this.x = x;
		this.y = y;
		this.z = z;
	}


	public void set(float [] aValues, int aIndex)
	{
		this.x = aValues[aIndex  ];
		this.y = aValues[aIndex+1];
		this.z = aValues[aIndex+2];
	}


	public void add(float x, float y, float z)
	{
		this.x += x;
		this.y += y;
		this.z += z;
	}


	public void add(Tuple3f aTuple)
	{
		this.x += aTuple.x;
		this.y += aTuple.y;
		this.z += aTuple.z;
	}


	public void sub(float x, float y, float z)
	{
		this.x -= x;
		this.y -= y;
		this.z -= z;
	}


	public void sub(Tuple3f aTuple)
	{
		this.x -= aTuple.x;
		this.y -= aTuple.y;
		this.z -= aTuple.z;
	}


	public float dot(float x, float y, float z)
	{
		return this.x * x + this.y * y + this.z * z;
	}


	public float dot(Tuple3f aTuple)
	{
		return this.x * aTuple.x + this.y * aTuple.y + this.z * aTuple.z;
	}


	@Override
	public String toString()
	{
		return ("[x="+x+", y="+y+", z="+z+"]").replace(".0,", ",").replace(".0]", "]");
	}
}