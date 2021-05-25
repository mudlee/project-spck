package spck.core.render.model;

import java.util.List;

public class Model {
    private final List<ModelPart> parts;

    public Model(List<ModelPart> parts) {
        this.parts = parts;
    }

    public List<ModelPart> getParts() {
        return parts;
    }
}
