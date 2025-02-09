package com.horrorcore.engine.core.ui;

import com.horrorcore.engine.core.Scene;
import com.horrorcore.engine.core.graphics.TextRenderer;


public class HierarchyPanel extends Panel {
    private TextRenderer textRenderer;
    private Scene scene;

    public HierarchyPanel(float x, float y, float width, float height, Scene scene) {
        super(x, y, width, height);
        this.scene = scene;
        this.textRenderer = new TextRenderer();
        setBackgroundColor(0.1f, 0.15f, 0.3f, 1.0f);
    }

    @Override
    public void render() {
        beginRender();

        // Header
        float[] textColor = {1.0f, 1.0f, 1.0f, 1.0f};
        textRenderer.renderText("Hierarchy", 10, height - 30, 1.2f, textColor);

        // TODO: Add scene hierarchy when implemented
        textRenderer.renderText("Scene Objects:", 10, height - 60, 1.0f, textColor);
        textRenderer.renderText("- TestCube", 20, height - 85, 1.0f, textColor);

        endRender();
    }
}