#version 120
attribute vec3 position;
attribute vec2 texCoord;

varying vec2 texCoord0;

uniform mat4 T_worldTransform;
uniform mat4 T_projectedView;

void main()
{
    gl_Position = T_projectedView * T_worldTransform * vec4(position, 1.0);
    texCoord0 = texCoord;
}