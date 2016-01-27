package org.terifan.zulu.math;


public class Tuple2d
{
	public double x, y;


	public Tuple2d()
	{
	}


	public Tuple2d(double x, double y)
	{
		this.x = x;
		this.y = y;
	}


	public double dot(double x, double y)
	{
		return this.x * x + this.y * y;
	}


	public double dot(Tuple2d aTuple)
	{
		return this.x * aTuple.x + this.y * aTuple.y;
	}


	@Override
	public String toString()
	{
		return ("[x="+x+", y="+y+"]").replace(".0,", ",").replace(".0]", "]");
	}
}