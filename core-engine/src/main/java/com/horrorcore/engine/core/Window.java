package com.horrorcore.engine.core;

import com.horrorcore.engine.core.graphics.ViewportManager;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWFramebufferSizeCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;

import java.nio.IntBuffer;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Window {
    private long windowHandle;
    private int width;
    private int height;
    private String title;
    private boolean resized;
    private ViewportManager viewportManager;
    private GLFWFramebufferSizeCallback framebufferSizeCallback;

    public Window(String title, int width, int height) {
        this.title = title;
        this.width = width;
        this.height = height;
        this.resized = false;
        this.viewportManager = new ViewportManager();
    }

    public void init() {
        // Set up error callback
        GLFWErrorCallback.createPrint(System.err).set();

        // Initialize GLFW
        if (!glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }

        // Configure window hints
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
        glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GL_TRUE);

        // Create window
        windowHandle = glfwCreateWindow(width, height, title, NULL, NULL);
        if (windowHandle == NULL) {
            throw new RuntimeException("Failed to create GLFW window");
        }

        // Set up resize callback
        framebufferSizeCallback = new GLFWFramebufferSizeCallback() {
            @Override
            public void invoke(long window, int width, int height) {
                Window.this.width = width;
                Window.this.height = height;
                Window.this.resized = true;
            }
        };
        glfwSetFramebufferSizeCallback(windowHandle, framebufferSizeCallback);

        // Center window on screen
        try (MemoryStack stack = stackPush()) {
            IntBuffer pWidth = stack.mallocInt(1);
            IntBuffer pHeight = stack.mallocInt(1);
            glfwGetWindowSize(windowHandle, pWidth, pHeight);

            GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
            glfwSetWindowPos(
                    windowHandle,
                    (vidmode.width() - pWidth.get(0)) / 2,
                    (vidmode.height() - pHeight.get(0)) / 2
            );
        }

        // Make OpenGL context current
        glfwMakeContextCurrent(windowHandle);

        // Enable v-sync
        glfwSwapInterval(1);

        // Make window visible
        glfwShowWindow(windowHandle);

        // Initialize OpenGL
        GL.createCapabilities();

        // Initialize viewport manager
        viewportManager.init();
        viewportManager.updateViewports(width, height);

        // Set clear color
        glClearColor(0.2f, 0.2f, 0.2f, 1.0f);
    }

    public void renderFrame() {
        // Handle window resize if needed
        if (resized) {
            viewportManager.updateViewports(width, height);
            resized = false;
        }

        // Clear buffers
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        // Draw viewport borders
        viewportManager.drawViewportBorders();
    }

    public void update() {
        renderFrame();
        glfwSwapBuffers(windowHandle);
        glfwPollEvents();
    }

    public boolean shouldClose() {
        return glfwWindowShouldClose(windowHandle);
    }

    public void cleanup() {
        viewportManager.cleanup();

        if (framebufferSizeCallback != null) {
            framebufferSizeCallback.free();
        }

        glfwFreeCallbacks(windowHandle);
        glfwDestroyWindow(windowHandle);

        glfwTerminate();
        GLFWErrorCallback callback = glfwSetErrorCallback(null);
        if (callback != null) {
            callback.free();
        }
    }

    // Getters
    public long getWindowHandle() { return windowHandle; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }
    public boolean isResized() { return resized; }
    public void setResized(boolean resized) { this.resized = resized; }
    public ViewportManager getViewportManager() { return viewportManager; }
}