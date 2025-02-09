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
    protected static float windowWidth;  // Renamed for clarity
    protected static float windowHeight;

    public Panel(float x, float y, float width, float height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.backgroundColor = new Vector4f(0.2f, 0.2f, 0.2f, 1.0f);
    }

    public void init() {
        // Create VAO and VBO
        vao = glGenVertexArrays();
        vbo = glGenBuffers();

        // Bind VAO first
        glBindVertexArray(vao);

        // Bind VBO and allocate memory
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferData(GL_ARRAY_BUFFER, 12 * Float.BYTES, GL_DYNAMIC_DRAW);

        // Setup vertex attributes (position)
        glVertexAttribPointer(0, 2, GL_FLOAT, false, 2 * Float.BYTES, 0);
        glEnableVertexAttribArray(0);

        // Unbind
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindVertexArray(0);

        // Create shaders after VAO/VBO setup
        createShaders();

        System.out.println(getClass().getSimpleName() + " initialized with VAO: " + vao + ", VBO: " + vbo);
    }

    private void createShaders() {
        int vertexShader = glCreateShader(GL_VERTEX_SHADER);
        glShaderSource(vertexShader, """
            #version 330 core
            layout (location = 0) in vec2 aPos;
            
            void main() {
                gl_Position = vec4(aPos.x, aPos.y, 0.0, 1.0);
            }
        """);
        glCompileShader(vertexShader);
        checkShaderCompilation(vertexShader, "vertex");

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
        checkShaderCompilation(fragmentShader, "fragment");

        shaderProgram = glCreateProgram();
        glAttachShader(shaderProgram, vertexShader);
        glAttachShader(shaderProgram, fragmentShader);
        glLinkProgram(shaderProgram);
        checkProgramLinking(shaderProgram);

        glDeleteShader(vertexShader);
        glDeleteShader(fragmentShader);
    }

    private void checkShaderCompilation(int shader, String type) {
        int success = glGetShaderi(shader, GL_COMPILE_STATUS);
        if (success != GL_TRUE) {
            String infoLog = glGetShaderInfoLog(shader);
            System.err.println(type + " shader compilation failed: " + infoLog);
        }
    }

    private void checkProgramLinking(int program) {
        int success = glGetProgrami(program, GL_LINK_STATUS);
        if (success != GL_TRUE) {
            String infoLog = glGetProgramInfoLog(program);
            System.err.println("Program linking failed: " + infoLog);
        }
    }

    public void setBackgroundColor(float r, float g, float b, float a) {
        this.backgroundColor.set(r, g, b, a);
    }

    protected void renderBackground() {
        // Safety check
        if (windowWidth <= 0 || windowHeight <= 0) return;

        try (MemoryStack stack = MemoryStack.stackPush()) {
            float left = (x / windowWidth) * 2.0f - 1.0f;
            float right = ((x + width) / windowWidth) * 2.0f - 1.0f;
            float bottom = 1.0f - ((y + height) / windowHeight) * 2.0f;
            float top = 1.0f - (y / windowHeight) * 2.0f;

            // Two triangles for a quad
            float[] vertices = new float[] {
                    // First triangle
                    left, bottom,    // Bottom-left
                    right, bottom,   // Bottom-right
                    right, top,      // Top-right
                    // Second triangle
                    left, bottom,    // Bottom-left
                    right, top,      // Top-right
                    left, top        // Top-left
            };

            // Debug vertices
            System.out.println(String.format("%s vertices: BL(%.2f,%.2f), BR(%.2f,%.2f), TR(%.2f,%.2f), TL(%.2f,%.2f)",
                    getClass().getSimpleName(), left, bottom, right, bottom, right, top, left, top));

            FloatBuffer vertexBuffer = stack.mallocFloat(12);
            vertexBuffer.put(vertices).flip();

            // Use our shader program
            glUseProgram(shaderProgram);

            // Update the background color
            int colorLoc = glGetUniformLocation(shaderProgram, "backgroundColor");
            glUniform4f(colorLoc, backgroundColor.x, backgroundColor.y, backgroundColor.z, backgroundColor.w);

            // Update the vertex data
            glBindVertexArray(vao);
            glBindBuffer(GL_ARRAY_BUFFER, vbo);
            glBufferSubData(GL_ARRAY_BUFFER, 0, vertexBuffer);

            // Draw the triangles
            glDrawArrays(GL_TRIANGLES, 0, 6);

            // Cleanup
            glBindBuffer(GL_ARRAY_BUFFER, 0);
            glBindVertexArray(0);
            glUseProgram(0);
        }
    }

    protected void beginRender() {
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        // Set viewport and scissor
        glViewport((int)x, (int)(windowHeight - y - height), (int)width, (int)height);
        glEnable(GL_SCISSOR_TEST);
        glScissor((int)x, (int)(windowHeight - y - height), (int)width, (int)height);

        // Clear the panel area
        float[] tempColor = new float[4];
        glGetFloatv(GL_COLOR_CLEAR_VALUE, tempColor);
        glClearColor(backgroundColor.x, backgroundColor.y, backgroundColor.z, backgroundColor.w);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        glClearColor(tempColor[0], tempColor[1], tempColor[2], tempColor[3]);

        // Render the actual background
        renderBackground();
    }

    protected void endRender() {
        glDisable(GL_SCISSOR_TEST);
        glDisable(GL_BLEND);
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
        System.out.println("Setting window dimensions: " + width + "x" + height);
        windowWidth = width;
        windowHeight = height;
    }
}