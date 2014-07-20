package org.jge.gpuresources;

import org.jge.Disposable;
import org.jge.render.Vertex;
import org.lwjgl.opengl.GL15;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL30.*;

public class MeshResource implements Disposable
{
	private int vbo;
    private int ibo;
    private int size;
    private int referenceCounter;
	private Vertex[] vertices;
	private int[] indices;	
	private int vao;
    
    public MeshResource()
    {
    	vbo = glGenBuffers();
    	ibo = glGenBuffers();
    	vao = glGenVertexArrays();
    	size = 0;
    	referenceCounter = 1;
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
    	glDeleteVertexArrays(vao);
    	GL15.glDeleteBuffers(ibo);
    	GL15.glDeleteBuffers(vbo);
    }

    public int getVao()
	{
		return vao;
	}
    
	public int getVbo()
	{
		return vbo;
	}

	public int getIbo()
	{
		return ibo;
	}

	public int getSize()
	{
		return size;
	}

	public void setSize(int size)
	{
		this.size = size;
	}

	public void setVertices(Vertex[] vertices)
	{
		this.vertices = vertices;
	}

	public Vertex[] getVertices()
	{
		return vertices;
	}
	
	public void setIndices(int[] indices)
	{
		this.indices = indices;
	}

	public int[] getIndices()
	{
		return indices;
	}
}
