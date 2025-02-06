package com.horrorcoresoftware.core;

import com.horrorcoresoftware.core.debug.Logger;
import com.horrorcoresoftware.core.events.EventSystem;
import com.horrorcoresoftware.core.graphics.Window;
import com.horrorcoresoftware.core.input.InputManager;
import com.horrorcoresoftware.core.renderer.Renderer;
import com.horrorcoresoftware.core.resource.ResourceManager;
import com.horrorcoresoftware.core.scene.Scene;
import com.horrorcoresoftware.exceptions.EngineInitException;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

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

            // Initialize input system first
            inputManager.initialize(window.getWindowHandle());
            logger.log(Logger.Level.DEBUG, "Engine", "Input system initialized successfully");

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

        double lastFpsTime = 0;
        int fps = 0;

        try {
            while (isRunning && !window.shouldClose()) {
                timeSystem.update();
                double currentTime = timeSystem.getCurrentTime();

                // FPS counter
                if (currentTime - lastFpsTime > 1.0) {
                    logger.log(Logger.Level.DEBUG, "Engine", String.format("FPS: %d", fps));
                    fps = 0;
                    lastFpsTime += 1.0;
                }

                // Process events
                eventSystem.processEvents();
                inputManager.update();

                // Input handling
                if (inputManager.isKeyPressed(GLFW.GLFW_KEY_ESCAPE)) {
                    isRunning = false;
                    continue;
                }

                // Fixed timestep updates
                while (timeSystem.needsFixedUpdate()) {
                    fixedUpdate();
                }

                // Variable timestep update
                update(timeSystem.getDeltaTime());

                // Render
                renderer.beginFrame();
                render(timeSystem.getAlpha());
                renderer.endFrame();

                // Window update
                window.swapBuffers();

                fps++;

                // Small sleep to prevent CPU from maxing out
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    logger.logError("Engine", "Sleep interrupted", e);
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
            if (inputManager.isKeyPressed(GLFW.GLFW_KEY_SPACE)) {
                logger.log(Logger.Level.DEBUG, "Engine", "Space key pressed!");
            }
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

            // Clear the framebuffer
            GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

            // Your rendering code will go here
            // For now, just set a background color so we can see something
            GL11.glClearColor(0.2f, 0.3f, 0.3f, 1.0f);

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
            inputManager.cleanup(); // Add this line
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