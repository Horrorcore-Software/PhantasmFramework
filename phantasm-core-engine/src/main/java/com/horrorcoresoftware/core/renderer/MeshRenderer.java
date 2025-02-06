package com.horrorcoresoftware.core.renderer;

import com.badlogic.gdx.graphics.Camera;
import com.horrorcoresoftware.core.graphics.Material;
import com.horrorcoresoftware.core.graphics.Mesh;
import com.horrorcoresoftware.core.scene.Component;
import com.jme3.light.Light;
import org.joml.Matrix4f;

public class MeshRenderer extends Component {
    private Mesh mesh;
    private Material material;

    public MeshRenderer(Mesh mesh, Material material) {
        this.mesh = mesh;
        this.material = material;
    }

    public void render(Camera camera, Light light) {
        // Get the transform matrices
//        Matrix4f model = getTransform().getWorldMatrix();
//        Matrix4f view = camera.getViewMatrix();
//        Matrix4f projection = camera.getProjectionMatrix();
//
//        // Bind material and set uniforms
//        material.bind(model, view, projection,
//                camera.getPosition(),
//                light.getPosition());

        // Draw the mesh
        mesh.render();

        // Cleanup
        material.unbind();
    }

    @Override
    public void cleanup() {
        if (mesh != null) {
            mesh.cleanup();
        }
    }
}
