package spck.core.ecs.entities;

import spck.core.ecs.components.ShaderComponent;
import spck.core.ecs.components.VertexArrayComponent;
import spck.core.render.Shader;
import spck.core.render.VertexArray;

public class RawRenderableEntity extends AbstractEntity {
  private final VertexArray vao;
  private final Shader shader;

  public RawRenderableEntity(String name, VertexArray vao, Shader shader) {
    super(name);
    this.vao = vao;
    this.shader = shader;
  }

  @Override
  public void onCreated() {
    final var vaoC = addComponent(VertexArrayComponent.class);
    vaoC.ifPresent(comp -> comp.vertexArray = vao);

    final var shaderC = addComponent(ShaderComponent.class);
    shaderC.ifPresent(comp -> comp.shader = shader);
  }
}
