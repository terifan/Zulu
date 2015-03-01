package org.terifan.zulu.scenegraph;

import org.terifan.zulu.core.RenderState;


public abstract class Node
{
	private Node mParent;


	public Node()
	{
	}


	public void setParent(Node aParent)
	{
		if (mParent != null)
		{
			throw new IllegalStateException("This node already has a parent.");
		}

		mParent = aParent;
	}


	public Node getParent()
	{
		return mParent;
	}


	public abstract void render(RenderState aRenderState);
}