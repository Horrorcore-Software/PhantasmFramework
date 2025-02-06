package com.horrorcoresoftware;

import com.horrorcoresoftware.core.Engine;
import com.horrorcoresoftware.core.debug.Logger;
import com.horrorcoresoftware.exceptions.EngineInitException;

public class Main {
    public static void main(String[] args) {
        Logger logger = Logger.getInstance();
        Engine engine = new Engine();
        try {
            engine.initialize();
            engine.start();
        } catch (EngineInitException e) {
            logger.logError("Main", "Failed to initialize the engine", e);
            System.exit(1);
        }
    }
}