package com.horrorcoresoftware.core;

import com.horrorcoresoftware.core.debug.Logger;
import com.horrorcoresoftware.core.events.EventSystem;
import com.horrorcoresoftware.core.graphics.Window;
import com.horrorcoresoftware.core.input.InputManager;
import com.horrorcoresoftware.core.renderer.Renderer;
import com.horrorcoresoftware.core.resource.ResourceManager;
import com.horrorcoresoftware.core.scene.Scene;
import com.horrorcoresoftware.exceptions.EngineInitException;

/**
 * The main engine class that manages the game loop and core systems.
 * Coordinates all engine subsystems and maintains the game loop.
 */
public class Engine {
    // Core systems
    private boolean isRunning;
    private Window window;
    private TimeSystem timeSystem;
    private InputManager inputManager;
    private ResourceManager resourceManager;
    private Renderer renderer;
    private Scene currentScene;

    // Support systems
    private final Logger logger;
    private final EventSystem eventSystem;

    /**
     * Initializes the engine with default configuration.
     * Sets up all core systems but doesn't start them yet.
     */
    public Engine() {
        this.isRunning = false;
        this.timeSystem = new TimeSystem();
        this.inputManager = new InputManager();
        this.resourceManager = new ResourceManager();

        // Initialize support systems
        this.logger = Logger.getInstance();
        this.eventSystem = EventSystem.getInstance();

        // Set up logging categories
        logger.getCategory("Engine").setLevel(Logger.Level.INFO);
        logger.getCategory("Renderer").setLevel(Logger.Level.DEBUG);
        logger.getCategory("Scene").setLevel(Logger.Level.INFO);
        logger.getCategory("Resource").setLevel(Logger.Level.INFO);
    }

    /**
     * Initializes all engine systems and prepares for the game loop.
     * @throws EngineInitException if initialization fails
     */
    public void initialize() throws EngineInitException {
        logger.log(Logger.Level.INFO, "Engine", "Initializing engine systems...");

        try {
            // Create and initialize window
            window = new Window("Phantasm Framework", 1280, 720);
            window.initialize();
            logger.log(Logger.Level.DEBUG, "Engine", "Window initialized successfully");

            // Initialize renderer
            renderer = new Renderer(window);
            try {
                renderer.initialize();
                logger.log(Logger.Level.DEBUG, "Engine", "Renderer initialized successfully");
            } catch (Exception e) {
                throw new EngineInitException("Failed to initialize renderer", e);
            }

            // Initialize other systems
            resourceManager.initialize();

            // Create initial scene
            currentScene = new Scene();
            currentScene.initialize();

            logger.log(Logger.Level.INFO, "Engine", "Engine initialization complete");
        } catch (Exception e) {
            logger.logError("Engine", "Failed to initialize engine", e);
            throw new EngineInitException("Engine initialization failed", e);
        }
    }

    /**
     * Starts the main game loop.
     */
    public void start() {
        if (isRunning) {
            logger.log(Logger.Level.WARNING, "Engine", "Attempted to start already running engine");
            return;
        }

        logger.log(Logger.Level.INFO, "Engine", "Starting engine");
        isRunning = true;
        run();
    }

    /**
     * The main game loop. Handles timing, updates, and rendering.
     */
    private void run() {
        logger.log(Logger.Level.INFO, "Engine", "Entering main loop");

        try {
            while (isRunning) {
                timeSystem.update();

                // Process any queued events
                eventSystem.processEvents();

                // Fixed timestep updates for physics and other time-critical systems
                while (timeSystem.needsFixedUpdate()) {
                    fixedUpdate();
                }

                // Variable timestep update for general game logic
                update(timeSystem.getDeltaTime());

                // Render with interpolation between fixed updates
                render(timeSystem.getAlpha());

                // Handle window updates
                window.swapBufffers();

                // Performance monitoring
                if (timeSystem.getDeltaTime() > 0.1) { // Frame took longer than 100ms
                    logger.log(Logger.Level.WARNING, "Engine",
                            String.format("Long frame time: %.2fms",
                                    timeSystem.getDeltaTime() * 1000));
                }
            }
        } catch (Exception e) {
            logger.logError("Engine", "Fatal error in main loop", e);
            isRunning = false;
        }

        cleanup();
    }

    /**
     * Fixed timestep update for physics and other time-critical systems.
     */
    private void fixedUpdate() {
        try {
            // Update physics here when implemented
            currentScene.fixedUpdate(timeSystem.getFixedTimeStep());
        } catch (Exception e) {
            logger.logError("Engine", "Error during fixed update", e);
        }
    }

    /**
     * Updates all engine systems with variable timestep.
     * @param deltaTime Time passed since last update in seconds
     */
    private void update(double deltaTime) {
        try {
            inputManager.update();
            currentScene.update(deltaTime);
        } catch (Exception e) {
            logger.logError("Engine", "Error during update", e);
        }
    }

    /**
     * Renders the current frame.
     * @param alpha Interpolation factor between fixed updates (0-1)
     */
    private void render(double alpha) {
        try {
            renderer.beginFrame();

            // Your rendering code will go here once implemented
            // For example:
            // renderer.getShaderManager().useShader("default");
            // renderer.renderScene(scene, alpha);

            renderer.endFrame();
        } catch (Exception e) {
            logger.logError("Engine", "Error during render", e);
        }
    }

    /**
     * Cleans up resources and shuts down the engine.
     */
    private void cleanup() {
        logger.log(Logger.Level.INFO, "Engine", "Shutting down engine");

        try {
            if (currentScene != null) {
                currentScene.cleanup();
            }

            renderer.cleanup();
            window.dispose();
            resourceManager.cleanup();
            eventSystem.cleanup();

            logger.log(Logger.Level.INFO, "Engine", "Engine shutdown complete");
        } catch (Exception e) {
            logger.logError("Engine", "Error during cleanup", e);
        }
    }

    /**
     * Changes the current scene.
     * @param newScene The scene to switch to
     */
    public void setScene(Scene newScene) {
        logger.log(Logger.Level.INFO, "Engine", "Changing scene");

        if (currentScene != null) {
            currentScene.cleanup();
        }

        currentScene = newScene;
        currentScene.initialize();
    }

    // Getters for accessing engine systems
    public Scene getCurrentScene() { return currentScene; }
    public TimeSystem getTimeSystem() { return timeSystem; }
    public InputManager getInputManager() { return inputManager; }
    public ResourceManager getResourceManager() { return resourceManager; }
    public Renderer getRenderer() { return renderer; }
    public Window getWindow() { return window; }
}