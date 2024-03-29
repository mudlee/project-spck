package spck.sandbox;

import org.joml.Vector3f;
import spck.core.LifeCycleListener;
import spck.core.Spck;
import spck.core.ecs.entities.RawRenderableEntity;
import spck.core.input.InputMultiplexer;
import spck.core.input.InputProcessor;
import spck.core.io.ModelLoader;
import spck.core.render.*;
import spck.core.render.camera.PerspectiveCamera;

import static org.lwjgl.glfw.GLFW.*;

public class SandboxGame implements LifeCycleListener, InputProcessor {
  private final InputMultiplexer inputMultiplexer = new InputMultiplexer();
  private PerspectiveCamera camera;
  private Shader shader;

  @Override
  public void onCreated() {
    inputMultiplexer.addProcessor(this);
    Spck.input.setMultiplexer(inputMultiplexer);

    camera = new PerspectiveCamera(60f, 0.01f, 1000f);
    camera.resize(Spck.window.getSize().x, Spck.window.getSize().y);
    camera.setPosition(new Vector3f(0,0,5));

    shader = Shader.create("basic.vert", "basic.frag");
    shader.createUniform(shader.getVertexProgramId(), Shader.UNIFORM_PROJ_MAT);
    shader.createUniform(shader.getVertexProgramId(), Shader.UNIFORM_VIEW_MAT);
    shader.setUniform(shader.getVertexProgramId(), Shader.UNIFORM_PROJ_MAT, camera.getProjectionMatrix());
    shader.setUniform(shader.getVertexProgramId(), Shader.UNIFORM_VIEW_MAT, camera.getViewMatrix());

    /*int stride = 7 * Float.BYTES;
    final var colorLayout = new VertexBufferLayout(
        new VertexLayoutAttribute(0, 3, Spck.types.shader.FLOAT, false, stride, 0),
        new VertexLayoutAttribute(1, 4, Spck.types.shader.FLOAT, false, stride, 3 * Float.BYTES)
    );

    final var va1 = VertexArray.create();
    va1.addVertexBuffer(VertexBuffer.create(squareColored, colorLayout));
    va1.setIndexBuffer(IndexBuffer.create(squareInd));

    final var va2 = VertexArray.create();
    va2.addVertexBuffer(VertexBuffer.create(triVertColored, colorLayout));
    va2.setIndexBuffer(IndexBuffer.create(triInd));

    final var va3 = VertexArray.create();
    va3.addVertexBuffer(VertexBuffer.create(squareNotIndexedButColored, colorLayout));*/

    /*final var square = new RawDrawableEntity("square", va1, shader);
    final var triangle = new RawDrawableEntity("triangle", va2, shader);
    Spck.ecs.addEntity(triangle);
    Spck.ecs.addEntity(square);

    Spck.ecs.addEntity(new RawRenderableEntity("square",va3, shader));*/

    final var layout = new VertexBufferLayout(
      new VertexLayoutAttribute(0,3,Spck.types.shader.FLOAT,false,3 * Float.BYTES,0),
      new VertexLayoutInstancedAttribute(1,4,Spck.types.shader.FLOAT,false,16 * Float.BYTES,0,1),
      new VertexLayoutInstancedAttribute(2,4,Spck.types.shader.FLOAT,false,16 * Float.BYTES,4 * Float.BYTES,1),
      new VertexLayoutInstancedAttribute(3,4,Spck.types.shader.FLOAT,false,16 * Float.BYTES,8 * Float.BYTES,1),
      new VertexLayoutInstancedAttribute(4,4,Spck.types.shader.FLOAT,false,16 * Float.BYTES,12 * Float.BYTES,1)
    );

    final var model = ModelLoader.load("/models/primitives/cube.obj");
    final var va4 = VertexArray.create();
    va4.addVertexBuffer(VertexBuffer.create(model.getParts().get(0).getMesh().getVertices(), layout));
    va4.setIndexBuffer(IndexBuffer.create(model.getParts().get(0).getMesh().getIndices()));
    va4.setInstanceCount(1);

    Spck.ecs.addEntity(new RawRenderableEntity("model", va4, shader));
  }

  @Override
  public void onKeyPress(int keyCode) {
    if(keyCode == GLFW_KEY_ESCAPE) {
      Spck.app.stop();
    }
  }

  @Override
  public void onResize(int width, int height) {
    camera.resize(width, height);
  }

  @Override
  public void onUpdate(float delta) {
    if(camera.update()) {
      shader.setUniform(shader.getVertexProgramId(), Shader.UNIFORM_PROJ_MAT, camera.getProjectionMatrix());
      shader.setUniform(shader.getVertexProgramId(), Shader.UNIFORM_VIEW_MAT, camera.getViewMatrix());
    }
  }

  private static final float[] triVertColored = {
      -1f, -1f, -1.5f, 1f, 0f, 0f, 1f,
      1f, -1f, -1.5f, 0f, 1f, 0f, 1f,
      0.0f, 1f, -1.5f, 0f, 0f, 1f, 1f,
  };

  private static final int[] triInd = {
      0, 1, 2
  };

  private static final float[] squareNotIndexedButColored = {
    -0.5f, -0.5f, -1.4f,0.2f, 0.5f, 0.5f, 1f,
    0.5f, -0.5f, -1.4f,0.2f, 0.5f, 0.5f, 1f,
    0.5f, 0.5f, -1.4f,0.2f, 0.5f, 0.5f, 1f,
    0.5f, 0.5f, -1.4f,0.2f, 0.5f, 0.5f, 1f,
    -0.5f, 0.5f, -1.4f,0.2f, 0.5f, 0.5f, 1f,
    -0.5f, -0.5f, -1.4f,0.2f, 0.5f, 0.5f, 1f,
  };

  private static final float[] squareColored = {
      -0.5f, -0.5f, -1.4f, 0.2f, 0.5f, 0.5f, 1f,
      0.5f, -0.5f, -1.4f, 0.2f, 0.5f, 0.5f, 1f,
      0.5f, 0.5f, -1.4f, 0.2f, 0.5f, 0.5f, 1f,
      -0.5f, 0.5f, -1.4f, 0.2f, 0.5f, 0.5f, 1f,
  };

  private static final int[] squareInd = {
      0, 1, 2, 2, 3, 0
  };
}
