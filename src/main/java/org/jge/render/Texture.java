package org.jge.render;

import static org.lwjgl.opengl.GL11.*;

import java.nio.ByteBuffer;
import java.util.HashMap;

import org.jge.AbstractResource;
import org.jge.Disposable;
import org.jge.EngineException;
import org.jge.EngineKernel;
import org.jge.gpuresources.TextureResource;
import org.jge.util.OpenGLUtils;
import org.lwjgl.opengl.GL13;

public class Texture implements Disposable
{

    private static HashMap<AbstractResource, TextureResource> loadedResources = new HashMap<AbstractResource, TextureResource>();
    private TextureResource data;

	private AbstractResource res;

	public Texture(int width, int height)
	{
		data = new TextureResource(width, height);
	}
	
	public Texture(int width, int height, ByteBuffer data, int target, float filter, int attachment, int internalFormat, int format, boolean clamp)
	{
		this.data = new TextureResource(target, 1,width, height, data, filter, attachment, internalFormat, format, clamp);
	}
	
	public Texture(AbstractResource res) throws EngineException
    {
		this(res, GL_LINEAR);
    }
	
    public Texture(AbstractResource res, float filter) throws EngineException
    {
    	this.res = res;
    	TextureResource existingResource = loadedResources.get(res);
    	
    	if(existingResource != null)
    	{
    		EngineKernel.getDisposer().add(this);
    		data = existingResource;
    		data.increaseCounter();
    	}
    	else
    	{
    		data = OpenGLUtils.loadTexture(res, filter);
            loadedResources.put(res, data);
    	}
    }
    
    public int getTarget()
    {
        return data.getTarget();
    }
    
    public void bind()
    {
    	bind(0);
    }
    
    public void bind(int samplerSlot)
    {
    	assert (samplerSlot >= 0 && samplerSlot <= 31) : "Sampler slot must be >= 0 and <= 31";
    	GL13.glActiveTexture(GL13.GL_TEXTURE0 + samplerSlot);
        glBindTexture(data.getTarget(), data.getID());
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

	public int getID()
	{
		return data.getID();
	}
	
	public Texture bindAsRenderTarget()
	{
		data.bindAsRenderTarget();
		return this;
	}

	public TextureResource getResource()
	{
		return data;
	}

	public int getWidth()
	{
		return data.getWidth();
	}
	
	public int getHeight()
	{
		return data.getHeight();
	}
}
