package org.jge.phys;

import javax.vecmath.Quat4f;

import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.RigidBodyConstructionInfo;
import com.bulletphysics.linearmath.DefaultMotionState;
import com.bulletphysics.linearmath.Transform;

import org.jge.CoreEngine;
import org.jge.components.SceneComponent;
import org.jge.components.SceneObject;
import org.jge.maths.Quaternion;
import org.jge.maths.Vector3;

public class PhysicsComponent extends SceneComponent
{

	private PhysicsEngine physicsEngine;
	private RigidBody body;
	private double mass;
	private CollisionShape shape;

	public PhysicsComponent(double mass, CollisionShape shape)
	{
		this.mass = mass;
		this.shape = shape;
	}
	
	public void init(SceneObject object)
	{
		super.init(object);
		physicsEngine = CoreEngine.getCurrent().getPhysicsEngine();
	}
	
	public void onAddToScene()
	{
		Transform startTrans = PhysicsEngine.toBullet(getParent().getTransform());
		RigidBodyConstructionInfo constructionInfo = new RigidBodyConstructionInfo((float)mass, new DefaultMotionState(startTrans), shape);
		constructionInfo.restitution = 0.01f;
		shape.calculateLocalInertia((float)mass, constructionInfo.localInertia);
		body = new RigidBody(constructionInfo);
		physicsEngine.addPhysObject(this);
	}
	
	public void update(double delta)
	{
		Transform out = new Transform();
		out = body.getWorldTransform(out);
		Quat4f rot = new Quat4f(0,0,0,1); 
		rot = out.getRotation(rot);
		Quaternion newRot = PhysicsEngine.toJGE(rot);
		this.getParent().getTransform().setRotation(newRot);
		Vector3 pos = PhysicsEngine.toJGE(out.origin);
		this.getParent().getTransform().setPosition(pos);
	}

	public RigidBody getRigidBody()
	{
		return body;
	}
}
