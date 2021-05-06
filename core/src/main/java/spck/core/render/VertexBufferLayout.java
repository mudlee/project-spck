package spck.core.render;

public class VertexBufferLayout {
    private final VertexLayoutAttribute[] attributes;

    public VertexBufferLayout(VertexLayoutAttribute... attributes) {
        this.attributes = attributes;
    }

    public VertexLayoutAttribute[] getAttributes(){
        return attributes;
    }
}
