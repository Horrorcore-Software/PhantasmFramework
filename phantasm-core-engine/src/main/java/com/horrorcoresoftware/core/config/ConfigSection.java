    package com.horrorcoresoftware.core.config;

import java.util.HashMap;
import java.util.Map; /**
 * Represents a section of configuration settings.
 */
public record ConfigSection(Map<String, Object> values) {
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

    @Override
    public Map<String, Object> values() {
        return new HashMap<>(values);
    }
}
