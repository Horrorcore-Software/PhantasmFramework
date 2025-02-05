package com.horrorcoresoftware.core;

public class InputEvent {
    private final InputType type;
    private final int code;
    private final boolean pressed;
    private final float value;

    public InputEvent(InputType type, int code, boolean pressed, float value) {
        this.type = type;
        this.code = code;
        this.pressed = pressed;
        this.value = value;
    }

    public InputType getType() { return type; }
    public int getCode() { return code; }
    public boolean isPressed() { return pressed; }
    public float getAxisValue() { return value; }
}
