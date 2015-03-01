package org.terifan.zulu;

import java.util.ArrayList;
import java.util.Arrays;
import org.terifan.zulu.core.PixelRenderer;
import org.terifan.zulu.core.PixelShaderCompiler;
import org.terifan.zulu.loaders.Resource;


public class Material
{
	private boolean mCompiled;
	private PixelRenderer [] mPixelRenderer;
	private ArrayList<Texture2D> mTextures;
	private Resource mResource;


	public Material()
	{
	}
	

	public Material(Resource aResource, Texture2D ... aTextures)
	{
		mResource = aResource;
		mTextures = new ArrayList<Texture2D>();

		if (aTextures != null)
		{
			mTextures.addAll(Arrays.asList(aTextures));
		}
	}


	public Texture2D getTexture(int aIndex)
	{
		return mTextures.get(aIndex);
	}


	public void setTexture(int aIndex, Texture2D aTexture)
	{
		mTextures.set(aIndex, aTexture);
	}


	public void addTexture(Texture2D aTexture)
	{
		mTextures.add(aTexture);
	}


	public int getTextureCount()
	{
		return mTextures.size();
	}


	public synchronized void compile()
	{
		if (!mCompiled)
		{
			mPixelRenderer = new PixelShaderCompiler().compile(this, mResource);
			mCompiled = true;
		}
	}


	public PixelRenderer getRenderer(int aThreadIndex)
	{
		if (!mCompiled)
		{
			compile();
		}

		return mPixelRenderer[aThreadIndex];
	}
}