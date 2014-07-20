#version 120
#include "sampling.hs"

varying vec2 texCoord0;
varying vec3 worldPos0;
varying mat3 tbnMatrix;

uniform vec3 Cam_eyePos;
uniform sampler2D diffuse;
uniform sampler2D dispMap;
uniform float dispMapScale;
uniform float dispMapBias;
uniform vec3 R_ambient;
uniform bool R_lightingOff;
uniform bool R_parallaxDispMappingEnabled;
uniform bool R_hasRemplacementColor;
uniform vec4 R_remplacementColor;


void main()
{
	vec3 directionToEye = normalize(Cam_eyePos - worldPos0);
	vec2 texCoords = texCoord0;
	if(R_parallaxDispMappingEnabled)
	 texCoords = calcParallaxTexCoords(dispMap, tbnMatrix, directionToEye, texCoord0, dispMapScale, dispMapBias);

	vec4 color = texture2D(diffuse, texCoords);
	if(!R_lightingOff)
		color *= vec4(R_ambient,1.0);

	if(color.a != 0.0 && R_hasRemplacementColor)
		color = R_remplacementColor;
	gl_FragColor = color;
}
