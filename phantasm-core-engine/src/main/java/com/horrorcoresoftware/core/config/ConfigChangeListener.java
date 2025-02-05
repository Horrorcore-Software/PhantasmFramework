package com.horrorcoresoftware.core.config;

/**
 * Interface for configuration change listeners.
 */
public interface ConfigChangeListener {
    void onConfigChanged(ConfigChangeEvent event);
}
