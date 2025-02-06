#version 330 core

// Input from vertex shader
in vec2 fragTexCoord;
in vec3 fragNormal;
in vec3 fragPos;

// Output color
out vec4 fragColor;

// Material properties
uniform sampler2D textureDiffuse;
uniform vec3 materialAmbient = vec3(0.2);
uniform vec3 materialDiffuse = vec3(0.8);
uniform vec3 materialSpecular = vec3(1.0);
uniform float materialShininess = 32.0;

// Light properties
uniform vec3 lightPos = vec3(10.0, 10.0, 10.0);
uniform vec3 lightColor = vec3(1.0);
uniform vec3 viewPos;

void main() {
    // Ambient lighting
    vec3 ambient = materialAmbient * lightColor;

    // Diffuse lighting
    vec3 norm = normalize(fragNormal);
    vec3 lightDir = normalize(lightPos - fragPos);
    float diff = max(dot(norm, lightDir), 0.0);
    vec3 diffuse = diff * materialDiffuse * lightColor;

    // Specular lighting
    vec3 viewDir = normalize(viewPos - fragPos);
    vec3 reflectDir = reflect(-lightDir, norm);
    float spec = pow(max(dot(viewDir, reflectDir), 0.0), materialShininess);
    vec3 specular = spec * materialSpecular * lightColor;

    // Combine results with texture
    vec4 texColor = texture(textureDiffuse, fragTexCoord);
    vec3 result = (ambient + diffuse + specular) * texColor.rgb;

    // Final color
    fragColor = vec4(result, texColor.a);
}