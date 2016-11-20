package org.terifan.zulu.core;

import java.util.Stack;
import org.terifan.zulu.Transform3D;


public class TransformStack
{
	private Stack<Transform3D> mStack = new Stack<>();
//	private Quaternion mTempQuaternion = new Quaternion();
//	private Vector mTempInversePosition = new Vector();
//	private Vector mTempPosition = new Vector();
//	private Vector mTempRotation = new Vector();


	public TransformStack()
	{
//		mTempQuaternion.setInverse(true);
	}


	public void clear()
	{
		mStack.clear();
	}


	public void push(Transform3D aTransform)
	{
		mStack.push(aTransform);
	}


	public Transform3D pop()
	{
		return mStack.pop();
	}


	public Transform3D get(int aIndex)
	{
		return mStack.get(aIndex);
	}


	public int size()
	{
		return mStack.size();
	}


	public void transform(GeometryBuffer aGeometryBuffer)
	{
		for (int i = mStack.size(); --i >= 0;)
		{
			mStack.get(i).transform(aGeometryBuffer);
		}
	}


	public String toString()
	{
		StringBuilder s = new StringBuilder();
		for (int i = mStack.size(); --i >= 0;)
		{
			s.append(mStack.get(i));
		}
		return s.toString();
	}


/*	public void transform(Vector [] aCoordinates, Vector [] aTransformedCoordinates, boolean [] aVertexVisibility)
	{
		int size = size();
		for (int j = aCoordinates.length; --j >= 0;)
		{
			if (aVertexVisibility[j])
			{
				Vector v = aTransformedCoordinates[j];
				v.set(aCoordinates[j]);
				for (int i = size; --i >= 0;)
				{
					mStack.get(i).transform(v);
				}
			}
		}
	}*/


/*	public Vector getInversePosition()
	{
		mTempInversePosition.set(0,0,0);

		for (int i = 0, size = size(); i < size; i++)
		{
			Transform3D transform = mStack.get(i);
			transform.getRotation(mTempRotation);
			transform.getPosition(mTempPosition);
			mTempQuaternion.setRotation(-mTempRotation.x, -mTempRotation.y, -mTempRotation.z);
			if (transform instanceof ViewerTransform)
			{
				mTempQuaternion.transform(mTempInversePosition);
				mTempInversePosition.subtract(mTempPosition);
			}
			else
			{
				mTempInversePosition.subtract(mTempPosition);
				mTempQuaternion.transform(mTempInversePosition);
			}
		}

		return mTempInversePosition;
	}*/
}