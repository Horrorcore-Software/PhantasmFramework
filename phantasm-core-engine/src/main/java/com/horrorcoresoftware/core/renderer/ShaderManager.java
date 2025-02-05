package com.horrorcoresoftware.core.renderer;

import com.horrorcoresoftware.core.Shader;
import com.horrorcoresoftware.core.ShaderLoader;

import java.util.HashMap;
import java.util.Map;

/**
 * Manages shader programs and their loading/unloading.
 */
public class ShaderManager {
    private final Map<String, Shader> shaders;
    private final ShaderLoader shaderLoader;
    private Shader currentShader;

    /**
     * Creates a new shader manager.
     */
    public ShaderManager() {
        this.shaders = new HashMap<>();
        this.shaderLoader = new ShaderLoader();
    }

    /**
     * Initializes the shader manager and loads default shaders.
     * @throws Exception if shader loading fails
     */
    public void initialize() throws Exception {
        // Load default shaders
        loadShader("default", "/shaders/default");
        loadShader("skybox", "/shaders/skybox");
        loadShader("terrain", "/shaders/terrain");
    }

    /**
     * Loads a shader program.
     * @param name The name to reference the shader by
     * @param path The shader file path (without extension)
     * @throws Exception if loading fails
     */
    public void loadShader(String name, String path) throws Exception {
        Shader shader = shaderLoader.loadResource(path);
        shaders.put(name, shader);
    }

    /**
     * Gets a loaded shader by name.
     * @param name The shader name
     * @return The shader program
     * @throws Exception if the shader is not loaded
     */
    public Shader getShader(String name) throws Exception {
        Shader shader = shaders.get(name);
        if (shader == null) {
            throw new Exception("Shader not found: " + name);
        }
        return shader;
    }

    /**
     * Sets the current active shader.
     * @param name The shader name
     * @throws Exception if the shader is not loaded
     */
    public void useShader(String name) throws Exception {
        Shader shader = getShader(name);
        if (shader != currentShader) {
            shader.bind();
            currentShader = shader;
        }
    }

    /**
     * Cleans up all shader resources.
     */
    public void cleanup() {
        shaders.values().forEach(Shader::close);
        shaders.clear();
        currentShader = null;
    }
}