package org.jge.render.mesh;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;

import java.util.HashMap;

import org.jge.AbstractResource;
import org.jge.Disposable;
import org.jge.EngineException;
import org.jge.EngineKernel;
import org.jge.gpuresources.MeshResource;
import org.jge.maths.Vector3;
import org.jge.render.Vertex;
import org.jge.util.Buffers;
import org.lwjgl.opengl.GL15;

public class Mesh implements Disposable
{

	private static HashMap<AbstractResource, MeshResource> loadedResources = new HashMap<AbstractResource, MeshResource>();
    private MeshResource data;
    private AbstractResource res;

    public Mesh(AbstractResource res) throws EngineException
    {
    	this.res = res;
    	MeshResource existingResource = loadedResources.get(res);
    	
    	if(existingResource != null)
    	{
    		EngineKernel.getDisposer().add(this);
    		data = existingResource;
    		data.increaseCounter();
    	}
    	else
    	{
        	initMeshData();
            loadModel(res);
            loadedResources.put(res, data);
    	}
    }
    
    private void loadModel(AbstractResource res) throws EngineException
	{
    	ModelFormats format = ModelFormats.get(res.getResourceLocation().getExtension().toUpperCase());
    	if(format != null)
    	{
    		format.getLoader().loadModel(res).toMesh(this);
    	}
    	else
    	{
    		throw new EngineException("Model format not supported: "+res.getResourceLocation().getExtension());
    	}
	}

	public Mesh(Vertex[] vertices, int[] indices)
    {
    	this(vertices, indices, false);
    }
    
    public Mesh(Vertex[] vertices, int[] indices, boolean calcNormals)
    {
    	initMeshData();
    	setVertices(vertices, indices,calcNormals);
    }
    
    private void initMeshData()
	{
    	EngineKernel.getDisposer().add(this);
        data = new MeshResource();
	}

	public void setVertices(Vertex[] vertices, int[] indices)
    {
        setVertices(vertices, indices, false);
    }
    
    public void setVertices(Vertex[] vertices, int[] indices, boolean recalcNormals)
    {
        if(recalcNormals)
        {
            computeNormals(vertices, indices);
        }
        data.setVertices(vertices);
        glBindBuffer(GL_ARRAY_BUFFER, data.getVbo());
        GL15.glBufferData(GL_ARRAY_BUFFER, Buffers.createFlippedBuffer(vertices), GL_STATIC_DRAW);

        data.setSize(indices.length);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, data.getIbo());
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, Buffers.createFlippedIntBuffer(indices), GL_STATIC_DRAW);
    }

    private void computeNormals(Vertex[] vertices, int[] indices)
    {
        for(int i = 0;i<indices.length;i+=3)
        {
            int i0 = indices[i];
            int i1 = indices[i+1];
            int i2 = indices[i+2];
            
            Vector3 l0 = vertices[i1].getPos().sub(vertices[i0].getPos());
            Vector3 l1 = vertices[i2].getPos().sub(vertices[i0].getPos());
            Vector3 normal = l0.cross(l1).normalize();
            
            vertices[i0].setNormal(vertices[i0].getNormal().add(normal));
            vertices[i1].setNormal(vertices[i1].getNormal().add(normal));
            vertices[i2].setNormal(vertices[i2].getNormal().add(normal));
        }
        
        for(Vertex v : vertices)
            v.setNormal(v.getNormal().normalize());
    }

    public void draw()
    {
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);
        glEnableVertexAttribArray(2);
        glEnableVertexAttribArray(3);

        glBindBuffer(GL_ARRAY_BUFFER, data.getVbo());
        glVertexAttribPointer(0, 3, GL_FLOAT, false, Vertex.SIZE * 4, 0);
        glVertexAttribPointer(1, 2, GL_FLOAT, false, Vertex.SIZE * 4, 12);
        glVertexAttribPointer(2, 3, GL_FLOAT, false, Vertex.SIZE * 4, 20);
        glVertexAttribPointer(3, 3, GL_FLOAT, false, Vertex.SIZE * 4, 32);
        glVertexAttribPointer(3, 3, GL_FLOAT, false, Vertex.SIZE * 4, 44);

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, data.getIbo());
        glDrawElements(GL_TRIANGLES, data.getSize(), GL_UNSIGNED_INT, 0);

        glDisableVertexAttribArray(3);
        glDisableVertexAttribArray(2);
        glDisableVertexAttribArray(1);
        glDisableVertexAttribArray(0);
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

	public Vertex[] getVertices()
	{
		return data.getVertices();
	}
}
