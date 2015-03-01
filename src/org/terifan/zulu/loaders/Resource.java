package org.terifan.zulu.loaders;

import java.io.InputStream;


public interface Resource
{
	public InputStream getStream();

	public String getName();
}
