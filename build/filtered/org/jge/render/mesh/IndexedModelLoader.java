package org.jge.render.mesh;

import org.jge.AbstractResource;
import org.jge.EngineException;

public abstract class IndexedModelLoader
{
	public abstract IndexedModel loadModel(AbstractResource res) throws EngineException;
}
