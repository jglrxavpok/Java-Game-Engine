#version 120

void main()
{
	float depth = gl_FragCoord.z;

	float dx = dFdx(depth);
	float dy = dFdy(depth);
	float variance = depth*depth + 0.25*(dx*dx+dy*dy);
	gl_FragColor = vec4(depth, variance,
		0.0,0.0);
}
