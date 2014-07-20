package org.jge.render.mesh;

import java.util.ArrayList;

import org.jge.AbstractResource;
import org.jge.EngineException;
import org.jge.maths.Quaternion;
import org.jge.maths.Vector2;
import org.jge.maths.Vector3;
import org.jge.util.Arrays;
import org.jge.util.BinaryUtils;
import org.jge.util.HashMapWithDefault;

public class OBJLoader extends IndexedModelLoader
{

	private final static class OBJIndex
	{
		int positionIndex;
		int texCoordsIndex;
		int normalIndex;
		
		public boolean equals(Object o)
		{
			if(o instanceof OBJIndex)
			{
				OBJIndex index = (OBJIndex)o;
				return index.normalIndex == normalIndex && index.positionIndex == positionIndex && index.texCoordsIndex == texCoordsIndex;
			}
			
			return false;
		}
		
		public int hashCode()
		{
			final int base = 17;
			final int multiplier = 31;
			
			int result = base;
			result = multiplier*result+positionIndex;
			result = multiplier*result+texCoordsIndex;
			result = multiplier*result+normalIndex;
			return result;
		}
	}
	
	private static final String COMMENT = "#";
	private static final String FACE = "f";
	private static final String POSITION = "v";
	private static final String TEX_COORDS = "vt";
	private static final String NORMAL = "vn";

	
	private boolean hasNormals = false;
	private boolean hasTexCoords = false;
	
	@Override
	public IndexedModel loadModel(AbstractResource res) throws EngineException
	{
		try
		{
			hasNormals = false;
			hasTexCoords = false;
    		IndexedModel result = new IndexedModel();
    		IndexedModel normalModel = new IndexedModel();
    		String lines[] = BinaryUtils.toString(res.getData()).split("\n|\r");
    		
    		ArrayList<Vector3> positions = new ArrayList<Vector3>();
    		ArrayList<Vector2> texCoords = new ArrayList<Vector2>();
    		ArrayList<Vector3> normals = new ArrayList<Vector3>();
            ArrayList<OBJIndex> indices = new ArrayList<OBJIndex>();
            HashMapWithDefault<OBJIndex, Integer> resultIndexMap = new HashMapWithDefault<OBJIndex, Integer>();
            HashMapWithDefault<Integer, Integer> normalIndexMap = new HashMapWithDefault<Integer, Integer>();
            HashMapWithDefault<Integer, Integer> indexMap = new HashMapWithDefault<Integer, Integer>();
            resultIndexMap.setDefault(-1);
            normalIndexMap.setDefault(-1);
            indexMap.setDefault(-1);
            for(String line : lines)
            {
                if(line != null && !line.trim().equals(""))
                {
                    String[] parts = Arrays.trim(line.split(" "));
                    if(parts.length == 0)
                        continue;
                    if(parts[0].equals(COMMENT))
                    {
                        continue;
                    }
                    else if(parts[0].equals(POSITION))
                    {
                        positions.add(new Vector3(Double.parseDouble(parts[1]), Double.parseDouble(parts[2]), Double.parseDouble(parts[3])));
                    }
                    else if(parts[0].equals(FACE))
                    {
                    	for(int i = 0;i<parts.length-3;i++)
                    	{
                    		indices.add(parseOBJIndex(parts[1]));
                    		indices.add(parseOBJIndex(parts[2+i]));
                    		indices.add(parseOBJIndex(parts[3+i]));
                    	}
                    }
                    else if(parts[0].equals(NORMAL))
                    {
                    	normals.add(new Vector3(Double.parseDouble(parts[1]), Double.parseDouble(parts[2]), Double.parseDouble(parts[3])));
                    }
                    else if(parts[0].equals(TEX_COORDS))
                    {
                    	texCoords.add(new Vector2(Double.parseDouble(parts[1]), Double.parseDouble(parts[2])));
                    }
                }
            }
            
            for(int i = 0;i< indices.size();i++)
            {
            	OBJIndex current = indices.get(i);
            	Vector3 pos = positions.get(current.positionIndex);
            	Vector2 texCoord;
            	if(hasTexCoords)
            	{
            		texCoord = texCoords.get(current.texCoordsIndex);
            	}
            	else
            	{
            		texCoord = new Vector2(0, 0);
            	}
            	Vector3 normal;
            	if(hasNormals)
            	{
            		normal = normals.get(current.normalIndex);
            	}
            	else
            	{
            		normal = new Vector3(0, 0,0);
            	}
            	
            	int modelVertexIndex = resultIndexMap.get(current);
            	if(modelVertexIndex == -1)
            	{
            		resultIndexMap.put(current, result.getPositions().size());
            		modelVertexIndex = result.getPositions().size();
            		
                	result.getPositions().add(pos);
                	result.getTexCoords().add(texCoord);
                	if(hasNormals)
                		result.getNormals().add(normal);
                	result.getTangents().add(new Vector3(0,0,0));
            	}
            	
            	int normalModelIndex = normalIndexMap.get(current.positionIndex);
            	
            	if(normalModelIndex == -1)
            	{
            		normalModelIndex = normalModel.getPositions().size();
            		normalIndexMap.put(current.positionIndex, normalModelIndex);
            		
            		normalModel.getPositions().add(pos);
            		normalModel.getTexCoords().add(texCoord);
            		normalModel.getNormals().add(normal);
            		normalModel.getTangents().add(new Vector3(0,0,0));
            	}
            	
        		result.getIndices().add(modelVertexIndex);
            	normalModel.getIndices().add(normalModelIndex);
            	indexMap.put(modelVertexIndex, normalModelIndex);
            }
    		
            if(!hasNormals)
            {
            	normalModel.computeNormals();
            	
            	for(int i = 0;i<result.getPositions().size();i++)
            	{
            		result.getNormals().add(normalModel.getNormals().get(indexMap.get(i)));
            	}
            }
            
            normalModel.computeTangents();
            for(int i = 0;i<result.getPositions().size();i++)
        	{
        		result.getTangents().add(normalModel.getTangents().get(indexMap.get(i)));
        	}
            
    		return result;
		}
		catch(Exception e)
		{
			throw new EngineException("Error while loading model "+res.getResourceLocation().getFullPath(), e);
		}
	}
	
	public OBJIndex parseOBJIndex(String token)
	{
		OBJIndex index = new OBJIndex();
		String[] values = token.split("/");
		
		index.positionIndex = Integer.parseInt(values[0])-1;
		if(values.length > 1)
		{
			if(values[1] != null && !values[1].equals(""))
			{
				index.texCoordsIndex = Integer.parseInt(values[1])-1;
			}
			hasTexCoords = true;
			if(values.length > 2)
			{
				index.normalIndex = Integer.parseInt(values[2])-1;
				hasNormals = true;
			}
		}
		
		return index;
	}

}
