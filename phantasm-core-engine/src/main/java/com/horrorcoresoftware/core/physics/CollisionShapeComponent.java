package com.horrorcoresoftware.core.physics;

import com.bulletphysics.collision.shapes.*;
import com.horrorcoresoftware.core.scene.Component;
import javax.vecmath.Vector3f;

public class CollisionShapeComponent extends Component {
    private CollisionShape shape;

    public static CollisionShapeComponent createBox(Vector3f halfExtents) {
        var component = new CollisionShapeComponent();
        component.shape = new BoxShape(halfExtents);
        return component;
    }

    public static CollisionShapeComponent createSphere(float radius) {
        var component = new CollisionShapeComponent();
        component.shape = new SphereShape(radius);
        return component;
    }

    public static CollisionShapeComponent createCapsule(float radius, float height) {
        var component = new CollisionShapeComponent();
        component.shape = new CapsuleShape(radius, height);
        return component;
    }

    public static CollisionShapeComponent createCylinder(Vector3f halfExtents) {
        var component = new CollisionShapeComponent();
        component.shape = new CylinderShape(halfExtents);
        return component;
    }

    public CollisionShape getCollisionShape() {
        return shape;
    }

    @Override
    public void cleanup() {
        // Clean up the collision shape
        if (shape != null) {
            // JBullet doesn't have explicit cleanup, but we could add reference counting here
            shape = null;
        }
    }
}