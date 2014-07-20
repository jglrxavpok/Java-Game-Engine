package org.jge.components;

import java.util.ArrayList;

import org.jge.Disposable;
import org.jge.EngineKernel;
import org.jge.maths.Transform;
import org.jge.render.RenderEngine;
import org.jge.render.shaders.Shader;

public class SceneObject implements Disposable
{

    private ArrayList<SceneObject> children;
    private ArrayList<SceneComponent> components;
    private Transform transform;
	private boolean addedToScene;
    
    public SceneObject()
    {
        EngineKernel.getDisposer().add(this);
        children = new ArrayList<SceneObject>();
        transform = new Transform();
        components = new ArrayList<SceneComponent>();
    }
    
    public void onAddToScene()
    {
    	
    }
    
    public void onAddToSceneAll()
    {
    	onAddToScene();
    	addedToScene = true;
    	for(SceneComponent component : components)
        {
            component.onAddToScene();
        }
        for(SceneObject child : children)
        {
            child.onAddToScene();
        }
    }
    
    public Transform getTransform()
    {
        return transform;
    }
    
    public SceneObject addChild(SceneObject object)
    {
        children.add(object);
        object.init();
        object.getTransform().setParent(getTransform());
        if(addedToScene)
        	object.onAddToSceneAll();
        return this;
    }
    
    public SceneObject addComponent(SceneComponent object)
    {
        components.add(object);
        object.init(this);
        if(addedToScene)
        	object.onAddToScene();
        return this;
    }
    
    public ArrayList<SceneObject> getChildren()
    {
        return children;
    }
    
    public ArrayList<SceneComponent> getComponents()
    {
        return components;
    }
    
    public void init()
    {
        for(SceneComponent component : components)
        {
            component.init(this);
        }
        for(SceneObject child : children)
        {
            child.init();
        }
    }
    
    public void update(double delta)
    {
    	
    }
    
    public void updateAll(double delta)
    {
    	update(delta);
        getTransform().update();
        for(SceneComponent component : components)
        {
            component.update(delta);
        }
        for(SceneObject child : children)
        {
            child.updateAll(delta);
        }
    }
    
    public void render(Shader shader, double delta, RenderEngine renderEngine)
    {
    	
    }
    
    public void renderAll(Shader shader, double delta, RenderEngine renderEngine)
    {
    	render(shader, delta, renderEngine);
        for(SceneComponent component : components)
        {
            component.render(shader, delta, renderEngine);
        }
        for(SceneObject child : children)
        {
            child.renderAll(shader, delta, renderEngine);
        }
    }

    public void dispose()
    {
        for(SceneComponent component : components)
        {
            component.dispose();
        }
        for(SceneObject child : children)
        {
            child.dispose();
        }
    }
}
