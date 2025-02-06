package com.horrorcore.engine.core;

public abstract class Component {
    // The GameObject this component is attached to
    protected GameObject gameObject;
    protected boolean isEnabled;

    public Component() {
        this.isEnabled = true;
    }

    // Lifecycle methods that can be overridden by specific components
    public void initialize() {}

    public void update(float deltaTime) {}

    public void render() {}

    public void cleanup() {}

    // Getters and setters
    public GameObject getGameObject() {
        return gameObject;
    }

    void setGameObject(GameObject gameObject) {
        this.gameObject = gameObject;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public void setEnabled(boolean enabled) {
        this.isEnabled = enabled;
    }

    // Convenience method to get the transform of the attached GameObject
    protected Transform getTransform() {
        return gameObject != null ? gameObject.getTransform() : null;
    }
}