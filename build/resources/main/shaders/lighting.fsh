#include "sampling.hs"
varying vec2 texCoord0;
varying mat3 tbnMatrix;

varying vec3 worldPos0;
varying vec4 shadowMapCoords0;

uniform sampler2D diffuse;
uniform sampler2D normalMap;
uniform sampler2D dispMap;
uniform sampler2D R_shadowMap;

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
	vec3 shadowMapCoord1 = (shadowMapCoords.xyz/shadowMapCoords.w);
	if(inRange(shadowMapCoord1.x) && inRange(shadowMapCoord1.y) && inRange(shadowMapCoord1.z))
		return sampleVarianceShadowMap(shadowMap, shadowMapCoord1.xy, shadowMapCoord1.z, R_shadowVarianceMin, R_shadowLightBleedingReduction);
	return 1.0f;
}

void main()
{
	vec3 directionToEye = normalize(Cam_eyePos - worldPos0);
	vec2 texCoords = calcParallaxTexCoords(dispMap, tbnMatrix, directionToEye, texCoord0, dispMapScale, dispMapBias);

	vec3 normal = normalize(tbnMatrix * (texture2D(normalMap, texCoords).xyz / 2.0));

	vec4 lightAmount = calcLightEffect(normalize(normal), worldPos0) * calcShadowMapEffect(R_shadowMap, shadowMapCoords0);

    gl_FragColor = texture2D(diffuse, texCoords.xy) * lightAmount;
}