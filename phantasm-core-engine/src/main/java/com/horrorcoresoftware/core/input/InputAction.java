package com.horrorcoresoftware.core.input;

import java.util.Arrays;
import java.util.List;

public class InputAction {
    private final String name;
    private final List<InputTrigger> triggers;
    private InputCallback callback;
    private boolean active;
    private float axisValue;

    public InputAction(String name, InputTrigger... triggers) {
        this.name = name;
        this.triggers = Arrays.asList(triggers);
        this.active = false;
        this.axisValue = 0;
    }

    public boolean matchesEvent(InputEvent event) {
        return triggers.stream().anyMatch(t -> t.matches(event));
    }

    public void trigger(InputEvent event) {
        active = event.isPressed();
        axisValue = event.getAxisValue();
        if (callback != null) {
            callback.onInput(event);
        }
    }

    public void setCallback(InputCallback callback) {
        this.callback = callback;
    }

    public boolean isActive() { return active; }
    public float getAxisValue() { return axisValue; }
}
