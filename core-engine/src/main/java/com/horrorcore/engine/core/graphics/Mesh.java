package com.horrorcore.engine.core.graphics;

import org.lwjgl.system.MemoryStack;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import static org.lwjgl.opengl.GL30.*;

public class Mesh {
    // Vertex Array Object and Vertex Buffer Objects IDs
    private final int vaoId;        // Stores the vertex attribute configuration
    private final int vertexVboId;  // Stores vertex positions
    private final int normalVboId;  // Stores vertex normals
    private final int eboId;        // Stores vertex indices (Element Buffer Object)

    // Mesh statistics
    private final int vertexCount;  // Number of vertices in the mesh
    private final int indexCount;   // Number of indices in the mesh

    public Mesh(float[] vertices, float[] normals, int[] indices) {
        vertexCount = vertices.length / 3; // Each vertex has 3 components (x,y,z)
        indexCount = indices.length;

        // Create and bind a Vertex Array Object
        vaoId = glGenVertexArrays();
        glBindVertexArray(vaoId);

        try (MemoryStack stack = MemoryStack.stackPush()) {
            // Create and upload vertex position data
            FloatBuffer vertexBuffer = stack.mallocFloat(vertices.length);
            vertexBuffer.put(vertices).flip();

            vertexVboId = glGenBuffers();
            glBindBuffer(GL_ARRAY_BUFFER, vertexVboId);
            glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_STATIC_DRAW);
            glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
            glEnableVertexAttribArray(0);

            // Create and upload vertex normal data
            FloatBuffer normalBuffer = stack.mallocFloat(normals.length);
            normalBuffer.put(normals).flip();

            normalVboId = glGenBuffers();
            glBindBuffer(GL_ARRAY_BUFFER, normalVboId);
            glBufferData(GL_ARRAY_BUFFER, normalBuffer, GL_STATIC_DRAW);
            glVertexAttribPointer(1, 3, GL_FLOAT, false, 0, 0);
            glEnableVertexAttribArray(1);

            // Create and upload index data
            IntBuffer indexBuffer = stack.mallocInt(indices.length);
            indexBuffer.put(indices).flip();

            eboId = glGenBuffers();
            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboId);
            glBufferData(GL_ELEMENT_ARRAY_BUFFER, indexBuffer, GL_STATIC_DRAW);
        }

        // Unbind the VAO to prevent accidental modifications
        glBindVertexArray(0);
    }

    public void render() {
        // Bind the mesh's VAO
        glBindVertexArray(vaoId);

        // Draw the mesh using indexed rendering
        glDrawElements(GL_TRIANGLES, indexCount, GL_UNSIGNED_INT, 0);

        // Unbind the VAO
        glBindVertexArray(0);
    }

    public void cleanup() {
        // Delete the VBOs
        glDeleteBuffers(vertexVboId);
        glDeleteBuffers(normalVboId);
        glDeleteBuffers(eboId);

        // Delete the VAO
        glDeleteVertexArrays(vaoId);
    }
}