package org.jge.phys.shapes;

import com.bulletphysics.collision.shapes.CollisionShape;

import org.jge.maths.Vector3;
import org.jge.phys.PhysicsEngine;
import org.jge.phys.PhysicsShape;

public class BoxPhysShape extends PhysicsShape
{

	private Vector3 halfExtend;

	public BoxPhysShape(Vector3 halfExtend)
	{
		this.halfExtend = halfExtend;
	}
	
	@Override
	protected CollisionShape toCollisionShape()
	{
		return new com.bulletphysics.collision.shapes.BoxShape(PhysicsEngine.toBullet(halfExtend));
	}

}
