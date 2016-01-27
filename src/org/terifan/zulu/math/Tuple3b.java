package org.terifan.zulu.math;


public class Tuple3b
{
	public byte x, y, z;


	public Tuple3b()
	{
	}


	public Tuple3b(byte x, byte y, byte z)
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