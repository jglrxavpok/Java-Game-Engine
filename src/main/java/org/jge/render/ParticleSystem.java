package org.jge.render;

import static org.lwjgl.opengl.GL11.*;

import java.util.ArrayList;

import org.jge.components.SceneObject;
import org.jge.maths.Vector3;
import org.jge.render.shaders.Shader;

public class ParticleSystem extends SceneObject
{

	private int				 max;
	private ArrayList<Particle> particles;
	private ArrayList<Particle> toRemove;

	public ParticleSystem()
	{
		this(2000);
	}

	public ParticleSystem(int max)
	{
		this.max = max;
		this.particles = new ArrayList<Particle>();
		this.toRemove = new ArrayList<Particle>();
	}

	public void update(double delta)
	{
		toRemove.clear();
		for(Particle p : particles)
		{
			if(p != null)
			{
				if(p.isDead())
					toRemove.add(p);
				p.update(delta);
			}
		}
		if(!toRemove.isEmpty())
			particles.removeAll(toRemove);
	}

	public void render(Shader shader, double delta, RenderEngine engine)
	{
		if(particles.isEmpty())
			return;
		shader.bind();
		shader.updateUniforms(getTransform(), engine.getCamera(), Material.defaultMaterial, engine);
		glEnable(GL_POINT_SMOOTH); 
		glPointSize(3);
		glBegin(GL_POINTS);
			for(Particle p : particles)
			{
				Vector3 pos = p.getPos();
				glVertex3d(pos.x, pos.y, pos.z);
			}
		glEnd();
		glDisable(GL_POINT_SMOOTH);
	}

	public ParticleSystem addParticle(Particle p)
	{
		particles.add(p);
		return this;
	}

}
