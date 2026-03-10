package com.example.lab2;

public class myShadersLibrary {

    public static final String vertexShaderCode =
            "#version 300 es\n" +
                    "in vec3 vPosition;\n" +
                    "in vec3 vColor;\n" +
                    "out vec3 outColor;\n" +
                    "void main() {\n" +
                    "  gl_Position = vec4(vPosition, 1.0);\n" +
                    "  outColor = vColor;\n" +
                    "}\n";

    public static final String fragmentShaderCode =
            "#version 300 es\n" +
                    "precision mediump float;\n" +
                    "uniform float vLight;\n" +
                    "in vec3 outColor;\n" +
                    "out vec4 resultColor;\n" +
                    "void main() {\n" +
                    "  resultColor = vec4(vLight * outColor, 1.0);\n" +
                    "}\n";
}