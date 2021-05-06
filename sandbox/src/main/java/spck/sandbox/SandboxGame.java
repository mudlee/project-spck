package spck.sandbox;

import spck.core.LifeCycleListener;
import spck.core.Spck;
import spck.core.input.InputMultiplexer;
import spck.core.input.InputProcessor;
import spck.core.render.*;

import static org.lwjgl.glfw.GLFW.*;

public class SandboxGame implements LifeCycleListener, InputProcessor {
  private final InputMultiplexer inputMultiplexer = new InputMultiplexer();

  @Override
  public void onCreated() {
    inputMultiplexer.addProcessor(this);
    Spck.input.setMultiplexer(inputMultiplexer);

    Shader shader = Shader.create("triangle.vert", "triangle.frag");

    int stride = 7 * Float.BYTES;
    VertexBufferLayout layout = new VertexBufferLayout(
        new VertexLayoutAttribute(0, 3, Spck.types.shader.FLOAT, false, stride, 0),
        new VertexLayoutAttribute(1, 4, Spck.types.shader.FLOAT, false, stride, 3 * Float.BYTES)
    );

    VertexArray va1 = VertexArray.create();
    va1.addVertexBuffer(VertexBuffer.create(squareColored, layout));
    va1.setIndexBuffer(IndexBuffer.create(squareInd));

    VertexArray va2 = VertexArray.create();
    va2.addVertexBuffer(VertexBuffer.create(triVertColored, layout));
    va2.setIndexBuffer(IndexBuffer.create(triInd));
  }

  @Override
  public void onKeyPress(int keyCode) {
    if(keyCode == GLFW_KEY_ESCAPE) {
      Spck.app.stop();
    }
  }

  private static final float[] cubeVertices = {
      -1.0f, 1.0f, 1.0f, 1f, 0f, 0f, 1f,
      1.0f, 1.0f, 1.0f, 1f, 0f, 0f, 1f,
      -1.0f, -1.0f, 1.0f, 1f, 0f, 0f, 1f,
      1.0f, -1.0f, 1.0f, 1f, 0f, 0f, 1f,
      -1.0f, 1.0f, -1.0f, 1f, 0f, 0f, 1f,
      1.0f, 1.0f, -1.0f, 1f, 0f, 0f, 1f,
      -1.0f, -1.0f, -1.0f, 1f, 0f, 0f, 1f,
      1.0f, -1.0f, -1.0f, 1f, 0f, 0f, 1f
  };

  private static final int[] cubeIndices = {
      0, 1, 2, // 0
      1, 3, 2,
      4, 6, 5, // 2
      5, 6, 7,
      0, 2, 4, // 4
      4, 2, 6,
      1, 5, 3, // 6
      5, 7, 3,
      0, 4, 1, // 8
      4, 5, 1,
      2, 3, 6, // 10
      6, 3, 7
  };

  private static final float[] triVertColored = {
      -0.5f, -0.5f, 0.0f, 1f, 0f, 0f, 1f,
      0.5f, -0.5f, 0.0f, 0f, 1f, 0f, 1f,
      0.0f, 0.5f, 0.0f, 0f, 0f, 1f, 1f,
  };

  private static final int[] triInd = {
      0, 1, 2
  };

  private static final float[] squareColored = {
      -0.5f, -0.5f, 0.0f, 0.2f, 0.5f, 0.5f, 1f,
      0.5f, -0.5f, 0.0f, 0.2f, 0.5f, 0.5f, 1f,
      0.5f, 0.5f, 0.0f, 0.2f, 0.5f, 0.5f, 1f,
      -0.5f, 0.5f, 0.0f, 0.2f, 0.5f, 0.5f, 1f,
  };

  private static final int[] squareInd = {
      0, 1, 2, 2, 3, 0
  };
}
