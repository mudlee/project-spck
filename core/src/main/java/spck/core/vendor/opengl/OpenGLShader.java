package spck.core.vendor.opengl;

import org.joml.Matrix4f;
import org.lwjgl.system.MemoryStack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spck.core.io.ResourceLoader;
import spck.core.render.Shader;

import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.opengl.GL41.*;
import static org.lwjgl.system.MemoryStack.stackPush;

public class OpenGLShader extends Shader {
    private static final Logger log = LoggerFactory.getLogger(OpenGLShader.class);
    private final int pipelineId;
    private final Map<String, Integer> uniforms = new HashMap<>();
    private final int vertexId;
    private final int fragmentId;

    public OpenGLShader(String vertexShaderName, String fragmentShaderName) {
        log.debug("Creating shader pipeline program for with vertex shader '{}' and fragment shader '{}'", vertexShaderName, fragmentShaderName);
        pipelineId = glGenProgramPipelines();
        log.debug("Pipeline was created [ID:{}] for {}", pipelineId, this.getClass().getSimpleName());

        String vertPath = String.format("/shaders/%s.glsl", vertexShaderName);
        String fragPath = String.format("/shaders/%s.glsl", fragmentShaderName);

        vertexId = glCreateShaderProgramv(GL_VERTEX_SHADER, ResourceLoader.load(vertPath));
        validateShaderLinkStatus(vertexId, vertPath);
        glUseProgramStages(pipelineId, GL_VERTEX_SHADER_BIT, vertexId);
        log.debug(" * vertex shader ready {}", vertexId);

        fragmentId = glCreateShaderProgramv(GL_FRAGMENT_SHADER, ResourceLoader.load(fragPath));
        validateShaderLinkStatus(vertexId, vertPath);
        glUseProgramStages(pipelineId, GL_FRAGMENT_SHADER_BIT, fragmentId);
        log.debug(" * fragment shader ready {}", fragmentId);
    }

    @Override
    public int getPipelineId() {
        return pipelineId;
    }

    @Override
    public void bind() {
        log.trace("Bind shader pipeline {}", pipelineId);
        glBindProgramPipeline(pipelineId);
    }

    @Override
    public void unbind() {
        log.trace("Unbind shader pipeline {}", pipelineId);
        glBindProgramPipeline(0);
    }

    @Override
    public int getVertexProgramId() {
        return vertexId;
    }

    @Override
    public int getFragmentProgramId() {
        return fragmentId;
    }

    @Override
    public void createUniform(int programId, String name) {
        if (uniforms.containsKey(name)) {
            log.error("Uniform [NAME:{}] is already created with ID {}", name, uniforms.get(name));
            return;
        }

        int location = glGetUniformLocation(programId, name);
        if (location < 0) {
            log.error("Uniform could not find ({}) or not used, so optimized out when trying to create: {}", location, name);
        } else {
            log.debug("Uniform [NAME:{}] [LOCATION:{}] created for shader [ID:{}] in pipeline [ID:{}]", name, location, programId, this.pipelineId);
            uniforms.put(name, location);
        }
    }

    @Override
    public void setUniform(int programId, String name, Matrix4f value) {
        if(doesUniformExist(name)) {
            try (MemoryStack stack = MemoryStack.stackPush()) {
                final var buffer = stack.mallocFloat(16);
                value.get(buffer);
                glProgramUniformMatrix4fv(programId, uniforms.get(name), false, buffer);
            }
        }
    }

    @Override
    public void dispose() {
        log.trace("Dispose shader pipeline {}", pipelineId);
        unbind();

        glDeleteProgramPipelines(pipelineId);
    }

    private boolean doesUniformExist(String uniformName) {
        if (!uniforms.containsKey(uniformName)) {
            log.error("Uniform '{}' was not created, cannot set", uniformName);
            return false;
        }

        return true;
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
}
