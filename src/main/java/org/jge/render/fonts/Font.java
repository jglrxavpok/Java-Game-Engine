package org.jge.render.fonts;

import java.util.ArrayList;

import org.jge.components.Camera;
import org.jge.maths.Maths;
import org.jge.maths.Quaternion;
import org.jge.maths.Transform;
import org.jge.maths.Vector2;
import org.jge.maths.Vector3;
import org.jge.render.Material;
import org.jge.render.RenderEngine;
import org.jge.render.TextureAtlas;
import org.jge.render.TextureRegion;
import org.jge.render.Vertex;
import org.jge.render.mesh.Mesh;
import org.jge.render.shaders.Shader;

public abstract class Font
{

	private TextureAtlas atlas;
	private String supportedChars;
	private Mesh mesh;
	private Material material;

	public Font(TextureAtlas atlas, String supportedChars)
	{
		this.atlas = atlas;
		this.supportedChars = supportedChars;
		this.mesh = new Mesh();
		this.material = new Material();
		material.setTexture("diffuse", atlas.getTexture());
	}
	
	public void drawString(Shader shader, String text, Transform transform, int color, double xo, double yo, Camera camera, RenderEngine renderEngine)
	{
		shader.bind();
		ArrayList<Vertex> vertices = new ArrayList<Vertex>();
		ArrayList<Integer> indices = new ArrayList<Integer>();
		int currentIndex = 0;
		double x = xo;
		double y = yo;
		
		boolean bold = false;
		boolean italic = false;
		boolean underlined = false;
		
		int toSkip = 0;
		int currentColor = color;
		for(int i = 0;i<text.length();i++)
		{
			if(toSkip > 0)
			{
				toSkip--;
				continue;
			}
			char c = text.charAt(i);
			char next = '\0';
			if(i != text.length()-1)
				next = text.charAt(i+1);
			
			if(c == TextFormatting.BEGINNING)
			{
				TextFormatting format = TextFormatting.fromString(next);
				if(format != null)
				{
					String after = "";
					for(int j = 0;j<format.getCharsAfter();j++)
					{
						after += text.charAt(j+i+2);
					}
					toSkip = format.getCharsAfter() + 1;
					if(format == TextFormatting.RESET)
					{
						bold = false;
						italic = false;
						underlined = false;
						currentColor = color;
					}
					else if(format == TextFormatting.COLOR)
					{
						flush(shader, vertices, indices, currentColor, transform, camera, renderEngine);
						currentColor = (int)Long.parseLong(after, 16);
						currentIndex = 0;
					}
					else if(format == TextFormatting.ITALIC)
					{
						italic = true;
					}
					else if(format == TextFormatting.BOLD)
					{
						bold = true;
					}
					else if(format == TextFormatting.UNDERLINED)
					{
						underlined = true;
					}
					continue;
				}
			}
			
			if(underlined)
			{
				int index = getIndex('_');
				int xPos = index % atlas.getXNbr();
				int yPos = index / atlas.getXNbr();
				TextureRegion region = atlas.getTiles()[xPos][yPos];
				vertices.add(new Vertex(new Vector3(x-2, y, 0), new Vector2(region.getMinU(), region.getMaxV())));
				vertices.add(new Vertex(new Vector3(x+2+getCharWidth(c), y, 0), new Vector2(region.getMaxU(), region.getMaxV())));
				vertices.add(new Vertex(new Vector3(x+2+getCharWidth(c), y+getCharHeight(c), 0), new Vector2(region.getMaxU(), region.getMinV())));
				vertices.add(new Vertex(new Vector3(x-2, y+getCharHeight(c), 0), new Vector2(region.getMinU(), region.getMinV())));
				
				indices.add(currentIndex+0);
				indices.add(currentIndex+2);
				indices.add(currentIndex+3);
				
				indices.add(currentIndex+0);
				indices.add(currentIndex+1);
				indices.add(currentIndex+2);
				
				currentIndex+=4;
			}
			if(c == ' ')
			{
				x+=getCharSpacing(c, next)+getCharWidth(c);
				continue;
			}
			int index = getIndex(c);
			if(index >= 0)
			{
				int xPos = index % atlas.getXNbr();
				int yPos = index / atlas.getXNbr();
				TextureRegion region = atlas.getTiles()[xPos][yPos];
				if(!italic)
				{
    				vertices.add(new Vertex(new Vector3(x, y, 0), new Vector2(region.getMinU(), region.getMaxV())));
    				vertices.add(new Vertex(new Vector3(x+getCharWidth(c), y, 0), new Vector2(region.getMaxU(), region.getMaxV())));
    				vertices.add(new Vertex(new Vector3(x+getCharWidth(c), y+getCharHeight(c), 0), new Vector2(region.getMaxU(), region.getMinV())));
    				vertices.add(new Vertex(new Vector3(x, y+getCharHeight(c), 0), new Vector2(region.getMinU(), region.getMinV())));
				}
				else
				{
					double italicFactor = 2.5;
					vertices.add(new Vertex(new Vector3(x-italicFactor, y, 0), new Vector2(region.getMinU(), region.getMaxV())));
    				vertices.add(new Vertex(new Vector3(x+getCharWidth(c)-italicFactor, y, 0), new Vector2(region.getMaxU(), region.getMaxV())));
    				vertices.add(new Vertex(new Vector3(x+getCharWidth(c), y+getCharHeight(c), 0), new Vector2(region.getMaxU(), region.getMinV())));
    				vertices.add(new Vertex(new Vector3(x+italicFactor, y+getCharHeight(c), 0), new Vector2(region.getMinU(), region.getMinV())));
				}
				
				indices.add(currentIndex+0);
				indices.add(currentIndex+2);
				indices.add(currentIndex+3);
				
				indices.add(currentIndex+0);
				indices.add(currentIndex+1);
				indices.add(currentIndex+2);
				
				currentIndex+=4;
				x+=getCharWidth(c)+getCharSpacing(c, next);
				x = Maths.floor(x);
			}
		}
		flush(shader, vertices, indices, currentColor, transform, camera, renderEngine);
        renderEngine.setRemplacingColor(null);
	}

	private int getIndex(char c)
	{
		if(supportedChars == null)
		{
			return c;
		}
		else
			return supportedChars.indexOf(c);
	}

	private void flush(Shader shader, ArrayList<Vertex> vertices, ArrayList<Integer> indices, int color, Transform transform, Camera camera, RenderEngine renderEngine)
	{
		if(vertices.isEmpty() || indices.isEmpty())
			return;
		Integer[] indicesArray = indices.toArray(new Integer[0]);
        Vertex[] verticesArray = vertices.toArray(new Vertex[0]);
        int[] indicesArrayInt = new int[indicesArray.length];
        for(int i = 0;i<indicesArray.length;i++)
            indicesArrayInt[i] = indicesArray[i];
        mesh.setVertices(verticesArray, indicesArrayInt, false);
        
        int a = color >> 24 & 0xFF;
        int r = color >> 16 & 0xFF;
        int g = color >> 8 & 0xFF;
        int b = color >> 0 & 0xFF;
        Quaternion colorVec = new Quaternion((double)r/255.0, (double)g/255.0, (double)b/255.0, (double)a/255.0);
        renderEngine.setRemplacingColor(colorVec);
        
        shader.updateUniforms(transform, camera, material, renderEngine);
        mesh.draw();
        
        vertices.clear();
        indices.clear();
	}

	public double getCharSpacing(char c, char next)
	{
		return 0;
	}

	public TextureAtlas getAtlas()
	{
		return atlas;
	}

	public String getSupportedChars()
	{
		return supportedChars;
	}
	
	public abstract double getCharWidth(char c);
	
	public abstract double getCharHeight(char c);
	
	public double getTextLength(String text)
	{
		double l = 0;
		for(int i = 0;i<text.length();i++)
		{
			char c = text.charAt(i);
			char next = '\0';
			if(text.length() -1 != i)
				next = text.charAt(i+1);
			l += getCharWidth(c)+getCharSpacing(c, next);
		}
		return l;
	}
}
