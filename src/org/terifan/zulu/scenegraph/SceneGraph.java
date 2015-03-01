package org.terifan.zulu.scenegraph;

import org.terifan.zulu.View;
import java.util.ArrayList;
import org.terifan.zulu.Scene;
import org.terifan.zulu.core.RenderState;


public class SceneGraph extends Scene
{
	protected ArrayList<BranchGroup> mBranchGroups = new ArrayList<BranchGroup>();


	/**
	 * Constructs a Scene which use a specified number processors.
	 *
	 * @param aWorkerThreadPercent
	 *   a value between 0.0 and 1.0 specifying how many percent of available
	 *   processors should be used. This method don't allow more threads than
	 *   available CPU count to be created.
	 * @param aViews
	 *   an optional list of Views to be attached to this Scene.
	 */
	public SceneGraph(double aWorkerThreadPercent, View ... aViews)
	{
		super(aWorkerThreadPercent, aViews);
	}


	/**
	 * Constructs a Scene which use a specified number processors.
	 *
	 * @param aWorkerThreadCount
	 *   specifices how many processors should be used.
	 * @param aViews
	 *   an optional list of Views to be attached to this Scene.
	 */
	public SceneGraph(int aWorkerThreadCount, View ... aViews)
	{
		super(aWorkerThreadCount, aViews);
	}


	public void addChild(BranchGroup aBranchGroup)
	{
		mBranchGroups.add(aBranchGroup);
	}


	public BranchGroup getChild(int aIndex)
	{
		return mBranchGroups.get(aIndex);
	}


	protected void initializeScene()
	{
	}


	protected void renderScene(RenderState aRenderState)
	{
		for (BranchGroup group : mBranchGroups)
		{
			group.render(aRenderState);
		}
	}
}