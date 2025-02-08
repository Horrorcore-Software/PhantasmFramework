package com.horrorcore.engine.core.editor;

import com.horrorcore.engine.core.GameObject;
import com.horrorcore.engine.core.Transform;
import com.horrorcore.engine.core.graphics.TextRenderer;
import org.joml.Vector3f;
import org.lwjgl.system.MemoryStack;

import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL15.*;

public class InspectorPanel {
    private int vaoId;
    private int vboId;
    private int shaderProgram;
    private boolean isInitialized = false;
    private static final float HEADER_HEIGHT = 30.0f;
    private static final float PROPERTY_HEIGHT = 25.0f;
    private static final float PADDING = 10.0f;
    private int currentWidth;  // Store current viewport width
    private int currentHeight; // Store current viewport height

    // Colors for different elements
    private static final float[] BACKGROUND_COLOR = {0.2f, 0.2f, 0.2f, 1.0f};
    private static final float[] HEADER_COLOR = {0.3f, 0.3f, 0.3f, 1.0f};
    private static final float[] PROPERTY_COLOR = {0.25f, 0.25f, 0.25f, 1.0f};
    private static final float[] HIGHLIGHT_COLOR = {0.4f, 0.4f, 0.4f, 1.0f};

    public void init() {
        if (isInitialized) return;

        // Create and bind VAO
        vaoId = glGenVertexArrays();
        glBindVertexArray(vaoId);

        // Create VBO
        vboId = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboId);

        // Initialize shaders
        initializeShader();

        // Setup vertex attributes
        glVertexAttribPointer(0, 2, GL_FLOAT, false, 2 * Float.BYTES, 0);
        glEnableVertexAttribArray(0);

        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindVertexArray(0);

        isInitialized = true;
    }

    private TextRenderer textRenderer;

    public void render(GameObject selectedObject, int viewportWidth, int viewportHeight) {
        if (!isInitialized || selectedObject == null) return;

        this.currentWidth = viewportWidth;
        this.currentHeight = viewportHeight;

        if (textRenderer == null) {
            textRenderer = new TextRenderer();
            textRenderer.init();
        }

        boolean depthTestEnabled = glIsEnabled(GL_DEPTH_TEST);
        glDisable(GL_DEPTH_TEST);

        glUseProgram(shaderProgram);
        glBindVertexArray(vaoId);

        try (MemoryStack stack = MemoryStack.stackPush()) {
            // Set viewport size uniform
            int viewportSizeLoc = glGetUniformLocation(shaderProgram, "viewportSize");
            glUniform2f(viewportSizeLoc, viewportWidth, viewportHeight);

            // Draw background
            drawRect(0, 0, viewportWidth, viewportHeight, BACKGROUND_COLOR);

            // Draw header
            drawRect(0, viewportHeight - HEADER_HEIGHT, viewportWidth, HEADER_HEIGHT, HEADER_COLOR);

            // Render header text
            // Define different colors for different elements
            float[] headerTextColor = {1.0f, 1.0f, 1.0f, 1.0f};      // White for header
            float[] propertyTextColor = {0.9f, 0.9f, 0.9f, 1.0f};    // Slightly dimmer white for properties

            // Render header text
            textRenderer.renderText("Inspector", PADDING, viewportHeight - HEADER_HEIGHT + PADDING, 1.0f, headerTextColor);

            float currentY = viewportHeight - HEADER_HEIGHT - PROPERTY_HEIGHT;

            // Draw object properties
            Transform transform = selectedObject.getTransform();
            Vector3f position = transform.getPosition();
            Vector3f scale = transform.getScale();

            // Draw name property
            drawPropertySection(currentY);
            textRenderer.renderText("Name: " + selectedObject.getName(),
                    PADDING * 2, currentY + PADDING, 1.0f, propertyTextColor);
            currentY -= PROPERTY_HEIGHT;

            // Draw position property
            drawPropertySection(currentY);
            textRenderer.renderText(String.format("Position: (%.1f, %.1f, %.1f)",
                            position.x, position.y, position.z),
                    PADDING * 2, currentY + PADDING, 1.0f, propertyTextColor);
            currentY -= PROPERTY_HEIGHT;

            // Draw scale property
            drawPropertySection(currentY);
            textRenderer.renderText(String.format("Scale: (%.1f, %.1f, %.1f)",
                            scale.x, scale.y, scale.z),
                    PADDING * 2, currentY + PADDING, 1.0f, propertyTextColor);

            // Draw "Selected Object Properties" in header
            drawHeaderText();
        }

        // Restore state
        glBindVertexArray(0);
        glUseProgram(0);
        if (depthTestEnabled) {
            glEnable(GL_DEPTH_TEST);
        }
    }

    private void drawRect(float x, float y, float width, float height, float[] color) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            float[] vertices = {
                    x, y,                    // Bottom left
                    x + width, y,            // Bottom right
                    x + width, y + height,   // Top right
                    x, y + height            // Top left
            };

            FloatBuffer vertexBuffer = stack.mallocFloat(8);
            vertexBuffer.put(vertices).flip();

            glBindBuffer(GL_ARRAY_BUFFER, vboId);
            glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_DYNAMIC_DRAW);

            int colorLoc = glGetUniformLocation(shaderProgram, "color");
            glUniform4f(colorLoc, color[0], color[1], color[2], color[3]);

            glDrawArrays(GL_TRIANGLE_FAN, 0, 4);
        }
    }

    private void drawPropertySection(float y) {
        // Draw property background
        drawRect(PADDING, y, currentWidth - (PADDING * 2), PROPERTY_HEIGHT, PROPERTY_COLOR);
    }

    private void drawHeaderText() {
        // Draw header highlight for better visibility
        drawRect(PADDING, HEADER_HEIGHT - PADDING, HEADER_HEIGHT - 2 * PADDING,
                HEADER_HEIGHT - 2 * PADDING, HIGHLIGHT_COLOR);
    }

    private void initializeShader() {
        // Enhanced vertex shader with position transformation
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
        checkShaderError(vertexShader, "vertex");

        // Fragment shader with color uniform
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
        checkShaderError(fragmentShader, "fragment");

        // Link shader program
        shaderProgram = glCreateProgram();
        glAttachShader(shaderProgram, vertexShader);
        glAttachShader(shaderProgram, fragmentShader);
        glLinkProgram(shaderProgram);
        checkProgramError(shaderProgram);

        // Cleanup
        glDeleteShader(vertexShader);
        glDeleteShader(fragmentShader);
    }

    private void checkShaderError(int shader, String type) {
        int success = glGetShaderi(shader, GL_COMPILE_STATUS);
        if (success == GL_FALSE) {
            String log = glGetShaderInfoLog(shader);
            System.err.println("Shader compilation error (" + type + "): " + log);
        }
    }

    private void checkProgramError(int program) {
        int success = glGetProgrami(program, GL_LINK_STATUS);
        if (success == GL_FALSE) {
            String log = glGetProgramInfoLog(program);
            System.err.println("Shader program linking error: " + log);
        }
    }

    public void cleanup() {
        if (!isInitialized) return;
        glDeleteProgram(shaderProgram);
        glDeleteBuffers(vboId);
        glDeleteVertexArrays(vaoId);
        isInitialized = false;
    }
}