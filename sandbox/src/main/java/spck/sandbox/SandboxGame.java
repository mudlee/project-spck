package spck.sandbox;

import spck.core.LifeCycleListener;
import spck.core.Spck;
import spck.core.input.InputMultiplexer;
import spck.core.input.InputProcessor;
import static org.lwjgl.glfw.GLFW.*;

public class SandboxGame implements LifeCycleListener, InputProcessor {
  private final InputMultiplexer inputMultiplexer = new InputMultiplexer();

  @Override
  public void onCreated() {
    inputMultiplexer.addProcessor(this);
    Spck.input.setMultiplexer(inputMultiplexer);
  }

  @Override
  public void onKeyPress(int keyCode) {
    if(keyCode == GLFW_KEY_ESCAPE) {
      Spck.app.stop();
    }
  }
}
