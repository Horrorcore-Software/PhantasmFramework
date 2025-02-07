package com.horrorcore.engine.core.graphics;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.system.MemoryStack;
import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL20.*;

public class BasicShader {
    private final int programId;
    private final int vertexShaderId;
    private final int fragmentShaderId;

    // Uniform locations
    private int modelMatrixLocation;
    private int viewMatrixLocation;
    private int projectionMatrixLocation;
    private int colorLocation;

    public BasicShader() {
        // Create shaders
        programId = glCreateProgram();
        vertexShaderId = glCreateShader(GL_VERTEX_SHADER);
        fragmentShaderId = glCreateShader(GL_FRAGMENT_SHADER);

        // Vertex shader source - transforms vertices and passes normal data
        String vertexShaderSource = """
            #version 330 core
            
            // Input vertex data
            layout (location = 0) in vec3 position;
            layout (location = 1) in vec3 normal;
            
            // Output data to fragment shader
            out vec3 fragNormal;
            out vec3 fragPos;
            
            // Transformation matrices
            uniform mat4 model;
            uniform mat4 view;
            uniform mat4 projection;
            
            void main() {
                // Calculate the position in world space
                fragPos = vec3(model * vec4(position, 1.0));
                
                // Transform normal to world space (excluding translation)
                fragNormal = mat3(transpose(inverse(model))) * normal;
                
                // Transform vertex to clip space
                gl_Position = projection * view * model * vec4(position, 1.0);
            }
            """;

        // Fragment shader source - calculates basic lighting
        String fragmentShaderSource = """
            #version 330 core
            
            in vec3 fragNormal;
            in vec3 fragPos;
            
            uniform vec3 objectColor;
            
            out vec4 fragColor;
            
            void main() {
                // Basic lighting parameters
                vec3 lightDir = normalize(vec3(1.0, 1.0, 1.0));
                vec3 lightColor = vec3(1.0, 1.0, 1.0);
                
                // Ambient lighting
                float ambientStrength = 0.3;
                vec3 ambient = ambientStrength * lightColor;
                
                // Diffuse lighting
                vec3 norm = normalize(fragNormal);
                float diff = max(dot(norm, lightDir), 0.0);
                vec3 diffuse = diff * lightColor;
                
                // Combine lighting with object color
                vec3 result = (ambient + diffuse) * objectColor;
                fragColor = vec4(result, 1.0);
            }
            """;

        // Compile and link shaders
        glShaderSource(vertexShaderId, vertexShaderSource);
        glCompileShader(vertexShaderId);
        checkShaderCompilation(vertexShaderId);

        glShaderSource(fragmentShaderId, fragmentShaderSource);
        glCompileShader(fragmentShaderId);
        checkShaderCompilation(fragmentShaderId);

        glAttachShader(programId, vertexShaderId);
        glAttachShader(programId, fragmentShaderId);
        glLinkProgram(programId);
        checkProgramLinking(programId);

        // Clean up shader objects (they're now part of the program)
        glDetachShader(programId, vertexShaderId);
        glDetachShader(programId, fragmentShaderId);
        glDeleteShader(vertexShaderId);
        glDeleteShader(fragmentShaderId);

        // Get uniform locations
        modelMatrixLocation = glGetUniformLocation(programId, "model");
        viewMatrixLocation = glGetUniformLocation(programId, "view");
        projectionMatrixLocation = glGetUniformLocation(programId, "projection");
        colorLocation = glGetUniformLocation(programId, "objectColor");
    }

    private void checkShaderCompilation(int shaderId) {
        int success = glGetShaderi(shaderId, GL_COMPILE_STATUS);
        if (success == GL_FALSE) {
            String infoLog = glGetShaderInfoLog(shaderId);
            throw new RuntimeException("Shader compilation failed: " + infoLog);
        }
    }

    private void checkProgramLinking(int programId) {
        int success = glGetProgrami(programId, GL_LINK_STATUS);
        if (success == GL_FALSE) {
            String infoLog = glGetProgramInfoLog(programId);
            throw new RuntimeException("Shader program linking failed: " + infoLog);
        }
    }

    public void bind() {
        glUseProgram(programId);
    }

    public void unbind() {
        glUseProgram(0);
    }

    public void setModelMatrix(Matrix4f matrix) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer buffer = stack.mallocFloat(16);
            matrix.get(buffer);
            glUniformMatrix4fv(modelMatrixLocation, false, buffer);
        }
    }

    public void setViewMatrix(Matrix4f matrix) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer buffer = stack.mallocFloat(16);
            matrix.get(buffer);
            glUniformMatrix4fv(viewMatrixLocation, false, buffer);
        }
    }

    public void setProjectionMatrix(Matrix4f matrix) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer buffer = stack.mallocFloat(16);
            matrix.get(buffer);
            glUniformMatrix4fv(projectionMatrixLocation, false, buffer);
        }
    }

    public void setColor(Vector3f color) {
        glUniform3f(colorLocation, color.x, color.y, color.z);
    }

    public void cleanup() {
        unbind();
        glDeleteProgram(programId);
    }
}