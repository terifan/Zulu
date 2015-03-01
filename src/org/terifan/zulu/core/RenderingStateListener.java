package org.terifan.zulu.core;

import org.terifan.zulu.View;


/**
 * A RenderingStateListener recieves a call before and another one efter a frame 
 * has been rendered.
 */
public interface RenderingStateListener
{
	public void renderingStarted(View aView);

	public void renderingFinished(View aView);
}