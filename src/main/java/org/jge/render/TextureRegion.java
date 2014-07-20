package org.jge.render;

public class TextureRegion
{

	private Texture texture;
	private double minU;
	private double minV;
	private double maxU;
	private double maxV;

	public TextureRegion(Texture texture, double minU, double minV, double maxU, double maxV)
	{
		this.texture = texture;
		this.minU = minU;
		this.minV = minV;
		this.maxU = maxU;
		this.maxV = maxV;
	}

	public Texture getTexture()
	{
		return texture;
	}

	public double getMinU()
	{
		return minU;
	}

	public double getMinV()
	{
		return minV;
	}

	public double getMaxU()
	{
		return maxU;
	}

	public double getMaxV()
	{
		return maxV;
	}
}
