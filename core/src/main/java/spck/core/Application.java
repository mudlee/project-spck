package spck.core;

import com.artemis.BaseSystem;
import org.joml.Vector4f;
import org.lwjgl.nanovg.NVGColor;
import org.lwjgl.opengl.GL41;
import org.lwjgl.system.MemoryStack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spck.core.ecs.ECS;
import spck.core.ecs.systems.RawRenderableSystem;
import spck.core.io.ResourceLoader;
import spck.core.props.Preferences;
import spck.core.render.Renderer;
import spck.core.window.Window;
import spck.core.window.WindowEventListener;

import java.util.ArrayList;
import java.util.Arrays;

import static org.lwjgl.nanovg.NanoVG.*;
import static org.lwjgl.nanovg.NanoVGGL3.NVG_STENCIL_STROKES;
import static org.lwjgl.nanovg.NanoVGGL3.nvgCreate;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Application implements WindowEventListener {
  private static final Logger log = LoggerFactory.getLogger(Application.class);
  private final Window window;
  private final LifeCycleListener lifeCycleListener;
  private final Renderer renderer;
  private float deltaTime;
  private float frameTime;
  // TEMPORARY DEBUG UI
  private long nvPtr;
  private int nvFont;
  private NVGColor nvColor;
  private StringBuilder frameBuilder = new StringBuilder();

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

    initTemporaryUI();
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

    frameTime = 0;
    while (!window.shouldClose()) {
      long now = System.nanoTime();
      long frameTimeNanos = now - lastTime;
      deltaTime = frameTimeNanos / 1_000_000_000f;
      lastTime = now;

      renderer.clear();
      Spck.ecs.update(deltaTime);
      lifeCycleListener.onUpdate(deltaTime);

      renderTemporaryUI();
      renderer.swapBuffers(deltaTime);
      window.pollEvents();
      frameTime = (System.nanoTime() - now) / 1_000_000f;
    }
  }

  private void initTemporaryUI() {
    nvPtr = nvgCreate(NVG_STENCIL_STROKES);
    if(nvPtr == NULL) {
      throw new RuntimeException("Could not create NV");
    }

    try(var stack = MemoryStack.stackPush()) {
      nvFont = nvgCreateFontMem(nvPtr, "font", ResourceLoader.loadToByteBuffer("/fonts/whitrabt.ttf", stack), 0);
      if(nvFont == -1) {
        throw new RuntimeException("Cannot load NV font");
      }
    }

    nvColor = NVGColor.create();
    nvColor.r(0f);
    nvColor.g(0f);
    nvColor.b(0f);
    nvColor.a(1f);
  }

  private void renderTemporaryUI() {
    nvgBeginFrame(nvPtr, window.getSize().x, window.getSize().y, window.getScreenPixelRatio());
    nvgFontFace(nvPtr, "font");

    nvgFontSize(nvPtr, 18 * window.getScreenPixelRatio());
    nvgTextAlign(nvPtr, NVG_ALIGN_TOP | NVG_ALIGN_LEFT);
    nvgFillColor(nvPtr, nvColor);
    frameBuilder.delete(0, frameBuilder.length());
    frameBuilder.append("JVM Heap: ");
    final var mb = (Long)((Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1024L / 1024L);
    frameBuilder.append(mb.intValue());
    frameBuilder.append("MB");
    nvgText(nvPtr, 0,0, frameBuilder.toString());

    nvgEndFrame(nvPtr);
    /*GL41.glEnable(GL41.GL_DEPTH_TEST);
    GL41.glEnable(GL41.GL_STENCIL_TEST);
    GL41.glBindTexture(GL41.GL_TEXTURE_2D, 0);
    GL41.glBindBuffer(GL41.GL_ARRAY_BUFFER, 0);
    GL41.glBindBuffer(GL41.GL_UNIFORM_BUFFER, 0);
    GL41.glEnable(GL41.GL_BLEND);
    GL41.glBlendFunc(GL41.GL_SRC_ALPHA, GL41.GL_ONE_MINUS_SRC_ALPHA);
    GL41.glEnable(GL41.GL_CULL_FACE);
    GL41.glCullFace(GL41.GL_BACK);
    GL41.glBindVertexArray(0);*/
  }
}
