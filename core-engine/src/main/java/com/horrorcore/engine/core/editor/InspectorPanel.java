package com.horrorcore.engine.core.editor;

import com.horrorcore.engine.core.GameObject;
import static org.lwjgl.opengl.GL11.*;

public class InspectorPanel {
    private static final int PADDING = 10;

    public void render(GameObject selectedObject, int viewportWidth, int viewportHeight) {
        if (selectedObject == null) {
            return;
        }

        // Save current OpenGL state
        boolean depthTestEnabled = glIsEnabled(GL_DEPTH_TEST);

        // Draw inspector background
        glDisable(GL_DEPTH_TEST);
        glBegin(GL_QUADS);
        glColor3f(0.2f, 0.2f, 0.2f);  // Dark gray background
        glVertex2f(0, 0);
        glVertex2f(viewportWidth, 0);
        glVertex2f(viewportWidth, viewportHeight);
        glVertex2f(0, viewportHeight);
        glEnd();

        // Draw header background
        glBegin(GL_QUADS);
        glColor3f(0.25f, 0.25f, 0.25f);  // Slightly lighter gray for header
        glVertex2f(0, viewportHeight - 30);
        glVertex2f(viewportWidth, viewportHeight - 30);
        glVertex2f(viewportWidth, viewportHeight);
        glVertex2f(0, viewportHeight);
        glEnd();

        // Restore previous OpenGL state
        if (depthTestEnabled) {
            glEnable(GL_DEPTH_TEST);
        }
    }
}