#version 120

#include "lighting.hs"

uniform SpotLight spotLight;

vec4 calcLightEffect(vec3 normal, vec3 worldPos0)
{
	return calcSpotLight(spotLight, normal, worldPos0);
}

#include "lighting.fsh"