package org.terifan.zulu.core;


public class VertexFormat
{
	public byte mColorCount;
	public int mColorComponentCount; // compact representation of 8 4-bit values, each declaring the number of components a color has
	public byte mTextureCoordinateCount;
	public int mTextureCoordinateComponentCount; // compact representation of 8 4-bit values, each declaring the number of components a texture coordinate has
	public byte mNormalCount;
	public byte mAuxiliaryCount;
	public byte mTextureCount;
	public byte mFieldCount;

	public short mColorFieldCount;
	public short mTextureCoordFieldCount;
	public short mNormalFieldCount;


	public VertexFormat()
	{
	}


	public VertexFormat(byte aColorCount, int aColorComponentCount, byte aTextureCoordinateCount, int aTextureCoordinateComponentCount, byte aNormalCount, byte aAuxiliaryCount)
	{
		mColorCount = aColorCount;
		mColorComponentCount = aColorComponentCount;
		mTextureCoordinateCount = aTextureCoordinateCount;
		mTextureCoordinateComponentCount = aTextureCoordinateComponentCount;
		mNormalCount = aNormalCount;
		mAuxiliaryCount = aAuxiliaryCount;

		updateFieldCount();
	}


	public VertexFormat(int aColorCount, int aColorComponentCount, int aTextureCoordinateCount, int aTextureCoordinateComponentCount, int aNormalCount, int aAuxiliaryCount)
	{
		mColorCount = (byte)aColorCount;
		mColorComponentCount = aColorComponentCount;
		mTextureCoordinateCount = (byte)aTextureCoordinateCount;
		mTextureCoordinateComponentCount = aTextureCoordinateComponentCount;
		mNormalCount = (byte)aNormalCount;
		mAuxiliaryCount = (byte)aAuxiliaryCount;

		updateFieldCount();
	}
	
	
	public void updateFieldCount()
	{
		short cc = 0;
		for (int i = 0; i < mColorCount; i++)
		{
			cc += (mColorComponentCount >>> (4 * i)) & 15;
		}

		short tc = 0;
		for (int i = 0; i < mTextureCoordinateCount; i++)
		{
			tc += (mTextureCoordinateComponentCount >>> (4 * i)) & 15;
		}

		mColorFieldCount = cc;
		mTextureCoordFieldCount = tc;
		mNormalFieldCount = (short)(3 * mNormalCount);

		mFieldCount = (byte)(3 + mColorFieldCount + mTextureCoordFieldCount + mNormalFieldCount + mAuxiliaryCount); // including a single coordinate with 3 fields
	}


/*	public void copyFrom(VertexFormat aSource)
	{
		this.mColorCount = aSource.mColorCount;
		this.mColorComponentCount = aSource.mColorComponentCount;
		this.mTextureCoordinateCount = aSource.mTextureCoordinateCount;
		this.mTextureCoordinateComponentCount = aSource.mTextureCoordinateComponentCount;
		this.mNormalCount = aSource.mNormalCount;
		this.mAuxiliaryCount = aSource.mAuxiliaryCount;
		this.mFieldCount = aSource.mFieldCount;
	}*/
}