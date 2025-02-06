package com.horrorcore.engine.core;

import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class Transform {
    // Transform components
    private Vector3f position;
    private Quaternionf rotation;
    private Vector3f scale;

    // Cached transform matrix
    private Matrix4f modelMatrix;
    private boolean isDirty;  // Flag to indicate if matrix needs updating

    public Transform() {
        // Initialize with default values
        position = new Vector3f(0.0f);          // At origin
        rotation = new Quaternionf().identity(); // No rotation
        scale = new Vector3f(1.0f);             // Unit scale
        modelMatrix = new Matrix4f();
        isDirty = true;
    }

    // Updates the model matrix if needed
    public Matrix4f getModelMatrix() {
        if (isDirty) {
            modelMatrix.identity()
                    .translate(position)
                    .rotate(rotation)
                    .scale(scale);
            isDirty = false;
        }
        return modelMatrix;
    }

    // Position methods
    public void setPosition(float x, float y, float z) {
        position.set(x, y, z);
        isDirty = true;
    }

    public void setPosition(Vector3f position) {
        this.position.set(position);
        isDirty = true;
    }

    public Vector3f getPosition() {
        return new Vector3f(position);
    }

    // Rotation methods
    public void setRotation(float x, float y, float z) {
        rotation.rotationXYZ(x, y, z);
        isDirty = true;
    }

    public void setRotation(Quaternionf rotation) {
        this.rotation.set(rotation);
        isDirty = true;
    }

    public Quaternionf getRotation() {
        return new Quaternionf(rotation);
    }

    // Scale methods
    public void setScale(float x, float y, float z) {
        scale.set(x, y, z);
        isDirty = true;
    }

    public void setScale(float uniform) {
        scale.set(uniform);
        isDirty = true;
    }

    public Vector3f getScale() {
        return new Vector3f(scale);
    }

    // Relative transformation methods
    public void translate(float x, float y, float z) {
        position.add(x, y, z);
        isDirty = true;
    }

    public void translate(Vector3f translation) {
        position.add(translation);
        isDirty = true;
    }

    public void rotate(float angleRadians, Vector3f axis) {
        rotation.rotateAxis(angleRadians, axis);
        isDirty = true;
    }

    public void scale(float factor) {
        scale.mul(factor);
        isDirty = true;
    }
}