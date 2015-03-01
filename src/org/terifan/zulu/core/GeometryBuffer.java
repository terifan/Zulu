package org.terifan.zulu.core;

import org.terifan.zulu.Material;
import org.terifan.zulu.Texture2D;


/**
 * The GeometryBuffer stores geometric data written by a producer, typically a 
 * Geometry object or the GeometryClipper and then read by a consumer, the 
 * GeometryClipper or GeometryRenderer classes.
 *
 * writing:
 *   write primitive (vertex format, material etc.)
 *   write vertex data (coordinates, colors etc.)
 *   write textures
 *
 * reading:
 *   vertex_offset = 0
 *   texture_offset = 0
 *   for each primitive
 *      read vertex_format, material, texture_count and vertex_count
 *      for each vertex
 *         use vertex data
 *         increment vertex_offset
 *      increment texture_offset
 */
public final class GeometryBuffer
{
	private final static int PRIMITIVE_CAPACITY = 100;
	private final static int VERTEX_CAPACITY = 20000;
	private final static int TEXTURE_CAPACITY = 300;

	// one item per primitive
	private VertexFormat [] mVertexFormat;
	private Material [] mMaterial;
	private byte [] mVertexCount;
	private byte [] mTextureCount;

	// many items per primitive
	private float [] mVertexData; // vertex data, one primitive has mVertexFormat[x].mFieldCount * mVertexCount[x] floats
	private Texture2D [] mTextures;

	private int mPrimitiveIndex; // vertex format offset when reading
	private int mVertexDataIndex;
	private int mTextureIndex;


	public GeometryBuffer()
	{
		mVertexFormat = new VertexFormat[PRIMITIVE_CAPACITY];
		mMaterial = new Material[PRIMITIVE_CAPACITY];
		mTextureCount = new byte[PRIMITIVE_CAPACITY];
		mVertexCount = new byte[PRIMITIVE_CAPACITY];

		mTextures = new Texture2D[TEXTURE_CAPACITY];
		mVertexData = new float[VERTEX_CAPACITY];
	}


	public void writePrimitive(int aVertexCount, VertexFormat aVertexFormat, Material aMaterial, int aTextureCount)
	{
		mVertexCount[mPrimitiveIndex] = (byte)aVertexCount;
		mVertexFormat[mPrimitiveIndex] = aVertexFormat;
		mMaterial[mPrimitiveIndex] = aMaterial;
		mTextureCount[mPrimitiveIndex] = (byte)aTextureCount;
		mPrimitiveIndex++;
	}


	public void writeVertexData(float aGeometryData)
	{
		mVertexData[mVertexDataIndex++] = aGeometryData;
	}


	public void writeVertexData(float [] aGeometryData)
	{
		System.arraycopy(aGeometryData, 0, mVertexData, mVertexDataIndex, aGeometryData.length);
		mVertexDataIndex += aGeometryData.length;
	}


	public void writeVertexData(float [] aGeometryData, int aOffset, int aLength)
	{
		System.arraycopy(aGeometryData, aOffset, mVertexData, mVertexDataIndex, aLength);
		mVertexDataIndex += aLength;
	}


	public void writeTexture(Texture2D aTexture)
	{
		mTextures[mTextureIndex++] = aTexture;
	}


	public void writeTexture(Texture2D [] aTextures, int aOffset, int aLength)
	{
		System.arraycopy(aTextures, aOffset, mTextures, mTextureIndex, aLength);
		mTextureIndex += aLength;
	}


	public boolean hasCapacity(int aMinimumPrimitives, int aMinimumVertexData, int aMinimumTextures)
	{
		return (PRIMITIVE_CAPACITY-mPrimitiveIndex) >= aMinimumPrimitives 
		    && (VERTEX_CAPACITY-mVertexDataIndex) >= aMinimumVertexData 
		    && (TEXTURE_CAPACITY-mTextureIndex) >= aMinimumTextures;
	}


	public Material getMaterial(int aPrimitiveIndex)
	{
		return mMaterial[aPrimitiveIndex];
	}


	public VertexFormat getVertexFormat(int aPrimitiveIndex)
	{
		return mVertexFormat[aPrimitiveIndex];
	}


	public int getTextureCount(int aPrimitiveIndex)
	{
		return mTextureCount[aPrimitiveIndex] & 255;
	}


	public int getVertexCount(int aPrimitiveIndex)
	{
		return mVertexCount[aPrimitiveIndex] & 255;
	}


	/**
	 * Returns the vertex data array. The GeometryBuffer reader must manage the 
	 * index, ie. increment it for each primitive read!
	 */
	public float [] getVertexData()
	{
		return mVertexData;
	}


	/**
	 * Returns the texture array. The GeometryBuffer reader must manage the 
	 * index, ie. increment it for each primitive read!
	 */
	public Texture2D [] getTextures()
	{
		return mTextures;
	}


	public int getPrimitiveCount()
	{
		return mPrimitiveIndex;
	}
}