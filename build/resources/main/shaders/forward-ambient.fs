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


void main()
{
	vec3 directionToEye = normalize(Cam_eyePos - worldPos0);
	vec2 texCoords = calcParallaxTexCoords(dispMap, tbnMatrix, directionToEye, texCoord0, dispMapScale, dispMapBias);
	gl_FragColor = texture2D(diffuse, texCoords) * vec4(R_ambient,1.0);
}
