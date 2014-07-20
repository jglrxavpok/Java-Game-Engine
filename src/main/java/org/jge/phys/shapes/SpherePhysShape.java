package org.jge.phys.shapes;

import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.collision.shapes.SphereShape;

import org.jge.phys.PhysicsShape;

public class SpherePhysShape extends PhysicsShape
{

	private float radius;

	public SpherePhysShape(float radius)
	{
		this.radius = radius;
	}
	
	public SpherePhysShape(double radius)
	{
		this((float)radius);
	}
	
	@Override
	protected CollisionShape toCollisionShape()
	{
		return new SphereShape(radius);
	}

}
