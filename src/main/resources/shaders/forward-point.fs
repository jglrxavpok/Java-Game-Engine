#version 120

#include "lighting.hs"

uniform PointLight pointLight;

vec4 calcLightEffect(vec3 normal, vec3 worldPos0)
{
	return calcPointLight(pointLight, normal, worldPos0);
}

#include "lighting.fsh"