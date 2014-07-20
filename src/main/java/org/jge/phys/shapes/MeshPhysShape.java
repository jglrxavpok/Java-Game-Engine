package org.jge.phys.shapes;

import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.collision.shapes.ConvexHullShape;

import org.jge.phys.PhysicsEngine;
import org.jge.phys.PhysicsShape;
import org.jge.render.mesh.Mesh;

public class MeshPhysShape extends PhysicsShape
{

	private Mesh mesh;

	public MeshPhysShape(Mesh mesh)
	{
		this.mesh = mesh;
	}
	
	@Override
	protected CollisionShape toCollisionShape()
	{
		return new ConvexHullShape(PhysicsEngine.toBullet(mesh));
	}

}
