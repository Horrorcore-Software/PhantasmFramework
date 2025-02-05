package com.horrorcoresoftware.core.physics;

import com.bulletphysics.dynamics.RigidBody;
import com.horrorcoresoftware.core.scene.Scene;

public class PhysicsManager {
    private static PhysicsManager instance;
    private PhysicsSystem physicsSystem;

    private PhysicsManager() {
        physicsSystem = new PhysicsSystem();
    }

    public static PhysicsManager getInstance() {
        if (instance == null) {
            instance = new PhysicsManager();
        }
        return instance;
    }

    public void update(float deltaTime) {
        physicsSystem.update(deltaTime);
    }

    public void addRigidBody(RigidBody body) {
        physicsSystem.getDynamicsWorld().addRigidBody(body);
    }

    public void removeRigidBody(RigidBody body) {
        physicsSystem.getDynamicsWorld().removeRigidBody(body);
    }

    public void cleanup() {
        physicsSystem.cleanup();
    }

    public PhysicsSystem getPhysicsSystem() {
        return physicsSystem;
    }
}