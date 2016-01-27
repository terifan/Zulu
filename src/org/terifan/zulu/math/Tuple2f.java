package org.terifan.zulu.math;


public class Tuple2f
{
	public float x, y;


	public Tuple2f()
	{
	}


	public Tuple2f(float x, float y)
	{
		this.x = x;
		this.y = y;
	}


	@Override
	public String toString()
	{
		return ("[x="+x+", y="+y+"]").replace(".0,", ",").replace(".0]", "]");
	}
}