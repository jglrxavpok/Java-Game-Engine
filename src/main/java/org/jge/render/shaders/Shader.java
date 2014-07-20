package org.jge.render.shaders;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL32.*;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.jge.AbstractResource;
import org.jge.CoreEngine;
import org.jge.Disposable;
import org.jge.EngineKernel;
import org.jge.ResourceLoader;
import org.jge.ResourceLocation;
import org.jge.RuntimeEngineException;
import org.jge.components.BaseLight;
import org.jge.components.Camera;
import org.jge.components.DirectionalLight;
import org.jge.components.PointLight;
import org.jge.components.SpotLight;
import org.jge.gpuresources.ShaderResource;
import org.jge.maths.Matrix4;
import org.jge.maths.Quaternion;
import org.jge.maths.Transform;
import org.jge.maths.Vector3;
import org.jge.render.Material;
import org.jge.render.RenderEngine;
import org.jge.util.BinaryUtils;
import org.jge.util.Buffers;
import org.jge.util.HashMapWithDefault;
import org.jge.util.Log;
import org.jge.util.Strings;

public class Shader implements Disposable
{

    private ShaderResource resource;
    private HashMap<String, String> uniformTypesMap = new HashMap<String, String>();
    private HashMapWithDefault<String, Integer> uniformsMap = new HashMapWithDefault<String, Integer>();
    private HashMap<Integer, String> sourceCodes = new HashMap<Integer, String>(); 
    
    /**
     * 	e.g. BaseLight --> vec3::color, float::intensity
     */
    private HashMap<String, ArrayList<String>> structs = new HashMap<String, ArrayList<String>>();
	private ResourceLocation res;

    private static HashMap<ResourceLocation, ShaderResource> loadedShaders = new HashMap<ResourceLocation, ShaderResource>();
    
    public Shader(ResourceLocation partialPath)
    {
    	this(partialPath, EngineKernel.getResourceLoader());
    }
    
    public Shader(ResourceLocation partialPath, ResourceLoader resLoader)
    {
        uniformsMap.setDefault(-2);
        this.res = partialPath;
        ShaderResource existingResource = loadedShaders.get(res);
    	
    	if(existingResource != null)
    	{
    		EngineKernel.getDisposer().add(this);
    		resource = existingResource;
    		resource.increaseCounter();
    	}
    	else
    	{
    		resource = new ShaderResource();
    		loadedShaders.put(res, resource);
    		
    		if(resLoader == null)
        		throw new RuntimeEngineException("ResourceLoader can't be null!");
            ResourceLocation vertLoc = new ResourceLocation(partialPath.getFullPath()+".vs");
            ResourceLocation geomLoc = new ResourceLocation(partialPath.getFullPath()+".gs");
            ResourceLocation fragLoc = new ResourceLocation(partialPath.getFullPath()+".fs");
            try
            {
                if(resLoader.doesResourceExists(vertLoc))
                {
                	addVertexShader(resLoader.getResource(vertLoc));
                }
                
                if(resLoader.doesResourceExists(geomLoc))
                {
                	addGeometryShader(resLoader.getResource(geomLoc));
                }
                
                if(resLoader.doesResourceExists(fragLoc))
                {
                	addFragmentShader(resLoader.getResource(fragLoc));
                }
            }
            catch(Exception e)
            {
            	throw new RuntimeEngineException(e);
            }
            compileShader();
    	}

    	loadAllUniforms();
    }
    
    public Shader bind()
    {
        glUseProgram(resource.getProgram());
        return this;
    }
    
    public Shader updateUniforms(Transform transform, Camera camera, Material material, RenderEngine renderEngine)
    {
    	Iterator<String> uniforms = uniformTypesMap.keySet().iterator();
    	while(uniforms.hasNext())
    	{
    		String uniform = uniforms.next();
    		String type = uniformTypesMap.get(uniform);
    		
    		if(uniform.startsWith("T_"))
    		{
    			String actualName = uniform.replace("T_", "");
    			if(actualName.equals("worldTransform") && type.equals("mat4"))
    			{
    				setUniform(uniform, transform.getTransformationMatrix());
    			}
    			else if(actualName.equals("projectedView") && type.equals("mat4"))
    			{
    				setUniform(uniform, camera.getViewProjection());
    			}
    			else
    				throw new RuntimeEngineException("Shader tried to get inexistant component of Transform: "+actualName+" of type "+type);
    		}
    		else if(uniform.startsWith("R_"))
    		{
    			String actualName = uniform.replace("R_", "");
    			if(actualName.equals("lightMatrix"))
    			{
    				setUniform(uniform, renderEngine.getLightMatrix().mul(transform.getTransformationMatrix()));
    			}
    			else if(type.equals("bool"))
    			{
    				if(actualName.equals("hasRemplacementColor"))
    					setUniformb(uniform, renderEngine.getRemplacingColor() != null);
    				else if(actualName.equals("lightingOff")) 
    					setUniformb(uniform, !renderEngine.isLightingOn());
    				else if(actualName.equals("shadowingEnabled"))
    					setUniformb(uniform, renderEngine.isShadowingOn());
    				else if(actualName.equals("parallaxDispMappingEnabled"))
    					setUniformb(uniform, renderEngine.isParallaxDispMappingOn());
    				else if(actualName.equals("normalMappingOn"))
    					setUniformb(uniform, renderEngine.getBoolean("normalMapping")); // TODO
    				else
    				{
    					setUniformb(uniform, renderEngine.getBoolean(actualName));
    				}
    			}
    			else if(type.equals("vec4"))
    			{
    				if(actualName.equals("remplacementColor"))
    				{
    					if(renderEngine.getRemplacingColor() != null)
    						setUniform(uniform, renderEngine.getRemplacingColor());
    				}
    			}
    			else if(type.equals("vec3"))
    			{
    				setUniform(uniform, renderEngine.getVector3(actualName));
    			}
    			else if(type.equals("int"))
    			{
    				setUniformi(uniform, renderEngine.getInt(actualName));
    			}
    			else if(type.equals("float"))
    			{
    				if(actualName.startsWith("SizeOf_"))
    				{
    					String request = actualName.replace("SizeOf_", "");
    					String requestedObject = request.substring(0, request.indexOf("_"));
    					String field = request.substring(request.indexOf("_")+1);
    					if(field.equals("X"))
    					{
    						setUniformf(uniform, material.getTexture(requestedObject).getWidth());
    					}
    					else if(field.equals("Y"))
    					{
    						setUniformf(uniform, material.getTexture(requestedObject).getHeight());
    					}
    					else
    						throw new RuntimeEngineException("Shader tried to get inexistant component size of "+requestedObject+": "+field+" of type "+type);
    				}
    				else
    					setUniformf(uniform, renderEngine.getFloat(actualName));
    			}
    			else if(type.equals("sampler2D"))
    			{
    				renderEngine.getTexture(actualName).bind(renderEngine.getSamplerSlot(actualName));
    				setUniformi(uniform, renderEngine.getSamplerSlot(actualName));
    			}
    			else
    				throw new RuntimeEngineException("Shader tried to get inexistant component of RenderEngine: "+actualName+" of type "+type);
    		}
    		else if(uniform.startsWith("E_"))
    		{
    			String actualName = uniform.replace("E_", "");
    			if(actualName.equals("tick"))
    				setUniformf(uniform, CoreEngine.getCurrent().getTick());
    			
    		}
    		else if(uniform.startsWith("Cam_"))
    		{
    			String actualName = uniform.replace("Cam_", "");
    			if(actualName.equals("eyePos"))
    				setUniform(uniform, camera.getParent().getTransform().getTransformedPos());
    		}
    		else if(type.equals("sampler2D") && renderEngine.getSamplerSlot(uniform) != -1)
			{
				material.getTexture(uniform).bind(renderEngine.getSamplerSlot(uniform));
				setUniformi(uniform, renderEngine.getSamplerSlot(uniform));
			}
    		else if(type.equals("DirectionalLight"))
    		{
    			setUniformDirectionalLight(uniform, (DirectionalLight)renderEngine.getActiveLight());
    		}
    		else if(type.equals("PointLight"))
    		{
    			setUniformPointLight(uniform, (PointLight)renderEngine.getActiveLight());
    		}
    		else if(type.equals("SpotLight"))
    		{
    			setUniformSpotLight(uniform, (SpotLight)renderEngine.getActiveLight());
    		}
    		else if(!uniform.contains("."))
    		{
    			if(type.equals("float"))
    				setUniformf(uniform, material.getFloat(uniform));
    			else if(type.equals("vec3"))
    				setUniform(uniform, material.getVector3(uniform));
    			else
    				throw new RuntimeEngineException("Shader tried to get inexistant component of Material: "+uniform+" of type "+type);
    		}
    	}
        return this;
    }
    
	public Shader setUniform(String uniform, Quaternion q)
	{
		int l = getLocation(uniform);
		glUniform4f(l, (float)q.getX(), (float)q.getY(), (float)q.getZ(), (float)q.getW());
		return this;
	}

	public Shader addVertexShader(AbstractResource res) throws UnsupportedEncodingException
    {
        addShader(res, GL_VERTEX_SHADER);
        return this;
    }
    
    public Shader addFragmentShader(AbstractResource res) throws UnsupportedEncodingException
    {
        addShader(res, GL_FRAGMENT_SHADER);
        return this;
    }
    
    public Shader addGeometryShader(AbstractResource res) throws UnsupportedEncodingException
    {
        addShader(res, GL_GEOMETRY_SHADER);
        return this;
    }
    
    private boolean areOppositesOrEqual(char c0, char c1)
	{
    	if(c0 == '{' && c1 == '}')
    		return true;
    	if(c1 == '{' && c0 == '}')
    		return true;
    	
    	if(c1 == '<' && c0 == '>')
    		return true;
    	if(c0 == '<' && c1 == '>')
    		return true;
    	
    	if(c0 == '(' && c1 == ')')
    		return true;
    	if(c1 == '(' && c0 == ')')
    		return true;
    	
    	if(c0 == '[' && c1 == ']')
    		return true;
    	if(c1 == '[' && c0 == ']')
    		return true;
    	
		return c0 == c1;
	}

	public Shader compileShader()
    {
		loadAttributes();
        glLinkProgram(resource.getProgram());

        if(glGetProgrami(resource.getProgram(), GL_LINK_STATUS) == 0)
        {
            System.err.println(glGetProgramInfoLog(resource.getProgram(), 1024));
            throw new RuntimeEngineException("Wrong shader linking");
        }

        glValidateProgram(resource.getProgram());

        if(glGetProgrami(resource.getProgram(), GL_VALIDATE_STATUS) == 0)
        {
            System.err.println(glGetProgramInfoLog(resource.getProgram(), 1024));
            throw new RuntimeEngineException("Wrong shader validation");
        }
        
        return this;
    }
	
	public void loadAttributes()
	{
		String source = sourceCodes.get(GL_VERTEX_SHADER);
		if(source != null)
		{
			final String ATTRIBUTE_START = "attribute";
			String[] lines = source.split("\r|\n");
			
			int i = 0;
			for(String line : lines)
			{
				line = Strings.removeSpacesAtStart(line).trim();
				if(line.startsWith(ATTRIBUTE_START))
				{
					line = line.substring(ATTRIBUTE_START.length()+1);
					String attributeType = line.substring(0, line.indexOf(' '));
					String attributeName = line.substring(attributeType.length()+1, line.length()-1);
					
					setAttribLocation(attributeName, i++);
				}
			}
		}
	}
	
	public void findAllStructs()
	{
		Iterator<Integer> types = sourceCodes.keySet().iterator();
		while(types.hasNext())
		{
			int type = types.next();
			String source = sourceCodes.get(type);
			findStructs(source);
		}
	}
	
	private void findStructs(String source)
	{
		final String STRUCT_START = "struct";
		int structIndex = source.indexOf(STRUCT_START,0);
		while(structIndex != -1)
		{
			int min = source.indexOf(' ', STRUCT_START.length()+1+structIndex);
			if(min == -1)
				min = source.length();
			if(source.indexOf('\n', STRUCT_START.length()+1+structIndex) < min && source.indexOf('\n', STRUCT_START.length()+1+structIndex) != -1)
			{
				min = source.indexOf('\n', STRUCT_START.length()+1+structIndex);
			}
			if(source.indexOf('\r', STRUCT_START.length()+1+structIndex) < min && source.indexOf('\r', STRUCT_START.length()+1+structIndex) != -1)
			{
				min = source.indexOf('\r', STRUCT_START.length()+1+structIndex);
			}
			if(source.indexOf('{', STRUCT_START.length()+1+structIndex) < min && source.indexOf('{', STRUCT_START.length()+1+structIndex) != -1)
			{
				min = source.indexOf('{', STRUCT_START.length()+1+structIndex);
			}
			String structName = source.substring(STRUCT_START.length()+1+structIndex, min);
			ArrayList<String> componentsList = new ArrayList<String>();
			String inside = source.substring(source.indexOf('{', STRUCT_START.length()+1+structIndex)+1, source.indexOf('}', STRUCT_START.length()+1+structIndex));
			String[] insideLines = inside.split("\n|\r");
			for(String line : insideLines)
			{
				line = Strings.removeSpacesAtStart(line);
				if(line.isEmpty())
					continue;
				String type = line.substring(0, line.indexOf(' '));
				String name = line.substring(type.length()+1, line.indexOf(';'));
				componentsList.add(type+"::"+name);
			}
			
			structs.put(structName, componentsList);

			structIndex = source.indexOf(STRUCT_START, STRUCT_START.length()+1+structIndex);
		}
	}

	public void loadAllUniforms()
	{
		findAllStructs();
		Iterator<Integer> types = sourceCodes.keySet().iterator();
		while(types.hasNext())
		{
			int type = types.next();
			String source = sourceCodes.get(type);
			loadUniforms(source);
		}
	}
	
	public void loadUniforms(String source)
	{
		final String UNIFORM_START = "uniform";
		String[] lines = source.split("\r|\n");
		for(String line : lines)
		{
			line = Strings.removeSpacesAtStart(line).trim();
			if(line.startsWith(UNIFORM_START))
			{
				line = line.substring(UNIFORM_START.length()+1);
				String uniformType = line.substring(0, line.indexOf(' '));
				String uniformName = line.substring(uniformType.length()+1, line.indexOf(';'));
				loadUniform(uniformType, uniformName);
			}
		}
	}

    public void loadUniform(String uniformType, String uniformName)
	{
    	if(structs.containsKey(uniformType))
    	{
    		ArrayList<String> components = structs.get(uniformType);
    		for(String component : components)
    		{
    			String[] tokens = component.split("::");
    			loadUniform(tokens[0], uniformName+"."+tokens[1]);
    		}
    		uniformTypesMap.put(uniformName, uniformType);
    	}
    	else
    	{
    		uniformsMap.put(uniformName, glGetUniformLocation(resource.getProgram(), uniformName));
    		uniformTypesMap.put(uniformName, uniformType);
    	}
	}

    public String getUniformType(String uniformName)
    {
    	return uniformTypesMap.get(uniformName);
    }
    
	private Shader addShader(AbstractResource res, int type)
    {
        int shader = glCreateShader(type);

        if(shader == 0)
        {
            System.err.println("Shader creation failed: Could not find valid memory location when adding shader");
            System.exit(1);
        }

        String text;
		try
		{
			text = BinaryUtils.toString(res.getData());
		}
		catch(UnsupportedEncodingException e)
		{
			throw new RuntimeEngineException(e);
		}
        text = prepareShaderSource(res, text);
    	this.sourceCodes.put(type, text);
        glShaderSource(shader, text);
        glCompileShader(shader);

        if(glGetShaderi(shader, GL_COMPILE_STATUS) == 0)
        {
            System.err.println(glGetShaderInfoLog(shader, 1024));
            throw new RuntimeEngineException("Wrong shader compiling");
        }

        glAttachShader(resource.getProgram(), shader);
        return this;
    }
    
    private String prepareShaderSource(AbstractResource res, String text)
	{
    	StringBuffer finalShader = new StringBuffer();
    	
    	String[] lines = text.split("\n|\r");
    	
    	final String INCLUDE_DIRECTIVE = "#include";
    	//#include "file.vs"
    	int lineIndex = 0;
    	for(String line : lines)
    	{
    		String trimed = Strings.removeSpacesAtStart(line);
    		if(trimed.startsWith(INCLUDE_DIRECTIVE))
    		{
    			String file = trimed.replaceFirst(INCLUDE_DIRECTIVE, "").substring(1);
    			char c = file.charAt(0); // " or < or { or whatever
    			StringBuffer fileNameBuffer = new StringBuffer();
    			for(int i = 1;i<file.length();i++)
    			{
    				if(areOppositesOrEqual(file.charAt(i),c))
    				{
    					break;
    				}
    				else
    				{
    					fileNameBuffer.append(file.charAt(i));
    				}
    				
    				if(i == file.length()-1)
    					throw new RuntimeEngineException("Error while compiling shader, include directive not closed at line "+lineIndex+": "+line);
    			}
    			try
				{
    				AbstractResource res1 = res.getLoader().getResource(
							new ResourceLocation(
									res.getResourceLocation().getDirectParent(),
									fileNameBuffer.toString()));
					finalShader.append(prepareShaderSource(res1, BinaryUtils.toString(res1.getData())))
					.append("\n");
				}
				catch(Exception e)
				{
					throw new RuntimeEngineException(e);
				}
    		}
    		else
    		{
    			finalShader.append(line).append("\n");
    		}
    		
    		lineIndex++;
    	}
    	
    	return finalShader.toString();
	}

	public Shader setUniformi(String uniform, int value)
    {
        int l = getLocation(uniform);
        glUniform1i(l, value);
        return this;
    }
    
    public Shader setUniformf(String uniform, float value)
    {
        int l = getLocation(uniform);
        glUniform1f(l, value);
        return this;
    }
    
    public Shader setUniform(String uniform, Vector3 v)
    {
        int l = getLocation(uniform);
        glUniform3f(l, (float)v.getX(), (float)v.getY(), (float)v.getZ());
        return this;
    }

    public Shader setUniform(String uniform, Matrix4 m)
    {
        int l = getLocation(uniform);
        glUniformMatrix4(l, true, Buffers.createFlippedBuffer(m));
        return this;
    }
    
    private Shader setUniformb(String uniform, boolean b)
   	{
    	int l = getLocation(uniform);
        glUniform1i(l, b ? 1 : 0);
   		return this;
   	}
    
    public int getLocation(String uniform)
    {
        int l = uniformsMap.get(uniform);
        if(l == -2) // In case the uniform wasn't registered for whatever reason
        {
            l = glGetUniformLocation(resource.getProgram(), uniform);
        }
        if(l == -1)
        {
            throw new RuntimeEngineException("No uniform with name: "+uniform);
        }
        else
        	uniformsMap.put(uniform, l);
        return l;
    }
    
    public Shader setAttribLocation(String attrib, int loc)
    {
        glBindAttribLocation(resource.getProgram(), loc, attrib);
        return this;
    }
    
    public Shader setUniformDirectionalLight(String uniform, DirectionalLight directionalLight)
    {
        setUniformBaseLight(uniform+".base", directionalLight);
        setUniform(uniform+".direction", directionalLight.getDirection());
        return this;
    }
    
    public Shader setUniformBaseLight(String uniform, BaseLight base)
    {
        setUniformf(uniform+".intensity", base.getIntensity());
        setUniform(uniform+".color", base.getColor());
        return this;
    }
    
    public Shader setUniformPointLight(String uniform, PointLight pointLight)
    {
        setUniformBaseLight(uniform+".base", pointLight);
        setUniform(uniform+".position", pointLight.getParent().getTransform().getTransformedPos());
        setUniformf(uniform+".range", pointLight.getRange());
        setUniformf(uniform+".atten.constant", (float)pointLight.getAttenuation().getX());
        setUniformf(uniform+".atten.linear", (float)pointLight.getAttenuation().getY());
        setUniformf(uniform+".atten.exponent", (float)pointLight.getAttenuation().getZ());
        return this;
    }
    
    public Shader setUniformSpotLight(String uniform, SpotLight spotLight)
    {
        setUniformPointLight(uniform+".pointLight", spotLight);
        setUniformf(uniform+".cutoff", spotLight.getCutoff());
        setUniform(uniform+".direction", spotLight.getDirection());
        return this;
    }

	public void dispose()
	{
		if(resource.decreaseCounter())
    	{
			resource.dispose();
    		if(res != null)
    			loadedShaders.remove(res);
    	}
	}
}
