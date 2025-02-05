package com.horrorcoresoftware.core;

import com.horrorcoresoftware.exceptions.EngineInitException;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;

import java.nio.*;
import java.util.*;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;

/**
 * Manages the game window and OpenGL context.
 */
public class Window {
    private long windowHandle;
    private int width, height;
    private String title;
    private boolean resized;

    /**
     * Creates a new window with the specified parameters.
     * @param title The window title
     * @param width The window width
     * @param height The window height
     */
    public Window(String title, int width, int height) {
        this.title = title;
        this.width = width;
        this.height = height;
        this.resized = false;
    }

    /**
     * Initializes the window and creates the OpenGL context.
     * @throws EngineInitException if initialization fails
     */
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

        // Setup resize callback
        glfwSetFramebufferSizeCallback(windowHandle, (window, width, height) -> {
            this.width = width;
            this.height = height;
            this.resized = true;
        });

        // Make the OpenGL context current
        glfwMakeContextCurrent(windowHandle);

        // Enable v-sync
        glfwSwapInterval(1);

        // Make the window visible
        glfwShowWindow(windowHandle);

        // Initialize OpenGL
        GL.createCapabilities();
    }

    /**
     * Swaps the frame buffers and polls for window events.
     */
    public void swapBuffers() {
        glfwSwapBuffers(windowHandle);
        glfwPollEvents();
    }

    /**
     * Disposes of the window and releases resources.
     */
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
