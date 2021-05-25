package spck.core.render.model;

import spck.core.render.Mesh;

public class ModelPart {
    private final Mesh mesh;

    public ModelPart(Mesh mesh) {
        this.mesh = mesh;
    }

    public Mesh getMesh() {
        return mesh;
    }
}
