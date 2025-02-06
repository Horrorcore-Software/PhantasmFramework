package com.horrorcore.engine.core.graphics;

import org.joml.Matrix4f;
import org.joml.Vector3f;

public class Camera {
    // Camera position and orientation
    private Vector3f position;        // Camera's current position
    private Vector3f target;          // Point the camera is looking at
    private Vector3f worldUp;         // World's up vector (typically 0,1,0)

    // Camera vectors recalculated each frame
    private Vector3f front;           // Direction camera is looking
    private Vector3f right;           // Camera's right vector
    private Vector3f up;              // Camera's up vector

    // Spherical coordinates for orbit
    private float distance;           // Distance from target
    private float phi;                // Vertical angle (pitch) in radians
    private float theta;              // Horizontal angle (yaw) in radians

    // Movement settings
    private float moveSpeed = 5.0f;
    private float rotateSpeed = 1.0f;
    private float zoomSpeed = 2.0f;

    // View matrix for rendering
    private Matrix4f viewMatrix;

    public Camera(Vector3f startPosition) {
        this.position = new Vector3f(startPosition);
        this.target = new Vector3f(0, 0, 0);  // Looking at origin by default
        this.worldUp = new Vector3f(0, 1, 0);

        this.front = new Vector3f();
        this.right = new Vector3f();
        this.up = new Vector3f();
        this.viewMatrix = new Matrix4f();

        // Initialize spherical coordinates based on start position
        this.distance = position.distance(target);
        this.theta = (float) Math.atan2(position.z - target.z, position.x - target.x);
        this.phi = (float) Math.acos((position.y - target.y) / distance);

        updateCameraVectors();
    }

    private void updateCameraVectors() {
        // Calculate new position based on spherical coordinates
        position.x = target.x + distance * (float)(Math.sin(phi) * Math.cos(theta));
        position.y = target.y + distance * (float)(Math.cos(phi));
        position.z = target.z + distance * (float)(Math.sin(phi) * Math.sin(theta));

        // Calculate camera vectors
        front.set(target).sub(position).normalize();
        right.set(front).cross(worldUp).normalize();
        up.set(right).cross(front).normalize();
    }

    public void rotate(float deltaX, float deltaY) {
        // Update angles based on mouse movement
        theta -= deltaX * rotateSpeed;  // Subtract for intuitive horizontal rotation
        phi += deltaY * rotateSpeed;

        // Clamp phi to avoid flipping
        phi = Math.max(0.1f, Math.min((float)Math.PI - 0.1f, phi));

        updateCameraVectors();
    }

    public void zoom(float deltaZoom) {
        // Adjust distance with clamping to avoid getting too close or too far
        distance = Math.max(1.0f, Math.min(100.0f, distance - deltaZoom * zoomSpeed));
        updateCameraVectors();
    }

    public void pan(float deltaX, float deltaY) {
        // Move target point (and thus camera) in the camera's local right and up directions
        Vector3f panRight = new Vector3f(right).mul(deltaX * moveSpeed);
        Vector3f panUp = new Vector3f(up).mul(deltaY * moveSpeed);

        target.add(panRight).add(panUp);
        updateCameraVectors();
    }

    public Matrix4f getViewMatrix() {
        return viewMatrix.identity().lookAt(position, target, up);
    }

    // Movement methods now move the target point instead of the camera directly
    public void moveForward(float deltaTime) {
        Vector3f movement = new Vector3f(front).mul(moveSpeed * deltaTime);
        target.add(movement);
        updateCameraVectors();
    }

    public void moveBackward(float deltaTime) {
        Vector3f movement = new Vector3f(front).mul(moveSpeed * deltaTime);
        target.sub(movement);
        updateCameraVectors();
    }

    public void moveRight(float deltaTime) {
        Vector3f movement = new Vector3f(right).mul(moveSpeed * deltaTime);
        target.add(movement);
        updateCameraVectors();
    }

    public void moveLeft(float deltaTime) {
        Vector3f movement = new Vector3f(right).mul(moveSpeed * deltaTime);
        target.sub(movement);
        updateCameraVectors();
    }

    public void moveUp(float deltaTime) {
        target.add(0, moveSpeed * deltaTime, 0);
        updateCameraVectors();
    }

    public void moveDown(float deltaTime) {
        target.sub(0, moveSpeed * deltaTime, 0);
        updateCameraVectors();
    }

    // Getters and setters
    public Vector3f getPosition() { return position; }
    public Vector3f getTarget() { return target; }
    public float getMoveSpeed() { return moveSpeed; }
    public void setMoveSpeed(float moveSpeed) { this.moveSpeed = moveSpeed; }
    public float getRotateSpeed() { return rotateSpeed; }
    public void setRotateSpeed(float rotateSpeed) { this.rotateSpeed = rotateSpeed; }
    public float getZoomSpeed() { return zoomSpeed; }
    public void setZoomSpeed(float zoomSpeed) { this.zoomSpeed = zoomSpeed; }
}