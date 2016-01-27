package org.terifan.zulu.math;


public class Tuple3i
{
	public int x, y, z;


	public Tuple3i()
	{
	}


	public Tuple3i(int x, int y, int z)
	{
		this.x = x;
		this.y = y;
		this.z = z;
	}


	@Override
	public String toString()
	{
		return ("[x="+x+", y="+y+", z="+z+"]");
	}
}