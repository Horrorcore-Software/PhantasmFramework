package com.horrorcore.engine.core.graphics;

import com.horrorcore.engine.core.GameObject;
import com.horrorcore.engine.core.Scene;
import com.horrorcore.engine.core.ui.HierarchyPanel;
import com.horrorcore.engine.core.ui.InspectorPanel;
import com.horrorcore.engine.core.ui.Panel;
import com.horrorcore.engine.core.ui.ScenePanel;
import org.joml.Vector4f;

import static org.lwjgl.opengl.GL11.*;

public class ViewportManager {
    private static final float SCENE_VIEW_WIDTH_PERCENT = 0.7f;
    private static final float HIERARCHY_WIDTH_PERCENT = 0.15f;
    private static final float INSPECTOR_WIDTH_PERCENT = 0.15f;

    private ScenePanel scenePanel;
    private HierarchyPanel hierarchyPanel;
    private InspectorPanel inspectorPanel;
    private Scene scene;
    private Camera camera;

    private int currentWidth;
    private int currentHeight;

    public ViewportManager(Scene scene, Camera camera) {
        this.scene = scene;
        this.camera = camera;
    }

    public void init() {
        // Panels will be properly positioned in updateViewports
        scenePanel = new ScenePanel(0, 0, 100, 100, scene, camera);
        hierarchyPanel = new HierarchyPanel(0, 0, 100, 100, scene);
        inspectorPanel = new InspectorPanel(0, 0, 100, 100);

        scenePanel.init();
        hierarchyPanel.init();
        inspectorPanel.init();
    }

    public void updateViewports(int windowWidth, int windowHeight) {
        this.currentWidth = windowWidth;
        this.currentHeight = windowHeight;

        // Update global window dimensions for panels
        Panel.setWindowDimensions(windowWidth, windowHeight);

        float hierarchyWidth = windowWidth * HIERARCHY_WIDTH_PERCENT;
        float sceneWidth = windowWidth * SCENE_VIEW_WIDTH_PERCENT;
        float inspectorWidth = windowWidth * INSPECTOR_WIDTH_PERCENT;

        // Debug output
        System.out.println("Window dimensions: " + windowWidth + "x" + windowHeight);
        System.out.println("Hierarchy panel: x=0, width=" + hierarchyWidth);
        System.out.println("Scene panel: x=" + hierarchyWidth + ", width=" + sceneWidth);
        System.out.println("Inspector panel: x=" + (hierarchyWidth + sceneWidth) + ", width=" + inspectorWidth);

        hierarchyPanel.setDimensions(0, 0, hierarchyWidth, windowHeight);
        scenePanel.setDimensions(hierarchyWidth, 0, sceneWidth, windowHeight);
        inspectorPanel.setDimensions(hierarchyWidth + sceneWidth, 0, inspectorWidth, windowHeight);

        // Update scene aspect ratio
        scene.setAspectRatio(sceneWidth / windowHeight);
    }

    public void renderViewports() {
        // Clear the entire window first
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        // Enable blending for UI
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        // Render panels in order
        hierarchyPanel.render();
        scenePanel.render();
        inspectorPanel.render();

        // Disable blending
        glDisable(GL_BLEND);
    }

//    public void setSelectedObject(GameObject object) {
//        inspectorPanel.setSelectedObject(object);
//    }

    public void cleanup() {
        scenePanel.cleanup();
        hierarchyPanel.cleanup();
        inspectorPanel.cleanup();
    }

    // Getter methods
    public Vector4f getSceneViewportDimensions() {
        return new Vector4f(
                HIERARCHY_WIDTH_PERCENT * currentWidth,
                0,
                SCENE_VIEW_WIDTH_PERCENT * currentWidth,
                currentHeight
        );
    }
}