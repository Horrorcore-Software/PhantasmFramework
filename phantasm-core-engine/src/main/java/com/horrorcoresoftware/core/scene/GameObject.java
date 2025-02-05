package com.horrorcoresoftware.core.scene;

import java.util.ArrayList;
import java.util.List;
import org.joml.Vector3f;
import org.joml.Quaternionf;

/**
 * Base class for all objects that can exist in a scene.
 * Provides core functionality for transformation, parent-child relationships,
 * and component management.
 */
public class GameObject {
    private String name;
    private boolean active;
    private Transform transform;
    private GameObject parent;
    private List<GameObject> children;
    private List<Component> components;

    public GameObject() {
        this(null);
    }

    public GameObject(String name) {
        this.name = name;
        this.active = true;
        this.transform = new Transform();
        this.children = new ArrayList<>();
        this.components = new ArrayList<>();
    }

    /**
     * Initializes this game object and all its components.
     */
    public void initialize() {
        // Initialize all components
        for (Component component : components) {
            component.initialize();
        }

        // Initialize all children
        for (GameObject child : children) {
            child.initialize();
        }
    }

    /**
     * Updates this game object and all its components.
     * @param deltaTime Time passed since last update in seconds
     */
    public void update(double deltaTime) {
        if (!active) return;

        // Update all components
        for (Component component : components) {
            if (component.isActive()) {
                component.update(deltaTime);
            }
        }

        // Update all children
        for (GameObject child : children) {
            child.update(deltaTime);
        }
    }

    /**
     * Adds a component to this game object.
     * @param component The component to add
     * @param <T> The type of component
     * @return The added component
     */
    public <T extends Component> T addComponent(T component) {
        component.setGameObject(this);
        components.add(component);
        if (isActive()) {
            component.initialize();
        }
        return component;
    }

    /**
     * Gets a component of the specified type.
     * @param componentClass The class of the component to get
     * @param <T> The type of component
     * @return The component, or null if not found
     */
    @SuppressWarnings("unchecked")
    public <T extends Component> T getComponent(Class<T> componentClass) {
        for (Component component : components) {
            if (componentClass.isAssignableFrom(component.getClass())) {
                return (T) component;
            }
        }
        return null;
    }

    /**
     * Adds a child game object to this game object.
     * @param child The child to add
     */
    public void addChild(GameObject child) {
        children.add(child);
        child.setParent(this);
    }

    /**
     * Removes a child game object from this game object.
     * @param child The child to remove
     */
    public void removeChild(GameObject child) {
        children.remove(child);
        child.setParent(null);
    }

    /**
     * Cleans up this game object and all its components.
     */
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
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public Transform getTransform() { return transform; }

    public GameObject getParent() { return parent; }
    void setParent(GameObject parent) { this.parent = parent; }
}