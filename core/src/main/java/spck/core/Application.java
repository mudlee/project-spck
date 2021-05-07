package spck.core;

import com.artemis.BaseSystem;
import org.joml.Vector4f;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spck.core.ecs.ECS;
import spck.core.ecs.systems.RawRenderableSystem;
import spck.core.props.Preferences;
import spck.core.render.Renderer;
import spck.core.window.Window;
import spck.core.window.WindowEventListener;

import java.util.ArrayList;
import java.util.Arrays;

public class Application implements WindowEventListener {
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

    final var systems = new ArrayList<BaseSystem>();
    systems.add(new RawRenderableSystem(renderer));
    systems.addAll(preferences.ecsSystems);
    Spck.ecs = new ECS(systems);

    window = new Window(preferences, Arrays.asList(renderer, this));
    Spck.window = window;
  }

  public void start() {
    log.info("Starting up application...");

    window.create();
    lifeCycleListener.onCreated();

    loop();

    log.info("Application is shutting down");
    lifeCycleListener.onDispose();
    renderer.dispose();
    window.dispose();
    log.info("Terminated");
  }

  public void stop() {
    window.close();
  }

  @Override
  public void onWindowResized(int width, int height) {
    lifeCycleListener.onResize(width,height);
  }

  private void loop() {
    long lastTime = System.nanoTime();

    renderer.setClearColor(new Vector4f(1f, 1f, 1f, 1f));
    renderer.setClearFlags(Spck.types.bufferBit.COLOR);

    while (!window.shouldClose()) {
      renderer.clear();
      long now = System.nanoTime();
      long frameTimeNanos = now - lastTime;
      float deltaTime = frameTimeNanos / 1_000_000_000f;
      lastTime = now;

      Spck.ecs.update(deltaTime);
      lifeCycleListener.onUpdate(deltaTime);

      renderer.swapBuffers(deltaTime);
      window.pollEvents();
    }
  }
}
