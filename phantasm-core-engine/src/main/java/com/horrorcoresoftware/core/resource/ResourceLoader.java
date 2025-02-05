package com.horrorcoresoftware.core.resource;

/**
 * Interface for resource loaders.
 * @param <T> The type of resource to load
 */
public interface ResourceLoader<T> {
    /**
     * Loads a resource from the specified path.
     * @param path The resource path
     * @return The loaded resource
     * @throws Exception if loading fails
     */
    T loadResource(String path) throws Exception;
}
