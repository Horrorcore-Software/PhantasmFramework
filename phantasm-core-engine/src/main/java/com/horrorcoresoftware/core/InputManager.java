package com.horrorcoresoftware.core;

import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.glfw.GLFW.*;

/**
 * Manages input handling for keyboard and mouse events.
 */
public class InputManager {
    private Map<Integer, Boolean> keyStates;
    private Map<Integer, Boolean> mouseButtonStates;
    private double mouseX, mouseY;
    private double scrollX, scrollY;

    /**
     * Creates a new input manager.
     */
    public InputManager() {
        keyStates = new HashMap<>();
        mouseButtonStates = new HashMap<>();
    }

    /**
     * Initializes input callbacks for the specified window.
     * @param window The window to handle input for
     */
    public void initialize(Window window) {
        long windowHandle = window.getWindowHandle();

        // Keyboard callback
        glfwSetKeyCallback(windowHandle, (win, key, scancode, action, mods) -> {
            keyStates.put(key, action != GLFW_RELEASE);
        });

        // Mouse button callback
        glfwSetMouseButtonCallback(windowHandle, (win, button, action, mods) -> {
            mouseButtonStates.put(button, action != GLFW_RELEASE);
        });

        // Cursor position callback
        glfwSetCursorPosCallback(windowHandle, (win, xpos, ypos) -> {
            mouseX = xpos;
            mouseY = ypos;
        });

        // Scroll callback
        glfwSetScrollCallback(windowHandle, (win, xoffset, yoffset) -> {
            scrollX = xoffset;
            scrollY = yoffset;
        });
    }

    /**
     * Updates the input state.
     */
    public void update() {
        scrollX = 0;
        scrollY = 0;
    }

    /**
     * Checks if a key is currently pressed.
     * @param key The GLFW key code
     * @return true if the key is pressed
     */
    public boolean isKeyPressed(int key) {
        return keyStates.getOrDefault(key, false);
    }

    /**
     * Checks if a mouse button is currently pressed.
     * @param button The GLFW mouse button code
     * @return true if the button is pressed
     */
    public boolean isMouseButtonPressed(int button) {
        return mouseButtonStates.getOrDefault(button, false);
    }

    // Getters for mouse position and scroll
    public double getMouseX() { return mouseX; }
    public double getMouseY() { return mouseY; }
    public double getScrollX() { return scrollX; }
    public double getScrollY() { return scrollY; }
}