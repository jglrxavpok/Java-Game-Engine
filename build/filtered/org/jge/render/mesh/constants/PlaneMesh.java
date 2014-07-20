package org.jge.render.mesh.constants;

import org.jge.EngineKernel;
import org.jge.ResourceLocation;
import org.jge.render.mesh.Mesh;

public class PlaneMesh extends Mesh
{

	public PlaneMesh() throws Exception
	{
		super(EngineKernel.getResourceLoader().getResource(new ResourceLocation("models", "plane.obj")));
	}

}
