package com.horrorcore.engine;

import com.horrorcore.engine.core.Window;

import static org.lwjgl.opengl.GL11.*;

public class Main {
    public static void main(String[] args) {
        Window window = new Window("Phantasm Engine Editor", 1280, 720);

        try {
            window.init();

            // Main game loop
            while (!window.shouldClose()) {
                glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

                // Clear with a dark gray color
                glClearColor(0.2f, 0.2f, 0.2f, 1.0f);

                window.update();
            }
        } finally {
            window.cleanup();
        }
    }
}