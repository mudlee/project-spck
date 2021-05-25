module spck.core {
    requires org.lwjgl;
    requires org.lwjgl.natives;
    requires org.lwjgl.opengl;
    requires org.lwjgl.opengl.natives;
    requires transitive org.lwjgl.glfw;
    requires org.lwjgl.glfw.natives;
    requires org.lwjgl.nanovg;
    requires org.lwjgl.nanovg.natives;
    requires org.lwjgl.assimp;
    requires org.lwjgl.assimp.natives;
    requires artemis.odb;
    requires transitive org.joml;
    requires org.slf4j;

    opens spck.core.ecs.components to artemis.odb;
    opens spck.core.ecs.systems to artemis.odb;

    exports spck.core;
    exports spck.core.ecs;
    exports spck.core.ecs.entities;
    exports spck.core.input;
    exports spck.core.io;
    exports spck.core.props;
    exports spck.core.props.type;
    exports spck.core.render;
    exports spck.core.render.camera;
    exports spck.core.render.model;
    exports spck.core.window;
}