package org.jge.render;

import java.nio.FloatBuffer;

import org.jge.maths.Matrix4;
import org.jge.maths.Vector2;
import org.jge.maths.Vector3;
import org.jge.util.BufferWritable;

public class Vertex implements BufferWritable
{

    public static final int SIZE = 3+2+3+3+16;
    private Vector3 pos;
    private Vector2 texCoords;
    private Vector3 normal;
    private Vector3 tangent;
    private Matrix4 skinningMatrix;

    public Vertex(Vector3 pos)
    {
        this(pos, new Vector2(0, 0));
    }
    
    public Vertex(Vector3 pos, Vector2 texCoords)
    {
        this(pos, texCoords, new Vector3(0,0,0));
    }
    
    public Vertex(Vector3 pos, Vector2 texCoords, Vector3 normal)
    {
    	this(pos, texCoords, normal, new Vector3(0,0,0));
    }
    
    public Vertex(Vector3 pos, Vector2 texCoords, Vector3 normal, Vector3 vector3)
    {
    	this(pos, texCoords, normal, vector3, new Matrix4().initIdentity());
    }
    
    public Vertex(Vector3 pos, Vector2 texCoords, Vector3 normal, Vector3 tangent, Matrix4 skinningMatrix)
    {
        this.pos = pos;
        this.texCoords = texCoords; 
        this.normal = normal.normalize();
        this.tangent = tangent.normalize();
        this.skinningMatrix = skinningMatrix;
    }
    
	public Matrix4 getSkinningMatrix()
    {
    	return skinningMatrix;
    }
    
    public Vector3 getPos()
    {
        return pos;
    }
    
    public Vector3 getTangent()
    {
        return tangent;
    }

    public void setPos(Vector3 pos)
    {
        this.pos = pos;
    }

    public void write(FloatBuffer buffer)
    {
        pos.write(buffer);
        texCoords.write(buffer);
        normal.write(buffer);
        tangent.write(buffer);
        skinningMatrix.write(buffer);
    }

    public int getSize()
    {
        return SIZE;
    }

    public Vector2 getTexCoords()
    {
        return texCoords;
    }

    public void setTexCoords(Vector2 texCoords)
    {
        this.texCoords = texCoords;
    }

    public Vector3 getNormal()
    {
        return normal;
    }
    
    public void setNormal(Vector3 normal)
    {
        this.normal = normal.normalize();
    }

	public void setSkinningMatrix(Matrix4 m)
	{
		this.skinningMatrix = m;
	}
}
