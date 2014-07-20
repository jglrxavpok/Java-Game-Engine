package org.jge.components;

import org.jge.render.Material;
import org.jge.render.RenderEngine;
import org.jge.render.mesh.Mesh;
import org.jge.render.shaders.Shader;

public class MeshRenderer extends SceneComponent
{

    private Mesh mesh;
    private Material material;

    public MeshRenderer(Mesh mesh, Material material)
    {
        this.mesh = mesh;
        this.material = material;
    }
    
    public void render(Shader shader, double delta, RenderEngine renderEngine)
    {
        shader.bind();
        shader.updateUniforms(getParent().getTransform(), renderEngine.getCamera(), material, renderEngine);
        mesh.draw();
    }
}
