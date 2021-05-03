package spck.core.render;

import org.joml.Vector4f;
import spck.core.Spck;
import spck.core.props.Preferences;
import spck.core.SpckRuntimeException;
import spck.core.vendor.opengl.OpenGLTypes;
import spck.core.vendor.opengl.OpenGLGraphicsContext;
import spck.core.window.WindowEventListener;

public class Renderer implements WindowEventListener {
  private final GraphicsContext context;
  private final Preferences.RendererBackend backend; // TODO

  public Renderer(Preferences.RendererBackend backend, boolean debug) {
    this.backend = backend;

    switch (backend) {
      case OPENGL:
        context = new OpenGLGraphicsContext(debug);
        Spck.types = new OpenGLTypes();
        break;
      default:
        throw new SpckRuntimeException("Only OPENGL backend is supported");
    }
  }

  @Override
  public void onWindowPrepared() {
    context.windowPrepared();
  }

  @Override
  public void onWindowCreated(long windowId, int width, int height, boolean vSync) {
    context.windowCreated(windowId, width, height, vSync);
  }

  @Override
  public void onWindowResized(int width, int height) {
    context.windowResized(width, height);
  }

  public void setClearColor(Vector4f color) {
    context.setClearColor(color);
  }

  public void setClearFlags(int mask) {
    context.setClearFlags(mask);
  }

  public void swapBuffers(float frameTime) {
    context.swapBuffers(frameTime);
  }

  public void clear() {
    context.clear();
  }

  public void dispose() {
    context.dispose();
  }
}
