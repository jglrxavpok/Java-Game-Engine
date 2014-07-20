package org.jge.components;

import org.jge.Disposable;
import org.jge.render.RenderEngine;
import org.jge.render.shaders.Shader;

public abstract class SceneComponent implements Disposable
{
    private SceneObject parent;

    public SceneObject getParent()
    {
        return parent;
    }
    
    public void onAddToScene()
    {
    	
    }

    public void init(SceneObject parent)
    {
        this.parent = parent;
    }
    
    public void update(double delta)
    {
        
    }
    
    public void render(Shader shader, double delta, RenderEngine renderEngine)
    {
        
    }
    
    public void dispose()
    {
        
    }
    
}
