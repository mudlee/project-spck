module spck.core {
	requires org.lwjgl;
	requires org.lwjgl.natives;
	requires org.lwjgl.opengl;
	requires org.lwjgl.opengl.natives;
	requires transitive org.lwjgl.glfw;
	requires org.lwjgl.glfw.natives;
	requires org.joml;
	requires org.slf4j;

	exports spck.core;
	exports spck.core.input;
  exports spck.core.props;
  exports spck.core.props.type;
}