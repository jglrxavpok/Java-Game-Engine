#version 120

varying vec2 texCoord0;
uniform sampler2D R_filterTexture;
uniform float R_SizeOf_diffuse_X;
uniform float E_tick;

void main()
{
	vec2 texCoords = texCoord0;
	texCoords.x = texCoords.x+((sin(E_tick / 40.0 + texCoords.y * 20.0) / 2.0 + 0.5) / R_SizeOf_diffuse_X * 15);
	gl_FragColor = texture2D(R_filterTexture, texCoords);
}
