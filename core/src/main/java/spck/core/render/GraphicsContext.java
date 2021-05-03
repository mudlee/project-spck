package spck.core.render;

import org.joml.Vector4f;
import spck.core.Disposable;

public interface GraphicsContext extends Disposable {
  void windowPrepared();

  void windowCreated(long windowId, int windowWidth, int windowHeight, boolean vSync);

  void setClearFlags(int mask);

  void setClearColor(Vector4f color);

  void clear();

  void swapBuffers(float frameTime);

  void windowResized(int newWidth, int newHeight);
}
