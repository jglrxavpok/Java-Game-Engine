package org.jge.components;

import org.jge.maths.Matrix4;

public class ShadowingInfo
{
	private Matrix4 projection;
	private boolean flipFaces;
	private float varianceMin;
	private float shadowSoftness;
	private float lightBleedingReduction;
	private ShadowMapSize shadowMapSize;

	public ShadowingInfo(Matrix4 projection)
	{
		this.projection = projection;
		this.varianceMin = 0.00000002f;
		this.shadowSoftness = 0.75f;
		this.lightBleedingReduction = 0.9f;
		this.shadowMapSize = ShadowMapSize._1024x1024;
	}

	public Matrix4 getProjection()
	{
		return projection;
	}
	
	public boolean flipFaces()
	{
		return flipFaces;
	}
	
	public ShadowingInfo flipFaces(boolean cull)
	{
		this.flipFaces = cull;
		return this;
	}

	public boolean isFlipFaces()
	{
		return flipFaces;
	}

	public float getVarianceMin()
	{
		return varianceMin;
	}

	public ShadowingInfo setVarianceMin(float varianceMin)
	{
		this.varianceMin = varianceMin;
		return this;
	}

	public float getShadowSoftness()
	{
		return shadowSoftness;
	}

	public ShadowingInfo setShadowSoftness(float shadowSoftness)
	{
		this.shadowSoftness = shadowSoftness;
		return this;
	}

	public float getLightBleedingReduction()
	{
		return lightBleedingReduction;
	}

	public ShadowingInfo setLightBleedingReduction(float lightBleedingReduction)
	{
		this.lightBleedingReduction = lightBleedingReduction;
		return this;
	}

	public ShadowingInfo setProjection(Matrix4 projection)
	{
		this.projection = projection;
		return this;
	}
	
	public ShadowingInfo setShadowMapSize(ShadowMapSize size)
	{
		this.shadowMapSize = size;
		return this;
	}
	
	public ShadowMapSize getShadowMapSize()
	{
		return shadowMapSize;
	}
}

