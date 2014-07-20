package org.jge.components;

import org.jge.EngineKernel;
import org.jge.maths.Matrix4;
import org.jge.maths.Vector3;

public class Camera extends SceneComponent
{

    private Matrix4 projection;

    private static Camera current;

    public Camera(Matrix4 projection)
    {
    	this.projection = projection;
    }
    
    public Camera(double fov, double aspect, double zNear, double zFar)
    {
        super();
        current = this;
        this.projection = new Matrix4().initPerspective(fov, aspect, zNear, zFar);
    }
    
    public Camera setProjection(Matrix4 projection)
    {
    	this.projection = projection;
    	return this;
    }

    public Matrix4 getViewProjection()
    {
        Matrix4 cameraRotation = getParent().getTransform().getTransformedRotation().conjugate().toRotationMatrix();
        Vector3 cameraPos = getParent().getTransform().getTransformedPos().mul(-1);
        Matrix4 cameraTranslation = new Matrix4().initTranslation(cameraPos.getX(), cameraPos.getY(), cameraPos.getZ());
        return projection.mul(cameraRotation.mul(cameraTranslation));
    }

    public void init(SceneObject object)
    {
        super.init(object);
        EngineKernel.getGame().getRenderEngine().addCamera(this);
    }
    
    public Vector3 getLeft()
    {
        return getParent().getTransform().getRotation().getLeft();
    }

    public Vector3 getRight()
    {
        return getParent().getTransform().getRotation().getRight();
    }

    public Vector3 getPos()
    {
        return getParent().getTransform().getTransformedPos();
    }

    public void setPos(Vector3 pos)
    {
        getParent().getTransform().setPosition(pos);
    }

    public Vector3 getForward()
    {
        return getParent().getTransform().getTransformedRotation().getForward();
    }

    public void setForward(Vector3 forward)
    {
        getParent().getTransform().getTransformedRotation().getForward().set(forward.getX(), forward.getY(), forward.getZ());
    }

    public Vector3 getUp()
    {
        return getParent().getTransform().getTransformedRotation().getUp();
    }

    public void setUp(Vector3 up)
    {
        getParent().getTransform().getRotation().getUp().set(up.getX(), up.getY(), up.getZ());
    }
    
    public static void setCurrent(Camera cam)
    {
        current = cam;
    }

    public static Camera getCurrent()
    {
        return current;
    }
    
}
