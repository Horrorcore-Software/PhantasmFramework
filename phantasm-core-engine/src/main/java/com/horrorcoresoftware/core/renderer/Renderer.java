package com.horrorcoresoftware.core.renderer;

import com.horrorcoresoftware.core.graphics.Window;

import static org.lwjgl.opengl.GL11.*;

/**
 * Manages the rendering pipeline and graphics state.
 */
public class Renderer {
    private Window window;
    private ShaderManager shaderManager;

    /**
     * Creates a new renderer for the specified window.
     * @param window The window to render to
     */
    public Renderer(Window window) {
        this.window = window;
        this.shaderManager = new ShaderManager();
    }

    /**
     * Initializes the rendering system.
     * @throws Exception if initialization fails
     */
    public void initialize() throws Exception {
        // Initialize OpenGL state
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_CULL_FACE);
        glCullFace(GL_BACK);

        // Initialize default shaders
        shaderManager.initialize();

        // Set clear color
        glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
    }

    /**
     * Begins a new frame, clearing the screen and setting up the render state.
     */
    public void beginFrame() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        if (window.isResized()) {
            glViewport(0, 0, window.getWidth(), window.getHeight());
            window.setResized(false);
        }
    }

    /**
     * Ends the current frame.
     */
    public void endFrame() {
        // Add any end-of-frame operations here
    }

    /**
     * Cleans up rendering resources.
     */
    public void cleanup() {
        shaderManager.cleanup();
    }

    /**
     * Gets the shader manager.
     * @return The shader manager
     */
    public ShaderManager getShaderManager() {
        return shaderManager;
    }
}