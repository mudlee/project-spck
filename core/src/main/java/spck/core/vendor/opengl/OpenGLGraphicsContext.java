package spck.core.vendor.opengl;

import org.joml.Vector4f;
import org.lwjgl.opengl.GLUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spck.core.render.GraphicsContext;
import spck.core.render.Shader;
import spck.core.render.VertexArray;
import spck.core.render.VertexBuffer;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL.createCapabilities;
import static org.lwjgl.opengl.GL41.*;

public class OpenGLGraphicsContext implements GraphicsContext {
  private static final Logger log = LoggerFactory.getLogger(OpenGLGraphicsContext.class);
  private final boolean debug;
  private int clearFlags = 0;
  private long windowId;

  public OpenGLGraphicsContext(boolean debug) {
    this.debug = debug;
  }

  @Override
  public void windowPrepared() {
    glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 4);
    glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 1);
    glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
    glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GL_TRUE);
    if(debug){
      glfwWindowHint(GLFW_OPENGL_DEBUG_CONTEXT, GLFW_TRUE);
    }
  }

  @Override
  public void windowCreated(long windowId, int windowWidth, int windowHeight, boolean vSync) {
    log.debug("Initializing OpenGL context...");
    this.windowId = windowId;

    glfwMakeContextCurrent(this.windowId);

    createCapabilities();

    if (debug) {
      GLUtil.setupDebugMessageCallback();
    }

    log.debug("Initialized");
    log.debug("\tOpenGL Vendor: {}", glGetString(GL_VENDOR));
    log.debug("\tVersion: {}", glGetString(GL_VERSION));
    log.debug("\tRenderer: {}", glGetString(GL_RENDERER));
    log.debug("\tShading Language Version: {}", glGetString(GL_SHADING_LANGUAGE_VERSION));
    log.debug("\tVsync: {}", vSync);

    glfwSwapInterval(vSync ? GLFW_TRUE : GLFW_FALSE);
  }

  @Override
  public void setClearFlags(int mask) {
    this.clearFlags = mask;
  }

  @Override
  public void setClearColor(Vector4f color) {
    glClearColor(color.x, color.y, color.z, color.w);
  }

  @Override
  public void clear() {
    glClear(clearFlags);
  }

  @Override
  public void renderRaw(VertexArray vao, Shader shader) {
    shader.bind();
    vao.bind();

    if(vao.isInstanced()) {
      if (vao.getIndexBuffer().isPresent()) {
        glDrawElementsInstanced(GL_TRIANGLES, vao.getIndexBuffer().get().getLength(), GL_UNSIGNED_INT, 0, vao.getInstanceCount());
      }
      else {
        //glDrawArraysInstanced(); // TODO
      }
    }
    else {
      if (vao.getIndexBuffer().isPresent()) {
        glDrawElements(GL_TRIANGLES, vao.getIndexBuffer().get().getLength(), GL_UNSIGNED_INT, 0);
      } else {
        for (VertexBuffer buffer : vao.getVertexBuffers()) {
          glDrawArrays(GL_TRIANGLES, 0, buffer.getLength() / 3);
        }
      }
    }

    vao.unbind();
    shader.unbind();
  }

  @Override
  public void swapBuffers(float frameTime) {
    glfwSwapBuffers(windowId);
  }

  @Override
  public void windowResized(int newWidth, int newHeight) {
    glViewport(0, 0, newWidth, newHeight);
  }

  @Override
  public void dispose() {
  }
}
