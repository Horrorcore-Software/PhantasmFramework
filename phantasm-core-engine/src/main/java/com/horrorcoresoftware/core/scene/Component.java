package com.horrorcoresoftware.core.scene;

/**
 * Base class for all components that can be attached to game objects.
 * Components provide specific behaviors and functionality to game objects.
 */
public abstract class Component {
    private GameObject gameObject;
    private boolean active;

    public Component() {
        this.active = true;
    }

    /**
     * Called when the component is first initialized.
     * Override this to set up the component's initial state.
     */
    public void initialize() {
        // Base implementation does nothing
    }

    /**
     * Called every frame to update the component.
     * Override this to implement component behavior.
     * @param deltaTime Time passed since last update in seconds
     */
    public void update(double deltaTime) {
        // Base implementation does nothing
    }

    /**
     * Called when the component is being destroyed.
     * Override this to clean up any resources.
     */
    public void cleanup() {
        // Base implementation does nothing
    }

    // Getters and setters
    public GameObject getGameObject() { return gameObject; }
    void setGameObject(GameObject gameObject) { this.gameObject = gameObject; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    /**
     * Gets the transform of the game object this component is attached to.
     * @return The transform component
     */
    protected Transform getTransform() {
        return gameObject.getTransform();
    }
}