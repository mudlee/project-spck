package spck.core.vendor.opengl;

import spck.core.props.type.BufferBit;

import static org.lwjgl.opengl.GL41.*;

public class OpenGLBufferBit extends BufferBit {
  public OpenGLBufferBit() {
    super(
      GL_COLOR_BUFFER_BIT,
      GL_DEPTH_BUFFER_BIT,
      GL_STENCIL_BUFFER_BIT
    );
  }
}
