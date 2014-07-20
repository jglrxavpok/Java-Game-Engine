package org.jge.components;

import java.util.Collection;
import java.util.HashMap;

import org.jge.Disposable;
import org.jge.EngineKernel;
import org.jge.maths.Transform;
import org.jge.render.RenderEngine;
import org.jge.render.shaders.Shader;

public class SceneObject implements Disposable
{

    private HashMap<String, SceneObject> children;
    private HashMap<String, SceneComponent> components;
    private Transform transform;
	private boolean addedToScene;
    
    public SceneObject()
    {
        EngineKernel.getDisposer().add(this);
        children = new HashMap<String, SceneObject>();
        transform = new Transform();
        components = new HashMap<String, SceneComponent>();
    }
    
    public void onAddToScene()
    {
    	
    }
    
    public void onAddToSceneAll()
    {
    	onAddToScene();
    	addedToScene = true;
    	for(SceneComponent component : components.values())
        {
            component.onAddToScene();
        }
        for(SceneObject child : children.values())
        {
            child.onAddToSceneAll();
        }
    }
    
    public Transform getTransform()
    {
        return transform;
    }
    
    public SceneObject addChild(SceneObject object)
    {
        return addChildAs("child"+children.size(), object);
    }
    
    public SceneObject addChildAs(String id, SceneObject object)
    {
    	children.put(id, object);
        object.init();
        object.getTransform().setParent(getTransform());
        if(addedToScene)
        	object.onAddToSceneAll();
        return this;
    }
    
    public SceneObject addComponent(SceneComponent object)
    {
        return addComponentAs(object.getClass().getName(), object);
    }
    
    public SceneObject addComponentAs(String id, SceneComponent object)
    {
    	components.put(id, object);
        object.init(this);
        if(addedToScene)
        	object.onAddToScene();
        return this;
    }
    
    public Collection<SceneObject> getChildren()
    {
        return children.values();
    }
    
    public Collection<SceneComponent> getComponents()
    {
        return components.values();
    }
    
    public void init()
    {
        for(SceneComponent component : components.values())
        {
            component.init(this);
        }
        for(SceneObject child : children.values())
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
        for(SceneComponent component : components.values())
        {
            component.update(delta);
        }
        for(SceneObject child : children.values())
        {
            child.updateAll(delta);
        }
    }
    
    public void render(Shader shader, Camera cam, double delta, RenderEngine renderEngine)
    {
    	
    }
    
    public void renderAll(Shader shader, Camera cam, double delta, RenderEngine renderEngine)
    {
    	render(shader, cam, delta, renderEngine);
        for(SceneComponent component : components.values())
        {
            component.render(shader, cam, delta, renderEngine);
        }
        for(SceneObject child : children.values())
        {
            child.renderAll(shader, cam, delta, renderEngine);
        }
    }

    public void dispose()
    {
        for(SceneComponent component : components.values())
        {
            component.dispose();
        }
        for(SceneObject child : children.values())
        {
            child.dispose();
        }
    }

	public SceneObject getChild(String string)
	{
		return children.get(string);
	}
	
	public boolean receivesShadows()
	{
		return true;
	}
}
