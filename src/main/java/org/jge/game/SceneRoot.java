package org.jge.game;

import org.jge.components.SceneObject;

public class SceneRoot extends SceneObject
{

	public SceneRoot()
	{
		addChildAs("world", new WorldObject());
		addChildAs("hud", new HUDObject());		
	}
}
