package com.horrorcoresoftware.core.debug;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * Provides logging and debugging functionality for the engine.
 * Supports different log levels, categories, and output formats.
 */
public class Logger {
    public enum Level {
        DEBUG(0),
        INFO(1),
        WARNING(2),
        ERROR(3);

        private final int priority;
        Level(int priority) { this.priority = priority; }
    }

    private static final Logger instance = new Logger();
    private final Map<String, LogCategory> categories;
    private Level globalLevel;
    private boolean consoleOutput;
    private String logFile;

    private Logger() {
        this.categories = new HashMap<>();
        this.globalLevel = Level.INFO;
        this.consoleOutput = true;
    }

    public static Logger getInstance() {
        return instance;
    }

    /**
     * Creates or gets a logging category.
     * Categories allow for fine-grained control over logging levels.
     */
    public LogCategory getCategory(String name) {
        return categories.computeIfAbsent(name, LogCategory::new);
    }

    /**
     * Logs a message with the specified level and category.
     */
    public void log(Level level, String category, String message) {
        LogCategory cat = getCategory(category);
        if (shouldLog(level, cat)) {
            String formattedMessage = formatLogMessage(level, category, message);
            outputMessage(formattedMessage);
        }
    }

    /**
     * Logs an error with stack trace.
     */
    public void logError(String category, String message, Throwable error) {
        if (shouldLog(Level.ERROR, getCategory(category))) {
            StringWriter sw = new StringWriter();
            error.printStackTrace(new PrintWriter(sw));
            String stackTrace = sw.toString();

            String fullMessage = message + "\n" + stackTrace;
            String formattedMessage = formatLogMessage(Level.ERROR, category, fullMessage);
            outputMessage(formattedMessage);
        }
    }

    /**
     * Formats a log message with timestamp and metadata.
     */
    private String formatLogMessage(Level level, String category, String message) {
        LocalDateTime now = LocalDateTime.now();
        String timestamp = now.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        return String.format("[%s] %s [%s]: %s",
                timestamp, level, category, message);
    }

    /**
     * Determines if a message should be logged based on level settings.
     */
    private boolean shouldLog(Level level, LogCategory category) {
        Level effectiveLevel = category.getLevel() != null ?
                category.getLevel() : globalLevel;
        return level.priority >= effectiveLevel.priority;
    }

    /**
     * Outputs a message to configured destinations (console, file, etc).
     */
    private void outputMessage(String message) {
        if (consoleOutput) {
            System.out.println(message);
        }
        // Add file output, network logging, etc. as needed
    }

    // Configuration methods
    public void setGlobalLevel(Level level) { this.globalLevel = level; }
    public void setConsoleOutput(boolean enabled) { this.consoleOutput = enabled; }
    public void setLogFile(String path) { this.logFile = path; }

    /**
     * Represents a logging category with its own level setting.
     */
    public static class LogCategory {
        private final String name;
        private Level level;

        public LogCategory(String name) {
            this.name = name;
        }

        public void setLevel(Level level) { this.level = level; }
        public Level getLevel() { return level; }
        public String getName() { return name; }
    }
}