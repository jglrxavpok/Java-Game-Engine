package org.jge.gpuresources;

import org.jge.EngineKernel;
import org.jge.ResourceLocation;
import org.jge.maths.Vector3;
import org.jge.render.Texture;
import org.jge.util.HashMapWithDefault;

public abstract class MappedValues
{

	private HashMapWithDefault<String, Vector3> vectors3;
    private HashMapWithDefault<String, Float> floats;
    private HashMapWithDefault<String, Texture> textures;
	private HashMapWithDefault<String, Integer> integers;
    
    public MappedValues()
    {
    	vectors3 = new HashMapWithDefault<String, Vector3>();
        vectors3.setDefault(new Vector3(0, 0, 0));
        floats = new HashMapWithDefault<String, Float>();
        floats.setDefault(0f);
        textures = new HashMapWithDefault<String, Texture>();
        integers = new HashMapWithDefault<String, Integer>();
        
        try
		{
			textures.setDefault(new Texture(EngineKernel.getResourceLoader().getResource(new ResourceLocation("textures", "damier.png"))));
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
    }
    
    public void setVector3(String name, Vector3 value)
    {
        vectors3.put(name, value);
    }
    
    public void setFloat(String name, float value)
    {
        floats.put(name, value);
    }
    
    public void setInt(String name, int value)
    {
        integers.put(name, value);
    }
    
    public int getInt(String name)
    {
        return integers.get(name);
    }
    
    public Vector3 getVector3(String name)
    {
        return vectors3.get(name);
    }
    
    public float getFloat(String name)
    {
        return floats.get(name);
    }
    
    public void setTexture(String name, Texture texture)
    {
        textures.put(name, texture);
    }
    
    public Texture getTexture(String name)
    {
        return textures.get(name);
    }
    
    public HashMapWithDefault<String, Vector3> getVector3s()
    {
    	return vectors3;
    }
    
    public HashMapWithDefault<String, Float> getFloats()
    {
    	return floats;
    }
    
    public HashMapWithDefault<String, Texture> getTextures()
    {
    	return textures;
    }
    
    public HashMapWithDefault<String, Integer> getInts()
    {
    	return integers;
    }
}
