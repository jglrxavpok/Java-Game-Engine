package org.jge.sound;

import java.io.ByteArrayInputStream;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

import org.jge.EngineException;
import org.lwjgl.openal.AL10;

public class WaveLoader extends SoundLoader
{

	@Override
	public SoundResource loadResource(byte[] data) throws EngineException
	{
		try
		{
    		ByteArrayInputStream in = new ByteArrayInputStream(data);
    		AudioInputStream ain = AudioSystem.getAudioInputStream(in);
    		int channels = ain.getFormat().getChannels();
    		int format = 0;
    		int sampleSize = ain.getFormat().getSampleSizeInBits();
    		if(sampleSize == 8)
    		{
    			if(channels == 1)
    			{
    				format = AL10.AL_FORMAT_MONO8;
    			}
    			else if(channels == 2)
    			{
    				format = AL10.AL_FORMAT_STEREO8;
    			}
    			else
    				throw new EngineException("Unknown format: "+channels+" channels and sample size of "+sampleSize+" bits");
    		}
    		else if(sampleSize == 16)
    		{
    			if(channels == 1)
    			{
    				format = AL10.AL_FORMAT_MONO16;
    			}
    			else if(channels == 2)
    			{
    				format = AL10.AL_FORMAT_STEREO16;
    			}
    			else
    				throw new EngineException("Unknown format: "+channels+" channels and sample size of "+sampleSize+" bits");
    		}
    		else
				throw new EngineException("Unknown format: "+channels+" channels and sample size of "+sampleSize+" bits");
    		SoundResource result = new SoundResource(data, format, (int)ain.getFormat().getSampleRate());
    		ain.close();
    		in.close();
    		return result;
		}
		catch(Exception e)
		{
			throw new EngineException("Error while loading wave audio data", e);
		}
	}

}
