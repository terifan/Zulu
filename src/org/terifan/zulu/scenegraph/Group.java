package org.terifan.zulu.scenegraph;

import java.util.ArrayList;
import org.terifan.zulu.core.RenderState;


public class Group extends Node
{
	private ArrayList<Node> mChildren;


	public Group()
	{
		mChildren = new ArrayList<Node>();
	}


	public Group(Node ... aNode)
	{
		this();

		for (Node n : aNode)
		{
			addChild(n);
		}
	}


	public void addChild(Node aChild)
	{
		aChild.setParent(this);

		mChildren.add(aChild);
	}


	public void setChild(Node aChild, int aIndex)
	{
		aChild.setParent(this);

		mChildren.set(aIndex, aChild);
	}


	public void removeChild(int aIndex)
	{
		mChildren.remove(aIndex).setParent(null);
	}


	public void removeChild(Node aChild)
	{
		if (mChildren.remove(aChild))
		{
			aChild.setParent(null);
		}
	}


	public void removeAllChildren()
	{
		for (Node n : mChildren)
		{
			n.setParent(null);
		}
		mChildren.clear();
	}


	public int numChildren()
	{
		return mChildren.size();
	}


	public int indexOfChildren(Node aChild)
	{
		return mChildren.indexOf(aChild);
	}


	public void insertChild(Node aChild, int aIndex)
	{
		aChild.setParent(this);

		mChildren.add(aIndex, aChild);
	}


	public ArrayList<Node> getAllChildren()
	{
		return mChildren;
	}


	public Node getChild(int aIndex)
	{
		return mChildren.get(aIndex);
	}


	public void render(RenderState aRenderState)
	{
		for (Node n : mChildren)
		{
			n.render(aRenderState);
		}
	}
}