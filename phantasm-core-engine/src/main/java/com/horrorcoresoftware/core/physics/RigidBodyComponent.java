package com.horrorcoresoftware.core.physics;

import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.RigidBodyConstructionInfo;
import com.bulletphysics.linearmath.DefaultMotionState;
import com.bulletphysics.linearmath.Transform;
import com.horrorcoresoftware.core.scene.Component;
import javax.vecmath.Matrix4f;
import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;

public class RigidBodyComponent extends Component {
    private RigidBody rigidBody;
    private float mass;
    private boolean isStatic;
    private Vector3f inertia = new Vector3f(0, 0, 0);

    public RigidBodyComponent(float mass, CollisionShapeComponent collisionShape) {
        this.mass = mass;
        this.isStatic = mass == 0;

        if (!isStatic) {
            collisionShape.getCollisionShape().calculateLocalInertia(mass, inertia);
        }

        // Create the rigid body
        var transform = new Transform();
        transform.setIdentity();

        var motionState = new DefaultMotionState(transform);
        var rbInfo = new RigidBodyConstructionInfo(
                mass,
                motionState,
                collisionShape.getCollisionShape(),
                inertia
        );

        rigidBody = new RigidBody(rbInfo);
    }

    @Override
    public void initialize() {
        updateTransform();
        PhysicsManager.getInstance().addRigidBody(rigidBody);
    }

    @Override
    public void update(double deltaTime) {
        if (!isStatic) {
            var transform = new Transform();
            rigidBody.getMotionState().getWorldTransform(transform);

            // Update game object transform
            var pos = transform.origin;
            var rot = new Quat4f();
            transform.getRotation(rot);

            getGameObject().getTransform().setPosition(pos.x, pos.y, pos.z);
            // Convert Bullet quaternion to our rotation system
        }
    }

    private void updateTransform() {
        var transform = new Transform();
        transform.setIdentity();

        var pos = getGameObject().getTransform().getPosition();
        transform.origin.set(pos.x(), pos.y(), pos.z());

        // Set rotation from game object transform
        rigidBody.setWorldTransform(transform);
    }

    public void applyForce(Vector3f force) {
        if (!isStatic) {
            rigidBody.applyCentralForce(force);
        }
    }

    public void applyImpulse(Vector3f impulse) {
        if (!isStatic) {
            rigidBody.applyCentralImpulse(impulse);
        }
    }

    public RigidBody getRigidBody() {
        return rigidBody;
    }

    @Override
    public void cleanup() {
        PhysicsManager.getInstance().removeRigidBody(rigidBody);
    }
}