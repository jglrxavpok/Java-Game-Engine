package org.jge.phys;

import javax.vecmath.Matrix4f;
import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;

import com.bulletphysics.collision.broadphase.AxisSweep3;
import com.bulletphysics.collision.dispatch.CollisionDispatcher;
import com.bulletphysics.collision.dispatch.DefaultCollisionConfiguration;
import com.bulletphysics.dynamics.DiscreteDynamicsWorld;
import com.bulletphysics.dynamics.constraintsolver.SequentialImpulseConstraintSolver;
import com.bulletphysics.util.ObjectArrayList;

import org.jge.Profiler.ProfileTimer;
import org.jge.maths.Matrix4;
import org.jge.maths.Quaternion;
import org.jge.maths.Transform;
import org.jge.maths.Vector3;
import org.jge.render.Vertex;
import org.jge.render.mesh.Mesh;

public class PhysicsEngine
{

	private ProfileTimer physProfileTimer = new ProfileTimer();
	private DiscreteDynamicsWorld			 world;
	private CollisionDispatcher			   dispatcher;
	private DefaultCollisionConfiguration	 collisionConfiguration;

	public PhysicsEngine()
	{

	}

	public void init()
	{
		collisionConfiguration = new DefaultCollisionConfiguration();
		dispatcher = new CollisionDispatcher(collisionConfiguration);
		Vector3f worldAabbMin = new Vector3f(-1000000,-1000000,-1000000);
		Vector3f worldAabbMax = new Vector3f(1000000,1000000,1000000);
		AxisSweep3 overlappingPairCache = new AxisSweep3(worldAabbMin, worldAabbMax);
		SequentialImpulseConstraintSolver solver = new SequentialImpulseConstraintSolver();
		
		world = new DiscreteDynamicsWorld(dispatcher, overlappingPairCache, solver, collisionConfiguration);
		world.setGravity(toBullet(new Vector3(0, -9.81, 0)));
	}

	public void addPhysObject(PhysicsComponent object)
	{
		world.addRigidBody(object.getRigidBody());
	}

	public static Matrix4f toBullet(Matrix4 v)
	{
		Matrix4f m = new Matrix4f();
		for(int x = 0; x < 4; x++ )
		{
			for(int y = 0; y < 4; y++ )
			{
				m.setElement(x, y, (float)v.get(x, y));
			}
		}
		return m;
	}

	public static com.bulletphysics.linearmath.Transform toBullet(
			Transform transform)
	{
		com.bulletphysics.linearmath.Transform t = new com.bulletphysics.linearmath.Transform();
		t.setRotation(toBullet(transform.getTransformedRotation()));
		t.origin.set((float)transform.getTransformedPos().getX(),
				(float)transform.getTransformedPos().getY(), (float)transform
						.getTransformedPos().getZ());
		return t;
	}

	public static Quat4f toBullet(Quaternion r)
	{
		return new Quat4f((float)r.getX(), (float)r.getY(), (float)r.getZ(),
				(float)r.getW());
	}

	public static Vector3f toBullet(Vector3 v)
	{
		return new Vector3f((float)v.getX(), (float)v.getY(), (float)v.getZ());
	}

	public static Vector3 toJGE(Vector3f v)
	{
		return new Vector3(v.x, v.y, v.z);
	}

	public void update(double delta)
	{
		physProfileTimer.startInvocation();
		world.stepSimulation((float)delta, 7);
		physProfileTimer.endInvocation();
	}

	public static Quaternion toJGE(Quat4f rot)
	{
		return new Quaternion(rot.x, rot.y, rot.z, rot.w);
	}

	public static ObjectArrayList<Vector3f> toBullet(Mesh mesh)
	{
		ObjectArrayList<Vector3f> list = new ObjectArrayList<Vector3f>();
		for(Vertex v : mesh.getVertices())
		{
			list.add(new Vector3f((float)v.getPos().getX(), (float)v.getPos().getY(), (float)v.getPos().getZ()));
		}
		return list;
	}

	public double displayProfileInfo()
	{
		return displayProfileInfo(0);
	}
	
	public double displayProfileInfo(double divisor)
	{
		return physProfileTimer.displayAndReset("Physics simulation time", divisor);
	}
}
