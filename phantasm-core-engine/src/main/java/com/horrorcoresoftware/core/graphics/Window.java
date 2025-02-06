package com.horrorcoresoftware.core.graphics;

import com.horrorcoresoftware.exceptions.EngineInitException;
import org.lwjgl.opengl.GL;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Window {
    private long windowHandle;
    private int width, height;
    private String title;
    private boolean resized;
    private boolean shouldClose;

    public Window(String title, int width, int height) {
        this.title = title;
        this.width = width;
        this.height = height;
        this.resized = false;
        this.shouldClose = false;
    }

    public void initialize() throws EngineInitException {
        // Initialize GLFW
        if (!glfwInit()) {
            throw new EngineInitException("Unable to initialize GLFW");
        }

        // Configure GLFW
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);

        // Create the window
        windowHandle = glfwCreateWindow(width, height, title, NULL, NULL);
        if (windowHandle == NULL) {
            throw new EngineInitException("Failed to create GLFW window");
        }

        // Setup callbacks
        glfwSetFramebufferSizeCallback(windowHandle, (window, width, height) -> {
            this.width = width;
            this.height = height;
            this.resized = true;
        });

        glfwSetWindowCloseCallback(windowHandle, window -> this.shouldClose = true);

        // Make the OpenGL context current
        glfwMakeContextCurrent(windowHandle);

        // Enable v-sync
        glfwSwapInterval(1);

        // Make the window visible
        glfwShowWindow(windowHandle);

        // Initialize OpenGL
        GL.createCapabilities();
    }

    public void swapBuffers() {
        glfwSwapBuffers(windowHandle);
        glfwPollEvents();
    }

    public boolean shouldClose() {
        return shouldClose || glfwWindowShouldClose(windowHandle);
    }

    public void dispose() {
        glfwFreeCallbacks(windowHandle);
        glfwDestroyWindow(windowHandle);
        glfwTerminate();
    }

    // Getters
    public long getWindowHandle() { return windowHandle; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }
    public boolean isResized() { return resized; }
    public void setResized(boolean resized) { this.resized = resized; }
}