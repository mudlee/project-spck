package spck.core.vendor.opengl.type;

import spck.core.props.type.Types;

public class OpenGLTypes extends Types {
  public OpenGLTypes() {
    super(
        new OpenGLBufferBitType(),
        new OpenGLShaderType()
    );
  }
}
