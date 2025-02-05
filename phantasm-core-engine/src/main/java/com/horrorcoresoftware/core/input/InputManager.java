package com.horrorcoresoftware.core.input;

import org.lwjgl.glfw.GLFW;

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

    public InputManager() {
        this.actions = new ConcurrentHashMap<>();
        this.keyStates = new HashMap<>();
        this.mouseButtonStates = new HashMap<>();
        this.axisValues = new HashMap<>();
        this.eventQueue = new LinkedList<>();
        this.activeGamepads = new HashSet<>();
        this.inputEnabled = true;
    }

    public void createAction(String name, InputTrigger... triggers) {
        actions.put(name, new InputAction(name, triggers));
    }

    public void update() {
        if (!inputEnabled) return;

        // Process gamepad input
        activeGamepads.forEach(gamepad -> {
            updateGamepadState(gamepad);
        });

        // Process queued events
        while (!eventQueue.isEmpty()) {
            processInputEvent(eventQueue.poll());
        }

        // Reset one-frame states
        scrollX = scrollY = 0;
    }

    public void bindCallback(String actionName, InputCallback callback) {
        InputAction action = actions.get(actionName);
        if (action != null) {
            action.setCallback(callback);
        }
    }

    public boolean isActionPressed(String actionName) {
        InputAction action = actions.get(actionName);
        return action != null && action.isActive();
    }

    public float getAxis(String actionName) {
        InputAction action = actions.get(actionName);
        return action != null ? action.getAxisValue() : 0.0f;
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
                }
            }

            if (buttons != null) {
                for (int i = 0; i < buttons.capacity(); i++) {
                    boolean pressed = buttons.get(i) == GLFW.GLFW_PRESS;
                    keyStates.put(gamepad * 100 + i, pressed);
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

    public void setInputEnabled(boolean enabled) {
        this.inputEnabled = enabled;
    }

    // Getters for raw input states
    public double getMouseX() { return mouseX; }
    public double getMouseY() { return mouseY; }
    public double getScrollX() { return scrollX; }
    public double getScrollY() { return scrollY; }
}




