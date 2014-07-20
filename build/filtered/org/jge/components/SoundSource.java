package org.jge.components;

import org.jge.maths.Vector3;
import org.jge.sound.Sound;

public class SoundSource extends SceneComponent
{

	public Vector3 pos;
	public Vector3 vel;
	private Sound sound;
	private long delayBetweenPlays;
	private long delay;
	
	public SoundSource(Sound sound, long delayBetweenPlays)
	{
		this.sound = sound;
		this.delayBetweenPlays = delayBetweenPlays;
		pos = new Vector3(0,0,0);
		vel = new Vector3(0,0,0);
	}
	
	public void onAddToScene()
	{
		
	}
	
	public void update(double delta)
	{
		if((delay += delta*1000) >= delayBetweenPlays && delayBetweenPlays > -1)
		{
			delay = 0;
			int id = sound.play();
			sound.setSourcePosition(id, pos = getParent().getTransform().getTransformedPos());
			sound.setSourceVelocity(id, getVel());
		}
	}
	
	public Vector3 getVel()
	{
		return vel;
	}
	
	public SoundSource setVel(Vector3 newVel)
	{
		this.vel = newVel;
		return this;
	}
}
