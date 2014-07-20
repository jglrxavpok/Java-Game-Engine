package org.jge.sound;

import org.jge.AbstractResource;
import org.jge.CoreEngine;
import org.jge.EngineException;
import org.jge.RuntimeEngineException;
import org.jge.components.Camera;
import org.jge.maths.Vector3;
import org.jge.util.Buffers;
import org.lwjgl.LWJGLException;
import org.lwjgl.openal.AL;
import org.lwjgl.openal.AL10;

public class SoundEngine
{

	private int maxSourcesPerSound;

	public SoundEngine()
	{
		maxSourcesPerSound = 16;
	}

	public void init()
	{
		try
		{
			AL.create();
		}
		catch(LWJGLException e)
		{
			e.printStackTrace();
		}
	}

	public void update(double delta)
	{
		if(Camera.getCurrent() != null)
    		if(Camera.getCurrent().getParent().getTransform().hasChanged())
    		{
        		AL10.alListener(AL10.AL_POSITION, Buffers.createFlippedBuffer(Camera.getCurrent().getParent().getTransform().getTransformedPos()));
        		AL10.alListener(AL10.AL_VELOCITY, Buffers.createFlippedBuffer(Vector3.NULL));
        		AL10.alListener(AL10.AL_ORIENTATION, Buffers.createFlippedBuffer(Camera.getCurrent().getParent().getTransform().getTransformedRotation().getForward()));
    		}
	}
	
	public SoundResource loadFile(AbstractResource res)
	{
		String ext = res.getResourceLocation().getExtension().toUpperCase();
		SoundFormats format = SoundFormats.valueOf(ext);
		if(format == null)
			throw new RuntimeEngineException("Unsupported sound format: " + ext);
		try
		{
			return format.getLoader().loadResource(res.getData());
		}
		catch(EngineException e)
		{
			throw new RuntimeEngineException("Error while loading sound "
					+ res.getResourceLocation().getFullPath(), e);
		}
	}

	public String getOpenALVendor()
	{
		return AL10.alGetString(AL10.AL_VENDOR);
	}

	public String getOpenALVersion()
	{
		return AL10.alGetString(AL10.AL_VERSION);
	}

	public static String getALErrorString(int err)
	{
		switch(err)
		{
			case AL10.AL_NO_ERROR:
				return "AL_NO_ERROR";
			case AL10.AL_INVALID_NAME:
				return "AL_INVALID_NAME";
			case AL10.AL_INVALID_ENUM:
				return "AL_INVALID_ENUM";
			case AL10.AL_INVALID_VALUE:
				return "AL_INVALID_VALUE";
			case AL10.AL_INVALID_OPERATION:
				return "AL_INVALID_OPERATION";
			case AL10.AL_OUT_OF_MEMORY:
				return "AL_OUT_OF_MEMORY";
			default:
				return "No such error code";
		}
	}

	public int getMaxSourcesPerSound()
	{
		return maxSourcesPerSound;
	}
	
	public SoundEngine setMaxSourcesPerSound(int max)
	{
		this.maxSourcesPerSound = max;
		return this;
	}
}
