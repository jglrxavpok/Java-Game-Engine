package org.jge.sound;

import java.nio.ByteBuffer;

import org.jge.Disposable;
import org.jge.EngineKernel;
import org.jge.RuntimeEngineException;
import org.jge.util.Buffers;
import org.lwjgl.openal.AL10;

public class SoundResource implements Disposable
{

	private int referenceCounter;
	private int bufferPointer;
	
	public SoundResource(byte[] bufferData, int format, int freq)
	{
		this(Buffers.createFlippedByteBuffer(bufferData), format, freq);
	}
	
	public SoundResource(ByteBuffer bufferData, int format, int freq)
	{
		EngineKernel.getDisposer().add(this);
		
		bufferPointer = AL10.alGenBuffers();
		if(AL10.alGetError() != AL10.AL_NO_ERROR)
			throw new RuntimeEngineException("Error while creating sound buffer");
		AL10.alBufferData(bufferPointer, format, bufferData, freq);
	}
	
	public int getBufferPointer()
	{
		return bufferPointer;
	}
	
	public boolean decreaseCounter()
    {
    	referenceCounter--;
    	return referenceCounter <= 0;
    }
    
    public void increaseCounter()
    {
    	referenceCounter++;
    }
    
    @Override
    protected void finalize()
    {
    	dispose();
    }
    
    public void dispose()
    {
    	
    }

	public int createSourceID()
	{
		int id = AL10.alGenSources();
		AL10.alSourcei(id, AL10.AL_BUFFER, bufferPointer);
		AL10.alSourcef(id, AL10.AL_PITCH, 1.0f);
		AL10.alSourcef(id, AL10.AL_GAIN, 1.0f);
		return id;
	}
}
