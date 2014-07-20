#version 120

varying vec2 texCoord0;
uniform sampler2D R_filterTexture;
uniform float E_tick;
uniform float R_SizeOf_diffuse_Y;

void main()
{
	vec4 color = texture2D(R_filterTexture, texCoord0);
	int nbr = 10;
	float stepHeight = R_SizeOf_diffuse_Y/nbr;
	for(int i = 0;i<nbr;i++)
	{
		float distance = abs(texCoord0.y - (mod(-E_tick+stepHeight*i, R_SizeOf_diffuse_Y)) / R_SizeOf_diffuse_Y);
		if(distance == 0.0)
			distance = 0.000001;
		if(1.0-distance < distance)
		{
			distance = 1.0-distance;
		}
		float ratio = (distance*nbr*3.0);
		if(ratio < 0.4)
			ratio = 0.4;
		if(ratio > 1.0)
			ratio = 1.0;
		color = color * ratio;
	}
	gl_FragColor = color;
}
