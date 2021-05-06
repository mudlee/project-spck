package spck.core.vendor.opengl.type;

import spck.core.props.type.ShaderType;

import static org.lwjgl.opengl.GL41.GL_FLOAT;

public class OpenGLShaderType extends ShaderType {
  public OpenGLShaderType() {
    super(GL_FLOAT);
  }
}
