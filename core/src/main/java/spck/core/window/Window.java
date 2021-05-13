package spck.core.window;

import org.joml.Vector2i;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.system.MemoryUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spck.core.Spck;
import spck.core.props.Antialiasing;
import spck.core.Disposable;
import spck.core.props.Preferences;

import java.nio.IntBuffer;
import java.util.List;
import java.util.Objects;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Window implements Disposable {
  private static final Logger log = LoggerFactory.getLogger(Window.class);
  private final Vector2i size = new Vector2i();
  private final Preferences preferences;
  private final List<WindowEventListener> listeners;
  private GLFWVidMode videoMode;
  private long id;
  private int screenPixelRatio;

  public Window(Preferences preferences, List<WindowEventListener> listeners) {
    this.preferences = preferences;
    this.listeners = listeners;
    size.set(preferences.windowSize);
  }

  public void create() {
    log.debug("Creating window...");

    if (!glfwInit()) {
      throw new RuntimeException("Error initializing GLFW");
    }

    glfwSetErrorCallback(GLFWErrorCallback.createPrint(System.err));

    glfwDefaultWindowHints();
    glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
    glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);

    videoMode = pickMonitor();

    if (preferences.antialiasing != Antialiasing.OFF) {
      log.debug("Antialiasing: {}", preferences.antialiasing.getValue());
      glfwWindowHint(GLFW_SAMPLES, preferences.antialiasing.getValue());
    }

    if (preferences.fullscreen) {
      size.set(videoMode.width(), videoMode.height());
      glfwWindowHint(GLFW_MAXIMIZED, GLFW_TRUE);
    }

    listeners.forEach(WindowEventListener::onWindowPrepared);

    id = glfwCreateWindow(size.x, size.y, preferences.title, preferences.fullscreen ? glfwGetPrimaryMonitor() : NULL, NULL);

    if (id == NULL) {
      throw new RuntimeException("Error creating GLFW window");
    }

    listeners.forEach(listener -> listener.onWindowCreated(id, size.x, size.y, preferences.vSync));

    glfwSetFramebufferSizeCallback(id, this::framebufferResized);
    calculateScreenPixelRatio();

    log.debug("Creating input");
    //input.initialize(new Input.InitializationParams(size.x, windowSize.y, this::cursorPositionHasChanged));

    log.debug("Setting up input callbacks");
    glfwSetKeyCallback(id, (window, key, scancode, action, mods) -> Spck.input.keyCallback(key, scancode, action, mods));
    /*glfwSetCursorPosCallback(id, (window, x, y) -> input.cursorPosCallback(x, y));
    glfwSetScrollCallback(id, (window, xOffset, yOffset) -> input.mouseScrollCallback(xOffset, yOffset));
    glfwSetMouseButtonCallback(id, (window, button, action, mods) -> input.mouseButtonCallback(button, action, mods));
     */

    if (!preferences.fullscreen) {
      glfwSetWindowPos(id, (videoMode.width() - size.x) / 2, (videoMode.height() - size.y) / 2);
    }

    log.debug("Window has been setup");
    glfwShowWindow(id);
  }

  public void close() {
    glfwSetWindowShouldClose(id, true);
  }

  public boolean shouldClose() {
    return glfwWindowShouldClose(id);
  }

  public void pollEvents() {
    glfwPollEvents();
  }

  @Override
  public void dispose() {
    glfwFreeCallbacks(id);
    glfwDestroyWindow(id);
    glfwTerminate();
    Objects.requireNonNull(glfwSetErrorCallback(null)).free();
  }

  public Vector2i getSize() {
    return size;
  }

  public int getScreenPixelRatio() {
    return screenPixelRatio;
  }

  private GLFWVidMode pickMonitor() {
    final var buffer = glfwGetMonitors();

    if (buffer == null) {
      throw new RuntimeException("No monitors were found");
    }

    if (buffer.capacity() == 1) {
      log.info("Found one monitor: {}", glfwGetMonitorName(buffer.get()));
    } else {
      log.info("Found multiple monitors:");
      for (int i = 0; i < buffer.capacity(); i++) {
        log.info(" Monitor-{} '{}'", i, glfwGetMonitorName(buffer.get(i)));
      }
    }

    return glfwGetVideoMode(glfwGetPrimaryMonitor());
  }

  private void framebufferResized(long window, int width, int height) {
    size.set(width, height);
    listeners.forEach(listener -> listener.onWindowResized(width, height));
    calculateScreenPixelRatio();
    log.trace("Framebuffer size change to {}x{}", width, height);
  }

  private void calculateScreenPixelRatio() {
    // https://en.wikipedia.org/wiki/4K_resolution
    int uhdMinWidth = 3840;
    int uhdMinHeight = 1716;
    boolean UHD = videoMode.width() >= uhdMinWidth && videoMode.height() >= uhdMinHeight;
    log.debug("Screen is {}x{}, UHD: {}", videoMode.width(), videoMode.height(), UHD);

    // Check if the monitor is 4K
    if (UHD) {
      screenPixelRatio = 2;
      log.debug("Screen pixel ratio has been set to: {}", screenPixelRatio);
      return;
    }

    IntBuffer widthScreenCoordBuf = MemoryUtil.memAllocInt(1);
    IntBuffer heightScreenCoordBuf = MemoryUtil.memAllocInt(1);
    IntBuffer widthPixelsBuf = MemoryUtil.memAllocInt(1);
    IntBuffer heightPixelsBuf = MemoryUtil.memAllocInt(1);

    glfwGetWindowSize(id, widthScreenCoordBuf, heightScreenCoordBuf);
    glfwGetFramebufferSize(id, widthPixelsBuf, heightPixelsBuf);

    screenPixelRatio = (int) Math.floor((float) widthPixelsBuf.get() / (float) widthScreenCoordBuf.get());
    log.debug("Screen pixel ratio has been set to: {}", screenPixelRatio);

    MemoryUtil.memFree(widthScreenCoordBuf);
    MemoryUtil.memFree(heightScreenCoordBuf);
    MemoryUtil.memFree(widthPixelsBuf);
    MemoryUtil.memFree(heightPixelsBuf);
  }
}
