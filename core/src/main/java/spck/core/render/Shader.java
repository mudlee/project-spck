package spck.core.render;

import org.joml.Matrix4f;
import spck.core.vendor.opengl.OpenGLShader;

public abstract class Shader {
    public static final String UNIFORM_PROJ_MAT = "projectionMatrix";
    public static final String UNIFORM_VIEW_MAT = "viewMatrix";

    public static Shader create(String vertexShaderName, String fragmentShaderName) {
        switch (Renderer.backend) {
            case OPENGL:
                return new OpenGLShader(vertexShaderName, fragmentShaderName);
            default:
                throw new UnsupportedOperationException();
        }
    }

    public abstract int getPipelineId();

    public abstract void bind();

    public abstract void unbind();

    public abstract int getVertexProgramId();

    public abstract int getFragmentProgramId();

    public abstract void createUniform(int programId, String name);

    public abstract void setUniform(int programId, String name, Matrix4f value);

    public abstract void dispose();
}
