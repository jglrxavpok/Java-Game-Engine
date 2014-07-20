package org.jge.game;

import static org.lwjgl.opengl.GL11.*;

import java.io.File;

import org.jge.CoreEngine;
import org.jge.Profiler.ProfileTimer;
import org.jge.ResourceLoader;
import org.jge.Window;
import org.jge.components.SceneObject;
import org.jge.render.RenderEngine;

public abstract class Game
{

	private File		folder;
	private CoreEngine  engine;
	private SceneObject root;
	private ProfileTimer updateTimer = new ProfileTimer();

	public CoreEngine getEngine()
	{
		return engine;
	}

	public Game setEngine(CoreEngine core)
	{
		this.engine = core;
		return this;
	}

	public void updateGame(double delta)
	{
	}

	public final void update(double delta)
	{
		updateTimer.startInvocation();
		getSceneRoot().updateAll(delta);
		updateGame(delta);
		updateTimer.endInvocation();
	}
	
	public double displayWindowSyncTime()
	{
		return displayUpdateTime(0);
	}
	
	public double displayUpdateTime(double divisor)
	{
		return updateTimer.displayAndReset("Game update", divisor);
	}

	public ResourceLoader getResourceLoader()
	{
		return engine.getResourceLoader();
	}

	public Window getWindow()
	{
		return engine.getWindow();
	}

	public File getGameFolder()
	{
		if(folder == null)
		{
			String appdata = System.getenv("APPDATA");
			if(appdata != null)
				folder = new File(appdata, getGameName().toLowerCase());
			else
				folder = new File(System.getProperty("user.home"),
						getGameName().toLowerCase());

			if(!folder.exists())
				folder.mkdirs();
		}
		return folder;
	}

	public abstract String getGameName();

	public void drawLoadingScreen(String message)
	{
		engine.getRenderEngine().clearBuffers();
		glBegin(GL_QUADS);
		glTexCoord2d(0, 1);
		glVertex2f(-1, -1);

		glTexCoord2d(1, 1);
		glVertex2f(1, -1);

		glTexCoord2d(1, 0);
		glVertex2f(1, 1);

		glTexCoord2d(0, 0);
		glVertex2f(-1, 1);
		glEnd();
	}

	public void init()
	{

	}

	public RenderEngine getRenderEngine()
	{
		return engine.getRenderEngine();
	}

	public Game addToScene(SceneObject object)
	{
		getSceneRoot().addChild(object);
		object.onAddToSceneAll();
		return this;
	}

	protected SceneObject getSceneRoot()
	{
		if(root == null)
			root = new SceneObject();
		return root;
	}

	public final void render(RenderEngine renderEngine, double delta)
	{
		renderEngine.render(root, delta);
	}
}
