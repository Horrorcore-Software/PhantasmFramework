package com.horrorcore.engine.core.input;

import com.horrorcore.engine.core.graphics.Camera;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

/**
 * Handles mouse picking in 3D space using ray casting.
 * Converts mouse screen coordinates to a ray in world space for object selection.
 */
public class MousePicker {
    private final Matrix4f projectionMatrix;
    private final Matrix4f viewMatrix;
    private final Camera camera;

    // Cached ray calculation values
    private Vector3f currentRay;
    private Vector2f normalizedCoords;
    private Vector4f clipCoords;
    private Vector4f eyeCoords;
    private Vector3f worldRay;

    public MousePicker(Camera camera, Matrix4f projectionMatrix) {
        this.camera = camera;
        this.projectionMatrix = projectionMatrix;
        this.viewMatrix = new Matrix4f();
    }

    /**
     * Updates the picking ray based on current mouse position
     * @param mouseX Screen space X coordinate
     * @param mouseY Screen space Y coordinate
     * @param viewportX Viewport X position
     * @param viewportY Viewport Y position
     * @param viewportWidth Viewport width
     * @param viewportHeight Viewport height
     */
    public void update(float mouseX, float mouseY,
                       float viewportX, float viewportY,
                       float viewportWidth, float viewportHeight) {
        // Convert mouse coordinates to viewport space
        float viewportMouseX = mouseX - viewportX;
        float viewportMouseY = mouseY - viewportY;

        // Convert to normalized device coordinates (-1 to 1)
        normalizedCoords = new Vector2f(
                (2.0f * viewportMouseX) / viewportWidth - 1.0f,
                1.0f - (2.0f * viewportMouseY) / viewportHeight
        );

        // Create the clip space ray
        clipCoords = new Vector4f(normalizedCoords.x, normalizedCoords.y, -1.0f, 1.0f);

        // Convert to eye space
        eyeCoords = toEyeSpace(clipCoords);
        worldRay = toWorldSpace(eyeCoords);

        currentRay = worldRay.normalize();
    }

    /**
     * Gets the current ray direction in world space
     */
    public Vector3f getCurrentRay() {
        return currentRay;
    }

    /**
     * Converts clip space coordinates to eye space
     */
    private Vector4f toEyeSpace(Vector4f clipCoords) {
        Matrix4f invertedProjection = new Matrix4f(projectionMatrix).invert();
        Vector4f eyeCoords = invertedProjection.transform(clipCoords);
        return new Vector4f(eyeCoords.x, eyeCoords.y, -1.0f, 0.0f);
    }

    /**
     * Converts eye space coordinates to world space
     */
    private Vector3f toWorldSpace(Vector4f eyeCoords) {
        Matrix4f invertedView = camera.getViewMatrix().invert();
        Vector4f rayWorld = invertedView.transform(eyeCoords);
        Vector3f worldRay = new Vector3f(rayWorld.x, rayWorld.y, rayWorld.z);
        return worldRay.normalize();
    }

    /**
     * Checks if a point is within a specified radius of the current ray
     * Useful for object selection
     */
    public boolean isPointNearRay(Vector3f point, float radius) {
        Vector3f directionToPoint = new Vector3f(point).sub(camera.getPosition());
        float lengthSquared = directionToPoint.lengthSquared();

        // Project the point onto the ray
        float dot = directionToPoint.dot(currentRay);
        Vector3f projection = new Vector3f(currentRay).mul(dot);

        // Calculate the distance from the point to the ray
        Vector3f distance = new Vector3f(directionToPoint).sub(projection);

        return distance.lengthSquared() < (radius * radius);
    }
}