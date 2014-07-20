package org.jge.phys;

import com.bulletphysics.collision.shapes.CollisionShape;

public abstract class PhysicsShape
{
	protected abstract CollisionShape toCollisionShape();
}
