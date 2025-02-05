package com.horrorcoresoftware.core;


import com.horrorcoresoftware.core.renderer.Renderer;
import com.horrorcoresoftware.core.scene.Scene;
import com.horrorcoresoftware.exceptions.EngineInitException;

/**
 * The main engine class that manages the game loop and core systems.
 */
public class Engine {
    private boolean isRunning;
    private Window window;
    private Timer timer;
    private InputManager inputManager;
    private ResourceManager resourceManager;
    private Renderer renderer;
    private Scene currentScene;

    /**
     * Initializes the engine with default configuration.
     */
    public Engine() {
        this.isRunning = false;
        this.timer = new Timer();
        this.inputManager = new InputManager();
        this.resourceManager = new ResourceManager();
    }

    /**
     * Initializes all engine systems and prepares for the game loop.
     * @throws EngineInitException if initialization fails
     */
    public void initialize() throws EngineInitException {
        window = new Window("Phantasm Framework", 1280, 720);
        window.initialize();
        renderer = new Renderer(window);
        try {
            renderer.initialize();
        } catch (Exception e) {
            throw new EngineInitException("Failed to initialize renderer", e);
        }
        currentScene = new Scene();
        currentScene.initialize();
        inputManager.initialize(window);
        resourceManager.initialize();
    }

    /**
     * Starts the main game loop.
     */
    public void start() {
        if (isRunning) return;

        isRunning = true;
        run();
    }

    /**
     * The main game loop. Handles timing, updates, and rendering.
     */
    private void run() {
        final double UPDATE_CAP = 1.0/60.0; // 60 FPS cap
        double lastTime = Timer.getTime();
        double unprocessedTime = 0;

        while (isRunning) {
            boolean render = false;
            double startTime = Timer.getTime();
            double passedTime = startTime - lastTime;
            lastTime = startTime;

            unprocessedTime += passedTime;

            while (unprocessedTime >= UPDATE_CAP) {
                unprocessedTime -= UPDATE_CAP;
                update(UPDATE_CAP);
                render = true;
            }

            if (render) {
                render();
                window.swapBuffers();
            } else {
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        cleanup();
    }

    /**
     * Updates all engine systems.
     * @param delta The time passed since the last update in seconds
     */
    private void update(double delta) {
        inputManager.update();
        currentScene.update(delta);
        // Additional system updates will be added here
    }

    /**
     * Renders the current frame.
     */
    private void render() {
        renderer.beginFrame();

        // Your rendering code will go here
        // For example:
        // renderer.getShaderManager().useShader("default");
        // renderer.renderScene(scene);

        renderer.endFrame();
    }

    /**
     * Cleans up resources and shuts down the engine.
     */
    private void cleanup() {
        if (currentScene != null) {
            currentScene.cleanup();
        }
        renderer.cleanup();
        window.dispose();
        resourceManager.cleanup();
    }

    public void setScene(Scene newScene) {
        if (currentScene != null) {
            currentScene.cleanup();
        }
        currentScene = newScene;
        currentScene.initialize();
    }

    public Scene getCurrentScene() {
        return currentScene;
    }
}
