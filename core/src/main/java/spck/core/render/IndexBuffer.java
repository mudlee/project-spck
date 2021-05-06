package spck.core.render;

import spck.core.vendor.opengl.OpenGLIndexBuffer;

public abstract class IndexBuffer {
    public static IndexBuffer create(int[] indices) {
        switch (Renderer.backend) {
	        case OPENGL:
		        return new OpenGLIndexBuffer(indices);
	        default:
		        throw new UnsupportedOperationException();
        }
    }

    public abstract int getId();

    public abstract int getLength();

    public abstract void bind();

    public abstract void unbind();

    public abstract void dispose();
}
