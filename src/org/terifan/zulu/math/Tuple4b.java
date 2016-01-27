package org.terifan.zulu.math;


public class Tuple4b
{
	public byte w, x, y, z;


	public Tuple4b()
	{
	}


	public Tuple4b(byte x, byte y, byte z, byte w)
	{
		this.x = x;
		this.y = y;
		this.z = z;
		this.w = w;
	}


	@Override
	public String toString()
	{
		return ("[x="+x+", y="+y+", z="+z+", w="+w+"]");
	}
}