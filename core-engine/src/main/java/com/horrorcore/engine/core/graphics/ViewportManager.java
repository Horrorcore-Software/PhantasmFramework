package com.horrorcore.engine.core.graphics;

import org.joml.Vector4f;
import java.nio.FloatBuffer;
import org.lwjgl.system.MemoryStack;

import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;

public class ViewportManager {
    private static final float SCENE_VIEW_WIDTH_PERCENT = 0.7f;
    private static final float HIERARCHY_WIDTH_PERCENT = 0.15f;
    private static final float INSPECTOR_WIDTH_PERCENT = 0.15f;

    private Vector4f sceneViewport;
    private Vector4f hierarchyViewport;
    private Vector4f inspectorViewport;
    private int currentWidth;
    private int currentHeight;

    private LineShader lineShader;
    private int vaoId;
    private int vboId;

    public ViewportManager() {
        sceneViewport = new Vector4f();
        hierarchyViewport = new Vector4f();
        inspectorViewport = new Vector4f();
        lineShader = new LineShader();
    }

    public void init() {
        // Initialize shader program for drawing lines
        lineShader.init();

        // Create and set up vertex arrays and buffers
        vaoId = glGenVertexArrays();
        vboId = glGenBuffers();

        glBindVertexArray(vaoId);
        glBindBuffer(GL_ARRAY_BUFFER, vboId);

        // Define how our vertex data is laid out in memory
        glVertexAttribPointer(0, 2, GL_FLOAT, false, 2 * Float.BYTES, 0);
        glEnableVertexAttribArray(0);

        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindVertexArray(0);
    }

    public void updateViewports(int windowWidth, int windowHeight) {
        // Store current dimensions for later use
        this.currentWidth = windowWidth;
        this.currentHeight = windowHeight;

        // Calculate the width of each section based on percentages
        float hierarchyWidth = windowWidth * HIERARCHY_WIDTH_PERCENT;
        float sceneWidth = windowWidth * SCENE_VIEW_WIDTH_PERCENT;
        float inspectorWidth = windowWidth * INSPECTOR_WIDTH_PERCENT;

        // Update viewport dimensions and positions
        hierarchyViewport.set(0, 0, hierarchyWidth, windowHeight);
        sceneViewport.set(hierarchyWidth, 0, sceneWidth, windowHeight);
        inspectorViewport.set(hierarchyWidth + sceneWidth, 0, inspectorWidth, windowHeight);

        // Update GL viewport to match new window size
        glViewport(0, 0, windowWidth, windowHeight);
    }

    public void setViewport(ViewportType type) {
        Vector4f viewport = switch (type) {
            case SCENE -> sceneViewport;
            case HIERARCHY -> hierarchyViewport;
            case INSPECTOR -> inspectorViewport;
        };

        glViewport((int)viewport.x, (int)viewport.y,
                (int)viewport.z, (int)viewport.w);
    }

    public void drawViewportBorders() {
        // Only draw borders if we have valid dimensions
        if (currentWidth <= 0 || currentHeight <= 0) return;

        try (MemoryStack stack = MemoryStack.stackPush()) {
            // Calculate line positions for borders
            float[] lineVertices = {
                    // Hierarchy-Scene border
                    hierarchyViewport.z, 0,
                    hierarchyViewport.z, currentHeight,

                    // Scene-Inspector border
                    hierarchyViewport.z + sceneViewport.z, 0,
                    hierarchyViewport.z + sceneViewport.z, currentHeight
            };

            // Update buffer with new line positions
            FloatBuffer vertices = stack.mallocFloat(lineVertices.length);
            vertices.put(lineVertices);
            vertices.flip();

            glBindBuffer(GL_ARRAY_BUFFER, vboId);
            glBufferData(GL_ARRAY_BUFFER, vertices, GL_DYNAMIC_DRAW);

            // Set up shader for drawing
            lineShader.use();
            lineShader.setViewportSize(currentWidth, currentHeight);
            lineShader.setColor(0.3f, 0.3f, 0.3f);

            // Draw the lines
            glBindVertexArray(vaoId);
            glDrawArrays(GL_LINES, 0, 4);
            glBindVertexArray(0);

            // Reset shader state
            glUseProgram(0);
        }
    }

    public void cleanup() {
        lineShader.cleanup();
        glDeleteBuffers(vboId);
        glDeleteVertexArrays(vaoId);
    }

    // Getter methods for viewport dimensions
    public Vector4f getSceneViewport() { return new Vector4f(sceneViewport); }
    public Vector4f getHierarchyViewport() { return new Vector4f(hierarchyViewport); }
    public Vector4f getInspectorViewport() { return new Vector4f(inspectorViewport); }
}