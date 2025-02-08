package com.horrorcore.engine.core.graphics;

import org.lwjgl.system.MemoryStack;
import java.nio.FloatBuffer;
import static org.lwjgl.opengl.GL30.*;

public class TextRenderer {
    private int vaoId;
    private int vboId;
    private int shaderProgram;
    private boolean isInitialized = false;

    public void init() {
        if (isInitialized) return;

        // Create VAO and VBO
        vaoId = glGenVertexArrays();
        vboId = glGenBuffers();

        // Create and compile shaders
        initShaders();

        isInitialized = true;
    }

    public void renderText(String text, float x, float y, float scale, float[] color) {
        if (!isInitialized) return;

        glUseProgram(shaderProgram);
        glBindVertexArray(vaoId);

        // For each character, create a quad
        float xpos = x;
        for (char c : text.toCharArray()) {
            renderCharacter(c, xpos, y, scale, color);
            xpos += 8.0f * scale; // Simple fixed-width character spacing
        }

        glBindVertexArray(0);
        glUseProgram(0);
    }

    private void renderCharacter(char c, float x, float y, float scale, float[] color) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            // Simple quad for each character
            float[] vertices = {
                    x, y,
                    x + 8.0f * scale, y,
                    x + 8.0f * scale, y + 12.0f * scale,
                    x, y + 12.0f * scale
            };

            FloatBuffer vertexBuffer = stack.mallocFloat(8);
            vertexBuffer.put(vertices).flip();

            glBindBuffer(GL_ARRAY_BUFFER, vboId);
            glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_DYNAMIC_DRAW);

            int colorLoc = glGetUniformLocation(shaderProgram, "textColor");
            glUniform4f(colorLoc, color[0], color[1], color[2], color[3]);

            glDrawArrays(GL_TRIANGLE_FAN, 0, 4);
        }
    }

    private void initShaders() {
        // Vertex shader
        int vertexShader = glCreateShader(GL_VERTEX_SHADER);
        glShaderSource(vertexShader, """
            #version 330 core
            layout (location = 0) in vec2 aPos;
            uniform vec2 viewportSize;
            
            void main() {
                vec2 normalizedPos = (aPos / viewportSize) * 2.0 - 1.0;
                gl_Position = vec4(normalizedPos, 0.0, 1.0);
            }
        """);
        glCompileShader(vertexShader);

        // Fragment shader
        int fragmentShader = glCreateShader(GL_FRAGMENT_SHADER);
        glShaderSource(fragmentShader, """
            #version 330 core
            uniform vec4 textColor;
            out vec4 FragColor;
            
            void main() {
                FragColor = textColor;
            }
        """);
        glCompileShader(fragmentShader);

        // Link shaders
        shaderProgram = glCreateProgram();
        glAttachShader(shaderProgram, vertexShader);
        glAttachShader(shaderProgram, fragmentShader);
        glLinkProgram(shaderProgram);

        // Cleanup
        glDeleteShader(vertexShader);
        glDeleteShader(fragmentShader);
    }

    public void cleanup() {
        if (!isInitialized) return;
        glDeleteProgram(shaderProgram);
        glDeleteBuffers(vboId);
        glDeleteVertexArrays(vaoId);
        isInitialized = false;
    }
}