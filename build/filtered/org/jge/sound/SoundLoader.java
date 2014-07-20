package org.jge.sound;

import org.jge.EngineException;


public abstract class SoundLoader
{

	public abstract SoundResource loadResource(byte[] data) throws EngineException;
}
