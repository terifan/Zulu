package org.terifan.zulu.scenegraph;

import java.util.ArrayList;
import org.terifan.zulu.Material;
import org.terifan.zulu.core.Geometry;
import org.terifan.zulu.core.RenderState;


public class Shape3D extends Leaf
{
	private ArrayList<Geometry> mGeometries;
	private Material mMaterial;


	public Shape3D()
	{
		mGeometries = new ArrayList<Geometry>();
	}


	public Shape3D(Geometry aGeometry, Material aMaterial)
	{
		this();

		addGeometry(aGeometry);
		setMaterial(aMaterial);
	}


	public void addGeometry(Geometry aGeometry)
	{
		mGeometries.add(aGeometry);
	}


	public void setGeometry(Geometry aGeometry, int aIndex)
	{
		mGeometries.set(aIndex, aGeometry);
	}


	public void removeGeometry(int aIndex)
	{
		mGeometries.remove(aIndex);
	}


	public void removeGeometry(Geometry aGeometry)
	{
		mGeometries.remove(aGeometry);
	}


	public void removeAllGeometries()
	{
		mGeometries.clear();
	}


	public int numGeometries()
	{
		return mGeometries.size();
	}


	public int indexOfGeometry(Geometry aGeometry)
	{
		return mGeometries.indexOf(aGeometry);
	}


	public void insertGeometry(Geometry aGeometry, int aIndex)
	{
		mGeometries.add(aIndex, aGeometry);
	}


	public ArrayList<Geometry> getAllGeometries()
	{
		return mGeometries;
	}


	public Geometry getGeometry(int aIndex)
	{
		return mGeometries.get(aIndex);
	}


	public void setMaterial(Material aMaterial)
	{
		if (aMaterial == null)
		{
			throw new IllegalArgumentException("Material is null.");
		}

		mMaterial = aMaterial;
	}


	public Material getMaterial()
	{
		return mMaterial;
	}


	public int size()
	{
		return mGeometries.size();
	}


	@Override
	public void render(RenderState aRenderState)
	{
		for (Geometry g : mGeometries)
		{
			g.render(aRenderState, mMaterial);
		}
	}
}