package com.example.lab4;

public class myShadersLibrary {

    public static final String VERTEX =
            "#version 300 es\n" +
            "in vec3 vPosition;\n" +
            "in vec3 vNormal;\n" +
            "uniform mat4 uModelMatrix;\n" +
            "uniform mat4 uViewMatrix;\n" +
            "uniform mat4 uProjMatrix;\n" +
            "out vec3 currentPos;\n" +
            "out vec3 currentNormal;\n" +
            "void main() {\n" +
            "  vec4 wp = uModelMatrix * vec4(vPosition, 1.0);\n" +
            "  gl_Position = uProjMatrix * uViewMatrix * wp;\n" +
            "  currentPos = wp.xyz;\n" +
            "  currentNormal = mat3(uModelMatrix) * vNormal;\n" +
            "  gl_PointSize = 15.0;\n" +
            "}\n";

    public static final String FRAG_DIFFUSE_ATTEN =
            "#version 300 es\n" +
            "precision mediump float;\n" +
            "in vec3 currentPos;\n" +
            "in vec3 currentNormal;\n" +
            "uniform vec3 vColor;\n" +
            "uniform vec3 vLightColor;\n" +
            "uniform vec3 vLightPos;\n" +
            "uniform vec3 vEyePos;\n" +
            "uniform int uIsLight;\n" +
            "out vec4 resultColor;\n" +
            "void main() {\n" +
            "  if (uIsLight == 1) {\n" +
            "    resultColor = vec4(vLightColor, 1.0);\n" +
            "    return;\n" +
            "  }\n" +
            "  vec3 norm = normalize(currentNormal);\n" +
            "  vec3 lightDir = normalize(vLightPos - currentPos);\n" +
            "  float distance = length(vLightPos - currentPos);\n" +
            "  float attenuation = 2.2 / (1.0 + distance*distance);\n" +
            "  float diffuse = max(dot(norm, lightDir), 0.0);\n" +
            "  vec3 vClr = 0.45*vColor + 0.70*diffuse*vLightColor*vColor;\n" +
            "  resultColor = vec4(attenuation * vClr, 1.0);\n" +
            "}\n";

    public static final String FRAG_SPECULAR_NO_ATTEN =
            "#version 300 es\n" +
            "precision mediump float;\n" +
            "in vec3 currentPos;\n" +
            "in vec3 currentNormal;\n" +
            "uniform vec3 vColor;\n" +
            "uniform vec3 vLightColor;\n" +
            "uniform vec3 vLightPos;\n" +
            "uniform vec3 vEyePos;\n" +
            "uniform int uIsLight;\n" +
            "out vec4 resultColor;\n" +
            "void main() {\n" +
            "  if (uIsLight == 1) {\n" +
            "    resultColor = vec4(vLightColor, 1.0);\n" +
            "    return;\n" +
            "  }\n" +
            "  vec3 norm = normalize(currentNormal);\n" +
            "  vec3 lightDir = normalize(vLightPos - currentPos);\n" +
            "  vec3 reflectDir = normalize(reflect(-lightDir, norm));\n" +
            "  vec3 eyeDir = normalize(vEyePos - currentPos);\n" +
            "  float spec = max(dot(eyeDir, reflectDir), 0.0);\n" +
            "  spec = pow(spec, 200.0);\n" +
            "  vec3 vClr = 0.30*vColor + 0.70*spec*vLightColor;\n" +
            "  resultColor = vec4(vClr, 1.0);\n" +
            "}\n";

    public static final String FRAG_PHONG_ATTEN =
            "#version 300 es\n" +
            "precision mediump float;\n" +
            "in vec3 currentPos;\n" +
            "in vec3 currentNormal;\n" +
            "uniform vec3 vColor;\n" +
            "uniform vec3 vLightColor;\n" +
            "uniform vec3 vLightPos;\n" +
            "uniform vec3 vEyePos;\n" +
            "uniform int uIsLight;\n" +
            "out vec4 resultColor;\n" +
            "void main() {\n" +
            "  if (uIsLight == 1) {\n" +
            "    resultColor = vec4(vLightColor, 1.0);\n" +
            "    return;\n" +
            "  }\n" +
            "  vec3 norm = normalize(currentNormal);\n" +
            "  vec3 lightDir = normalize(vLightPos - currentPos);\n" +
            "  float distance = length(vLightPos - currentPos);\n" +
            "  float attenuation = 1.0 / (1.0 + distance*distance);\n" +
            "  float diffuse = max(dot(norm, lightDir), 0.0);\n" +
            "  vec3 reflectDir = normalize(reflect(-lightDir, norm));\n" +
            "  vec3 eyeDir = normalize(vEyePos - currentPos);\n" +
            "  float spec = max(dot(eyeDir, reflectDir), 0.0);\n" +
            "  spec = pow(spec, 200.0);\n" +
            "  vec3 vClr = 0.30*vColor\n" +
            "           + 0.40*diffuse*vLightColor*vColor\n" +
            "           + 0.30*spec*vLightColor;\n" +
            "  resultColor = vec4(attenuation * vClr, 1.0);\n" +
            "}\n";
}
