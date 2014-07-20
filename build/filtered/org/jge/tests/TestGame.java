package org.jge.tests;

import java.nio.FloatBuffer;

import javax.vecmath.Vector3f;

import com.bulletphysics.collision.shapes.BoxShape;
import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.collision.shapes.ConvexHullShape;

import org.jge.CoreEngine;
import org.jge.EngineException;
import org.jge.ResourceLocation;
import org.jge.Window;
import org.jge.cl.GPUProgram;
import org.jge.cl.GPUProgramObject;
import org.jge.components.Camera;
import org.jge.components.DirectionalLight;
import org.jge.components.FreeLook;
import org.jge.components.FreeMove;
import org.jge.components.MeshRenderer;
import org.jge.components.SceneObject;
import org.jge.components.ShadowMapSize;
import org.jge.components.SpotLight;
import org.jge.game.Game;
import org.jge.game.Input;
import org.jge.maths.Maths;
import org.jge.maths.Quaternion;
import org.jge.maths.Vector3;
import org.jge.phys.PhysicsComponent;
import org.jge.phys.PhysicsEngine;
import org.jge.render.Material;
import org.jge.render.ParticleGenerator;
import org.jge.render.Texture;
import org.jge.render.mesh.Mesh;
import org.jge.sound.Sound;
import org.jge.util.Buffers;
import org.jge.util.Log;

public class TestGame extends Game
{

	public static void main(String[] args)
	{
		try
		{
			new CoreEngine(new TestGame()).start(new Window(900, 500)
					/*.setFullscreen(true)*/);//.setVsync(true));
		}
		catch(EngineException e)
		{
			e.printStackTrace();
		}
	}

	private Material	material;
	private SceneObject spotLightObject;
	private Sound soundTest;
	private SpotLight spotLight;
	private SceneObject cameraObject;
	private Mesh lowMonkeyMesh;

	public void init()
	{
		try
		{
			getRenderEngine().getAmbientColor().set(0.15, 0.15, 0.15);
			material = new Material();
			material.setTexture(
					"diffuse",
					getRenderEngine().loadTexture(
							getResourceLoader().getResource(
									new ResourceLocation("textures",
											"bricks2.png"))));
			material.setTexture(
					"normalMap",
					getRenderEngine().loadTexture(
							getResourceLoader().getResource(
									new ResourceLocation("textures",
											"bricks2_normal.png"))));
			material.setTexture(
					"dispMap",
					getRenderEngine().loadTexture(
							getResourceLoader().getResource(
									new ResourceLocation("textures",
											"bricks2_disp.png"))));
			material.setFloat("specularIntensity", 0.5f);
			material.setFloat("specularPower", 4);
			getWindow().setIcons(
					getResourceLoader().getResource(
							new ResourceLocation("textures", "icon16.png")),
					getResourceLoader().getResource(
							new ResourceLocation("textures", "icon32.png")));
    		Mesh mesh = new Mesh(getResourceLoader().getResource(new ResourceLocation("plane.obj")));
    
    		Material materialBricks = new Material();
    		try
    		{
    			materialBricks.setTexture("diffuse", new Texture(getResourceLoader().getResource(new ResourceLocation("textures", "bricks.png"))));
    			materialBricks.setFloat("specularIntensity", 0.5f);
    			materialBricks.setFloat("specularPower", 4);
    			materialBricks.setTexture(
    					"normalMap",
    					getRenderEngine().loadTexture(
    							getResourceLoader().getResource(
    									new ResourceLocation("textures",
    											"bricks_normal.png"))));
    			materialBricks.setTexture(
    					"dispMap",
    					getRenderEngine().loadTexture(
    							getResourceLoader().getResource(
    									new ResourceLocation("textures",
    											"bricks_disp.png"))));
    		}
    		catch(Exception e1)
    		{
    			e1.printStackTrace();
    		}
    		MeshRenderer renderer = new MeshRenderer(mesh, materialBricks);
    
    		SceneObject plane = new SceneObject();
    		plane.addComponent(renderer);
    
    		addToScene(plane);
    
    		spotLightObject = new SceneObject();
    		spotLight = new SpotLight(new Vector3(1, 1, 1), 0.8f,
    				new Vector3(0, 0, 0.005f), (float)Maths.toRadians(90));
    		spotLightObject.addComponent(spotLight);
    		spotLight.getShadowingInfo().setShadowMapSize(ShadowMapSize._128x128);
    
    		Mesh mesh1 = new Mesh(getResourceLoader().getResource(new ResourceLocation("plane.obj")));
    
    		Mesh mesh2 = new Mesh(getResourceLoader().getResource(new ResourceLocation("plane.obj")));
    
    		SceneObject test1 = new SceneObject().addComponent(new MeshRenderer(
    				mesh1, material));
    		SceneObject test2 = new SceneObject().addComponent(new MeshRenderer(
    				mesh2, material));
    		cameraObject = new SceneObject().addComponent(new Camera(Maths
    				.toRadians(70), (double)Window.getCurrent().getWidth()
    				/ (double)Window.getCurrent().getHeight(), 0.1, 400)).addComponent(new FreeLook(0.5)).addComponent(new FreeMove(0.5));
    
    		addToScene(cameraObject);
    
    		test1.getTransform().getPosition().set(0, 5, 0);
    		test1.getTransform().setRotation(
    				new Quaternion(new Vector3(0, 1, 0), 0.4));
    		test2.getTransform().getPosition().set(0, 0, 2.5*10);
    		test1.addChild(test2);
    
    		addToScene(test1);
    		
//    		camera.addChild(spotLightObject);
    		addToScene(spotLightObject);
    		
    		SceneObject directionalLight = new SceneObject()
    				.addComponent(new DirectionalLight(new Vector3(1, 1, 1), 1f));
    		directionalLight
    				.getTransform().rotate(new Vector3(1, 0, 0), Maths.toRadians(-45)).rotate(new Vector3(0, 1, 0), Maths.toRadians(45));
    		addToScene(directionalLight);
    
    		try
    		{
    			lowMonkeyMesh = new Mesh((getResourceLoader().getResource(new ResourceLocation("models", "lowmonkey.obj"))));
    			for(int i = 0;i<2;i++)
    			{
        			SceneObject monkey = new SceneObject();
        			monkey.getTransform().setPosition(new Vector3(5+i*6, 15, 5));
        			Mesh monkeyMesh = new Mesh((getResourceLoader().getResource(new ResourceLocation("monkey.obj"))));
        			Material material2 = new Material();
        			material2.setTexture("diffuse", new Texture(getResourceLoader().getResource(
        							new ResourceLocation("textures", "bricks.png"))));
        			material2.setTexture("normalMap", new Texture(getResourceLoader().getResource(
							new ResourceLocation("textures", "bricks_normal.png"))));
        			material2.setTexture(
        					"dispMap",
        					getRenderEngine().loadTexture(
        							getResourceLoader().getResource(
        									new ResourceLocation("textures",
        											"bricks_disp.png"))));
        			material2.setFloat("specularIntensity", 0.5f);
        			material2.setFloat("specularPower", 4);
        			monkey.addComponent(new MeshRenderer(monkeyMesh, material2));
        			
        			
        			addToScene(monkey);
        			
//        			monkey.getTransform().scale(2, 2, 2);
        			
        			ConvexHullShape monkeyShape = new ConvexHullShape(PhysicsEngine.toBullet(lowMonkeyMesh));
        			monkey.addComponent(new PhysicsComponent(1, monkeyShape));
    			}
    			
    		}
    		catch(Exception e)
    		{
    			e.printStackTrace();
    		}
    		SceneObject cube = new SceneObject();
			cube.addComponent(new MeshRenderer(new Mesh((getResourceLoader().getResource(new ResourceLocation("models","cube.obj")))), material));
			cube.getTransform().setPosition(new Vector3(8, 15, 12));
			cube.getTransform().rotate(new Vector3(0, 1, 0), Maths.toRadians(-30));
			addToScene(cube);
			cube.getTransform().scale(2, 2, 2);
    		plane.getTransform().scale(4, 4, 4);
    		
    		soundTest = new Sound(getResourceLoader().getResource(new ResourceLocation("sounds", "Jump1.wav")));
//    		soundTest.play();
    		
    		cube.getTransform().rotate(new Vector3(1, 0, 0), Maths.toRadians(45));
    		
    		ParticleGenerator gen = new ParticleGenerator(0,10);
    		gen.getTransform().setPosition(new Vector3(0, 5, 0));
    		addToScene(gen);
    		
//    		cube.addComponent(new SoundSource(soundTest, 1000));
    		
    		GPUProgram program = new GPUProgram(getResourceLoader().getResource(new ResourceLocation("cl", "test.cls")));
    		int size = 10000;
    		float[] aArray = new float[size];
    		float[] bArray = new float[size];
    		float[] result = new float[size];
    		for(int i = 0;i<aArray.length;i++)
    		{
    			aArray[i] = i*i;
    			bArray[i] = i+i;
    		}
    		GPUProgramObject aMem = program.createMemory(aArray);
    		GPUProgramObject bMem = program.createMemory(bArray);
    		GPUProgramObject resultMem = program.createMemory(size*4);

    		long cpuTime = 0;
    		long gpuTime = 0;
    		int passes= 0;
    		
    		program.getData().runKernel("test", new GPUProgramObject[]
    				{
    				aMem,bMem,resultMem,new GPUProgramObject(size)
    				}, size); // Fair play
    		for(int index = 0;index<passes;index++)
    		{
        		long s = System.currentTimeMillis();
        		
        		program.getData().runKernel("test", new GPUProgramObject[]
        				{
        				aMem,bMem,resultMem,new GPUProgramObject(size)
        				}, size);
        		
        		long gpuTime1 = (System.currentTimeMillis()-s);
        		Log.error("("+index+")GPU used "+gpuTime1+ " ms");
    
        		s = System.currentTimeMillis();
        		for(int i = 0;i<size;i++)
        		{
        			result[i] = aArray[i]+bArray[i];
        		}
        		
        		long cpuTime1 = (System.currentTimeMillis()-s);
        		Log.error("("+index+")CPU used "+cpuTime1+ " ms");
        		
        		cpuTime+=cpuTime1;
        		gpuTime+=gpuTime1;
    		}
    		Log.error("CPU average: "+((float)cpuTime/(float)passes)+" to add two arrays of size "+size);
    		Log.error("GPU average: "+((float)gpuTime/(float)passes)+" to add two arrays of size "+size);
    		FloatBuffer w = Buffers.createFloatBuffer(size);
    		program.read(resultMem, w, true);
//    		for(int i = 0; i < w.capacity(); i++)
//    		{
//    			Log.message("GPU result at " + i + " is " + w.get(i));
//    		}
    		
    		CollisionShape planeShape = new BoxShape(new Vector3f(31.5f, 0.12f, 31.5f));
    		plane.addComponent(new PhysicsComponent(0, planeShape));
    		
    		CollisionShape cubeShape = new BoxShape(new Vector3f(2, 2, 2));
    		cube.addComponent(new PhysicsComponent(1, cubeShape));
		}
		catch(Exception e1)
		{
			e1.printStackTrace();
		}
	}

	public void updateGame(double delta)
	{
		if(Input.isKeyDown(Input.KEY_F))
		{
			spotLightObject.getTransform().setPosition(Camera.getCurrent().getParent().getTransform().getTransformedPos());
			spotLightObject.getTransform().setRotation(Camera.getCurrent().getParent().getTransform().getTransformedRotation());
		}
		
		if(Input.isKeyJustPressed(Input.KEY_C))
		{
			if(Maths.floor(Maths.rand()*2) == 0)
			{
        		SceneObject cube = new SceneObject();
    			try
    			{
    				cube.addComponent(new MeshRenderer(new Mesh((getResourceLoader().getResource(new ResourceLocation("models","cube.obj")))), material));
    			}
    			catch(EngineException e)
    			{
    				e.printStackTrace();
    			}
    			catch(Exception e)
    			{
    				e.printStackTrace();
    			}
    			addToScene(cube);
    			cube.getTransform().scale(2, 2, 2);
    
        		cube.getTransform().setPosition(cameraObject.getTransform().getTransformedPos());
    			cube.getTransform().setRotation(cameraObject.getTransform().getTransformedRotation());
    			
    			CollisionShape cubeShape = new BoxShape(new Vector3f(2, 2, 2));
    			cube.addComponent(new PhysicsComponent(100, cubeShape));
			}
			else
			{
				try
				{
    				SceneObject monkey = new SceneObject();
        			Mesh monkeyMesh = new Mesh((getResourceLoader().getResource(new ResourceLocation("monkey.obj"))));
        			Material material2 = new Material();
        			material2.setTexture("diffuse", new Texture(getResourceLoader().getResource(
        							new ResourceLocation("textures", "bricks.png"))));
        			material2.setTexture("normalMap", new Texture(getResourceLoader().getResource(
    						new ResourceLocation("textures", "bricks_normal.png"))));
        			material2.setTexture(
        					"dispMap",
        					getRenderEngine().loadTexture(
        							getResourceLoader().getResource(
        									new ResourceLocation("textures",
        											"bricks_disp.png"))));
        			material2.setFloat("specularIntensity", 0.5f);
        			material2.setFloat("specularPower", 4);
        			monkey.addComponent(new MeshRenderer(monkeyMesh, material2));
        			
        			
        			addToScene(monkey);
        			
    //    			monkey.getTransform().scale(2, 2, 2);
        			monkey.getTransform().setPosition(cameraObject.getTransform().getTransformedPos());
        			monkey.getTransform().setRotation(cameraObject.getTransform().getTransformedRotation());
        			
        			ConvexHullShape monkeyShape = new ConvexHullShape(PhysicsEngine.toBullet(lowMonkeyMesh));
        			monkey.addComponent(new PhysicsComponent(1, monkeyShape));
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
		}
//		spotLight.setEnabled(Input.isKeyDown(Keyboard.KEY_F));
	}

	@Override
	public String getGameName()
	{
		return "Test Game";
	}
}
