package com.horrorcoresoftware.core.scene;

import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

/**
 * Handles the position, rotation, and scale of game objects.
 * Uses JOML (Java OpenGL Math Library) for efficient mathematical operations.
 */
public class Transform {
    private Vector3f position;
    private Quaternionf rotation;
    private Vector3f scale;
    private Matrix4f worldMatrix;
    private boolean dirty;

    public Transform() {
        position = new Vector3f(0, 0, 0);
        rotation = new Quaternionf().identity();
        scale = new Vector3f(1, 1, 1);
        worldMatrix = new Matrix4f();
        dirty = true;
    }

    /**
     * Gets the world transformation matrix.
     * @return The 4x4 transformation matrix
     */
    public Matrix4f getWorldMatrix() {
        if (dirty) {
            updateWorldMatrix();
        }
        return worldMatrix;
    }

    /**
     * Updates the world transformation matrix if needed.
     */
    private void updateWorldMatrix() {
        worldMatrix.identity()
                .translate(position)
                .rotate(rotation)
                .scale(scale);
        dirty = false;
    }

    /**
     * Sets the position of the transform.
     * @param x X coordinate
     * @param y Y coordinate
     * @param z Z coordinate
     */
    public void setPosition(float x, float y, float z) {
        position.set(x, y, z);
        dirty = true;
    }

    /**
     * Sets the rotation of the transform using Euler angles.
     * @param x Pitch (rotation around X-axis) in radians
     * @param y Yaw (rotation around Y-axis) in radians
     * @param z Roll (rotation around Z-axis) in radians
     */
    public void setRotation(float x, float y, float z) {
        rotation.rotationXYZ(x, y, z);
        dirty = true;
    }

    /**
     * Sets the scale of the transform.
     * @param x Scale along X-axis
     * @param y Scale along Y-axis
     * @param z Scale along Z-axis
     */
    public void setScale(float x, float y, float z) {
        scale.set(x, y, z);
        dirty = true;
    }

    // Getters
    public Vector3f getPosition() { return new Vector3f(position); }
    public Quaternionf getRotation() { return new Quaternionf(rotation); }
    public Vector3f getScale() { return new Vector3f(scale); }
}