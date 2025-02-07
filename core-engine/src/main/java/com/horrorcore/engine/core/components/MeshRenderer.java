package com.horrorcore.engine.core.components;

import com.horrorcore.engine.core.Component;
import com.horrorcore.engine.core.graphics.Mesh;
import com.horrorcore.engine.core.graphics.BasicShader;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.glEnable;

public class MeshRenderer extends Component {
    private Mesh mesh;
    private BasicShader shader;
    private Vector3f color;

    // Add references to view and projection matrices
    private Matrix4f viewMatrix;
    private Matrix4f projectionMatrix;

    public MeshRenderer(Mesh mesh) {
        this.mesh = mesh;
        this.shader = new BasicShader();
        this.color = new Vector3f(1.0f);
        // Initialize matrices
        this.viewMatrix = new Matrix4f();
        this.projectionMatrix = new Matrix4f();
    }

    @Override
    public void initialize() {
        // Enable depth testing for proper 3D rendering
        glEnable(GL_DEPTH_TEST);
    }

    public void setMatrices(Matrix4f view, Matrix4f projection) {
        this.viewMatrix.set(view);
        this.projectionMatrix.set(projection);
    }

    @Override
    public void render() {
        if (!isEnabled() || mesh == null) return;

        // Bind shader and set uniforms
        shader.bind();

        // Set transformation matrices
        shader.setModelMatrix(getTransform().getModelMatrix());
        shader.setViewMatrix(viewMatrix);
        shader.setProjectionMatrix(projectionMatrix);

        // Set the object's color
        shader.setColor(color);

        // Render the mesh
        mesh.render();

        // Unbind shader
        shader.unbind();
    }

    @Override
    public void cleanup() {
        if (shader != null) {
            shader.cleanup();
            shader = null;
        }
    }

    // Color control methods
    public void setColor(float r, float g, float b) {
        this.color.set(r, g, b);
    }

    public void setColor(Vector3f color) {
        this.color.set(color);
    }

    public Vector3f getColor() {
        return new Vector3f(color);
    }
}