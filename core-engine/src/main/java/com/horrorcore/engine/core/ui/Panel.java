package com.horrorcore.engine.core.ui;

import com.horrorcore.engine.core.GameObject;
import com.horrorcore.engine.core.graphics.Camera;
import com.horrorcore.engine.core.Scene;
import com.horrorcore.engine.core.graphics.TextRenderer;
import org.joml.Vector4f;
import org.lwjgl.system.MemoryStack;

import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

public abstract class Panel {
    protected float x, y, width, height;
    protected Vector4f backgroundColor;
    protected int vao;
    protected int vbo;
    protected int shaderProgram;
    protected static float currentWidth;  // Window width
    protected static float currentHeight;

    public Panel(float x, float y, float width, float height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.backgroundColor = new Vector4f(0.2f, 0.2f, 0.2f, 1.0f);
    }

    public void init() {
        // Create VAO and VBO for the panel background
        vao = glGenVertexArrays();
        vbo = glGenBuffers();

        // Create and compile shaders
        createShaders();

        // Set up vertex attributes
        glBindVertexArray(vao);
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glVertexAttribPointer(0, 2, GL_FLOAT, false, 0, 0);
        glEnableVertexAttribArray(0);
    }

    private void createShaders() {
        // Vertex shader for panel background
        int vertexShader = glCreateShader(GL_VERTEX_SHADER);
        glShaderSource(vertexShader, """
            #version 330 core
            layout (location = 0) in vec2 aPos;
            uniform vec2 screenSize;
            
            void main() {
                vec2 normalizedPos = aPos / screenSize;  // Convert to 0-1 range
                normalizedPos = normalizedPos * 2.0 - 1.0;  // Convert to -1 to 1 range
                normalizedPos.y = -normalizedPos.y;  // Flip Y coordinate
                gl_Position = vec4(normalizedPos, 0.0, 1.0);
            }
        """);
        glCompileShader(vertexShader);

        // Fragment shader for panel background
        int fragmentShader = glCreateShader(GL_FRAGMENT_SHADER);
        glShaderSource(fragmentShader, """
            #version 330 core
            uniform vec4 backgroundColor;
            out vec4 FragColor;
            
            void main() {
                FragColor = backgroundColor;
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

    public void setBackgroundColor(float r, float g, float b, float a) {
        this.backgroundColor.set(r, g, b, a);
    }

    protected void renderBackground() {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            // Define quad vertices using window coordinates
            float[] vertices = {
                    x, y,                    // Bottom-left
                    x + width, y,            // Bottom-right
                    x + width, y + height,   // Top-right
                    x, y + height            // Top-left
            };

            FloatBuffer vertexBuffer = stack.mallocFloat(8);
            vertexBuffer.put(vertices).flip();

            // Update vertex buffer
            glBindBuffer(GL_ARRAY_BUFFER, vbo);
            glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_STATIC_DRAW);

            // Use shader and set uniforms
            glUseProgram(shaderProgram);

            // Pass the full window size, not just panel size
            glUniform2f(glGetUniformLocation(shaderProgram, "screenSize"),
                    currentWidth, currentHeight);
            glUniform4f(glGetUniformLocation(shaderProgram, "backgroundColor"),
                    backgroundColor.x, backgroundColor.y, backgroundColor.z, backgroundColor.w);

            // Draw background quad
            glBindVertexArray(vao);
            glDrawArrays(GL_TRIANGLE_FAN, 0, 4);
            glBindVertexArray(0);
            glUseProgram(0);
        }
    }

    protected void beginRender() {
        // Set viewport
        glViewport((int)x, (int)y, (int)width, (int)height);

        // Enable scissor test to clip rendering to panel area
        glEnable(GL_SCISSOR_TEST);
        glScissor((int)x, (int)y, (int)width, (int)height);

        // Render panel background
        renderBackground();
    }

    protected void endRender() {
        glDisable(GL_SCISSOR_TEST);
    }

    public void setDimensions(float x, float y, float width, float height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;

        // Debug output
        System.out.println(getClass().getSimpleName() + " dimensions: x=" + x + ", y=" + y +
                ", width=" + width + ", height=" + height);
    }

    public abstract void render();

    public void cleanup() {
        glDeleteBuffers(vbo);
        glDeleteVertexArrays(vao);
        glDeleteProgram(shaderProgram);
    }

    public static void setWindowDimensions(float width, float height) {
        currentWidth = width;
        currentHeight = height;
    }
}