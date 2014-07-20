package org.jge.render;

import org.jge.maths.Vector3;
import org.jge.util.Log;

public class Particle
{

	private Vector3 pos;
	private Vector3 velocity;
	private int life;
	private boolean dead;
	private Vector3 color;
	private Vector3 gravityForce;

	public Particle(Vector3 pos, Vector3 velocity)
	{
		this(pos, velocity, 60);
	}
	
	public Particle(Vector3 pos, Vector3 velocity, int life)
	{
		this(pos, velocity, life, new Vector3(1,1,1));
	}
	
	public Particle(Vector3 pos, Vector3 velocity, int life, Vector3 color)
	{
		this(pos, velocity, life, color, Vector3.NULL);
	}
	
	public Particle(Vector3 pos, Vector3 velocity, int life, Vector3 color, Vector3 gravity)
	{
		this.pos = pos;
		this.velocity = velocity;
		this.life = life;
		this.color = color;
		this.gravityForce = gravity;
	}

	public void update(double delta)
	{
		life--;
		velocity.set(velocity.add(gravityForce.mul(delta)));
		pos.set(pos.add(velocity.mul(delta)));
		if(life <= 0)
			dead = true;
	}
	
	public Vector3 getPos()
	{
		return pos;
	}

	public void setPos(Vector3 pos)
	{
		this.pos = pos;
	}

	public Vector3 getVelocity()
	{
		return velocity;
	}

	public void setVelocity(Vector3 velocity)
	{
		this.velocity = velocity;
	}

	public boolean isDead()
	{
		return dead;
	}

	public Vector3 getColor()
	{
		return color;
	}

	public void setColor(Vector3 color)
	{
		this.color = color;
	}

	public Vector3 getGravityForce()
	{
		return gravityForce;
	}

	public void setGravityForce(Vector3 gravityForce)
	{
		this.gravityForce = gravityForce;
	}

	public float getSize()
	{
		return 4f;
	}
}
