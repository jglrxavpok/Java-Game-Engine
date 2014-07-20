package org.jge.render.mesh;

import java.util.ArrayList;

import org.jge.maths.Maths;
import org.jge.maths.Vector2;
import org.jge.maths.Vector3;
import org.jge.render.Vertex;

public class IndexedModel
{

	private ArrayList<Vector3> vertices;
	private ArrayList<Vector2> texCoords;
	private ArrayList<Vector3> normals;
	private ArrayList<Vector3> tangents;
	private ArrayList<Integer> indices;
	
	public IndexedModel()
	{
		vertices = new ArrayList<Vector3>();
		texCoords = new ArrayList<Vector2>();
		normals = new ArrayList<Vector3>();
		tangents = new ArrayList<Vector3>();
		indices = new ArrayList<Integer>();
	}

	public ArrayList<Vector3> getPositions()
	{
		return vertices;
	}

	public ArrayList<Vector2> getTexCoords()
	{
		return texCoords;
	}

	public ArrayList<Vector3> getNormals()
	{
		return normals;
	}

	public ArrayList<Integer> getIndices()
	{
		return indices;
	}
	
	public ArrayList<Vector3> getTangents()
	{
		return tangents;
	}

	public IndexedModel toMesh(Mesh mesh)
	{
		ArrayList<Vertex> verticesList = new ArrayList<Vertex>();
		for(int i = 0;i<vertices.size();i++)
		{
			Vertex vertex = new Vertex(vertices.get(i), texCoords.get(i), normals.get(i), tangents.get(i));
			verticesList.add(vertex);
		}
		Integer[] indicesArray = indices.toArray(new Integer[0]);
        Vertex[] verticesArray = verticesList.toArray(new Vertex[0]);
        int[] indicesArrayInt = new int[indicesArray.length];
        for(int i = 0;i<indicesArray.length;i++)
            indicesArrayInt[i] = indicesArray[i];
        mesh.setVertices(verticesArray, indicesArrayInt, false);
		return this;
	}
	
	public void computeNormals()
    {
        for(int i = 0;i<indices.size();i+=3)
        {
            int i0 = indices.get(i);
            int i1 = indices.get(i+1);
            int i2 = indices.get(i+2);
            
            Vector3 l0 = vertices.get(i1).sub(vertices.get(i0));
            Vector3 l1 = vertices.get(i2).sub(vertices.get(i0));
            Vector3 normal = l0.cross(l1);
            
            normals.get(i0).set(normals.get(i0).add(normal));
            normals.get(i1).set(normals.get(i1).add(normal));
            normals.get(i2).set(normals.get(i2).add(normal));
        }
        
        for(int i = 0;i<normals.size();i++)
        	normals.get(i).set(normals.get(i).normalize());
    }
	
	public void computeTangents()
	{
		for(int i = 0; i < normals.size(); i += 3)
		{
			int i0 = indices.get(i);
			int i1 = indices.get(i + 1);
			int i2 = indices.get(i + 2);

			Vector3 edge1 = vertices.get(i1).sub(vertices.get(i0));
			Vector3 edge2 = vertices.get(i2).sub(vertices.get(i0));

			double deltaU1 = texCoords.get(i1).getX() - texCoords.get(i0).getX();
			double deltaV1 = texCoords.get(i1).getY() - texCoords.get(i0).getY();
			double deltaU2 = texCoords.get(i2).getX() - texCoords.get(i0).getX();
			double deltaV2 = texCoords.get(i2).getY() - texCoords.get(i0).getY();

			double dividend = (deltaU1*deltaV2 - deltaU2*deltaV1);
			//TODO: The first 1.0f may need to be changed to 0.0f here.
			double f = dividend == 0.0 ? 1.0 : 1.0/dividend;

			Vector3 tangent = new Vector3(0,0,0);
			tangent.set(f * (deltaV2 * edge1.getX() - deltaV1 * edge2.getX()),
					f * (deltaV2 * edge1.getY() - deltaV1 * edge2.getY()),
					f * (deltaV2 * edge1.getZ() - deltaV1 * edge2.getZ()));

			tangents.get(i0).set(tangents.get(i0).add(tangent));
			tangents.get(i1).set(tangents.get(i1).add(tangent));
			tangents.get(i2).set(tangents.get(i2).add(tangent));
		}

		for(int i = 0; i < tangents.size(); i++)
			tangents.get(i).set(tangents.get(i).normalize());
	}
}
