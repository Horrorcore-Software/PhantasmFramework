package com.horrorcoresoftware.core;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.horrorcoresoftware.exceptions.EngineInitException;
import com.horrorcoresoftware.exceptions.ResourceLoadException;
import org.lwjgl.system.MemoryUtil;

/**
 * Manages all game resources including textures, models, shaders, and sounds.
 * Implements resource pooling and reference counting for efficient memory management.
 */
public class ResourceManager {
    private final Map<String, ResourceEntry<?>> resources;
    private final Set<String> resourcePaths;
    private static final String DEFAULT_RESOURCE_PATH = "resources/";

    /**
     * Creates a new ResourceManager with default settings.
     */
    public ResourceManager() {
        // Using ConcurrentHashMap for thread safety
        this.resources = new ConcurrentHashMap<>();
        this.resourcePaths = new HashSet<>();
        this.resourcePaths.add(DEFAULT_RESOURCE_PATH);
    }

    /**
     * Initializes the resource management system.
     * @throws EngineInitException if initialization fails
     */
    public void initialize() throws EngineInitException {
        try {
            // Create default resource directories if they don't exist
            createDefaultDirectories();
        } catch (IOException e) {
            throw new EngineInitException("Failed to initialize resource system", e);
        }
    }

    /**
     * Creates the default directory structure for resources.
     * @throws IOException if directory creation fails
     */
    private void createDefaultDirectories() throws IOException {
        String[] dirs = {
                "textures",
                "models",
                "shaders",
                "sounds",
                "materials"
        };

        for (String dir : dirs) {
            File directory = new File(DEFAULT_RESOURCE_PATH + dir);
            if (!directory.exists()) {
                if (!directory.mkdirs()) {
                    throw new IOException("Failed to create directory: " + directory.getPath());
                }
            }
        }
    }

    /**
     * Loads a resource of the specified type from the given path.
     * @param <T> The type of resource to load
     * @param path The resource path
     * @param loader The resource loader to use
     * @return The loaded resource
     * @throws ResourceLoadException if the resource cannot be loaded
     */
    public <T> T loadResource(String path, ResourceLoader<T> loader) throws ResourceLoadException {
        String fullPath = resolveResourcePath(path);

        // Check if resource is already loaded
        @SuppressWarnings("unchecked")
        ResourceEntry<T> entry = (ResourceEntry<T>) resources.get(fullPath);

        if (entry != null) {
            entry.incrementRefCount();
            return entry.getResource();
        }

        // Load new resource
        try {
            T resource = loader.loadResource(fullPath);
            ResourceEntry<T> newEntry = new ResourceEntry<>(resource);
            resources.put(fullPath, newEntry);
            return resource;
        } catch (Exception e) {
            throw new ResourceLoadException("Failed to load resource: " + path, e);
        }
    }

    /**
     * Releases a reference to a resource. When the reference count reaches zero,
     * the resource is unloaded.
     * @param path The resource path
     */
    public void releaseResource(String path) {
        String fullPath = resolveResourcePath(path);
        ResourceEntry<?> entry = resources.get(fullPath);

        if (entry != null) {
            if (entry.decrementRefCount() <= 0) {
                unloadResource(fullPath);
            }
        }
    }

    /**
     * Unloads a resource and frees its memory.
     * @param fullPath The full resource path
     */
    private void unloadResource(String fullPath) {
        ResourceEntry<?> entry = resources.remove(fullPath);
        if (entry != null && entry.getResource() instanceof AutoCloseable) {
            try {
                ((AutoCloseable) entry.getResource()).close();
            } catch (Exception e) {
                // Log error but continue with resource removal
                System.err.println("Error closing resource: " + fullPath);
                e.printStackTrace();
            }
        }
    }

    /**
     * Resolves a resource path to its full path.
     * @param path The resource path to resolve
     * @return The full resource path
     */
    private String resolveResourcePath(String path) {
        if (path.startsWith("/")) {
            return path.substring(1);
        }
        return DEFAULT_RESOURCE_PATH + path;
    }

    /**
     * Reads a file into a ByteBuffer.
     * @param path The file path
     * @return ByteBuffer containing the file data
     * @throws IOException if the file cannot be read
     */
    public ByteBuffer readFileToBuffer(String path) throws IOException {
        Path filePath = Paths.get(resolveResourcePath(path));
        try (FileChannel fc = FileChannel.open(filePath)) {
            ByteBuffer buffer = MemoryUtil.memAlloc((int) fc.size());
            while (buffer.hasRemaining()) {
                fc.read(buffer);
            }
            buffer.flip();
            return buffer;
        }
    }

    /**
     * Adds a resource search path.
     * @param path The path to add
     */
    public void addResourcePath(String path) {
        resourcePaths.add(path.endsWith("/") ? path : path + "/");
    }

    /**
     * Cleans up all resources and releases memory.
     */
    public void cleanup() {
        resources.forEach((path, entry) -> {
            try {
                unloadResource(path);
            } catch (Exception e) {
                System.err.println("Error during cleanup of resource: " + path);
                e.printStackTrace();
            }
        });
        resources.clear();
    }
}



