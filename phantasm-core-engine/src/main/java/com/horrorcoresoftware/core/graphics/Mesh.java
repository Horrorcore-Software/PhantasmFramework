package com.horrorcoresoftware.core.graphics;

import org.lwjgl.opengl.GL30;

import java.util.List;

import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;

public class Mesh implements AutoCloseable {
    private int vaoId;
    private int vertexCount;
    private List<Integer> vboIds;

    public void render() {
        // Bind VAO and draw
        glBindVertexArray(vaoId);
        glDrawElements(GL_TRIANGLES, vertexCount, GL_UNSIGNED_INT, 0);
        glBindVertexArray(0);
    }

    @Override
    public void close() {
        cleanup();
    }

    public void cleanup() {
        // Delete VBOs
        vboIds.forEach(GL30::glDeleteBuffers);

        // Delete VAO
        GL30.glDeleteVertexArrays(vaoId);
    }
}