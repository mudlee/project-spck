package spck.core.window;

public interface WindowEventListener {
  void onWindowPrepared();

  void onWindowCreated(long windowId, int width, int height, boolean vSync);

  void onWindowResized(int width, int height);
}
