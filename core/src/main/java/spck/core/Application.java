package spck.core;

import org.joml.Vector4f;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spck.core.props.Preferences;
import spck.core.render.Renderer;
import spck.core.window.Window;

public class Application {
  private static final Logger log = LoggerFactory.getLogger(Application.class);
  private final Window window;
  private final LifeCycleListener lifeCycleListener;
  private final Renderer renderer;

  public Application(LifeCycleListener lifeCycleListener, Preferences preferences) {
    if(Spck.app != null) {
      throw new SpckRuntimeException("Cannot run multiple applications");
    }
    Spck.app = this;
    this.lifeCycleListener = lifeCycleListener;
    renderer = new Renderer(preferences.renderBackend, preferences.debug);
    window = new Window(preferences, renderer);
  }

  public void start() {
    log.info("Starting up application...");

    window.create();
    lifeCycleListener.onCreated();

    loop();

    log.info("Application is shutting down");
    renderer.dispose();
    window.dispose();
    log.info("Terminated");
  }

  public void stop() {
    window.close();
  }

  private void loop() {
    long lastTime = System.nanoTime();

    renderer.setClearColor(new Vector4f(1f, 1f, 1f, 1f));
    renderer.setClearFlags(Spck.types.bufferBit.COLOR);

    while (!window.shouldClose()) {
      renderer.clear();
      long now = System.nanoTime();
      long frameTimeNanos = now - lastTime;
      float frameTime = frameTimeNanos / 1_000_000_000f;
      lastTime = now;

      renderer.swapBuffers(frameTime);
      window.pollEvents();
    }
  }
}
