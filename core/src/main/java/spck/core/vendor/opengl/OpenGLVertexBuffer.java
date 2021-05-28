package spck.core.vendor.opengl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spck.core.render.VertexBuffer;
import spck.core.render.VertexBufferLayout;

import static org.lwjgl.opengl.GL41.*;
import static org.lwjgl.system.MemoryStack.stackPush;

public class OpenGLVertexBuffer extends VertexBuffer {
    private static final Logger log = LoggerFactory.getLogger(OpenGLVertexBuffer.class);
    private final int id;
    private final VertexBufferLayout layout;
    private final int length;

    public OpenGLVertexBuffer(float[] vertices, VertexBufferLayout layout) {
        try(final var stack = stackPush()) {
            this.layout = layout;
            length = vertices.length;
            id = glGenBuffers();
            bind();
            final var buffer = stack.callocFloat(vertices.length).put(vertices).flip();
            glBufferData(GL_ARRAY_BUFFER, buffer, GL_STATIC_DRAW);
            unbind();
            log.debug("VertexBuffer created {}", id);
        }
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public int getLength() {
        return length;
    }

    @Override
    public VertexBufferLayout getLayout() {
        return this.layout;
    }

    @Override
    public void bind() {
        log.trace("Bind vertex buffer {}", id);
        glBindBuffer(GL_ARRAY_BUFFER, id);
    }

	@Override
	public void unbind() {
		log.trace("Unbind vertex buffer {}", id);
		glBindBuffer(GL_ARRAY_BUFFER, 0);
	}

	@Override
	public void dispose() {
		log.trace("Bind vertex buffer {}", id);
		glDeleteBuffers(id);
	}
}
