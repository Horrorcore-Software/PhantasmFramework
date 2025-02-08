package com.horrorcore.engine.core.graphics;

import com.horrorcore.engine.core.GameObject;
import com.horrorcore.engine.core.editor.InspectorPanel;
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
    private InspectorPanel inspectorPanel;
    private GameObject selectedObject;

    public ViewportManager() {
        sceneViewport = new Vector4f();
        hierarchyViewport = new Vector4f();
        inspectorViewport = new Vector4f();
        lineShader = new LineShader();
        inspectorPanel = new InspectorPanel();
    }

    public void init() {
        // Initialize shader program for drawing lines
        lineShader.init();

        // Initialize vertex arrays and buffers for border lines
        vaoId = glGenVertexArrays();
        vboId = glGenBuffers();

        glBindVertexArray(vaoId);
        glBindBuffer(GL_ARRAY_BUFFER, vboId);
        glVertexAttribPointer(0, 2, GL_FLOAT, false, 2 * Float.BYTES, 0);
        glEnableVertexAttribArray(0);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindVertexArray(0);

        // Initialize inspector panel
        inspectorPanel.init();
    }

    public void updateViewports(int windowWidth, int windowHeight) {
        this.currentWidth = windowWidth;
        this.currentHeight = windowHeight;

        float hierarchyWidth = windowWidth * HIERARCHY_WIDTH_PERCENT;
        float sceneWidth = windowWidth * SCENE_VIEW_WIDTH_PERCENT;
        float inspectorWidth = windowWidth * INSPECTOR_WIDTH_PERCENT;

        hierarchyViewport.set(0, 0, hierarchyWidth, windowHeight);
        sceneViewport.set(hierarchyWidth, 0, sceneWidth, windowHeight);
        inspectorViewport.set(hierarchyWidth + sceneWidth, 0, inspectorWidth, windowHeight);
    }

    public void renderViewports() {
        // Clear the entire window first
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        // Render scene viewport
        setViewport(ViewportType.SCENE);
        // Scene rendering code...

        // Render hierarchy viewport
        setViewport(ViewportType.HIERARCHY);
        // Hierarchy rendering code...

        // Render inspector viewport - with proper state management
        if (selectedObject != null) {
            setViewport(ViewportType.INSPECTOR);
            inspectorPanel.render(selectedObject,
                    (int)inspectorViewport.z,
                    (int)inspectorViewport.w);
        }

        // Render borders last
        glViewport(0, 0, currentWidth, currentHeight);
        renderBorders();
    }

    private void renderBorders() {
        // Save current viewport
        glViewport(0, 0, currentWidth, currentHeight);

        try (MemoryStack stack = MemoryStack.stackPush()) {
            float[] lineVertices = {
                    // Hierarchy-Scene border
                    hierarchyViewport.z, 0,
                    hierarchyViewport.z, currentHeight,
                    // Scene-Inspector border
                    hierarchyViewport.z + sceneViewport.z, 0,
                    hierarchyViewport.z + sceneViewport.z, currentHeight
            };

            FloatBuffer vertices = stack.mallocFloat(lineVertices.length);
            vertices.put(lineVertices).flip();

            glBindBuffer(GL_ARRAY_BUFFER, vboId);
            glBufferData(GL_ARRAY_BUFFER, vertices, GL_DYNAMIC_DRAW);

            lineShader.use();
            lineShader.setViewportSize(currentWidth, currentHeight);
            lineShader.setColor(0.3f, 0.3f, 0.3f);

            glBindVertexArray(vaoId);
            glDrawArrays(GL_LINES, 0, 4);
            glBindVertexArray(0);

            glUseProgram(0);
        }
    }

    private void renderInspectorPanel() {
        // Set viewport for inspector panel
        glViewport((int)inspectorViewport.x, (int)inspectorViewport.y,
                (int)inspectorViewport.z, (int)inspectorViewport.w);

        // Disable depth testing for UI rendering
        boolean depthTestEnabled = glIsEnabled(GL_DEPTH_TEST);
        glDisable(GL_DEPTH_TEST);

        // Render inspector panel
        inspectorPanel.render(selectedObject, (int)inspectorViewport.z, (int)inspectorViewport.w);

        // Restore depth testing if it was enabled
        if (depthTestEnabled) {
            glEnable(GL_DEPTH_TEST);
        }
    }

    public void setViewport(ViewportType type) {
        Vector4f viewport = switch (type) {
            case SCENE -> sceneViewport;
            case HIERARCHY -> hierarchyViewport;
            case INSPECTOR -> inspectorViewport;
        };

        // Ensure we're setting valid viewport dimensions
        if (viewport.z > 0 && viewport.w > 0) {
            glViewport((int)viewport.x, (int)viewport.y,
                    (int)viewport.z, (int)viewport.w);
        }
    }

    public void setSelectedObject(GameObject object) {
        this.selectedObject = object;
    }

    public void cleanup() {
        lineShader.cleanup();
        inspectorPanel.cleanup();
        glDeleteBuffers(vboId);
        glDeleteVertexArrays(vaoId);
    }

    // Getter methods
    public Vector4f getSceneViewport() { return new Vector4f(sceneViewport); }
    public Vector4f getHierarchyViewport() { return new Vector4f(hierarchyViewport); }
    public Vector4f getInspectorViewport() { return new Vector4f(inspectorViewport); }
}