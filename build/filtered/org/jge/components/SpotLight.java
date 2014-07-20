package org.jge.components;

import org.jge.ResourceLocation;
import org.jge.maths.Maths;
import org.jge.maths.Matrix4;
import org.jge.maths.Vector3;
import org.jge.render.shaders.Shader;

public class SpotLight extends PointLight
{

    private float cutoff;
    
    public SpotLight(float cutoff)
    {
        this(new Vector3(1,1,1), 1f, new Vector3(0,0,1), cutoff);
    }
    
    public SpotLight(Vector3 atten, float cutoff)
    {
        this(new Vector3(1,1,1), 1f, atten, cutoff);
    }
    
    public SpotLight(Vector3 color, float intensity, Vector3 atten, float fov)
    {
        super(color, intensity, atten);
        this.cutoff = (float)Maths.acos(fov/2.0);
        
        setShader(new Shader(new ResourceLocation("shaders", "forward-spot")));
        setShadowingInfo(new ShadowingInfo(new Matrix4().initPerspective(fov, 1.0, 0.1, this.getRange())).flipFaces(true));
    }
    
    public float getCutoff()
    {
        return cutoff;
    }
    
    public void setCutoff(float cutoff)
    {
        this.cutoff = cutoff;
    }
    
    public Vector3 getDirection()
    {
        return getParent().getTransform().getTransformedRotation().getForward();
    }
}
