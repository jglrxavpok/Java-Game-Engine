package org.jge.components;

import org.jge.game.Input;
import org.jge.maths.Vector3;

public class FreeMove extends SceneComponent
{

	private static final Vector3 yAxis = new Vector3(0, 1, 0);
	private double speed;
	
	/**
	 * @param speed: in m/s!
	 */
	public FreeMove(double speed)
	{
		this.speed = speed;
	}

	public void moveForward(double amnt)
	{
		this.move(getParent().getTransform().getRotation().getForward(), amnt);
	}

	public void moveBackwards(double amnt)
	{
		this.move(getParent().getTransform().getRotation().getForward()
				.negative(), amnt);
	}

	public void strafeLeft(double amnt)
	{
		Vector3 left = getParent().getTransform().getRotation().getLeft();
		move(left, amnt);
	}

	public void strafeRight(double amnt)
	{
		Vector3 right = getParent().getTransform().getRotation().getRight();
		move(right, amnt);
	}

	public void scrollUp(double amnt)
	{
		move(yAxis, amnt);
	}

	public void scrollDown(double amnt)
	{
		move(yAxis.negative(), amnt);
	}

	public void moveDown(double amnt)
	{
		move(getParent().getTransform().getRotation().getUp().negative(), amnt);
	}

	public void moveUp(double amnt)
	{
		move(getParent().getTransform().getRotation().getUp(), amnt);
	}

	public void update(double delta)
	{
		double speed = this.speed * delta * 60;
		if(Input.isKeyDown(Input.KEY_D))
		{
			strafeRight(speed);
		}

		if(Input.isKeyDown(Input.KEY_Q))
		{
			strafeLeft(speed);
		}

		if(Input.isKeyDown(Input.KEY_Z))
		{
			moveForward(speed);
		}

		if(Input.isKeyDown(Input.KEY_S))
		{
			moveBackwards(speed);
		}

		if(Input.isKeyDown(Input.KEY_SPACE))
		{
			scrollUp(speed);
		}

		if(Input.isKeyDown(Input.KEY_LSHIFT))
		{
			scrollDown(speed);
		}
	}

	public void move(Vector3 dir, double amt)
	{
		getParent().getTransform().setPosition(
				getParent().getTransform().getPosition().add(dir.mul(amt)));
	}
}
