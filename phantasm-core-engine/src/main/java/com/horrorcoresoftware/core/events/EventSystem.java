package com.horrorcoresoftware.core.events;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Global event system that allows components to communicate without direct coupling.
 * Uses a publisher-subscriber (pub/sub) pattern for flexible communication.
 */
public class EventSystem {
    private static EventSystem instance;
    private final Map<String, List<EventListener>> listeners;
    private final Queue<EventData> eventQueue;

    private EventSystem() {
        // Using ConcurrentHashMap for thread safety
        this.listeners = new ConcurrentHashMap<>();
        this.eventQueue = new LinkedList<>();
    }

    /**
     * Gets the singleton instance of the event system.
     * @return The event system instance
     */
    public static EventSystem getInstance() {
        if (instance == null) {
            instance = new EventSystem();
        }
        return instance;
    }

    /**
     * Adds a listener for a specific event type.
     * @param eventType The type of event to listen for
     * @param listener The listener to notify when the event occurs
     */
    public void addEventListener(String eventType, EventListener listener) {
        listeners.computeIfAbsent(eventType, k -> new ArrayList<>()).add(listener);
    }

    /**
     * Removes a listener for a specific event type.
     * @param eventType The type of event
     * @param listener The listener to remove
     */
    public void removeEventListener(String eventType, EventListener listener) {
        List<EventListener> typeListeners = listeners.get(eventType);
        if (typeListeners != null) {
            typeListeners.remove(listener);
            if (typeListeners.isEmpty()) {
                listeners.remove(eventType);
            }
        }
    }

    /**
     * Queues an event for processing in the next update cycle.
     * This ensures events are processed in a controlled manner.
     * @param eventType The type of event
     * @param data The event data
     */
    public void queueEvent(String eventType, Object data) {
        eventQueue.offer(new EventData(eventType, data));
    }

    /**
     * Immediately emits an event to all registered listeners.
     * Use with caution as this bypasses the event queue.
     * @param eventType The type of event
     * @param data The event data
     */
    public void emitImmediate(String eventType, Object data) {
        List<EventListener> typeListeners = listeners.get(eventType);
        if (typeListeners != null) {
            EventData event = new EventData(eventType, data);
            for (EventListener listener : typeListeners) {
                try {
                    listener.onEvent(event);
                } catch (Exception e) {
                    // Log error but continue processing other listeners
                    System.err.println("Error processing event " + eventType + ": " + e.getMessage());
                }
            }
        }
    }

    /**
     * Processes all queued events.
     * Should be called during the engine update cycle.
     */
    public void processEvents() {
        while (!eventQueue.isEmpty()) {
            EventData event = eventQueue.poll();
            emitImmediate(event.getType(), event.getData());
        }
    }

    /**
     * Clears all event listeners and queued events.
     */
    public void cleanup() {
        listeners.clear();
        eventQueue.clear();
    }
}

/**
 * Represents the data for an event.
 */
class EventData {
    private final String type;
    private final Object data;

    public EventData(String type, Object data) {
        this.type = type;
        this.data = data;
    }

    public String getType() { return type; }
    public Object getData() { return data; }
}

