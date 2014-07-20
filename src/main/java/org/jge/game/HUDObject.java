package org.jge.game;

import static org.lwjgl.opengl.GL11.*;

import org.jge.Window;
import org.jge.components.Camera;
import org.jge.components.SceneObject;
import org.jge.maths.Matrix4;
import org.jge.render.RenderEngine;
import org.jge.render.shaders.Shader;

public class HUDObject extends SceneObject
{

	private Camera hudCam;

	public HUDObject()
	{
		this.hudCam = new Camera(new Matrix4().initOrthographic(0, Window.getCurrent().getRealWidth(), 0, Window.getCurrent().getRealHeight(), -1, 1));
		addComponentAs("hudCam", hudCam);
	}
	
	public void render(Shader shader, Camera cam, double delta, RenderEngine renderEngine)
    {
    	glClear(GL_DEPTH_BUFFER_BIT);
    }
    
    public void renderAll(Shader shader, Camera cam, double delta, RenderEngine renderEngine)
    {
    	if(renderEngine.isRenderingShadowMap())
    		return;
    	boolean disableBlendAfter = false;
    	if(!glIsEnabled(GL_BLEND))
    	{
    		disableBlendAfter = true;
    		glEnable(GL_BLEND);
    	}
    	glEnable(GL_ALPHA_TEST);
    	glAlphaFunc(GL_GEQUAL, 0.001f);
//    	glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
    	Matrix4 copyOfLightMatrix = renderEngine.getLightMatrix();
    	renderEngine.setLightMatrix(new Matrix4().initIdentity());
    	glDisable(GL_CULL_FACE);
    	
    	boolean lightingWasOn = renderEngine.isLightingOn();
    	boolean shadowingWasOn = renderEngine.isShadowingOn();
    	boolean dispWasOn = renderEngine.isParallaxDispMappingOn();
    	renderEngine.setLighting(false);
    	renderEngine.setShadowing(false);
    	renderEngine.setParallaxDispMapping(false);
    	super.renderAll(shader, hudCam, delta, renderEngine);
    	renderEngine.setShadowing(shadowingWasOn);
    	renderEngine.setLighting(lightingWasOn);
    	renderEngine.setParallaxDispMapping(dispWasOn);
    	glEnable(GL_CULL_FACE);
    	renderEngine.setLightMatrix(copyOfLightMatrix);
    	glBlendFunc(GL_ONE, GL_ONE);
    	if(disableBlendAfter)
    		glDisable(GL_BLEND);
    	glDisable(GL_ALPHA_TEST);
    }
    
    public boolean receivesShadows()
	{
		return false;
	}
}
