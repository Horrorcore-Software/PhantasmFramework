package com.horrorcore.engine.core.graphics;

import static org.lwjgl.opengl.GL20.*;

public class LineShader {
    private int programId;
    private int vertexShaderId;
    private int fragmentShaderId;

    public void init() {
        // Create shader program
        programId = glCreateProgram();

        // Create and compile vertex shader
        vertexShaderId = glCreateShader(GL_VERTEX_SHADER);
        glShaderSource(vertexShaderId, """
            #version 330 core
            layout (location = 0) in vec2 position;
            uniform vec2 viewportSize;
            
            void main() {
                // Convert pixel coordinates to normalized device coordinates
                vec2 ndc = (position / viewportSize) * 2.0 - 1.0;
                gl_Position = vec4(ndc, 0.0, 1.0);
            }
        """);
        glCompileShader(vertexShaderId);

        // Create and compile fragment shader
        fragmentShaderId = glCreateShader(GL_FRAGMENT_SHADER);
        glShaderSource(fragmentShaderId, """
            #version 330 core
            uniform vec3 color;
            out vec4 fragColor;
            
            void main() {
                fragColor = vec4(color, 1.0);
            }
        """);
        glCompileShader(fragmentShaderId);

        // Link shaders into program
        glAttachShader(programId, vertexShaderId);
        glAttachShader(programId, fragmentShaderId);
        glLinkProgram(programId);

        // Clean up shader objects
        glDetachShader(programId, vertexShaderId);
        glDetachShader(programId, fragmentShaderId);
        glDeleteShader(vertexShaderId);
        glDeleteShader(fragmentShaderId);
    }

    public void use() {
        glUseProgram(programId);
    }

    public void setViewportSize(float width, float height) {
        int location = glGetUniformLocation(programId, "viewportSize");
        glUniform2f(location, width, height);
    }

    public void setColor(float r, float g, float b) {
        int location = glGetUniformLocation(programId, "color");
        glUniform3f(location, r, g, b);
    }

    public void cleanup() {
        glDeleteProgram(programId);
    }
}