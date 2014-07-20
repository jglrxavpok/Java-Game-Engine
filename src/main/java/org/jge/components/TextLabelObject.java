package org.jge.components;

import org.jge.render.RenderEngine;
import org.jge.render.fonts.Font;
import org.jge.render.shaders.Shader;

public class TextLabelObject extends SceneObject
{

	private Font font;
	private String text;
	private int color;
	private double xo;
	private double yo;

	public TextLabelObject(Font font, String text, int argbcolor)
	{
		this.font = font;
		this.text = text;
		this.color = argbcolor;
	}

	public void render(Shader shader, Camera cam, double delta, RenderEngine engine)
	{
		font.drawString(shader, text, getTransform(), color, xo,yo, cam, engine);
	}
	
	public String getText()
	{
		return text;
	}
	
	public TextLabelObject setText(String text)
	{
		this.text = text;
		return this;
	}
	
	public int getColor()
	{
		return color;
	}
	
	public TextLabelObject setColor(int color)
	{
		this.color = color;
		return this;
	}
	
	public TextLabelObject setOffsetY(double yo)
	{
		this.yo = yo;
		return this;
	}
	
	public TextLabelObject setOffsetX(double xo)
	{
		this.xo = xo;
		return this;
	}
	
	public boolean receivesShadows()
	{
		return false;
	}
}
