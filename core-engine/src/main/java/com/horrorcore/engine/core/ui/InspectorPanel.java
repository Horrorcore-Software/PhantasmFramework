package com.horrorcore.engine.core.ui;

import com.horrorcore.engine.core.GameObject;
import com.horrorcore.engine.core.Transform;
import com.horrorcore.engine.core.graphics.TextRenderer;
import org.joml.Vector3f;

public class InspectorPanel extends Panel {
    private TextRenderer textRenderer;
    private GameObject selectedObject;

    public InspectorPanel(float x, float y, float width, float height) {
        super(x, y, width, height);
        this.textRenderer = new TextRenderer();
        setBackgroundColor(0.2f, 0.1f, 0.3f, 1.0f);
    }

    @Override
    public void render() {
        beginRender();

        float yPos = height - 30;
        float[] textColor = {1.0f, 1.0f, 1.0f, 1.0f};

        // Header
        textRenderer.renderText("Inspector", 10, yPos, 1.2f, textColor);
        yPos -= 40;

        if (selectedObject != null) {
            // Object properties
            textRenderer.renderText("Object Properties:", 10, yPos, 1.1f, textColor);
            yPos -= 25;

            textRenderer.renderText("Name: " + selectedObject.getName(), 20, yPos, 1.0f, textColor);
            yPos -= 20;

            textRenderer.renderText("ID: " + selectedObject.getId(), 20, yPos, 1.0f, textColor);
            yPos -= 20;

            textRenderer.renderText("Active: " + selectedObject.isActive(), 20, yPos, 1.0f, textColor);
            yPos -= 30;

            // Transform component
            textRenderer.renderText("Transform Component:", 10, yPos, 1.1f, textColor);
            yPos -= 25;

            Transform transform = selectedObject.getTransform();
            Vector3f position = transform.getPosition();
            textRenderer.renderText(String.format("Position: %.2f, %.2f, %.2f",
                    position.x, position.y, position.z), 20, yPos, 1.0f, textColor);

        } else {
            textRenderer.renderText("No object selected", 10, yPos, 1.0f, textColor);
        }

        endRender();
    }

    public void setSelectedObject(GameObject object) {
        this.selectedObject = object;
    }
}