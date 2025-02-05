package com.horrorcoresoftware.core.graphics;


import static org.lwjgl.opengl.GL11.*;

/**
 * Represents a texture resource in the game engine.
 */
public class Texture implements AutoCloseable {
    private final int id;
    private final int width;
    private final int height;

    /**
     * Creates a new texture.
     * @param id The OpenGL texture ID
     * @param width The texture width
     * @param height The texture height
     */
    public Texture(int id, int width, int height) {
        this.id = id;
        this.width = width;
        this.height = height;
    }

    /**
     * Binds this texture for rendering.
     */
    public void bind() {
        glBindTexture(GL_TEXTURE_2D, id);
    }

    /**
     * Unbinds any currently bound texture.
     */
    public static void unbind() {
        glBindTexture(GL_TEXTURE_2D, 0);
    }

    /**
     * Gets the texture width.
     * @return The texture width in pixels
     */
    public int getWidth() {
        return width;
    }

    /**
     * Gets the texture height.
     * @return The texture height in pixels
     */
    public int getHeight() {
        return height;
    }

    /**
     * Gets the OpenGL texture ID.
     * @return The texture ID
     */
    public int getId() {
        return id;
    }

    /**
     * Cleans up the texture resources.
     */
    @Override
    public void close() {
        glDeleteTextures(id);
    }
}
