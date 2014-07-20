#version 120

attribute vec3 position;

uniform mat4 T_worldTransform;
uniform mat4 T_projectedView;

void main()
{
	gl_Position = T_projectedView * T_worldTransform * vec4(position, 1.0);
}