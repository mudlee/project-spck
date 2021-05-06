package spck.core.render;

import spck.core.vendor.opengl.OpenGLVertexBuffer;

public abstract class VertexBuffer {
    public static VertexBuffer create(float[] vertices, VertexBufferLayout layout) {
        switch (Renderer.backend) {
	        case OPENGL:
		        return new OpenGLVertexBuffer(vertices, layout);
	        default:
		        throw new UnsupportedOperationException();
        }
    }

    public abstract int getId();

    public abstract int getLength();

    public abstract VertexBufferLayout getLayout();

    public abstract void bind();

    public abstract void unbind();

    public abstract void dispose();
}
