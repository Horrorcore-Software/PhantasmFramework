package com.horrorcoresoftware.core;

public class InputTrigger {
    private final InputType type;
    private final int code;
    private final float threshold;

    public InputTrigger(InputType type, int code) {
        this(type, code, 0.5f);
    }

    public InputTrigger(InputType type, int code, float threshold) {
        this.type = type;
        this.code = code;
        this.threshold = threshold;
    }

    public boolean matches(InputEvent event) {
        if (event.getType() != type) return false;
        if (type == InputType.GAMEPAD_AXIS) {
            return event.getCode() == code && Math.abs(event.getAxisValue()) >= threshold;
        }
        return event.getCode() == code;
    }
}
