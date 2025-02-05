package com.horrorcoresoftware.core;


import org.lwjgl.system.MemoryStack;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.stb.STBImage.*;

/**
 * Loads and manages texture resources.
 */
public class TextureLoader implements ResourceLoader<Texture> {

    /**
     * Loads a texture from the specified path.
     * @param path The texture file path
     * @return The loaded texture
     * @throws Exception if loading fails
     */
    @Override
    public Texture loadResource(String path) throws Exception {
        ByteBuffer imageBuffer;
        int width, height;
        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer w = stack.mallocInt(1);
            IntBuffer h = stack.mallocInt(1);
            IntBuffer channels = stack.mallocInt(1);

            // Load image
            stbi_set_flip_vertically_on_load(true);
            imageBuffer = stbi_load(path, w, h, channels, 4);
            if (imageBuffer == null) {
                throw new Exception("Failed to load texture file: " + stbi_failure_reason());
            }

            width = w.get();
            height = h.get();
        }

        // Create texture
        int textureId = glGenTextures();
        Texture texture = new Texture(textureId, width, height);

        // Bind and set texture parameters
        glBindTexture(GL_TEXTURE_2D, textureId);
        glPixelStorei(GL_UNPACK_ALIGNMENT, 1);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, imageBuffer);
        glGenerateMipmap(GL_TEXTURE_2D);

        // Set texture parameters
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

        // Free image memory
        stbi_image_free(imageBuffer);

        return texture;
    }
}

