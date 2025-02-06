package com.horrorcoresoftware.core.graphics;

import com.horrorcoresoftware.core.resource.ResourceLoader;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Collectors;
import static org.lwjgl.opengl.GL20.*;

public class ShaderLoader implements ResourceLoader<Shader> {

    @Override
    public Shader loadResource(String path) throws Exception {
        // Ensure path starts with '/'
        if (!path.startsWith("/")) {
            path = "/" + path;
        }

        // Load shader source code
        String vertexSource = loadShaderSource(path + ".vert");
        String fragmentSource = loadShaderSource(path + ".frag");

        // Create shader program
        int programId = glCreateProgram();
        int vertexShaderId = compileShader(GL_VERTEX_SHADER, vertexSource);
        int fragmentShaderId = compileShader(GL_FRAGMENT_SHADER, fragmentSource);

        // Link program
        glAttachShader(programId, vertexShaderId);
        glAttachShader(programId, fragmentShaderId);
        glLinkProgram(programId);

        // Check for linking errors
        if (glGetProgrami(programId, GL_LINK_STATUS) == GL_FALSE) {
            throw new Exception("Shader program linking failed: " + glGetProgramInfoLog(programId));
        }

        // Delete shaders as they're no longer needed
        glDeleteShader(vertexShaderId);
        glDeleteShader(fragmentShaderId);

        return new Shader(programId);
    }

    private int compileShader(int type, String source) throws Exception {
        int shaderId = glCreateShader(type);
        glShaderSource(shaderId, source);
        glCompileShader(shaderId);

        if (glGetShaderi(shaderId, GL_COMPILE_STATUS) == GL_FALSE) {
            String shaderType = type == GL_VERTEX_SHADER ? "vertex" : "fragment";
            String errorLog = glGetShaderInfoLog(shaderId);
            throw new Exception(shaderType + " shader compilation failed: " + errorLog);
        }

        return shaderId;
    }

    private String loadShaderSource(String path) throws Exception {
        try (InputStream is = ShaderLoader.class.getResourceAsStream(path)) {
            if (is == null) {
                throw new Exception("Shader file not found: " + path);
            }

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
                return reader.lines().collect(Collectors.joining("\n"));
            }
        } catch (Exception e) {
            throw new Exception("Failed to load shader file: " + path, e);
        }
    }
}