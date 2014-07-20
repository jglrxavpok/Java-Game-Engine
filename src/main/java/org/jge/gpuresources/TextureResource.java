package org.jge.gpuresources;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

import java.nio.ByteBuffer;

import org.jge.util.Buffers;
import org.jge.util.Log;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL30;

public class TextureResource
{

	private int ids[];
    private int referenceCounter;
	private int target;
	private int width;
	private int height;
	private int framebufferID;
	private int depthRenderBufferID;
    
	public TextureResource(int width, int height)
    {
		this(GL_TEXTURE_2D, width, height);
    }
	
	public TextureResource(int target, int width, int height)
    {
		this(target, 1, width, height);
    }
	
    public TextureResource(int target, int numTextures, int width, int height)
    {
    	ids = new int[numTextures];
    	this.target = target;
    	referenceCounter = 1;
    	this.width = width;
    	this.height = height;
    	this.framebufferID = 0;
    }
    
    public TextureResource(int target, int numTextures, int width, int height, ByteBuffer data, float filter, int internalFormat, int format, boolean clamp)
    {
    	this(target, numTextures, width, height, new ByteBuffer[]{data}, new float[]{filter}, null, new int[]{internalFormat}, new int[]{format}, new boolean[]{clamp});
    }
    
    public TextureResource(int target, int numTextures, int width, int height, ByteBuffer data, float filter, int attachment, int internalFormat, int format, boolean clamp)
    {
    	this(target, numTextures, width, height, new ByteBuffer[]{data}, new float[]{filter}, new int[]{attachment}, new int[]{internalFormat}, new int[]{format}, new boolean[]{clamp});
    }
    
    public TextureResource(int target, int numTextures, int width, int height, ByteBuffer[] data, float[] filters, int[] attachments, int[] internalFormats, int[] formats, boolean[] clamp)
    {
    	this(target, numTextures, width, height);
    	initTextures(internalFormats, formats,data, filters, clamp);
    	if(attachments != null)
    		initRenderTargets(attachments);
    }
    
    public void initTexture(int internalFormat, int format, ByteBuffer data, float filter, boolean clamp)
    {
    	this.initTextures(new int[]{internalFormat},new int[]{format},new ByteBuffer[]{data}, new float[]{filter}, new boolean[]{clamp});
    }
    
    public void initTextures(int[] internalFormats, int[] formats, ByteBuffer[] data, float[] filters, boolean[] clamp)
    {
    	if((data != null && data.length != ids.length) || filters.length != ids.length)
    	{
    		Log.error("Wrong lengths");
    		return;
    	}
    	
    	for(int i = 0;i<ids.length;i++)
    	{
    		ids[i] = glGenTextures();
    		glBindTexture(target, ids[i]);
    		glTexParameterf(target, GL_TEXTURE_MIN_FILTER, filters[i]);
    		glTexParameterf(target, GL_TEXTURE_MAG_FILTER, filters[i]);
    		glTexEnvf(GL_TEXTURE_ENV, GL_TEXTURE_ENV_MODE, GL_MODULATE);
    		glTexParameteri(target, GL12.GL_TEXTURE_BASE_LEVEL,0);
    		glTexParameteri(target, GL12.GL_TEXTURE_MAX_LEVEL,0);
    		if(clamp[i])
    		{
    			glTexParameterf(target, GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
        		glTexParameterf(target, GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
    		}
    		
    		GL30.glClampColor(target, GL_FALSE);
    		glTexImage2D(target,
                    0,
                    internalFormats[i],
                    getWidth(),
                    getHeight(),
                    0, formats[i], GL_UNSIGNED_BYTE,
                    data[i]);
    	}
    }
    
    public void initRenderTargets(int[] attachments)
    {
    	if(attachments == null)
    	{
    		return;
    	}
    	if(attachments.length != ids.length)
    	{
    		Log.error("Wrong lengths");
    		return;
    	}
    	
    	int[] drawBuffers = new int[attachments.length];
    	boolean hasDepth = false;
    	for(int i = 0;i<attachments.length;i++)
    	{
    		if(attachments[i] == GL_NONE)
    		{
    			continue;
    		}
    		else if(attachments[i] == GL_DEPTH_ATTACHMENT || attachments[i] == GL_DEPTH_STENCIL_ATTACHMENT || attachments[i] == GL_STENCIL_ATTACHMENT)
    		{
    			drawBuffers[i] = GL_NONE;
    		}
    		else
    		{
    			drawBuffers[i] = attachments[i];
    		}
    		
    		if(attachments[i] == GL_DEPTH_ATTACHMENT)
			{
    			hasDepth = true;
			}
    		if(framebufferID == 0)
    			framebufferID = glGenFramebuffers();
        	glBindFramebuffer(GL_FRAMEBUFFER, framebufferID);
        	glFramebufferTexture2D(GL_FRAMEBUFFER, attachments[i], target, ids[i], 0);
    	}
    	
    	if(!hasDepth)
    	{
    		depthRenderBufferID = glGenRenderbuffers();
    		glBindRenderbuffer(GL_RENDERBUFFER, depthRenderBufferID);
    		glRenderbufferStorage(GL_RENDERBUFFER, GL_DEPTH_COMPONENT, width, height);
    		glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_RENDERBUFFER, depthRenderBufferID);
    	}
    	
    	if(framebufferID == 0)
    	{
    		return;
    	}
    	
    	glDrawBuffers(Buffers.createFlippedIntBuffer(drawBuffers));
    	
    	int status = glCheckFramebufferStatus(GL_FRAMEBUFFER);
    	if(status != GL_FRAMEBUFFER_COMPLETE)
    	{
    		Log.error("Framebuffer creation failed ");
    		switch(status)
    		{
    			case GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT:
    				Log.error("Incomplete attachment");
    				break;
    				
    			case GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT:
    				Log.error("No attachments");
    				break;
    				
    			case GL_FRAMEBUFFER_INCOMPLETE_DRAW_BUFFER:
    				Log.error("Incomplete draw buffer");
    				break;
    				
    			case GL_FRAMEBUFFER_INCOMPLETE_READ_BUFFER:
    				Log.error("Incomplete read buffer");
    				break;
    				
    			case GL_FRAMEBUFFER_UNSUPPORTED:
    				Log.error("Unsupported format");
    				break;
    				
    			case GL_FRAMEBUFFER_INCOMPLETE_MULTISAMPLE:
    				Log.error("Invalid multisamples");
    				break;
    				
				default:
					Log.error("Reason unknown");
					break;
    		}
    	}
    }
    
    public int getTarget()
    {
    	return target;
    }
    
    public int getWidth()
    {
    	return width;
    }
    
    public int getHeight()
    {
    	return height;
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
    	glDeleteTextures(Buffers.createFlippedIntBuffer(ids));
    	if(framebufferID != 0)
    		glDeleteFramebuffers(framebufferID);
    }

	public int[] getIDs()
	{
		return ids;
	}
	
	public int getID()
	{
		return ids[0];
	}
	
	public TextureResource bindAsRenderTarget()
	{
		glBindTexture(target, 0);
		glBindFramebuffer(GL_FRAMEBUFFER, framebufferID);
		glViewport(0, 0, width, height);
		return this;
	}
}
