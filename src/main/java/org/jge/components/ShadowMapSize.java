package org.jge.components;

public enum ShadowMapSize
{
	_2x2(2), _4x4(4),_8x8(8),_16x16(16),_32x32(32),_64x64(64),_128x128(128),_256x256(256),
	_512x512(512),_1024x1024(1024),_2048x2048(2048);
	
	private int size;

	ShadowMapSize(int size)
	{
		this.size = size;
	}
	
	public int getSize()
	{
		return size;
	}
}
