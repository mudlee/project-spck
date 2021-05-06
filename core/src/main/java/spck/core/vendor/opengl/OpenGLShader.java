package spck.core.vendor.opengl;

import org.lwjgl.system.MemoryStack;
import org.lwjgl.util.shaderc.Shaderc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spck.core.io.ResourceLoader;
import spck.core.render.Shader;
import spck.core.render.spirv.SPIRVCompiler;

import static org.lwjgl.opengl.ARBGLSPIRV.GL_SHADER_BINARY_FORMAT_SPIR_V_ARB;
import static org.lwjgl.opengl.ARBGLSPIRV.glSpecializeShaderARB;
import static org.lwjgl.opengl.GL41.*;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.memCalloc;

public class OpenGLShader extends Shader {
    private static final Logger log = LoggerFactory.getLogger(OpenGLShader.class);
    private final int programId;

    public OpenGLShader(String vertexShaderName, String fragmentShaderName) {
        log.debug("Creating shader program for with vertex shader '{}' and fragment shader '{}'", vertexShaderName, fragmentShaderName);
        programId = glGenProgramPipelines();
        log.debug("Pipeline was created [ID:{}] for {}", programId, this.getClass().getSimpleName());

        String vertPath = String.format("/shaders/%s.glsl", vertexShaderName);
        String fragPath = String.format("/shaders/%s.glsl", fragmentShaderName);

        ensureSPIRVCompatibility(vertexShaderName, vertPath, SPIRVShaderType.VERTEX);
        ensureSPIRVCompatibility(fragmentShaderName, fragPath, SPIRVShaderType.FRAGMENT);

        final var vertexId = glCreateShaderProgramv(GL_VERTEX_SHADER, ResourceLoader.load(vertPath));
        validateShaderLinkStatus(vertexId, vertPath);
        glUseProgramStages(programId, GL_VERTEX_SHADER_BIT, vertexId);
        log.debug(" * vertex shader ready {}", vertexId);

        final var fragmentId = glCreateShaderProgramv(GL_FRAGMENT_SHADER, ResourceLoader.load(fragPath));
        validateShaderLinkStatus(vertexId, vertPath);
        glUseProgramStages(programId, GL_FRAGMENT_SHADER_BIT, fragmentId);
        log.debug(" * fragment shader ready {}", fragmentId);
    }

    @Override
    public int getProgram() {
        return programId;
    }

    @Override
    public void bind() {
        log.trace("Bind shader program {}", programId);
        glBindProgramPipeline(programId);
    }

    @Override
    public void unbind() {
        log.trace("Unbind shader program {}", programId);
        glBindProgramPipeline(0);
    }

    @Override
    public void dispose() {
        log.trace("Dispose shader program {}", programId);
        unbind();

        glDeleteProgramPipelines(programId);
    }

    private void ensureSPIRVCompatibility(String shaderName, String path, SPIRVShaderType type) {
        try(final var stack = stackPush()) {
            final var spirv = SPIRVCompiler.compile(
              ResourceLoader.loadToByteBuffer(path, stack),
              type.shadercCode,
              shaderName
            );

            // Left here for possible future SPIR-V implementations.
            // Note: OSX doesn't support SPIR-V, hence the drop.
            /*final var id = glCreateShader(type.glCode);
            final var idPtr = stack.callocInt(1).put(id).flip();
            final var spirvBuf = memCalloc(spirv.getBytes().length).put(spirv.getBytes()).flip();

            glShaderBinary(idPtr, GL_SHADER_BINARY_FORMAT_SPIR_V_ARB, spirvBuf);
            glSpecializeShaderARB(id, stack.UTF8("main"), new int[]{}, new int[]{});

            validateShaderCompileStatus(id, path);*/

            spirv.dispose();
        }
    }

    private void validateShaderCompileStatus(int shaderId, String path) {
        if(glGetShaderi(shaderId,GL_COMPILE_STATUS) == GL_TRUE) {
            return;
        }

        log.error(
                "Shader '{}' could not be compiled\n---\n{}---",
                path,
                glGetShaderInfoLog(shaderId, 1024)
        );
        throw new RuntimeException("Shader validating failed");
    }

    private void validateShaderLinkStatus(int shaderId, String path) {
        log.debug("Validating shader {}",path);
        try (MemoryStack stack = stackPush()) {
            final var buffer = stack.callocInt(1);
            glGetProgramiv(shaderId, GL_LINK_STATUS, buffer);
            if (buffer.get() == GL_FALSE) {
                log.error(
                        "Shader '{}' could not be linked\n---\n{}---",
                        path,
                        glGetProgramInfoLog(shaderId, 1024)
                );
                throw new RuntimeException("Shader validating failed");
            }
        }
    }

    private enum SPIRVShaderType {
        VERTEX(Shaderc.shaderc_vertex_shader, GL_VERTEX_SHADER),
        FRAGMENT(Shaderc.shaderc_fragment_shader, GL_FRAGMENT_SHADER);

        private final int shadercCode;
        private final int glCode;

        SPIRVShaderType(int shadercCode, int glCode) {
            this.shadercCode = shadercCode;
            this.glCode = glCode;
        }
    }
}
