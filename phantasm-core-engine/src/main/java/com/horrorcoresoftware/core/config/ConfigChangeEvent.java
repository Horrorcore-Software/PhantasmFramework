package com.horrorcoresoftware.core.config;

/**
 * Event class for configuration changes.
 */
public record ConfigChangeEvent(String path, Object oldValue, Object newValue) {
}
