package org.jge.render;

import org.jge.Disposable;
import org.jge.util.Log;

public class TextureAtlas implements Disposable
{

	private Texture texture;
	private int tileWidth;
	private int tileHeight;
	private int xSpacing;
	private int ySpacing;
	
	/**
	 *  [Column][Row]
	 */
	private TextureRegion[][] tiles;
	private int xNbr;
	private int yNbr;

	public TextureAtlas(Texture texture, int tileWidth, int tileHeight)
	{
		this(texture, tileWidth, tileHeight, 0,0);
	}
	
	public TextureAtlas(Texture texture, int tileWidth, int tileHeight, int xSpacing, int ySpacing)
	{
		this.texture = texture;
		this.tileWidth = tileWidth;
		this.tileHeight = tileHeight;
		this.xSpacing = xSpacing;
		this.ySpacing = ySpacing;
		
		for(int i = 0;i<texture.getWidth();i+=tileWidth+xSpacing)
		{
			xNbr++;
		}
		
		for(int i = 0;i<texture.getHeight();i+=tileHeight+ySpacing)
		{
			yNbr++;
		}
		tiles = new TextureRegion[xNbr][yNbr];
		for(int x = 0;x<xNbr;x++)
		{
			for(int y = 0;y<yNbr;y++)
			{
				double minU = (x*(tileWidth+xSpacing))/(double)texture.getWidth();
				double minV = (y*(tileHeight+ySpacing))/(double)texture.getHeight();
				
				double maxU = (x*(tileWidth+xSpacing)+(tileWidth+xSpacing))/(double)texture.getWidth();
				double maxV = (y*(tileHeight+ySpacing)+(tileHeight+ySpacing))/(double)texture.getHeight();
				
				tiles[x][y] = new TextureRegion(texture, minU, minV, maxU, maxV);
			}
		}
	}
	
	/**
	 *  [Column][Row]
	 */
	public TextureRegion[][] getTiles()
	{
		return tiles;
	}
	
	public void dispose()
	{
		texture.dispose();
	}

	public Texture getTexture()
	{
		return texture;
	}

	public int getTileWidth()
	{
		return tileWidth;
	}

	public int getTileHeight()
	{
		return tileHeight;
	}

	public int getXSpacing()
	{
		return xSpacing;
	}

	public int getYSpacing()
	{
		return ySpacing;
	}

	public int getXNbr()
	{
		return xNbr;
	}

	public int getYNbr()
	{
		return yNbr;
	}

}
