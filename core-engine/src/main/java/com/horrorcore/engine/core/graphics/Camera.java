package com.horrorcore.engine.core.graphics;

import org.joml.Matrix4f;
import org.joml.Vector3f;

public class Camera {
    // Camera position and orientation
    private Vector3f position;
    private float pitch;    // Looking up/down (in radians)
    private float yaw;      // Looking left/right (in radians)

    // Movement settings
    private float moveSpeed = 5.0f;
    private float rotateSpeed = 1.0f;

    // View matrix for rendering
    private Matrix4f viewMatrix;

    // Camera vectors
    private Vector3f front;
    private Vector3f right;
    private Vector3f up;

    public Camera(Vector3f position) {
        this.position = position;
        this.pitch = 0.0f;
        this.yaw = (float) -Math.PI / 2.0f;  // Start looking along negative Z
        this.viewMatrix = new Matrix4f();
        this.front = new Vector3f();
        this.right = new Vector3f();
        this.up = new Vector3f(0.0f, 1.0f, 0.0f);

        // Initialize camera orientation
        updateCameraVectors();
    }

    private void updateCameraVectors() {
        // Calculate new front vector
        front.x = (float) (Math.cos(yaw) * Math.cos(pitch));
        front.y = (float) Math.sin(pitch);
        front.z = (float) (Math.sin(yaw) * Math.cos(pitch));
        front.normalize();

        // Recalculate right and up vectors
        right.set(front).cross(up).normalize();
        up.set(right).cross(front).normalize();
    }

    public void moveForward(float deltaTime) {
        position.add(new Vector3f(front).mul(moveSpeed * deltaTime));
    }

    public void moveBackward(float deltaTime) {
        position.sub(new Vector3f(front).mul(moveSpeed * deltaTime));
    }

    public void moveRight(float deltaTime) {
        position.add(new Vector3f(right).mul(moveSpeed * deltaTime));
    }

    public void moveLeft(float deltaTime) {
        position.sub(new Vector3f(right).mul(moveSpeed * deltaTime));
    }

    public void moveUp(float deltaTime) {
        position.add(new Vector3f(up).mul(moveSpeed * deltaTime));
    }

    public void moveDown(float deltaTime) {
        position.sub(new Vector3f(up).mul(moveSpeed * deltaTime));
    }

    public void rotate(float deltaX, float deltaY) {
        // Update yaw and pitch based on mouse movement
        yaw += deltaX * rotateSpeed;
        pitch += deltaY * rotateSpeed;

        // Constrain pitch to avoid camera flipping
        pitch = Math.max(Math.min(pitch, (float) Math.PI / 2.0f - 0.1f),
                (float) -Math.PI / 2.0f + 0.1f);

        // Update camera orientation vectors
        updateCameraVectors();
    }

    public Matrix4f getViewMatrix() {
        // Calculate the view matrix based on current camera state
        return viewMatrix.identity()
                .lookAt(position,
                        new Vector3f(position).add(front),
                        up);
    }

    // Getters and setters
    public Vector3f getPosition() { return position; }
    public void setPosition(Vector3f position) { this.position.set(position); }
    public float getMoveSpeed() { return moveSpeed; }
    public void setMoveSpeed(float moveSpeed) { this.moveSpeed = moveSpeed; }
    public float getRotateSpeed() { return rotateSpeed; }
    public void setRotateSpeed(float rotateSpeed) { this.rotateSpeed = rotateSpeed; }
}