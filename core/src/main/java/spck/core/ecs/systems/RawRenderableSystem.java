package spck.core.ecs.systems;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.systems.IteratingSystem;
import spck.core.ecs.components.ShaderComponent;
import spck.core.ecs.components.VertexArrayComponent;
import spck.core.render.Renderer;

public class RawRenderableSystem extends IteratingSystem {
  private final Renderer renderer;
  private ComponentMapper<VertexArrayComponent> vaoMapper;
  private ComponentMapper<ShaderComponent> shaderMapper;

  public RawRenderableSystem(Renderer renderer) {
    super(Aspect.all(VertexArrayComponent.class, ShaderComponent.class));
    this.renderer = renderer;
  }

  @Override
  protected void process(int entityId) {
    final var vaoC = vaoMapper.get(entityId);
    final var shaderC = shaderMapper.get(entityId);

    renderer.renderRaw(vaoC.vertexArray, shaderC.shader);
  }
}
