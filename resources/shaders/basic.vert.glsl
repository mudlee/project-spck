#version 410 core
#extension GL_ARB_separate_shader_objects: enable

layout (location = 0) in vec3 position;
layout (location = 1) in mat4 ATTR_INS_TRANSFORMATION_MATRIX;

uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;

out gl_PerVertex {
    vec4 gl_Position;
};

layout (location = 0) out vec4 V_COLOR;

void main()
{
    vec4 vertexWorldPosition = ATTR_INS_TRANSFORMATION_MATRIX * vec4(position,1.0);
    gl_Position = projectionMatrix * viewMatrix * vertexWorldPosition;
    V_COLOR = vec4(0.5,0.5,0.5,1.0);
}