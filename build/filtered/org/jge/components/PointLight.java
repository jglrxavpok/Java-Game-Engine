package org.jge.components;

import org.jge.ResourceLocation;
import org.jge.maths.Maths;
import org.jge.maths.Vector3;
import org.jge.render.shaders.Shader;

public class PointLight extends BaseLight
{
    private static final int COLOR_DEPTH = 256;
    private Vector3 attenuation;
    private float range;
    
    public PointLight()
    {
        this(new Vector3(0, 0, 0));
    }
    
    public PointLight(Vector3 attenuation)
    {
        this(new Vector3(1, 1, 1),1f, attenuation);
    }
    
    public PointLight(Vector3 color, float intensity, Vector3 attenuation)
    {
        super(color, intensity);
        
        double a = attenuation.getZ();
        double b = attenuation.getY();
        double c = attenuation.getX() - COLOR_DEPTH * getIntensity() * getColor().max();
        //ax²+bx+c = 0
        
        this.range = (float)(-b + Maths.sqrt(b*b -4 *a*c)/(2*a));
        this.attenuation = attenuation;
        setShader(new Shader(new ResourceLocation("shaders", "forward-point")));
    }

    public Vector3 getAttenuation()
    {
        return attenuation;
    }

    public void setAttenuation(Vector3 attenuation)
    {
        this.attenuation = attenuation;
    }

    public float getRange()
    {
        return range;
    }

    public void setRange(float range)
    {
        this.range = range;
    }
    
}
