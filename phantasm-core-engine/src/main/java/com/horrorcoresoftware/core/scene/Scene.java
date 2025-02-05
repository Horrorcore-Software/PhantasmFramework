package com.horrorcoresoftware.core.scene;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The Scene class represents a collection of game objects and manages their lifecycle.
 * It serves as the main container for all entities in the game world.
 */
public class Scene {
    private final List<GameObject> gameObjects;
    private final Map<String, GameObject> gameObjectsMap;
    private boolean isActive;

    public Scene() {
        this.gameObjects = new ArrayList<>();
        this.gameObjectsMap = new HashMap<>();
        this.isActive = false;
    }

    /**
     * Initializes the scene and all its game objects.
     */
    public void initialize() {
        for (GameObject gameObject : gameObjects) {
            gameObject.initialize();
        }
        isActive = true;
    }

    /**
     * Updates all game objects in the scene.
     * @param deltaTime Time passed since last update in seconds
     */
    public void update(double deltaTime) {
        if (!isActive) return;

        // Update all game objects
        for (GameObject gameObject : gameObjects) {
            if (gameObject.isActive()) {
                gameObject.update(deltaTime);
            }
        }
    }

    /**
     * Adds a game object to the scene.
     * @param gameObject The game object to add
     */
    public void addGameObject(GameObject gameObject) {
        gameObjects.add(gameObject);
        if (gameObject.getName() != null) {
            gameObjectsMap.put(gameObject.getName(), gameObject);
        }

        // If scene is already active, initialize the game object immediately
        if (isActive) {
            gameObject.initialize();
        }
    }

    /**
     * Finds a game object by name.
     * @param name The name of the game object
     * @return The game object, or null if not found
     */
    public GameObject findGameObject(String name) {
        return gameObjectsMap.get(name);
    }

    /**
     * Removes a game object from the scene.
     * @param gameObject The game object to remove
     */
    public void removeGameObject(GameObject gameObject) {
        gameObjects.remove(gameObject);
        if (gameObject.getName() != null) {
            gameObjectsMap.remove(gameObject.getName());
        }
        gameObject.cleanup();
    }

    /**
     * Cleans up all resources used by the scene.
     */
    public void cleanup() {
        for (GameObject gameObject : gameObjects) {
            gameObject.cleanup();
        }
        gameObjects.clear();
        gameObjectsMap.clear();
        isActive = false;
    }
}