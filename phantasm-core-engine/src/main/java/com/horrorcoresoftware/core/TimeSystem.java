package com.horrorcoresoftware.core;

/**
 * Manages time-related operations and provides detailed timing information
 * for the game loop and performance monitoring.
 */
public class TimeSystem {
    private static final int FPS_SAMPLE_SIZE = 60;

    private double currentTime;
    private double deltaTime;
    private double timeScale;
    private double fixedTimeStep;
    private double accumulator;

    private final double[] fpsHistory;
    private int fpsIndex;
    private double frameTime;
    private int frameCount;

    public TimeSystem() {
        this.currentTime = getTime();
        this.timeScale = 1.0;
        this.fixedTimeStep = 1.0 / 60.0; // 60 Hz physics updates
        this.fpsHistory = new double[FPS_SAMPLE_SIZE];
        this.frameCount = 0;
    }

    /**
     * Updates the time system. Should be called at the start of each frame.
     */
    public void update() {
        double newTime = getTime();
        deltaTime = (newTime - currentTime) * timeScale;
        currentTime = newTime;

        // Update FPS calculation
        frameTime += deltaTime;
        frameCount++;

        if (frameTime >= 1.0) {
            double fps = frameCount / frameTime;
            fpsHistory[fpsIndex] = fps;
            fpsIndex = (fpsIndex + 1) % FPS_SAMPLE_SIZE;

            frameTime = 0;
            frameCount = 0;
        }
    }

    /**
     * Accumulates time for fixed timestep updates.
     * @return true if a fixed update should occur
     */
    public boolean needsFixedUpdate() {
        accumulator += deltaTime;
        if (accumulator >= fixedTimeStep) {
            accumulator -= fixedTimeStep;
            return true;
        }
        return false;
    }

    /**
     * Gets the current system time in seconds.
     */
    private static double getTime() {
        return System.nanoTime() / 1_000_000_000.0;
    }

    /**
     * Calculates the average FPS over the sample period.
     * @return The average frames per second
     */
    public double getAverageFPS() {
        double sum = 0;
        for (double fps : fpsHistory) {
            sum += fps;
        }
        return sum / FPS_SAMPLE_SIZE;
    }

    /**
     * Gets interpolation factor for smooth rendering between fixed updates.
     * @return The interpolation alpha value between 0 and 1
     */
    public double getAlpha() {
        return accumulator / fixedTimeStep;
    }

    // Getters and setters
    public double getDeltaTime() { return deltaTime; }
    public double getFixedTimeStep() { return fixedTimeStep; }
    public double getTimeScale() { return timeScale; }
    public void setTimeScale(double scale) { this.timeScale = scale; }
    public void setFixedTimeStep(double step) { this.fixedTimeStep = step; }
}