package org.jge.render.mesh;

import java.util.ArrayList;

import org.jge.maths.Matrix4;
import org.jge.render.Vertex;

public class SkeletonBone
{

	private SkeletonBone parent;
	private ArrayList<Vertex> vertices;
	private Matrix4 parentMatrix;
	private Matrix4 matrix;

	public SkeletonBone(SkeletonBone parent, ArrayList<Vertex> vertices)
	{
		this.parent = parent;
		this.vertices = vertices;
		parentMatrix = new Matrix4().initIdentity();
		this.matrix = new Matrix4().initIdentity();
		
		for(Vertex v : vertices)
		{
			v.setSkinningMatrix(getFullTransformationMatrix());
		}
	}
	
	public SkeletonBone setMatrix(Matrix4 m)
	{
		matrix.set(m);
		return this;
	}
	
	public SkeletonBone getParent()
	{
		return parent;
	}
	
	public ArrayList<Vertex> getAffectedVertices()
	{
		return vertices;
	}
	
	public Matrix4 getTransformationMatrix()
	{
		return matrix;
	}
	
	public Matrix4 getFullTransformationMatrix()
	{
		if(parent != null)
			parentMatrix = parent.getFullTransformationMatrix();
		return parentMatrix.mul(getTransformationMatrix());
	}
}
