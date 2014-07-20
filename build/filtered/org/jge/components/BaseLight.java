package org.jge.components;

import org.jge.EngineKernel;
import org.jge.maths.Quaternion;
import org.jge.maths.Vector3;
import org.jge.render.shaders.Shader;

public class BaseLight extends SceneComponent
{

    public static class LightCamTrans
	{
    	public Vector3 pos = Vector3.NULL.copy();
    	public Quaternion rot = Quaternion.NULL.copy();
	};

	private Vector3 color;
    private float intensity;
    private Shader shader;
    private ShadowingInfo shadowInfo;
	private boolean enabled;
    
    public BaseLight()
    {
        this(new Vector3(1,1,1), 0.8f);
    }

    public BaseLight(Vector3 color, float intensity)
    {
        super();
        this.color = color;
        this.enabled = true;
        this.intensity = intensity;
    }
    
    public LightCamTrans getLightCamTrans(Camera mainCam)
    {
    	LightCamTrans trans = new LightCamTrans();
    	trans.pos = getParent().getTransform().getTransformedPos();
    	trans.rot = getParent().getTransform().getTransformedRotation();
    	return trans;
    }
    
    public ShadowingInfo getShadowingInfo()
    {
    	return shadowInfo;
    }
    
    public BaseLight setEnabled(boolean b)
    {
    	this.enabled = b;
    	return this;
    }
    
    public boolean isEnabled()
    {
    	return enabled;
    }
    
    public BaseLight setShadowingInfo(ShadowingInfo shadowInfo)
    {
    	this.shadowInfo = shadowInfo;
    	return this;
    }
    
    public Shader getShader()
    {
        return shader;
    }
    
    public BaseLight setShader(Shader shader)
    {
        this.shader = shader;
        return this;
    }

    public Vector3 getColor()
    {
        return color;
    }

    public void setColor(Vector3 color)
    {
        this.color = color;
    }

    public float getIntensity()
    {
        return intensity;
    }

    public void setIntensity(float intensity)
    {
        this.intensity = intensity;
    }
    
    public void onAddToScene()
    {
        super.onAddToScene();
        EngineKernel.getGame().getRenderEngine().addLight(this);
    }
    
    public void dispose()
    {
        EngineKernel.getGame().getRenderEngine().removeLight(this);
    }

}

