package com.horrorcore.engine.core;

import com.horrorcore.engine.core.graphics.Camera;
import com.horrorcore.engine.core.graphics.ViewportManager;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWFramebufferSizeCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;

import java.nio.DoubleBuffer;
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
    private Scene scene;
    private Camera camera;
    private double lastMouseX, lastMouseY;
    private boolean firstMouse = true;
    private boolean mouseInSceneView = false;

    public Window(String title, int width, int height) {
        this.title = title;
        this.width = width;
        this.height = height;
        this.resized = false;
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
            if (vidmode != null) {
                glfwSetWindowPos(
                        windowHandle,
                        (vidmode.width() - pWidth.get(0)) / 2,
                        (vidmode.height() - pHeight.get(0)) / 2
                );
            }
        }

        // Make OpenGL context current
        glfwMakeContextCurrent(windowHandle);

        // Enable v-sync
        glfwSwapInterval(1);

        // Make window visible
        glfwShowWindow(windowHandle);

        // Initialize OpenGL
        GL.createCapabilities();

        // Initialize scene and camera
        camera = new Camera(new Vector3f(5.0f, 5.0f, 5.0f));
        scene = new Scene(camera);
        scene.setAspectRatio((float)width / height);

        // Initialize viewport manager with scene and camera
        viewportManager = new ViewportManager(scene, camera);
        viewportManager.init();
        viewportManager.updateViewports(width, height);

        // Set up mouse callbacks
        setupMouseCallbacks();

        // Set clear color
        glClearColor(0.1f, 0.1f, 0.1f, 1.0f);
    }

    private void setupMouseCallbacks() {
        // Mouse movement callback
        glfwSetCursorPosCallback(windowHandle, (window, xpos, ypos) -> {
            Vector4f sceneViewport = viewportManager.getSceneViewportDimensions();
            if (!mouseInSceneView) return;

            if (firstMouse) {
                lastMouseX = xpos;
                lastMouseY = ypos;
                firstMouse = false;
                return;
            }

            float xOffset = (float) (xpos - lastMouseX);
            float yOffset = (float) (lastMouseY - ypos);

            lastMouseX = xpos;
            lastMouseY = ypos;

            // Adjust sensitivity
            xOffset *= 0.005f;
            yOffset *= 0.005f;

            camera.rotate(xOffset, yOffset);
        });

        // Mouse button callback
        glfwSetMouseButtonCallback(windowHandle, (window, button, action, mods) -> {
            if (button == GLFW_MOUSE_BUTTON_RIGHT) {
                if (action == GLFW_PRESS && isMouseInSceneViewport()) {
                    mouseInSceneView = true;
                    glfwSetInputMode(windowHandle, GLFW_CURSOR, GLFW_CURSOR_DISABLED);
                    firstMouse = true;
                } else if (action == GLFW_RELEASE) {
                    mouseInSceneView = false;
                    glfwSetInputMode(windowHandle, GLFW_CURSOR, GLFW_CURSOR_NORMAL);
                }
            } else if (button == GLFW_MOUSE_BUTTON_LEFT && action == GLFW_PRESS) {
                handleMouseClick();
            }
        });
    }

    private void handleMouseClick() {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            DoubleBuffer xpos = stack.mallocDouble(1);
            DoubleBuffer ypos = stack.mallocDouble(1);
            glfwGetCursorPos(windowHandle, xpos, ypos);

            Vector4f sceneViewport = viewportManager.getSceneViewportDimensions();

            if (isMouseInSceneViewport()) {
                scene.handleMouseClick(
                        (float)xpos.get(0), (float)ypos.get(0),
                        sceneViewport.x, sceneViewport.y,
                        sceneViewport.z, sceneViewport.w
                );
            }
        }
    }

    private boolean isMouseInSceneViewport() {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            DoubleBuffer xpos = stack.mallocDouble(1);
            DoubleBuffer ypos = stack.mallocDouble(1);
            glfwGetCursorPos(windowHandle, xpos, ypos);

            Vector4f sceneViewport = viewportManager.getSceneViewportDimensions();
            return xpos.get(0) >= sceneViewport.x &&
                    xpos.get(0) <= sceneViewport.x + sceneViewport.z &&
                    ypos.get(0) >= sceneViewport.y &&
                    ypos.get(0) <= sceneViewport.y + sceneViewport.w;
        }
    }

    public void update() {
        if (resized) {
            viewportManager.updateViewports(width, height);
            scene.setAspectRatio((float)width / height);
            resized = false;
        }

        processInput();
        render();

        glfwSwapBuffers(windowHandle);
        glfwPollEvents();
    }

    private void processInput() {
        if (!mouseInSceneView) return;

        float deltaTime = 0.016f; // Fixed time step for now

        if (glfwGetKey(windowHandle, GLFW_KEY_W) == GLFW_PRESS)
            camera.moveForward(deltaTime);
        if (glfwGetKey(windowHandle, GLFW_KEY_S) == GLFW_PRESS)
            camera.moveBackward(deltaTime);
        if (glfwGetKey(windowHandle, GLFW_KEY_A) == GLFW_PRESS)
            camera.moveLeft(deltaTime);
        if (glfwGetKey(windowHandle, GLFW_KEY_D) == GLFW_PRESS)
            camera.moveRight(deltaTime);
        if (glfwGetKey(windowHandle, GLFW_KEY_SPACE) == GLFW_PRESS)
            camera.moveUp(deltaTime);
        if (glfwGetKey(windowHandle, GLFW_KEY_LEFT_SHIFT) == GLFW_PRESS)
            camera.moveDown(deltaTime);
    }

    private void render() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        viewportManager.renderViewports();
    }

    public boolean shouldClose() {
        return glfwWindowShouldClose(windowHandle);
    }

    public void cleanup() {
        viewportManager.cleanup();
        scene.cleanup();

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
    public Scene getScene() { return scene; }
    public Camera getCamera() { return camera; }
}