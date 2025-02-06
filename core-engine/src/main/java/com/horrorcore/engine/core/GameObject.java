package com.horrorcore.engine.core;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class GameObject {
    private final String id;           // Unique identifier
    private String name;               // Display name
    private boolean isActive;          // Active state
    private Transform transform;       // Transform component
    private GameObject parent;         // Parent object in hierarchy
    private List<GameObject> children; // Child objects
    private List<Component> components;// Attached components

    public GameObject(String name) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.isActive = true;
        this.transform = new Transform();
        this.children = new ArrayList<>();
        this.components = new ArrayList<>();
    }

    public void initialize() {
        // Initialize all components
        for (Component component : components) {
            if (component.isEnabled()) {
                component.initialize();
            }
        }

        // Initialize all children
        for (GameObject child : children) {
            if (child.isActive) {
                child.initialize();
            }
        }
    }

    public void update(float deltaTime) {
        if (!isActive) return;

        // Update components
        for (Component component : components) {
            if (component.isEnabled()) {
                component.update(deltaTime);
            }
        }

        // Update children
        for (GameObject child : children) {
            child.update(deltaTime);
        }
    }

    public void render() {
        if (!isActive) return;

        // Render components
        for (Component component : components) {
            if (component.isEnabled()) {
                component.render();
            }
        }

        // Render children
        for (GameObject child : children) {
            child.render();
        }
    }

    // Component management
    public <T extends Component> T addComponent(T component) {
        component.setGameObject(this);
        components.add(component);
        if (isActive) {
            component.initialize();
        }
        return component;
    }

    public <T extends Component> T getComponent(Class<T> componentClass) {
        for (Component component : components) {
            if (componentClass.isAssignableFrom(component.getClass())) {
                return componentClass.cast(component);
            }
        }
        return null;
    }

    public void removeComponent(Component component) {
        if (components.remove(component)) {
            component.cleanup();
            component.setGameObject(null);
        }
    }

    // Child management
    public void addChild(GameObject child) {
        if (child.parent != null) {
            child.parent.children.remove(child);
        }
        children.add(child);
        child.parent = this;
    }

    public void removeChild(GameObject child) {
        if (children.remove(child)) {
            child.parent = null;
        }
    }

    // Resource cleanup
    public void cleanup() {
        // Cleanup all components
        for (Component component : components) {
            component.cleanup();
        }
        components.clear();

        // Cleanup all children
        for (GameObject child : children) {
            child.cleanup();
        }
        children.clear();
    }

    // Getters and setters
    public String getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { this.isActive = active; }
    public Transform getTransform() { return transform; }
    public GameObject getParent() { return parent; }
    public List<GameObject> getChildren() { return new ArrayList<>(children); }
}