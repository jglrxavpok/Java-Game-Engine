package org.jge.cl;

import java.nio.FloatBuffer;
import java.util.HashMap;

import org.jge.AbstractResource;
import org.jge.Disposable;
import org.jge.EngineException;
import org.jge.EngineKernel;

public class GPUProgram implements Disposable
{
	
	private static HashMap<AbstractResource, GPUProgramResource> loadedResources = new HashMap<AbstractResource, GPUProgramResource>();
    private GPUProgramResource data;
	private AbstractResource res;

	public GPUProgram(AbstractResource res) throws EngineException
	{
		this.res = res;
		
		GPUProgramResource existingResource = loadedResources.get(res);
    	
    	if(existingResource != null)
    	{
    		EngineKernel.getDisposer().add(this);
    		data = existingResource;
    		data.increaseCounter();
    	}
    	else
    	{
    		data = new GPUProgramResource(res);
            loadedResources.put(res, data);
    	}

	}

	public GPUProgramResource getData()
	{
		return data;
	}
	
	public void dispose()
    {
    	if(data.decreaseCounter())
    	{
    		data.dispose();
    		if(res != null)
    			loadedResources.remove(res);
    	}
    }

	public GPUProgramObject createMemory(float[] data)
	{
		return this.data.createMemory(data);
	}

	public GPUProgramObject createMemory(FloatBuffer buffer)
	{
		return data.createMemory(buffer);
	}

	public GPUProgramObject createMemory(int size)
	{
		return data.createMemory(size);
	}
	
	public void read(GPUProgramObject o, FloatBuffer writeTo, boolean blocking)
	{
		data.read(o, writeTo, blocking);
	}
}
