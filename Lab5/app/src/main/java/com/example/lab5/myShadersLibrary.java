package com.example.lab5;

public class myShadersLibrary {

    public static final String VERTEX =
            "#version 300 es\n" +
            "in vec3 vPosition;\n" +
            "in vec3 vNormal;\n" +
            "in vec2 vTexture;\n" +
            "uniform mat4 uModelMatrix;\n" +
            "uniform mat4 uViewMatrix;\n" +
            "uniform mat4 uProjMatrix;\n" +
            "out vec3 currentPos;\n" +
            "out vec3 currentNormal;\n" +
            "out vec2 currentTexCoord;\n" +
            "void main() {\n" +
            "  vec4 wp = uModelMatrix * vec4(vPosition, 1.0);\n" +
            "  gl_Position = uProjMatrix * uViewMatrix * wp;\n" +
            "  currentPos = wp.xyz;\n" +
            "  currentNormal = mat3(uModelMatrix) * vNormal;\n" +
            "  currentTexCoord = vTexture;\n" +
            "}\n";

    public static final String FRAGMENT =
            "#version 300 es\n" +
            "precision mediump float;\n" +
            "in vec3 currentPos;\n" +
            "in vec3 currentNormal;\n" +
            "in vec2 currentTexCoord;\n" +
            "uniform sampler2D vTextureSample;\n" +
            "uniform vec3 vColor;\n" +
            "uniform vec3 uLightColor;\n" +
            "uniform vec3 uLightPos;\n" +
            "uniform vec3 uEyePos;\n" +
            "uniform int uUseLighting;\n" +
            "out vec4 resultColor;\n" +
            "void main() {\n" +
            "  vec4 tex = texture(vTextureSample, currentTexCoord);\n" +
            "  vec3 base = tex.rgb;\n" +
            "  if (uUseLighting == 0) {\n" +
            "    resultColor = vec4(base, tex.a);\n" +
            "    return;\n" +
            "  }\n" +
            "  vec3 N = normalize(currentNormal);\n" +
            "  vec3 L = normalize(uLightPos - currentPos);\n" +
            "  float dist = length(uLightPos - currentPos);\n" +
            "  float attenuation = 5.0 / (1.0 + dist*dist);\n" +
            "  float diff = max(dot(N, L), 0.0);\n" +
            "  vec3 V = normalize(uEyePos - currentPos);\n" +
            "  vec3 R = reflect(-L, N);\n" +
            "  float spec = pow(max(dot(V, R), 0.0), 64.0);\n" +
            "  vec3 light = (1.5*vColor) + (0.55*diff*uLightColor*vColor) + (0.20*spec*uLightColor);\n" +
            "  vec3 rgb = attenuation * light * base;\n" +
            "  resultColor = vec4(rgb, tex.a);\n" +
            "}\n";
}
