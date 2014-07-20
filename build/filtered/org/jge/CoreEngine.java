package org.jge;

import static org.lwjgl.opengl.GL11.*;

import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.jge.Profiler.ProfileTimer;
import org.jge.cl.GPUProgramResource;
import org.jge.game.Game;
import org.jge.game.Input;
import org.jge.phys.PhysicsEngine;
import org.jge.render.RenderEngine;
import org.jge.sound.SoundEngine;
import org.jge.util.LWJGLHandler;
import org.jge.util.Log;
import org.jge.util.OpenGLUtils;
import org.lwjgl.openal.AL;
import org.lwjgl.opencl.CL;
import org.lwjgl.opengl.GLContext;

public final class CoreEngine
{

	private ProfileTimer sleepTimer = new ProfileTimer();
	private ProfileTimer windowUpdateTimer = new ProfileTimer();
    private boolean              running;
    private Window               window;
    private RenderEngine         renderEngine;
    private SoundEngine         soundEngine;
    private ClasspathSimpleResourceLoader resLoader;
    private Game                 game;

    private int                  frame;
    private int                  fps;
    private double               expectedFrameRate           = 60.0;
    private double               timeBetweenUpdates          = 1000000000 / expectedFrameRate;
    private final int            maxUpdatesBeforeRender      = 2;
    private double               lastUpdateTime              = System.nanoTime();
    private double               lastRenderTime              = System.nanoTime();

    // If we are able to get as high as this FPS, don't render again.
    private final double                 TARGET_FPS                  = 60;
    private final double                 TARGET_TIME_BETWEEN_RENDERS = 1000000000 / TARGET_FPS;

    private int                          lastSecondTime              = (int)(lastUpdateTime / 1000000000);
    private boolean                      paused                      = false;
	private PhysicsEngine physEngine;
	private static CoreEngine current;

    public CoreEngine(Game game)
    {
    	current = this;
        EngineKernel.setGame(game.setEngine(this));
        this.game = game;
    }

    public Game getGame()
    {
        return game;
    }

    public boolean isRunning()
    {
        return running;
    }

    public void start(Window window)
    {
        running = true;
        this.window = window;
        expectedFrameRate = window.getFPSCap();
        timeBetweenUpdates = 1000000000 / expectedFrameRate;
        try
        {
            LWJGLHandler.load(EngineKernel.getEngineFolder().getAbsolutePath()
                    + "/natives");
            window.setTitle(game.getGameName());
            window.init();
            if(!GLContext.getCapabilities().OpenGL33)
            {
            	Log.message("Incompatible OpenGL version: " + OpenGLUtils.getOpenGLVersion());
            	Log.message("Must be at least: 3.3.0");
            	cleanup();
            }
            soundEngine = new SoundEngine();
            CL.create();
            Log.message("[------OpenGL infos------]");
            Log.message("  Version: " + OpenGLUtils.getOpenGLVersion());
            Log.message("  Vendor: " + OpenGLUtils.getOpenGLVendor());
            Log.message("[------Engine infos------]");
            Log.message("  Version: " + EngineKernel.getEngineVersion());
            Log.message("------------------------");
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        resLoader = new ClasspathSimpleResourceLoader();
        renderEngine = new RenderEngine();
        physEngine = new PhysicsEngine();
        try
		{
			renderEngine.loadTexture(resLoader.getResource(new ResourceLocation("textures", "loadingScreen.png"))).bind();
		}
		catch(EngineException e)
		{
			e.printStackTrace();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
        game.drawLoadingScreen("Loading...");
        window.refresh();
        renderEngine.init();
        soundEngine.init();
        physEngine.init();
        game.init();
        while(running && !window.shouldBeClosed())
        {
            tick();
        }
        cleanup();
    }

    private void cleanup()
    {
    	running = false;
    	AL.destroy();
    	GPUProgramResource.destroyAll();
    	CL.destroy();
        EngineKernel.disposeAll();
        window.disposeWindow();
        System.exit(0);
    }

    private final void tick()
    {
        double now = System.nanoTime();
        int updateCount = 0;

        if(!paused)
        {
        	Input.pollEvents();
            double delta = timeBetweenUpdates/1000000000;
            while(now - lastUpdateTime > timeBetweenUpdates
                    && updateCount < maxUpdatesBeforeRender)
            {
                
                update(delta);
                lastUpdateTime += timeBetweenUpdates;
                updateCount++;
            }

            if(now - lastUpdateTime > timeBetweenUpdates)
            {
                lastUpdateTime = now - timeBetweenUpdates;
            }

            render(delta);
            windowUpdateTimer.startInvocation();
            window.refresh();
            windowUpdateTimer.endInvocation();
            lastRenderTime = now;
            // Update the frames we got.
            int thisSecond = (int)(lastUpdateTime / 1000000000);
            frame++ ;
            if(thisSecond > lastSecondTime)
            {
                fps = frame;
                double totalTime = (1000.0 * (double)(thisSecond-lastSecondTime))/(double)frame;
                double totalRecordedTime = 0;
                if(Profiler.display)
                {
                    totalRecordedTime += physEngine.displayProfileInfo((double)frame);
                    totalRecordedTime += game.displayUpdateTime((double)frame);
                    totalRecordedTime += renderEngine.displayRenderTime((double)frame);
                    totalRecordedTime += renderEngine.displayWindowSyncTime((double)frame);
                    totalRecordedTime += windowUpdateTimer.displayAndReset("Window update time", (double)frame);
                    totalRecordedTime += sleepTimer.displayAndReset("Sleep time", (double)frame);
                    Log.message("Non-profiled time: "+(totalTime - totalRecordedTime) + " ms");
                    Log.message("Total time: "+ totalTime+ " ms");
                    Log.message(fps + " fps");
                }
                frame = 0;
                lastSecondTime = thisSecond;
            }

            while(now - lastRenderTime < TARGET_TIME_BETWEEN_RENDERS
                    && now - lastUpdateTime < timeBetweenUpdates)
            {
            	sleepTimer.startInvocation();
                Thread.yield();

                try
                {
                    Thread.sleep(1);
                }
                catch(Exception e)
                {
                }

                now = System.nanoTime();
                sleepTimer.endInvocation();
            }
        }

        if(!window.isActive())
            try
            {
                window.setFullscreen(false);
            }
            catch(EngineException e)
            {
                e.printStackTrace();
            }
    }

    private boolean screenshotKey = false;
    
    private void update(double delta)
    {
    	if(Input.isKeyDown(Input.KEY_F2) && !screenshotKey)
    	{
    		screenshotKey = true;
    		try
			{
				ImageIO.write(LWJGLHandler.takeScreenshot(), "png", new File(EngineKernel.getEngineFolder(), System.currentTimeMillis()+".png"));
			}
			catch(IOException e)
			{
				e.printStackTrace();
			}
    	}
    	else if(!Input.isKeyDown(Input.KEY_F2) && screenshotKey)
    	{
    		screenshotKey = false;
    	}
    	physEngine.update(delta);
    	soundEngine.update(delta);
        game.update(delta);
    }

    public RenderEngine getRenderEngine()
    {
        return renderEngine;
    }

    private void render(double delta)
    {
        renderEngine.clearBuffers();
        glColor4f(1, 1, 1, 1);
        glViewport(0, 0, window.getRealWidth(), window.getRealHeight());
        game.render(renderEngine, delta);
    }

    public ResourceLoader getResourceLoader()
    {
        return resLoader;
    }

    public Window getWindow()
    {
        return window;
    }

	public static CoreEngine getCurrent()
	{
		return current;
	}

	public SoundEngine getSoundEngine()
	{
		return soundEngine;
	}
	
	public PhysicsEngine getPhysicsEngine()
	{
		return physEngine;
	}

}
