package spck.core;

import spck.core.ecs.ECS;
import spck.core.input.InputManager;
import spck.core.props.type.Types;
import spck.core.window.Window;

public class Spck {
  public static final InputManager input = new InputManager();
  public static Application app;
  public static Types types;
  public static ECS ecs;
  public static Window window;
}
