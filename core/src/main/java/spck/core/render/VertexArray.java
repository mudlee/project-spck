package spck.core.render;

import spck.core.vendor.opengl.OpenGLVertexArray;

import java.util.List;
import java.util.Optional;

public abstract class VertexArray {
    public static VertexArray create(){
        switch (Renderer.backend) {
            case OPENGL:
                return new OpenGLVertexArray();
            default:
                throw new UnsupportedOperationException();
        }
    }

    public abstract void bind();

    public abstract void unbind();

    public abstract void addVertexBuffer(VertexBuffer buffer);

    public abstract void setIndexBuffer(IndexBuffer indexBuffer);

    public abstract List<VertexBuffer> getVertexBuffers();

    public abstract Optional<IndexBuffer> getIndexBuffer();

    public abstract void dispose();
}
