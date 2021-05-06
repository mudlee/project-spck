package spck.core.render;

import spck.core.vendor.opengl.OpenGLShader;

public abstract class Shader {
    public static Shader create(String vertexShaderName, String fragmentShaderName){
        switch (Renderer.backend) {
	        case OPENGL:
		        return new OpenGLShader(vertexShaderName, fragmentShaderName);
	        default:
		        throw new UnsupportedOperationException();
        }
    }

    public abstract int getProgram();

    public abstract void bind();

    public abstract void unbind();

    public abstract void dispose();
}
