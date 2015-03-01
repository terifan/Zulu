package org.terifan.zulu.loaders;

import java.io.InputStream;


public class PackagedResource implements Resource
{
	private Class mClass;
	private String mName;


	public PackagedResource(Class aClass, String aName)
	{
		mClass = aClass;
		mName = aName;
	}


	public PackagedResource(Object aObject, String aName)
	{
		this(aObject.getClass(), aName);
	}


	@Override
	public InputStream getStream()
	{
		return mClass.getResourceAsStream(mName);
	}


	@Override
	public String getName()
	{
		return mName;
	}
}
