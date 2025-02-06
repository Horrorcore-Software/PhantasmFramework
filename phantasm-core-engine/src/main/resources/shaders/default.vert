#version 330 core

// Input vertex data
layout (location = 0) in vec3 position;
layout (location = 1) in vec2 texCoord;
layout (location = 2) in vec3 normal;

// Output data to fragment shader
out vec2 fragTexCoord;
out vec3 fragNormal;
out vec3 fragPos;

// Uniforms for transformations
uniform mat4 model;
uniform mat4 view;
uniform mat4 projection;

void main() {
    // Calculate the final position in clip space
    gl_Position = projection * view * model * vec4(position, 1.0);

    // Pass texture coordinates to fragment shader
    fragTexCoord = texCoord;

    // Transform normal to world space
    fragNormal = mat3(transpose(inverse(model))) * normal;

    // Calculate fragment position in world space
    fragPos = vec3(model * vec4(position, 1.0));
}