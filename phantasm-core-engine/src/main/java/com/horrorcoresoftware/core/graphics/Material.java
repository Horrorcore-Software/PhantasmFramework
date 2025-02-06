package com.horrorcoresoftware.core.graphics;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import static org.lwjgl.opengl.GL13.*;

public class Material {
    private Shader shader;
    private Texture diffuseMap;
    private Vector3f ambient;
    private Vector3f diffuse;
    private Vector3f specular;
    private float shininess;

    public Material(Shader shader) {
        this.shader = shader;
        this.ambient = new Vector3f(0.2f);
        this.diffuse = new Vector3f(0.8f);
        this.specular = new Vector3f(1.0f);
        this.shininess = 32.0f;
        setupUniforms();
    }

    private void setupUniforms() {
        try {
            shader.createUniform("model");
            shader.createUniform("view");
            shader.createUniform("projection");
            shader.createUniform("viewPos");
            shader.createUniform("lightPos");
            shader.createUniform("lightColor");
            shader.createUniform("textureDiffuse");
            shader.createUniform("materialAmbient");
            shader.createUniform("materialDiffuse");
            shader.createUniform("materialSpecular");
            shader.createUniform("materialShininess");
        } catch (Exception e) {
            throw new RuntimeException("Failed to create uniforms", e);
        }
    }

    public void bind(Matrix4f model, Matrix4f view, Matrix4f projection,
                     Vector3f cameraPos, Vector3f lightPos) {
        shader.bind();

        // Set transformation matrices
        shader.setUniform("model", model.get(new float[16]));
        shader.setUniform("view", view.get(new float[16]));
        shader.setUniform("projection", projection.get(new float[16]));

        // Set camera and light properties
        shader.setUniform("viewPos", cameraPos.x, cameraPos.y, cameraPos.z);
        shader.setUniform("lightPos", lightPos.x, lightPos.y, lightPos.z);
        shader.setUniform("lightColor", 1.0f, 1.0f, 1.0f);

        // Set material properties
        shader.setUniform("materialAmbient", ambient.x, ambient.y, ambient.z);
        shader.setUniform("materialDiffuse", diffuse.x, diffuse.y, diffuse.z);
        shader.setUniform("materialSpecular", specular.x, specular.y, specular.z);
        shader.setUniform("materialShininess", shininess);

        // Bind textures
        if (diffuseMap != null) {
            glActiveTexture(GL_TEXTURE0);
            diffuseMap.bind();
            shader.setUniform("textureDiffuse", 0);
        }
    }

    public void unbind() {
        if (diffuseMap != null) {
            diffuseMap.unbind();
        }
        Shader.unbind();
    }

    // Setters for material properties
    public void setDiffuseMap(Texture texture) {
        this.diffuseMap = texture;
    }

    public void setAmbient(float r, float g, float b) {
        this.ambient.set(r, g, b);
    }

    public void setDiffuse(float r, float g, float b) {
        this.diffuse.set(r, g, b);
    }

    public void setSpecular(float r, float g, float b) {
        this.specular.set(r, g, b);
    }

    public void setShininess(float shininess) {
        this.shininess = shininess;
    }
}