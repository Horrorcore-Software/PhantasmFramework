package com.horrorcore.engine.core.editor;

import com.horrorcore.engine.core.GameObject;
import org.lwjgl.system.MemoryStack;

import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL15.*;

public class InspectorPanel {
    private int vaoId;
    private int vboId;
    private int shaderProgram;
    private static final float[] COLORS = {
            0.2f, 0.2f, 0.2f, 1.0f,  // Background color
            0.25f, 0.25f, 0.25f, 1.0f // Header color
    };

    public InspectorPanel() {
        // Defer initialization until OpenGL context is created
    }

    public void init() {
        initializeShader();
        initializeBuffers();
    }

    private void initializeShader() {
        // Vertex shader
        int vertexShader = glCreateShader(GL_VERTEX_SHADER);
        glShaderSource(vertexShader, """
            #version 330 core
            layout (location = 0) in vec2 position;
            uniform vec2 viewportSize;
            
            void main() {
                vec2 ndc = (position / viewportSize) * 2.0 - 1.0;
                gl_Position = vec4(ndc, 0.0, 1.0);
            }
        """);
        glCompileShader(vertexShader);

        // Fragment shader
        int fragmentShader = glCreateShader(GL_FRAGMENT_SHADER);
        glShaderSource(fragmentShader, """
            #version 330 core
            uniform vec4 color;
            out vec4 FragColor;
            
            void main() {
                FragColor = color;
            }
        """);
        glCompileShader(fragmentShader);

        // Create and link shader program
        shaderProgram = glCreateProgram();
        glAttachShader(shaderProgram, vertexShader);
        glAttachShader(shaderProgram, fragmentShader);
        glLinkProgram(shaderProgram);

        // Clean up shaders
        glDeleteShader(vertexShader);
        glDeleteShader(fragmentShader);
    }

    private void initializeBuffers() {
        vaoId = glGenVertexArrays();
        vboId = glGenBuffers();

        glBindVertexArray(vaoId);
        glBindBuffer(GL_ARRAY_BUFFER, vboId);

        // Position attribute
        glVertexAttribPointer(0, 2, GL_FLOAT, false, 2 * Float.BYTES, 0);
        glEnableVertexAttribArray(0);
    }

    public void render(GameObject selectedObject, int viewportWidth, int viewportHeight) {
        if (selectedObject == null) return;

        // Disable depth testing for UI
        boolean depthTestEnabled = glIsEnabled(GL_DEPTH_TEST);
        glDisable(GL_DEPTH_TEST);

        glUseProgram(shaderProgram);

        try (MemoryStack stack = MemoryStack.stackPush()) {
            // Set viewport size uniform
            int viewportSizeLoc = glGetUniformLocation(shaderProgram, "viewportSize");
            glUniform2f(viewportSizeLoc, viewportWidth, viewportHeight);

            // Background quad vertices
            float[] backgroundVertices = {
                    0.0f, 0.0f,
                    viewportWidth, 0.0f,
                    viewportWidth, viewportHeight,
                    0.0f, viewportHeight
            };

            // Header quad vertices
            float[] headerVertices = {
                    0.0f, viewportHeight - 30,
                    viewportWidth, viewportHeight - 30,
                    viewportWidth, viewportHeight,
                    0.0f, viewportHeight
            };

            // Draw background with proper viewport coordinates
            FloatBuffer vertexBuffer = stack.mallocFloat(8);
            vertexBuffer.put(backgroundVertices).flip();

            // Debug output
            System.out.println("Drawing inspector panel: width=" + viewportWidth + ", height=" + viewportHeight);

            glBindBuffer(GL_ARRAY_BUFFER, vboId);
            glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_DYNAMIC_DRAW);

            int colorLoc = glGetUniformLocation(shaderProgram, "color");
            glUniform4f(colorLoc, COLORS[0], COLORS[1], COLORS[2], COLORS[3]);

            glDrawArrays(GL_TRIANGLE_FAN, 0, 4);

            // Draw header
            vertexBuffer.clear();
            vertexBuffer.put(headerVertices).flip();

            glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_DYNAMIC_DRAW);
            glUniform4f(colorLoc, COLORS[4], COLORS[5], COLORS[6], COLORS[7]);

            glDrawArrays(GL_TRIANGLE_FAN, 0, 4);
        }

        // Reset OpenGL state
        glUseProgram(0);
        if (depthTestEnabled) {
            glEnable(GL_DEPTH_TEST);
        }
    }

    public void cleanup() {
        glDeleteBuffers(vboId);
        glDeleteVertexArrays(vaoId);
        glDeleteProgram(shaderProgram);
    }
}