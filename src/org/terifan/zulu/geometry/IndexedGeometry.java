package org.terifan.zulu.geometry;

import org.terifan.zulu.Material;
import org.terifan.zulu.Texture2D;
import org.terifan.zulu.core.Geometry;
import org.terifan.zulu.core.GeometryBuffer;
import org.terifan.zulu.core.RenderState;
import org.terifan.zulu.core.VertexFormat;


public class IndexedGeometry implements Geometry
{
	protected float [] mVertexData;
	protected short [] mIndices;
	protected VertexFormat mVertexFormat;
	protected int mVerticesPerPrimitive;
	protected int mPrimitiveCount;
	protected Texture2D [] mTextures;


	public IndexedGeometry(VertexFormat aVertexFormat, int aPrimitiveCount, int aVerticesPerPrimitive, int aVertexCount)
	{
		mVertexFormat = aVertexFormat;
		mVerticesPerPrimitive = aVerticesPerPrimitive;
		mPrimitiveCount = aPrimitiveCount;
		mVertexData = new float[aVertexFormat.mFieldCount * aVertexCount];
		mIndices = new short[aPrimitiveCount * mVerticesPerPrimitive];
	}


	/**
	 * Sets all the vertex fields defined by the VertexFormat in constructor.
	 *
	 * @param aVertexIndex
	 *   the first vertex index to set fields for.
	 * @param aVertexData
	 *   the vertex data. This array must
	 */
	public void setVertex(int aVertexIndex, float ... aVertexData)
	{
		if ((aVertexData.length % mVertexFormat.mFieldCount) != 0)
		{
			throw new IllegalArgumentException("aVertexData has illegal length: " + aVertexData.length + ", must be a multiple of: " + mVertexFormat.mFieldCount);
		}

		System.arraycopy(aVertexData, 0, mVertexData, aVertexIndex * mVertexFormat.mFieldCount, mVertexFormat.mFieldCount);
	}


	public void setIndices(int aPrimitiveIndex, short ... aIndices)
	{
		if ((aIndices.length % mVerticesPerPrimitive) != 0)
		{
			throw new IllegalArgumentException("aIndices has illegal length: " + aIndices.length + ", must be a multiple of: " + mVerticesPerPrimitive);
		}
		if (aPrimitiveIndex >= mPrimitiveCount)
		{
			throw new IllegalArgumentException("Illegal Primitive index: " + aPrimitiveIndex + ", Primitive count: " + mPrimitiveCount);
		}

		System.arraycopy(aIndices, 0, mIndices, aPrimitiveIndex * mVerticesPerPrimitive, mVerticesPerPrimitive);
	}


	public void setIndices(int aPrimitiveIndex, int ... aIndices)
	{
		if ((aIndices.length % mVerticesPerPrimitive) != 0)
		{
			throw new IllegalArgumentException("aIndices has illegal length: " + aIndices.length + ", must be a multiple of: " + mVerticesPerPrimitive);
		}
		if (aPrimitiveIndex >= mPrimitiveCount)
		{
			throw new IllegalArgumentException("Illegal Primitive index: " + aPrimitiveIndex + ", Primitive count: " + mPrimitiveCount);
		}
		if (aIndices.length < mVerticesPerPrimitive)
		{
			throw new IllegalArgumentException("Illegal indices count: must be: "+mVerticesPerPrimitive);
		}

		for (int i = 0; i < mVerticesPerPrimitive; i++)
		{
			mIndices[aPrimitiveIndex * mVerticesPerPrimitive + i] = (short)aIndices[i];
		}
	}


	public void setTextures(Texture2D ... aTextures)
	{
		if (aTextures != null && aTextures.length == 0)
		{
			aTextures = null;
		}
		mTextures = aTextures;
	}


	public Texture2D [] getTextures()
	{
		return mTextures;
	}


	@Override
	public void render(RenderState aRenderState, Material aMaterial)
	{
		//aRenderState.getView().getTransformStack().transform(aGeometryBuffer);

		int textureCount = mTextures == null ? 0 : mTextures.length;
		int fieldCount = mVertexFormat.mFieldCount;

		for (int primIndex = 0; primIndex < mPrimitiveCount; primIndex++)
		{
			GeometryBuffer buf = aRenderState.getGeometryBuffer(1, mVerticesPerPrimitive * fieldCount, textureCount);

			buf.writePrimitive(mVerticesPerPrimitive, mVertexFormat, aMaterial, textureCount);

			for (int fieldIndex = 0; fieldIndex < mVerticesPerPrimitive; fieldIndex++)
			{
				buf.writeVertexData(mVertexData, mIndices[mVerticesPerPrimitive * primIndex + fieldIndex] * fieldCount, fieldCount);
			}

			for (int texIndex = 0; texIndex < textureCount; texIndex++)
			{
				buf.writeTexture(mTextures[texIndex]);
			}
		}
	}
}