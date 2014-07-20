package org.jge.components;

import org.jge.game.Input;
import org.jge.maths.Maths;
import org.jge.maths.Vector3;

public class FreeLook extends SceneComponent
{

    private static final Vector3 yAxis = new Vector3(0,1,0);
	private double sensitivity;
    
    public FreeLook(double sensitivity)
    {
    	this.sensitivity = sensitivity;
    }

	public void update(double delta)
    {
        Input.showCursor(false);
		getParent().getTransform().rotate(yAxis, Maths.toRadians(Input.getMouseDX()* sensitivity));
        getParent().getTransform().rotate(getParent().getTransform().getRotation().getRight(), Maths.toRadians(-Input.getMouseDY() * sensitivity));
    }
}
