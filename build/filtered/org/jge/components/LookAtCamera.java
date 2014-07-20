package org.jge.components;

import org.jge.maths.Quaternion;
import org.jge.maths.Vector3;

/**
 * TO FIX!!!!
 * @author jglrxavpok
 *
 */
public class LookAtCamera extends SceneComponent
{

	public void update(double delta)
	{
		Quaternion rot = getParent().getTransform().getLookAtRotation(Camera.getCurrent().getParent().getTransform().getTransformedPos(), new Vector3(0,1,0).normalize());
		getParent().getTransform().setRotation(/*getParent().getTransform().getRotation().nlerp(rot, delta * 5.0f, true)*/rot);
	}
}
