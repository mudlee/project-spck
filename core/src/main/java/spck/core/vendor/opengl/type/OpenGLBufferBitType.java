package spck.core.vendor.opengl.type;

import spck.core.props.type.BufferBitType;

import static org.lwjgl.opengl.GL41.*;

public class OpenGLBufferBitType extends BufferBitType {
  public OpenGLBufferBitType() {
    super(
      GL_COLOR_BUFFER_BIT,
      GL_DEPTH_BUFFER_BIT,
      GL_STENCIL_BUFFER_BIT
    );
  }
}
