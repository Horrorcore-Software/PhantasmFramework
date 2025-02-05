package com.horrorcoresoftware.core.events;

/**
 * Represents the data for an event.
 */
public class EventData {
    private final String type;
    private final Object data;

    public EventData(String type, Object data) {
        this.type = type;
        this.data = data;
    }

    public String getType() { return type; }
    public Object getData() { return data; }
}
