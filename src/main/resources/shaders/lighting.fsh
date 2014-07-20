#include "sampling.hs"
varying vec2 texCoord0;
varying mat3 tbnMatrix;

varying vec3 worldPos0;
varying vec3 normal0;
varying vec4 shadowMapCoords0;


uniform sampler2D diffuse;
uniform sampler2D normalMap;
uniform sampler2D dispMap;
uniform sampler2D R_shadowMap;
uniform bool R_shadowingEnabled;
uniform bool R_lightingOff;
uniform bool R_parallaxDispMappingEnabled;
uniform bool R_hasRemplacementColor;
uniform bool R_normalMappingOn;
uniform vec4 R_remplacementColor;

uniform float dispMapScale;
uniform float dispMapBias;

uniform float R_shadowVarianceMin;
uniform float R_shadowLightBleedingReduction;

bool inRange(float val)
{
	return val >= 0 && val <= 1.0;
}

float calcShadowMapEffect(sampler2D shadowMap, vec4 shadowMapCoords)
{
	if(!R_shadowingEnabled)
		return 1.0;
	vec3 shadowMapCoord1 = (shadowMapCoords.xyz/shadowMapCoords.w);
	if(inRange(shadowMapCoord1.x) && inRange(shadowMapCoord1.y) && inRange(shadowMapCoord1.z))
		return sampleVarianceShadowMap(shadowMap, shadowMapCoord1.xy, shadowMapCoord1.z, R_shadowVarianceMin, R_shadowLightBleedingReduction);
	return 1.0f;
}

void main()
{
	vec3 directionToEye = normalize(Cam_eyePos - worldPos0);
	vec2 texCoords = texCoord0;
	if(R_parallaxDispMappingEnabled)
		texCoords = calcParallaxTexCoords(dispMap, tbnMatrix, directionToEye, texCoord0, dispMapScale, dispMapBias);

	vec3 normal = normal0;
	if(R_normalMappingOn)
		normal = normalize( (tbnMatrix * (( (texture2D(normalMap, texCoords).xyz - 0.5 ) * 2.0) )) );

	vec4 lightAmount = vec4(1,1,1,1);
	if(!R_lightingOff)
		lightAmount = calcLightEffect(normal, worldPos0);
	if(R_shadowingEnabled)
		lightAmount = lightAmount * calcShadowMapEffect(R_shadowMap, shadowMapCoords0);
	vec4 color = texture2D(diffuse, texCoords.xy) * lightAmount;
	if(color.a != 0.0 && R_hasRemplacementColor)
		color = R_remplacementColor;
    gl_FragColor = color;
}