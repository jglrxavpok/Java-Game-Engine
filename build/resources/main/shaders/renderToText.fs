#version 120

varying vec2 texCoord0;
uniform sampler2D diffuse;
uniform int R_lightNumber;

void main()
{
	gl_FragColor = texture2D(diffuse, texCoord0) / R_lightNumber;
}