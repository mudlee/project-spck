package spck.core.input;

public interface InputProcessor {
  default void onKeyPress(int keyCode) {}

  default void onKeyReleased(int keyCode) {}
}
