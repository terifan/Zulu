package org.terifan.zulu.math;


public class Tuple2i
{
	public int x, y;


	public Tuple2i()
	{
	}


	public Tuple2i(int x, int y)
	{
		this.x = x;
		this.y = y;
	}


	@Override
	public String toString()
	{
		return ("[x="+x+", y="+y+"]");
	}
}