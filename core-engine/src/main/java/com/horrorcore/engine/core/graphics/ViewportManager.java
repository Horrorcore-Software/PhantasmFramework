package com.horrorcore.engine.core.graphics;

import com.horrorcore.engine.core.GameObject;
import com.horrorcore.engine.core.Scene;
import com.horrorcore.engine.core.ui.*;
import org.joml.Vector4f;

public class ViewportManager {
    private static final float SCENE_WIDTH_PERCENT = 0.7f;
    private static final float SIDE_PANEL_WIDTH_PERCENT = 0.15f;

    private ScenePanel scenePanel;
    private HierarchyPanel hierarchyPanel;
    private InspectorPanel inspectorPanel;
    private Scene scene;
    private Camera camera;
    private LayoutManager layout;

    public ViewportManager(Scene scene, Camera camera) {
        this.scene = scene;
        this.camera = camera;
        this.layout = new LayoutManager();
    }

    public void init() {
        // Set initial window size
        Panel.setWindowDimensions(1280, 720);  // Default size

        // Define layout areas
        layout.defineArea("hierarchy", 0.0f, 0.0f, SIDE_PANEL_WIDTH_PERCENT, 1.0f);
        layout.defineArea("scene", SIDE_PANEL_WIDTH_PERCENT, 0.0f, SCENE_WIDTH_PERCENT, 1.0f);
        layout.defineArea("inspector", SIDE_PANEL_WIDTH_PERCENT + SCENE_WIDTH_PERCENT, 0.0f,
                SIDE_PANEL_WIDTH_PERCENT, 1.0f);

        // Create and initialize panels
        hierarchyPanel = new HierarchyPanel(0, 0, 100, 100, scene);
        scenePanel = new ScenePanel(0, 0, 100, 100, scene, camera);
        inspectorPanel = new InspectorPanel(0, 0, 100, 100);

        hierarchyPanel.init();
        scenePanel.init();
        inspectorPanel.init();

        // Add panels to layout
        layout.addPanel("hierarchy", hierarchyPanel);
        layout.addPanel("scene", scenePanel);
        layout.addPanel("inspector", inspectorPanel);

        // Set initial layout size
        layout.setSize(1280, 720);
    }


    public void updateViewports(int windowWidth, int windowHeight) {
        System.out.println("Updating viewports to: " + windowWidth + "x" + windowHeight);

        // Update window dimensions in Panel class
        Panel.setWindowDimensions(windowWidth, windowHeight);

        // Update layout with new window dimensions
        layout.setSize(windowWidth, windowHeight);

        // Update scene aspect ratio
        Vector4f sceneDimensions = layout.getAreaDimensions("scene");
        if (sceneDimensions != null) {
            scene.setAspectRatio(sceneDimensions.z / sceneDimensions.w);
        }
    }

    public void renderViewports() {
        layout.render();
    }

    public void setSelectedObject(GameObject object) {
        inspectorPanel.setSelectedObject(object);
    }

    public void cleanup() {
        layout.cleanup();
    }

    public Vector4f getSceneViewportDimensions() {
        return layout.getAreaDimensions("scene");
    }
}