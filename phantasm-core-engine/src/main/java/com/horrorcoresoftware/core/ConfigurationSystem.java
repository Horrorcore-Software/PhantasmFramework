package com.horrorcoresoftware.core;

import com.horrorcoresoftware.exceptions.ConfigurationException;
import java.io.*;
import java.util.*;
import java.nio.file.*;
import com.google.gson.*;

/**
 * Manages engine and game configuration settings.
 * Supports hierarchical configuration with inheritance and hot-reloading.
 */
public class ConfigurationSystem {
    private static ConfigurationSystem instance;
    private final Map<String, ConfigSection> sections;
    private final List<ConfigChangeListener> listeners;
    private final Gson gson;
    private String configPath;

    private ConfigurationSystem() {
        this.sections = new HashMap<>();
        this.listeners = new ArrayList<>();
        this.gson = new GsonBuilder().setPrettyPrinting().create();
        this.configPath = "config/";
    }

    public static ConfigurationSystem getInstance() {
        if (instance == null) {
            instance = new ConfigurationSystem();
        }
        return instance;
    }

    /**
     * Initializes the configuration system and loads default settings.
     * @throws ConfigurationException if initialization fails
     */
    public void initialize() throws ConfigurationException {
        try {
            // Create config directory if it doesn't exist
            Files.createDirectories(Paths.get(configPath));

            // Load engine defaults
            loadSection("engine", createDefaultEngineConfig());

            // Load graphics defaults
            loadSection("graphics", createDefaultGraphicsConfig());

            // Load input defaults
            loadSection("input", createDefaultInputConfig());

        } catch (Exception e) {
            throw new ConfigurationException("Failed to initialize configuration system", e);
        }
    }

    /**
     * Creates default engine configuration settings.
     */
    private Map<String, Object> createDefaultEngineConfig() {
        Map<String, Object> config = new HashMap<>();
        config.put("targetFPS", 60);
        config.put("fixedTimeStep", 1.0/60.0);
        config.put("maxPhysicsIterations", 8);
        config.put("resourcePath", "resources/");
        return config;
    }

    /**
     * Creates default graphics configuration settings.
     */
    private Map<String, Object> createDefaultGraphicsConfig() {
        Map<String, Object> config = new HashMap<>();
        config.put("width", 1280);
        config.put("height", 720);
        config.put("fullscreen", false);
        config.put("vsync", true);
        config.put("msaa", 4);
        return config;
    }

    /**
     * Creates default input configuration settings.
     */
    private Map<String, Object> createDefaultInputConfig() {
        Map<String, Object> config = new HashMap<>();
        config.put("mouseSensitivity", 1.0f);
        config.put("invertY", false);
        config.put("gamepadEnabled", true);
        return config;
    }

    /**
     * Loads a configuration section from a file or creates it with default values.
     * @param sectionName The name of the configuration section
     * @param defaults Default values if file doesn't exist
     * @throws ConfigurationException if loading fails
     */
    public void loadSection(String sectionName, Map<String, Object> defaults) throws ConfigurationException {
        Path configFile = Paths.get(configPath, sectionName + ".json");
        ConfigSection section;

        try {
            if (Files.exists(configFile)) {
                String json = Files.readString(configFile);
                Map<String, Object> values = gson.fromJson(json, Map.class);
                section = new ConfigSection(values);
            } else {
                section = new ConfigSection(defaults);
                saveSection(sectionName);
            }
            sections.put(sectionName, section);
        } catch (Exception e) {
            throw new ConfigurationException("Failed to load configuration section: " + sectionName, e);
        }
    }

    /**
     * Saves a configuration section to file.
     * @param sectionName The name of the section to save
     * @throws ConfigurationException if saving fails
     */
    public void saveSection(String sectionName) throws ConfigurationException {
        ConfigSection section = sections.get(sectionName);
        if (section == null) {
            throw new ConfigurationException("Configuration section not found: " + sectionName);
        }

        try {
            Path configFile = Paths.get(configPath, sectionName + ".json");
            String json = gson.toJson(section.getValues());
            Files.writeString(configFile, json);
        } catch (Exception e) {
            throw new ConfigurationException("Failed to save configuration section: " + sectionName, e);
        }
    }

    /**
     * Gets a configuration value.
     * @param path The configuration path (section.key)
     * @param defaultValue Default value if not found
     * @return The configuration value
     */
    public <T> T getValue(String path, T defaultValue) {
        String[] parts = path.split("\\.");
        if (parts.length != 2) {
            return defaultValue;
        }

        ConfigSection section = sections.get(parts[0]);
        if (section == null) {
            return defaultValue;
        }

        return section.getValue(parts[1], defaultValue);
    }

    /**
     * Sets a configuration value.
     * @param path The configuration path (section.key)
     * @param value The value to set
     * @throws ConfigurationException if the path is invalid
     */
    public void setValue(String path, Object value) throws ConfigurationException {
        String[] parts = path.split("\\.");
        if (parts.length != 2) {
            throw new ConfigurationException("Invalid configuration path: " + path);
        }

        ConfigSection section = sections.get(parts[0]);
        if (section == null) {
            throw new ConfigurationException("Configuration section not found: " + parts[0]);
        }

        Object oldValue = section.getValue(parts[1], null);
        section.setValue(parts[1], value);

        // Notify listeners
        if (!Objects.equals(oldValue, value)) {
            notifyListeners(path, oldValue, value);
        }
    }

    /**
     * Adds a configuration change listener.
     * @param listener The listener to add
     */
    public void addChangeListener(ConfigChangeListener listener) {
        listeners.add(listener);
    }

    /**
     * Removes a configuration change listener.
     * @param listener The listener to remove
     */
    public void removeChangeListener(ConfigChangeListener listener) {
        listeners.remove(listener);
    }

    private void notifyListeners(String path, Object oldValue, Object newValue) {
        ConfigChangeEvent event = new ConfigChangeEvent(path, oldValue, newValue);
        for (ConfigChangeListener listener : listeners) {
            listener.onConfigChanged(event);
        }
    }

    /**
     * Resets a section to default values.
     * @param sectionName The section to reset
     * @throws ConfigurationException if the section doesn't exist
     */
    public void resetSection(String sectionName) throws ConfigurationException {
        ConfigSection section = sections.get(sectionName);
        if (section == null) {
            throw new ConfigurationException("Configuration section not found: " + sectionName);
        }

        switch (sectionName) {
            case "engine" -> loadSection(sectionName, createDefaultEngineConfig());
            case "graphics" -> loadSection(sectionName, createDefaultGraphicsConfig());
            case "input" -> loadSection(sectionName, createDefaultInputConfig());
            default -> throw new ConfigurationException("No defaults for section: " + sectionName);
        }
    }

    public void setConfigPath(String path) {
        this.configPath = path;
    }
}

/**
 * Represents a section of configuration settings.
 */
class ConfigSection {
    private final Map<String, Object> values;

    public ConfigSection(Map<String, Object> values) {
        this.values = new HashMap<>(values);
    }

    @SuppressWarnings("unchecked")
    public <T> T getValue(String key, T defaultValue) {
        Object value = values.get(key);
        if (value != null && defaultValue != null &&
                value.getClass().isAssignableFrom(defaultValue.getClass())) {
            return (T) value;
        }
        return defaultValue;
    }

    public void setValue(String key, Object value) {
        values.put(key, value);
    }

    public Map<String, Object> getValues() {
        return new HashMap<>(values);
    }
}

/**
 * Interface for configuration change listeners.
 */
interface ConfigChangeListener {
    void onConfigChanged(ConfigChangeEvent event);
}

/**
 * Event class for configuration changes.
 */
class ConfigChangeEvent {
    private final String path;
    private final Object oldValue;
    private final Object newValue;

    public ConfigChangeEvent(String path, Object oldValue, Object newValue) {
        this.path = path;
        this.oldValue = oldValue;
        this.newValue = newValue;
    }

    public String getPath() { return path; }
    public Object getOldValue() { return oldValue; }
    public Object getNewValue() { return newValue; }
}