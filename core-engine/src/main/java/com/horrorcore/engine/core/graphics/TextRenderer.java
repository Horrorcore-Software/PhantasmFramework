package com.horrorcore.engine.core.graphics;

import org.lwjgl.system.MemoryStack;
import java.nio.FloatBuffer;

import static com.horrorcore.engine.core.ui.Panel.windowHeight;
import static com.horrorcore.engine.core.ui.Panel.windowWidth;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

public class TextRenderer {
    private int shaderProgram;
    private int vao;
    private int vbo;
    private static final float CHAR_WIDTH = 10.0f;  // Width of each character
    private static final float CHAR_HEIGHT = 16.0f; // Height of each character
    private float panelX, panelY;

    public void init() {
        // Create shader program
        shaderProgram = createShaderProgram();

        // Create VAO and VBO for character quads
        vao = glGenVertexArrays();
        vbo = glGenBuffers();

        glBindVertexArray(vao);
        glBindBuffer(GL_ARRAY_BUFFER, vbo);

        // Position attribute
        glVertexAttribPointer(0, 2, GL_FLOAT, false, 4 * Float.BYTES, 0);
        glEnableVertexAttribArray(0);

        // Color attribute
        glVertexAttribPointer(1, 2, GL_FLOAT, false, 4 * Float.BYTES, 2 * Float.BYTES);
        glEnableVertexAttribArray(1);
    }

    private int createShaderProgram() {
        // Vertex shader
        int vertexShader = glCreateShader(GL_VERTEX_SHADER);
        glShaderSource(vertexShader, """
            #version 330 core
            layout (location = 0) in vec2 aPos;
            layout (location = 1) in vec2 aTexCoord;
            uniform vec2 screenSize;
            
            void main() {
                // Convert from panel-local to screen coordinates
                vec2 screenPos = aPos / screenSize;
                gl_Position = vec4(screenPos.x * 2.0 - 1.0, -(screenPos.y * 2.0 - 1.0), 0.0, 1.0);
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
        int shaderProgram = glCreateProgram();
        glAttachShader(shaderProgram, vertexShader);
        glAttachShader(shaderProgram, fragmentShader);
        glLinkProgram(shaderProgram);

        // Clean up
        glDeleteShader(vertexShader);
        glDeleteShader(fragmentShader);

        return shaderProgram;
    }

    public void setPanelPosition(float x, float y) {
        this.panelX = x;
        this.panelY = y;
    }

    public void renderText(String text, float x, float y, float scale, float[] color) {
        System.out.println("Rendering text: " + text + " at " + x + ", " + y);
        System.out.println("Window dimensions: " + windowWidth + "x" + windowHeight);
        glUseProgram(shaderProgram);
        glBindVertexArray(vao);

        // Enable blending for text
        boolean blendEnabled = glIsEnabled(GL_BLEND);
        if (!blendEnabled) {
            glEnable(GL_BLEND);
            glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        }

        int screenSizeLoc = glGetUniformLocation(shaderProgram, "screenSize");
        glUniform2f(screenSizeLoc, windowWidth, windowHeight);

        // Set text color
        glUniform4f(glGetUniformLocation(shaderProgram, "textColor"),
                color[0], color[1], color[2], color[3]);

        try (MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer verticesBuffer = stack.mallocFloat(6 * 4); // 6 vertices per char, 4 components per vertex

            float xpos = x + panelX;
            float ypos = y + panelY; // Add panel's y offset
            for (char c : text.toCharArray()) {
                // Calculate character quad
                float x1 = xpos;
                float x2 = xpos + CHAR_WIDTH * scale;
                float y1 = ypos;
                float y2 = ypos + CHAR_HEIGHT * scale;

                // Two triangles to form a quad
                float[] vertices = {
                        // positions    // texture coords
                        x1, y1,        0.0f, 0.0f,  // bottom left
                        x2, y1,        1.0f, 0.0f,  // bottom right
                        x2, y2,        1.0f, 1.0f,  // top right
                        x1, y1,        0.0f, 0.0f,  // bottom left
                        x2, y2,        1.0f, 1.0f,  // top right
                        x1, y2,        0.0f, 1.0f   // top left
                };

                verticesBuffer.clear();
                verticesBuffer.put(vertices);
                verticesBuffer.flip();

                glBindBuffer(GL_ARRAY_BUFFER, vbo);
                glBufferData(GL_ARRAY_BUFFER, verticesBuffer, GL_DYNAMIC_DRAW);

                glDrawArrays(GL_TRIANGLES, 0, 6);

                xpos += CHAR_WIDTH * scale; // Advance cursor
            }
        }

        // Restore blend state
        if (!blendEnabled) {
            glDisable(GL_BLEND);
        }

        glBindVertexArray(0);
        glUseProgram(0);
    }

    public void cleanup() {
        glDeleteProgram(shaderProgram);
        glDeleteBuffers(vbo);
        glDeleteVertexArrays(vao);
    }
}