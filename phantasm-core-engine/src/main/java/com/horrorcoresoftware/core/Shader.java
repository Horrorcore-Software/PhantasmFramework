package com.horrorcoresoftware.core;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents a shader program resource in the game engine.
 */
public class Shader implements AutoCloseable {
    private final int programId;
    private final Map<String, Integer> uniforms;

    /**
     * Creates a new shader program.
     * @param programId The OpenGL shader program ID
     */
    public Shader(int programId) {
        this.programId = programId;
        this.uniforms = new HashMap<>();
    }

    /**
     * Binds this shader program for rendering.
     */
    public void bind() {
        glUseProgram(programId);
    }

    /**
     * Unbinds any currently bound shader program.
     */
    public static void unbind() {
        glUseProgram(0);
    }

    /**
     * Creates a uniform variable location in the shader.
     * @param uniformName The name of the uniform variable
     * @throws Exception if the uniform doesn't exist in the shader
     */
    public void createUniform(String uniformName) throws Exception {
        int uniformLocation = glGetUniformLocation(programId, uniformName);
        if (uniformLocation < 0) {
            throw new Exception("Could not find uniform: " + uniformName);
        }
        uniforms.put(uniformName, uniformLocation);
    }

    /**
     * Sets an integer uniform value.
     * @param uniformName The uniform name
     * @param value The value to set
     */
    public void setUniform(String uniformName, int value) {
        glUniform1i(uniforms.get(uniformName), value);
    }

    /**
     * Sets a float uniform value.
     * @param uniformName The uniform name
     * @param value The value to set
     */
    public void setUniform(String uniformName, float value) {
        glUniform1f(uniforms.get(uniformName), value);
    }

    /**
     * Sets a vec3 uniform value.
     * @param uniformName The uniform name
     * @param x The x component
     * @param y The y component
     * @param z The z component
     */
    public void setUniform(String uniformName, float x, float y, float z) {
        glUniform3f(uniforms.get(uniformName), x, y, z);
    }

    /**
     * Sets a 4x4 matrix uniform value.
     * @param uniformName The uniform name
     * @param matrix The matrix values in column-major order
     */
    public void setUniform(String uniformName, float[] matrix) {
        glUniformMatrix4fv(uniforms.get(uniformName), false, matrix);
    }

    /**
     * Gets the shader program ID.
     * @return The program ID
     */
    public int getProgramId() {
        return programId;
    }

    /**
     * Cleans up the shader program resources.
     */
    @Override
    public void close() {
        unbind();
        if (programId != 0) {
            glDeleteProgram(programId);
        }
    }
}