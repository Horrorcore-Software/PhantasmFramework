package com.horrorcoresoftware.core.graphics;

import com.horrorcoresoftware.core.resource.ResourceLoader;

import static org.lwjgl.opengl.GL20.*;

/**
 * Loads and manages shader resources.
 */
public class ShaderLoader implements ResourceLoader<Shader> {

    /**
     * Loads a shader program from vertex and fragment shader files.
     * @param path The base path for shader files (without extension)
     * @return The compiled shader program
     * @throws Exception if loading or compilation fails
     */
    @Override
    public Shader loadResource(String path) throws Exception {
        String vertexPath = path + ".vert";
        String fragmentPath = path + ".frag";

        // Load shader source code
        String vertexSource = loadShaderSource(vertexPath);
        String fragmentSource = loadShaderSource(fragmentPath);

        // Create shader program
        int programId = glCreateProgram();
        int vertexShaderId = compileShader(GL_VERTEX_SHADER, vertexSource);
        int fragmentShaderId = compileShader(GL_FRAGMENT_SHADER, fragmentSource);

        // Link program
        glAttachShader(programId, vertexShaderId);
        glAttachShader(programId, fragmentShaderId);
        glLinkProgram(programId);

        // Check for linking errors
        if (glGetProgrami(programId, GL_LINK_STATUS) == GL_FALSE) {
            throw new Exception("Shader program linking failed: " + glGetProgramInfoLog(programId));
        }

        // Delete shaders as they're no longer needed
        glDeleteShader(vertexShaderId);
        glDeleteShader(fragmentShaderId);

        return new Shader(programId);
    }

    /**
     * Compiles a shader from source code.
     * @param type The shader type (vertex or fragment)
     * @param source The shader source code
     * @return The compiled shader ID
     * @throws Exception if compilation fails
     */
    private int compileShader(int type, String source) throws Exception {
        int shaderId = glCreateShader(type);
        glShaderSource(shaderId, source);
        glCompileShader(shaderId);

        if (glGetShaderi(shaderId, GL_COMPILE_STATUS) == GL_FALSE) {
            String shaderType = type == GL_VERTEX_SHADER ? "vertex" : "fragment";
            throw new Exception(shaderType + " shader compilation failed: " + glGetShaderInfoLog(shaderId));
        }

        return shaderId;
    }

    /**
     * Loads shader source code from a file.
     * @param path The shader file path
     * @return The shader source code
     * @throws Exception if loading fails
     */
    private String loadShaderSource(String path) throws Exception {
        try {
            return new String(java.nio.file.Files.readAllBytes(java.nio.file.Paths.get(path)));
        } catch (Exception e) {
            throw new Exception("Failed to load shader file: " + path, e);
        }
    }
}
