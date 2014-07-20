package org.jge.render;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.*;

import java.util.ArrayList;

import org.jge.AbstractResource;
import org.jge.EngineException;
import org.jge.EngineKernel;
import org.jge.Profiler.ProfileTimer;
import org.jge.ResourceLocation;
import org.jge.Window;
import org.jge.components.BaseLight;
import org.jge.components.BaseLight.LightCamTrans;
import org.jge.components.Camera;
import org.jge.components.SceneObject;
import org.jge.components.ShadowMapSize;
import org.jge.components.ShadowingInfo;
import org.jge.gpuresources.MappedValues;
import org.jge.maths.Maths;
import org.jge.maths.Matrix4;
import org.jge.maths.Quaternion;
import org.jge.maths.Transform;
import org.jge.maths.Vector3;
import org.jge.render.mesh.Mesh;
import org.jge.render.shaders.Shader;
import org.jge.util.HashMapWithDefault;
import org.jge.util.Log;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL32;
import org.lwjgl.util.glu.GLU;

public class RenderEngine extends MappedValues
{
	private static final Matrix4 bias = new Matrix4().initScale(0.5, 0.5, 0.5).mul(new Matrix4().initTranslation(1, 1, 1));

	private static boolean TEST_DRAW_SHADOW_MAP = false;
	
	private ProfileTimer renderProfileTimer = new ProfileTimer();
	private ProfileTimer windowSyncProfileTimer = new ProfileTimer();
	private Texture renderToTextTarget;
	private Mesh planeMesh;
	private Transform altTransform;
	private Material planeMaterial;
	private Camera altCamera;
	private SceneObject renderToTextCameraObject;
	
	private Camera							  cam;
	private Vector3							 ambientColor;
	private Shader							  ambientShader;

	private ArrayList<Shader>				postFilters;
	private ArrayList<BaseLight>				lights;
	private BaseLight						   activeLight;

	private HashMapWithDefault<String, Integer> samplers;
	public Shader defaultShader;
	private Shader renderToTextShader;
	private Shader shadowMapShader;
	public Shader nullFilterShader;
	public Shader gausBlurFilterShader;
	
	private Matrix4 initMatrix;
	
	private Matrix4 lightMatrix;

	private Texture[] shadowMaps;

	private Texture[] shadowMapTempTargets;

	private Texture renderToTextTargetTemp;

	private boolean renderingShadowMap;

	private Quaternion remplacingColor;

	public RenderEngine()
	{
		glEnable(GL_TEXTURE_2D);
	}
	
	public double displayRenderTime()
	{
		return displayRenderTime(0);
	}
	
	public double displayRenderTime(double divisor)
	{
		return renderProfileTimer.displayAndReset("Render time", divisor);
	}

	public double displayWindowSyncTime()
	{
		return displayWindowSyncTime(0);
	}
	
	public double displayWindowSyncTime(double divisor)
	{
		return windowSyncProfileTimer.displayAndReset("Window sync time", divisor);
	}
	
	public RenderEngine setShadowing(boolean shadowing)
	{
		this.setBoolean("shadowing", shadowing);
		return this;
	}
	
	public RenderEngine setLighting(boolean lighting)
	{
		this.setBoolean("lighting", lighting);
		return this;
	}
	
	public RenderEngine setParallaxDispMapping(boolean parDispMapping)
	{
		this.setBoolean("dispMapping", parDispMapping);
		return this;
	}
	
	public boolean isLightingOn()
	{
		return getBoolean("lighting");
	}
	
	public boolean isShadowingOn()
	{
		return getBoolean("shadowing");
	}
	
	public boolean isParallaxDispMappingOn()
	{
		return getBoolean("dispMapping");
	}
	
	public void init()
	{
		
		try
		{
			this.setBoolean("normalMapping", true);
			this.setParallaxDispMapping(true);
			this.setLighting(true);
			this.setShadowing(true);
			postFilters = new ArrayList<Shader>();
			
			lightMatrix = new Matrix4().initScale(0, 0, 0);
			ambientShader = new Shader(new ResourceLocation("shaders",
					"forward-ambient"));
			defaultShader = new Shader(new ResourceLocation("shaders",
					"basic"));
			renderToTextShader = new Shader(new ResourceLocation("shaders",
					"renderToText"));
			shadowMapShader = new Shader(new ResourceLocation("shaders",
					"shadowMapGen"));
			nullFilterShader = new Shader(new ResourceLocation("shaders",
					"filter-null"));
			gausBlurFilterShader = new Shader(new ResourceLocation("shaders",
					"filter-gausBlur7x1"));
			samplers = new HashMapWithDefault<String, Integer>();
			samplers.setDefault(-1);

			samplers.put("diffuse", 0);
			samplers.put("normalMap", 1);
			samplers.put("dispMap", 2);
			samplers.put("shadowMap", 3);
			samplers.put("filterTexture", 4);

			glClearColor(0, 0, 0, 0);
			glFrontFace(GL_CW);
			glCullFace(GL_BACK);
			glEnable(GL_CULL_FACE);
			glEnable(GL_DEPTH_TEST);
			glEnable(GL32.GL_DEPTH_CLAMP);
			
			glShadeModel(GL_SMOOTH);
			ambientColor = new Vector3(0.75, 0.75, 0.75);

			setVector3("ambient", ambientColor);
			
			shadowMaps = new Texture[ShadowMapSize.values().length];
			shadowMapTempTargets = new Texture[ShadowMapSize.values().length];
			for(int i = 0;i<ShadowMapSize.values().length;i++)
			{
				shadowMaps[i] = new Texture(ShadowMapSize.values()[i].getSize(), ShadowMapSize.values()[i].getSize(), null, GL_TEXTURE_2D, GL_LINEAR, GL_COLOR_ATTACHMENT0, GL_RG32F, GL_RGBA, true);
				shadowMapTempTargets[i] = new Texture(ShadowMapSize.values()[i].getSize(), ShadowMapSize.values()[i].getSize(), null, GL_TEXTURE_2D, GL_LINEAR, GL_COLOR_ATTACHMENT0, GL_RG32F, GL_RGBA, true);
			}

			lights = new ArrayList<BaseLight>();
			
			glClampColor(GL_CLAMP_FRAGMENT_COLOR, GL_FALSE);
			glClampColor(GL_CLAMP_READ_COLOR, GL_FALSE);
			glClampColor(GL_CLAMP_VERTEX_COLOR, GL_FALSE);
			
			double width = (double)Window.getCurrent().getWidth()/1.0;
			double height = (double)Window.getCurrent().getHeight()/1.0;
			
			renderToTextTarget = new Texture((int)width, (int)height,null,GL_TEXTURE_2D,GL_NEAREST,GL30.GL_COLOR_ATTACHMENT0, GL30.GL_RGBA32F, GL_RGBA, false);
			renderToTextTargetTemp = new Texture((int)width, (int)height,null,GL_TEXTURE_2D,GL_NEAREST,GL30.GL_COLOR_ATTACHMENT0, GL30.GL_RGBA32F, GL_RGBA, false);
			planeMaterial = new Material();
			planeMaterial.setFloat("specularIntensity", 1);
			planeMaterial.setFloat("specularPower", 8);
			planeMaterial.setTexture("diffuse", renderToTextTarget);
			altTransform = new Transform();
			altCamera = new Camera(initMatrix = new Matrix4().initIdentity());
			renderToTextCameraObject = new SceneObject().addComponent(altCamera);
			renderToTextCameraObject.getTransform().rotate(new Vector3(0,1,0), Maths.toRadians(180));
			
			altTransform.rotate(new Vector3(1, 0, 0), Maths.toRadians(90));
			altTransform.rotate(new Vector3(0, 1, 0), Maths.toRadians(180));
			planeMesh = new Mesh(EngineKernel.getResourceLoader().getResource(new ResourceLocation("models", "planePrimitive.obj")));
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	public RenderEngine addPostProcessingFilter(Shader filter)
	{
		postFilters.add(filter);
		return this;
	}
	
	public RenderEngine removeAllPostProcessingFilters()
	{
		postFilters.clear();
		return this;
	}
	
	public Camera getCamera()
	{
		return cam;
	}

	public RenderEngine clearBuffers()
	{
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		return this;
	}

	public Texture loadTexture(AbstractResource res) throws EngineException
	{
		return new Texture(res);
	}
	
	public void blurShadowMap(int index, float blurScale)
	{
		Texture text = shadowMaps[index];
		setVector3("blurScale", new Vector3(blurScale/(text.getWidth()), 0, 0));
		applyFilter(gausBlurFilterShader, text, shadowMapTempTargets[index]);
		
		setVector3("blurScale", new Vector3(0, blurScale/(text.getHeight()), 0));
		applyFilter(gausBlurFilterShader, shadowMapTempTargets[index], text);
	}
	
	public void applyFilter(Shader filter, Texture source, Texture dest)
	{
		if(dest == null)
			Window.getCurrent().bindAsRenderTarget();
		else
			dest.bindAsRenderTarget();
		
		Camera temp = cam;
		
		setTexture("filterTexture", source);
	    cam = altCamera;
	    altCamera.getParent().getTransform().setPosition(new Vector3(0, 0, 0));
	    altCamera.getParent().getTransform().setRotation(new Quaternion());
	    altCamera.setProjection(initMatrix);
	    Camera.setCurrent(cam);
	    glClear(GL_DEPTH_BUFFER_BIT);
	    
	    filter.bind();
	    filter.updateUniforms(altTransform, altCamera, planeMaterial, this);
		planeMesh.draw();
		
		cam = temp;
		
		setTexture("filterTexture", null);
		Camera.setCurrent(cam);
	}
	
	public RenderEngine render(SceneObject object, double delta)
	{
		renderProfileTimer.startInvocation();
		setInt("lightNumber", Maths.max(1, lights.size()+1));
		
		renderToTextTarget.bindAsRenderTarget();
		glClearColor(0, 0, 0, 0f);
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

		object.renderAll(ambientShader, getCamera(), delta, this);

		for(BaseLight light : lights)
		{
			if(!light.isEnabled())
				continue;
			int shadowMapIndex = 0;
			activeLight = light;
			ShadowingInfo shadowingInfo = light.getShadowingInfo();
			if(shadowingInfo != null)
			{
				shadowMapIndex = shadowingInfo.getShadowMapSize().ordinal();
			}
			lightMatrix = initMatrix;
			shadowMaps[shadowMapIndex].bindAsRenderTarget();
			setTexture("shadowMap", shadowMaps[shadowMapIndex]);
			setTexture("shadowMapTempTarget", shadowMapTempTargets[shadowMapIndex]);
			glClearColor(0, 0, 0, 0);
			glClear(GL_DEPTH_BUFFER_BIT | GL_COLOR_BUFFER_BIT);
			renderingShadowMap = true;
			if(shadowingInfo != null && isShadowingOn())
			{
				setFloat("shadowLightBleedingReduction", shadowingInfo.getLightBleedingReduction());
				setFloat("shadowVarianceMin", shadowingInfo.getVarianceMin());
				setVector3("shadowTexelSize", new Vector3(1.0f/(double)ShadowMapSize.values()[shadowMapIndex].getSize(),1.0f/(double)ShadowMapSize.values()[shadowMapIndex].getSize(),0));
				altCamera.setProjection(shadowingInfo.getProjection());
				LightCamTrans tr = activeLight.getLightCamTrans(cam);
				altCamera.getParent().getTransform().setPosition(tr.pos);
				altCamera.getParent().getTransform().setRotation(tr.rot);
				
				lightMatrix = bias.mul(altCamera.getViewProjection());
				
				Camera temp = cam;
			    cam = altCamera;
			    Camera.setCurrent(cam);
			    
			    if(shadowingInfo.flipFaces()) glCullFace(GL_FRONT);
				object.renderAll(shadowMapShader, getCamera(), delta, this);
				if(shadowingInfo.flipFaces()) glCullFace(GL_BACK);
				cam = temp;
				Camera.setCurrent(cam);
				
				if(shadowingInfo.getShadowSoftness() != 0.0)
					blurShadowMap(shadowMapIndex, shadowingInfo.getShadowSoftness());
			}
			else
			{
				lightMatrix = new Matrix4().initScale(0, 0, 0);
				
				setFloat("shadowLightBleedingReduction", 0);
				setFloat("shadowVarianceMin", 0.0002f);
			}
			
			renderingShadowMap = false;
			renderToTextTarget.bindAsRenderTarget();
			
			glEnable(GL_BLEND);
			glBlendFunc(GL_ONE, GL_ONE);
			glDepthMask(false);
			glDepthFunc(GL_EQUAL);
			if(light.getShader() != null)
				object.renderAll(light.getShader(), getCamera(), delta, this);
			glDepthFunc(GL_LESS);
			glDepthMask(true);
			glDisable(GL_BLEND);
			
			activeLight = null;
		}
		if(!postFilters.isEmpty())
		{
			int i=0;
			for(Shader filter : postFilters)
			{
				if(i % 2 == 0)
				{
					applyFilter(filter, renderToTextTarget, renderToTextTargetTemp);
				}
				else
				{
					applyFilter(filter, renderToTextTargetTemp, renderToTextTarget);
				}
				i++;
			}
			
			if(i % 2 != 0)
			{
				applyFilter(nullFilterShader, renderToTextTargetTemp, renderToTextTarget);
			}
		}
		
		Window.getCurrent().bindAsRenderTarget();
		Camera temp = cam;
		
	    cam = altCamera;
	    altCamera.getParent().getTransform().setPosition(new Vector3(0, 0, 0));
	    altCamera.getParent().getTransform().setRotation(new Quaternion());
	    altCamera.setProjection(initMatrix);
	    Camera.setCurrent(cam);
	    glClearColor(0.0f, 0.0f, 1f, 1.0f);
	    glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
	    renderToTextShader.bind();
	    renderToTextShader.updateUniforms(altTransform, altCamera, planeMaterial, this);
		planeMesh.draw();

		
		if(TEST_DRAW_SHADOW_MAP)
		{
			glClear(GL_DEPTH_BUFFER);
			glDisable(GL_DEPTH_TEST);
    		planeMaterial.setTexture("diffuse", getTexture("shadowMap"));
    		defaultShader.bind();
    		altTransform.setScale(new Vector3(0.1, 0.1, 0.1));
    		altTransform.setPosition(new Vector3(-0.9,-0.9,0));
    		defaultShader.updateUniforms(altTransform, altCamera, planeMaterial, this);
    		planeMesh.draw();
    		altTransform.setPosition(new Vector3(0,0,0));
    		altTransform.setScale(new Vector3(1,1,1));
    		planeMaterial.setTexture("diffuse", renderToTextTarget);
    		glEnable(GL_DEPTH_TEST);
		}
		
		cam = temp;
		Camera.setCurrent(cam);
		
		renderProfileTimer.endInvocation();
		
		windowSyncProfileTimer.startInvocation();
		windowSyncProfileTimer.endInvocation();
		
		//printIfGLError();
		return this;
	}

	public static void printIfGLError()
	{
		int error = glGetError();
		if(error != GL_NO_ERROR)
		{
			Log.error("Error "+error+": "+GLU.gluErrorString(error));
		}
	}

	public boolean isRenderingShadowMap()
	{
		return renderingShadowMap;
	}
	
	public Matrix4 getLightMatrix()
	{
		return lightMatrix;
	}
	
	public Vector3 getAmbientColor()
	{
		return ambientColor;
	}

	public RenderEngine addLight(BaseLight light)
	{
		lights.add(light);
		return this;
	}

	public BaseLight getActiveLight()
	{
		return activeLight;
	}

	public RenderEngine removeLight(BaseLight light)
	{
		lights.remove(light);
		return this;
	}

	public RenderEngine addCamera(Camera camera)
	{
		this.cam = camera;
		return this;
	}

	public int getSamplerSlot(String name)
	{
		return samplers.get(name);
	}
	
	public void setLightMatrix(Matrix4 m)
	{
		this.lightMatrix = m;
	}

	public RenderEngine setRemplacingColor(Quaternion color)
	{
		this.remplacingColor = color;
		return this;
	}

	public Quaternion getRemplacingColor()
	{
		return remplacingColor;
	}
}
