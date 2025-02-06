package com.horrorcore.engine.core;

import com.horrorcore.engine.core.graphics.Camera;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import java.nio.FloatBuffer;
import org.lwjgl.system.MemoryStack;

import static org.lwjgl.opengl.GL30.*;

public class Scene {
    private int gridVAO;
    private int gridVBO;
    private int gridShader;
    private Matrix4f projectionMatrix;
    private Matrix4f viewMatrix;

    // Grid configuration
    private static final int GRID_SIZE = 20;  // Grid will extend from -10 to +10 on X and Z
    private static final float GRID_SPACING = 1.0f;
    private static final Vector3f GRID_COLOR = new Vector3f(0.5f, 0.5f, 0.5f);

    public Scene() {
        projectionMatrix = new Matrix4f();
        viewMatrix = new Matrix4f();
        createGrid();
        initializeShader();
    }

    private void createGrid() {
        // Create lines for a simple grid on the XZ plane
        float[] gridVertices = generateGridVertices();

        try (MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer vertexBuffer = stack.mallocFloat(gridVertices.length);
            vertexBuffer.put(gridVertices).flip();

            gridVAO = glGenVertexArrays();
            gridVBO = glGenBuffers();

            glBindVertexArray(gridVAO);
            glBindBuffer(GL_ARRAY_BUFFER, gridVBO);
            glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_STATIC_DRAW);

            // Position attribute
            glVertexAttribPointer(0, 3, GL_FLOAT, false, 3 * Float.BYTES, 0);
            glEnableVertexAttribArray(0);
        }
    }

    private float[] generateGridVertices() {
        // Calculate number of lines and vertices needed
        int numLines = (GRID_SIZE * 2 + 1) * 2; // Lines in both X and Z directions
        float[] vertices = new float[numLines * 6]; // 2 points per line, 3 coordinates per point

        int idx = 0;
        float halfSize = GRID_SIZE * GRID_SPACING;

        // Create X-axis parallel lines
        for (int i = -GRID_SIZE; i <= GRID_SIZE; i++) {
            float pos = i * GRID_SPACING;
            // Line start
            vertices[idx++] = -halfSize;  // x
            vertices[idx++] = 0.0f;       // y
            vertices[idx++] = pos;        // z
            // Line end
            vertices[idx++] = halfSize;   // x
            vertices[idx++] = 0.0f;       // y
            vertices[idx++] = pos;        // z
        }

        // Create Z-axis parallel lines
        for (int i = -GRID_SIZE; i <= GRID_SIZE; i++) {
            float pos = i * GRID_SPACING;
            // Line start
            vertices[idx++] = pos;        // x
            vertices[idx++] = 0.0f;       // y
            vertices[idx++] = -halfSize;  // z
            // Line end
            vertices[idx++] = pos;        // x
            vertices[idx++] = 0.0f;       // y
            vertices[idx++] = halfSize;   // z
        }

        return vertices;
    }

    private void initializeShader() {
        // Create and compile vertex shader
        int vertexShader = glCreateShader(GL_VERTEX_SHADER);
        glShaderSource(vertexShader, """
            #version 330 core
            layout (location = 0) in vec3 aPos;
            uniform mat4 projection;
            uniform mat4 view;
            void main() {
                gl_Position = projection * view * vec4(aPos, 1.0);
            }
        """);
        glCompileShader(vertexShader);

        // Create and compile fragment shader
        int fragmentShader = glCreateShader(GL_FRAGMENT_SHADER);
        glShaderSource(fragmentShader, """
            #version 330 core
            uniform vec3 gridColor;
            out vec4 FragColor;
            void main() {
                FragColor = vec4(gridColor, 1.0);
            }
        """);
        glCompileShader(fragmentShader);

        // Create shader program
        gridShader = glCreateProgram();
        glAttachShader(gridShader, vertexShader);
        glAttachShader(gridShader, fragmentShader);
        glLinkProgram(gridShader);

        // Clean up shader objects
        glDeleteShader(vertexShader);
        glDeleteShader(fragmentShader);
    }

    public void render(Camera camera) {
        glUseProgram(gridShader);

        // Set shader uniforms
        try (MemoryStack stack = MemoryStack.stackPush()) {
            // Update view matrix for a simple overhead view
            Matrix4f viewMatrix = camera.getViewMatrix();

            // Upload matrices to shader
            int projLoc = glGetUniformLocation(gridShader, "projection");
            int viewLoc = glGetUniformLocation(gridShader, "view");
            int colorLoc = glGetUniformLocation(gridShader, "gridColor");

            FloatBuffer matBuffer = stack.mallocFloat(16);

            projectionMatrix.get(matBuffer);
            glUniformMatrix4fv(projLoc, false, matBuffer);

            viewMatrix.get(matBuffer);
            glUniformMatrix4fv(viewLoc, false, matBuffer);

            glUniform3f(colorLoc, GRID_COLOR.x, GRID_COLOR.y, GRID_COLOR.z);
        }

        // Draw the grid
        glBindVertexArray(gridVAO);
        glDrawArrays(GL_LINES, 0, (GRID_SIZE * 2 + 1) * 4);
        glBindVertexArray(0);
    }

    public void cleanup() {
        glDeleteVertexArrays(gridVAO);
        glDeleteBuffers(gridVBO);
        glDeleteProgram(gridShader);
    }

    public void setAspectRatio(float ratio) {
        projectionMatrix.identity()
                .perspective((float) Math.toRadians(45.0f), ratio, 0.1f, 100.0f);
    }
}