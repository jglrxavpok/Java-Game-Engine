package org.jge.render.mesh;

public enum ModelFormats
{

	OBJ(new OBJLoader());
	
	private IndexedModelLoader loader;
	
	private ModelFormats(IndexedModelLoader loader)
	{
		this.loader = loader;
	}
	
	public IndexedModelLoader getLoader()
	{
		return loader;
	}

	public static ModelFormats get(String extension)
	{
		for(ModelFormats format : values())
		{
			if(format.name().equalsIgnoreCase(extension))
				return format;
		}
		return null;
	}
}
