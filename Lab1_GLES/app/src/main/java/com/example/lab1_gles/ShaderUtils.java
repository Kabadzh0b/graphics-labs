package com.example.lab1_gles;

import android.opengl.GLES30;

public class ShaderUtils {

    public static int createProgram(String vertexCode, String fragmentCode) {

        int vertexShader = loadShader(GLES30.GL_VERTEX_SHADER, vertexCode);
        int fragmentShader = loadShader(GLES30.GL_FRAGMENT_SHADER, fragmentCode);

        int program = GLES30.glCreateProgram();
        GLES30.glAttachShader(program, vertexShader);
        GLES30.glAttachShader(program, fragmentShader);
        GLES30.glLinkProgram(program);

        return program;
    }

    private static int loadShader(int type, String code) {
        int shader = GLES30.glCreateShader(type);
        GLES30.glShaderSource(shader, code);
        GLES30.glCompileShader(shader);
        return shader;
    }
}