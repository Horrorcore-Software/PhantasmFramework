package com.horrorcore.engine.core.ui;

import com.horrorcore.engine.core.Scene;
import com.horrorcore.engine.core.graphics.Camera;
import com.horrorcore.engine.core.graphics.TextRenderer;

import static org.lwjgl.opengl.GL11.*;

public class ScenePanel extends Panel {
    private Scene scene;
    private Camera camera;
    private TextRenderer textRenderer;

    public ScenePanel(float x, float y, float width, float height, Scene scene, Camera camera) {
        super(x, y, width, height);
        this.scene = scene;
        this.camera = camera;
        this.textRenderer = new TextRenderer();
        setBackgroundColor(0.15f, 0.15f, 0.15f, 1.0f);
    }

    @Override
    public void init() {
        super.init();
        textRenderer.init();
    }

    @Override
    public void render() {
        beginRender();
        // Render the 3D scene
        glEnable(GL_DEPTH_TEST);
        scene.render(camera);
        glDisable(GL_DEPTH_TEST);
        // Render scene overlay text
        float[] textColor = {1.0f, 1.0f, 1.0f, 0.8f};
        textRenderer.renderText("Scene View", 10, height - 30, 1.2f, textColor);
        // Camera controls help
        float yPos = 60;
        textRenderer.renderText("Controls:", 10, yPos, 1.0f, textColor);
        yPos -= 20;
        textRenderer.renderText("Right Click + Drag: Rotate", 20, yPos, 0.9f, textColor);
        yPos -= 20;
        textRenderer.renderText("WASD: Move Camera", 20, yPos, 0.9f, textColor);
        yPos -= 20;
        textRenderer.renderText("Space/Shift: Up/Down", 20, yPos, 0.9f, textColor);
        endRender();
    }

    @Override
    public void cleanup() {
        super.cleanup();
        if (textRenderer != null) {
            textRenderer.cleanup();
        }
    }
}