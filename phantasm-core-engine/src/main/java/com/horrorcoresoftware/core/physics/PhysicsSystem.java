package com.horrorcoresoftware.core.physics;

import com.bulletphysics.collision.broadphase.BroadphaseInterface;
import com.bulletphysics.collision.broadphase.DbvtBroadphase;
import com.bulletphysics.collision.dispatch.CollisionDispatcher;
import com.bulletphysics.collision.dispatch.DefaultCollisionConfiguration;
import com.bulletphysics.dynamics.DiscreteDynamicsWorld;
import com.bulletphysics.dynamics.DynamicsWorld;
import com.bulletphysics.dynamics.constraintsolver.SequentialImpulseConstraintSolver;
import javax.vecmath.Vector3f;

public class PhysicsSystem {
    private DynamicsWorld dynamicsWorld;
    private float fixedTimeStep = 1.0f / 60.0f;
    private int maxSubSteps = 10;

    public PhysicsSystem() {
        // Initialize Bullet Physics components
        var broadphase = new DbvtBroadphase();
        var collisionConfiguration = new DefaultCollisionConfiguration();
        var dispatcher = new CollisionDispatcher(collisionConfiguration);
        var solver = new SequentialImpulseConstraintSolver();

        dynamicsWorld = new DiscreteDynamicsWorld(dispatcher, broadphase, solver, collisionConfiguration);
        dynamicsWorld.setGravity(new Vector3f(0, -9.81f, 0));
    }

    public void update(float deltaTime) {
        dynamicsWorld.stepSimulation(deltaTime, maxSubSteps, fixedTimeStep);
    }

    public DynamicsWorld getDynamicsWorld() {
        return dynamicsWorld;
    }

    public void setGravity(Vector3f gravity) {
        dynamicsWorld.setGravity(gravity);
    }

    public void cleanup() {
        // Clean up physics resources
        if (dynamicsWorld != null) {
            for (int i = dynamicsWorld.getNumCollisionObjects() - 1; i >= 0; i--) {
                var obj = dynamicsWorld.getCollisionObjectArray().getQuick(i);
                dynamicsWorld.removeCollisionObject(obj);
            }
        }
    }
}