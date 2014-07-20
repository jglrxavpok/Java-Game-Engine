#version 120

#include "lighting.hs"

uniform DirectionalLight directionalLight;

vec4 calcLightEffect(vec3 normal, vec3 worldPos0)
{
	return calcDirectionalLight(directionalLight, normal, worldPos0);
}

#include "lighting.fsh"