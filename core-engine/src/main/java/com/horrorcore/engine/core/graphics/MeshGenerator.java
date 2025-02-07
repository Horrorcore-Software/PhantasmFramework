package com.horrorcore.engine.core.graphics;

public class MeshGenerator {
    public static Mesh createCube() {
        // Vertices for a unit cube centered at the origin
        float[] vertices = {
                // Front face
                -0.5f, -0.5f,  0.5f,  // 0
                0.5f, -0.5f,  0.5f,  // 1
                0.5f,  0.5f,  0.5f,  // 2
                -0.5f,  0.5f,  0.5f,  // 3
                // Back face
                -0.5f, -0.5f, -0.5f,  // 4
                0.5f, -0.5f, -0.5f,  // 5
                0.5f,  0.5f, -0.5f,  // 6
                -0.5f,  0.5f, -0.5f   // 7
        };

        // Normals for each vertex
        float[] normals = {
                // Front face
                0.0f,  0.0f,  1.0f,
                0.0f,  0.0f,  1.0f,
                0.0f,  0.0f,  1.0f,
                0.0f,  0.0f,  1.0f,
                // Back face
                0.0f,  0.0f, -1.0f,
                0.0f,  0.0f, -1.0f,
                0.0f,  0.0f, -1.0f,
                0.0f,  0.0f, -1.0f
        };

        // Indices defining the triangles
        int[] indices = {
                // Front face
                0, 1, 2,
                2, 3, 0,
                // Right face
                1, 5, 6,
                6, 2, 1,
                // Back face
                7, 6, 5,
                5, 4, 7,
                // Left face
                4, 0, 3,
                3, 7, 4,
                // Bottom face
                4, 5, 1,
                1, 0, 4,
                // Top face
                3, 2, 6,
                6, 7, 3
        };

        return new Mesh(vertices, normals, indices);
    }
}