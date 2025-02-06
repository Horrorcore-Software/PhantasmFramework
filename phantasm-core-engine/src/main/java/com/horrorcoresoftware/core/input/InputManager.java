package com.horrorcoresoftware.core.input;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWMouseButtonCallback;
import org.lwjgl.glfw.GLFWScrollCallback;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class InputManager {
    private final Map<String, InputAction> actions;
    private final Map<Integer, Boolean> keyStates;
    private final Map<Integer, Boolean> mouseButtonStates;
    private final Map<Integer, Float> axisValues;
    private final Queue<InputEvent> eventQueue;
    private final Set<Integer> activeGamepads;

    private double mouseX, mouseY;
    private double scrollX, scrollY;
    private boolean inputEnabled;

    private GLFWKeyCallback keyCallback;
    private GLFWCursorPosCallback cursorPosCallback;
    private GLFWMouseButtonCallback mouseButtonCallback;
    private GLFWScrollCallback scrollCallback;

    public InputManager() {
        this.actions = new ConcurrentHashMap<>();
        this.keyStates = new HashMap<>();
        this.mouseButtonStates = new HashMap<>();
        this.axisValues = new HashMap<>();
        this.eventQueue = new LinkedList<>();
        this.activeGamepads = new HashSet<>();
        this.inputEnabled = true;
    }

    public void initialize(long windowHandle) {
        // Keyboard input
        keyCallback = new GLFWKeyCallback() {
            @Override
            public void invoke(long window, int key, int scancode, int action, int mods) {
                if (!inputEnabled) return;
                boolean pressed = action != GLFW.GLFW_RELEASE;
                keyStates.put(key, pressed);
                eventQueue.offer(new InputEvent(InputType.KEYBOARD, key, pressed, 0));
            }
        };
        GLFW.glfwSetKeyCallback(windowHandle, keyCallback);

        // Mouse position
        cursorPosCallback = new GLFWCursorPosCallback() {
            @Override
            public void invoke(long window, double xpos, double ypos) {
                if (!inputEnabled) return;
                mouseX = xpos;
                mouseY = ypos;
            }
        };
        GLFW.glfwSetCursorPosCallback(windowHandle, cursorPosCallback);

        // Mouse buttons
        mouseButtonCallback = new GLFWMouseButtonCallback() {
            @Override
            public void invoke(long window, int button, int action, int mods) {
                if (!inputEnabled) return;
                boolean pressed = action != GLFW.GLFW_RELEASE;
                mouseButtonStates.put(button, pressed);
                eventQueue.offer(new InputEvent(InputType.MOUSE, button, pressed, 0));
            }
        };
        GLFW.glfwSetMouseButtonCallback(windowHandle, mouseButtonCallback);

        // Scroll wheel
        scrollCallback = new GLFWScrollCallback() {
            @Override
            public void invoke(long window, double xoffset, double yoffset) {
                if (!inputEnabled) return;
                scrollX = xoffset;
                scrollY = yoffset;
            }
        };
        GLFW.glfwSetScrollCallback(windowHandle, scrollCallback);

        // Initialize gamepad support
        for (int i = GLFW.GLFW_JOYSTICK_1; i <= GLFW.GLFW_JOYSTICK_LAST; i++) {
            if (GLFW.glfwJoystickPresent(i)) {
                activeGamepads.add(i);
            }
        }
    }

    public void createAction(String name, InputTrigger... triggers) {
        actions.put(name, new InputAction(name, triggers));
    }

    public void update() {
        if (!inputEnabled) return;

        // Process gamepad input
        activeGamepads.forEach(this::updateGamepadState);

        // Process queued events
        while (!eventQueue.isEmpty()) {
            processInputEvent(eventQueue.poll());
        }

        // Reset one-frame states
        scrollX = scrollY = 0;
    }

    private void updateGamepadState(int gamepad) {
        if (GLFW.glfwJoystickPresent(gamepad)) {
            FloatBuffer axes = GLFW.glfwGetJoystickAxes(gamepad);
            ByteBuffer buttons = GLFW.glfwGetJoystickButtons(gamepad);

            if (axes != null) {
                for (int i = 0; i < axes.capacity(); i++) {
                    float value = axes.get(i);
                    // Apply deadzone
                    if (Math.abs(value) < 0.1f) value = 0;
                    axisValues.put(gamepad * 100 + i, value);
                    eventQueue.offer(new InputEvent(InputType.GAMEPAD_AXIS, gamepad * 100 + i, value != 0, value));
                }
            }

            if (buttons != null) {
                for (int i = 0; i < buttons.capacity(); i++) {
                    boolean pressed = buttons.get(i) == GLFW.GLFW_PRESS;
                    keyStates.put(gamepad * 100 + i, pressed);
                    eventQueue.offer(new InputEvent(InputType.GAMEPAD_BUTTON, gamepad * 100 + i, pressed, 0));
                }
            }
        }
    }

    private void processInputEvent(InputEvent event) {
        for (InputAction action : actions.values()) {
            if (action.matchesEvent(event)) {
                action.trigger(event);
            }
        }
    }

    public void cleanup() {
        if (keyCallback != null) keyCallback.free();
        if (cursorPosCallback != null) cursorPosCallback.free();
        if (mouseButtonCallback != null) mouseButtonCallback.free();
        if (scrollCallback != null) scrollCallback.free();
    }

    public void setInputEnabled(boolean enabled) {
        this.inputEnabled = enabled;
    }

    public boolean isKeyPressed(int key) {
        return keyStates.getOrDefault(key, false);
    }

    public boolean isMouseButtonPressed(int button) {
        return mouseButtonStates.getOrDefault(button, false);
    }

    public boolean isActionPressed(String actionName) {
        InputAction action = actions.get(actionName);
        return action != null && action.isActive();
    }

    public float getAxis(String actionName) {
        InputAction action = actions.get(actionName);
        return action != null ? action.getAxisValue() : 0.0f;
    }

    // Getters for raw input states
    public double getMouseX() { return mouseX; }
    public double getMouseY() { return mouseY; }
    public double getScrollX() { return scrollX; }
    public double getScrollY() { return scrollY; }
}