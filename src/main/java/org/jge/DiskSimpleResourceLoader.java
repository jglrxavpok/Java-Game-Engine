package org.jge;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;

import org.jge.util.Lists;

public class DiskSimpleResourceLoader extends ResourceLoader
{

	private HashMap<String, AbstractResource> resources = new HashMap<String, AbstractResource>();

	@Override
	public AbstractResource getResourceOrCreate(ResourceLocation location) throws Exception
	{
		if(!doesResourceExists(location))
			new File(location.getFullPath()).createNewFile();
		
		return getResource(location);
	}
	
	@Override
	public AbstractResource getResource(ResourceLocation location)
			throws Exception
	{
		if(!isLoaded(location))
		{
			File file = new File(location.getFullPath());
			if(file.exists())
				resources.put(location.getFullPath(),
						new DiskSimpleResource(location, file, this));
			else
				throw new FileNotFoundException("Resource "
						+ location.getFullPath() + " not found.");
		}
		if(!isLoaded(location))
			throw new FileNotFoundException("Resource "
					+ location.getFullPath() + " not found.");
		return resources.get(location.getFullPath());
	}

	public boolean isLoaded(ResourceLocation location)
	{
		return resources.containsKey(location.getFullPath());
	}

	@Override
	public List<AbstractResource> getAllResources(ResourceLocation location)
			throws Exception
	{
		return Lists.asList(getResource(location));
	}

	@Override
	public boolean doesResourceExists(ResourceLocation location)
	{
		return new File(location.getFullPath()).exists();
	}

}
