package com.example.lab4;

import android.opengl.GLES32;
import android.opengl.Matrix;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class myWorkMode {

    protected int gl_Program = 0;
    protected int VAO_id = 0;
    protected int VBO_id = 0;

    protected float[] arrayVertex = null;

    protected final float[] modelMatrix = new float[16];
    protected final float[] viewMatrix = new float[16];
    protected final float[] projMatrix = new float[16];

    protected int uModel = -1;
    protected int uView = -1;
    protected int uProj = -1;

    protected int uColor = -1;
    protected int uLightColor = -1;
    protected int uLightPos = -1;
    protected int uEyePos = -1;
    protected int uIsLight = -1;

    protected final float[] lightPos = new float[]{2.0f, 2.0f, 3.0f};
    protected final float[] lightColor = new float[]{1.0f, 0.2f, 1.0f};

    protected float alpha = 35f;
    protected float beta = 65f;
    protected float distance = 10f;

    protected final float[] clearColor = new float[]{0.05f, 0.05f, 0.18f};

    public int getProgramId() {
        return gl_Program;
    }

    public float[] getClearColor() {
        return clearColor;
    }

    protected int compileShader(int type, String code) {
        int sh = GLES32.glCreateShader(type);
        GLES32.glShaderSource(sh, code);
        GLES32.glCompileShader(sh);

        int[] ok = new int[1];
        GLES32.glGetShaderiv(sh, GLES32.GL_COMPILE_STATUS, ok, 0);
        if (ok[0] == 0) {
            Log.e("Lab4", "Shader compile error: " + GLES32.glGetShaderInfoLog(sh));
            GLES32.glDeleteShader(sh);
            return 0;
        }
        return sh;
    }

    protected void compileAndLinkProgram(String vsh, String fsh) {
        int vs = compileShader(GLES32.GL_VERTEX_SHADER, vsh);
        if (vs == 0) return;

        int fs = compileShader(GLES32.GL_FRAGMENT_SHADER, fsh);
        if (fs == 0) return;

        gl_Program = GLES32.glCreateProgram();
        GLES32.glAttachShader(gl_Program, vs);
        GLES32.glAttachShader(gl_Program, fs);
        GLES32.glLinkProgram(gl_Program);

        int[] linked = new int[1];
        GLES32.glGetProgramiv(gl_Program, GLES32.GL_LINK_STATUS, linked, 0);
        if (linked[0] == 0) {
            Log.e("Lab4", "Program link error: " + GLES32.glGetProgramInfoLog(gl_Program));
            GLES32.glDeleteProgram(gl_Program);
            gl_Program = 0;
        }

        GLES32.glDeleteShader(vs);
        GLES32.glDeleteShader(fs);
    }

    protected void createVAOandVBO() {
        int[] tmp = new int[1];
        GLES32.glGenVertexArrays(1, tmp, 0);
        VAO_id = tmp[0];
        GLES32.glGenBuffers(1, tmp, 0);
        VBO_id = tmp[0];
    }

    protected void bindPositionNormal(float[] src) {
        if (gl_Program <= 0) return;

        ByteBuffer bb = ByteBuffer.allocateDirect(src.length * 4);
        bb.order(ByteOrder.nativeOrder());
        FloatBuffer fb = bb.asFloatBuffer();
        fb.put(src);
        fb.position(0);

        createVAOandVBO();

        GLES32.glBindVertexArray(VAO_id);
        GLES32.glBindBuffer(GLES32.GL_ARRAY_BUFFER, VBO_id);
        GLES32.glBufferData(GLES32.GL_ARRAY_BUFFER, src.length * 4, fb, GLES32.GL_STATIC_DRAW);

        int strideBytes = 6 * 4;

        int hPos = GLES32.glGetAttribLocation(gl_Program, "vPosition");
        GLES32.glEnableVertexAttribArray(hPos);
        GLES32.glVertexAttribPointer(hPos, 3, GLES32.GL_FLOAT, false, strideBytes, 0);

        int hNrm = GLES32.glGetAttribLocation(gl_Program, "vNormal");
        GLES32.glEnableVertexAttribArray(hNrm);
        GLES32.glVertexAttribPointer(hNrm, 3, GLES32.GL_FLOAT, false, strideBytes, 3 * 4);

        GLES32.glBindBuffer(GLES32.GL_ARRAY_BUFFER, 0);
        GLES32.glBindVertexArray(0);
    }

    protected void cacheUniformLocations() {
        uModel = GLES32.glGetUniformLocation(gl_Program, "uModelMatrix");
        uView = GLES32.glGetUniformLocation(gl_Program, "uViewMatrix");
        uProj = GLES32.glGetUniformLocation(gl_Program, "uProjMatrix");

        uColor = GLES32.glGetUniformLocation(gl_Program, "vColor");
        uLightColor = GLES32.glGetUniformLocation(gl_Program, "vLightColor");
        uLightPos = GLES32.glGetUniformLocation(gl_Program, "vLightPos");
        uEyePos = GLES32.glGetUniformLocation(gl_Program, "vEyePos");
        uIsLight = GLES32.glGetUniformLocation(gl_Program, "uIsLight");
    }

    protected void setMatrices() {
        GLES32.glUniformMatrix4fv(uView, 1, false, viewMatrix, 0);
        GLES32.glUniformMatrix4fv(uProj, 1, false, projMatrix, 0);
        GLES32.glUniformMatrix4fv(uModel, 1, false, modelMatrix, 0);
    }

    protected void setCommonUniforms(float eyeX, float eyeY, float eyeZ) {
        GLES32.glUniform3fv(uLightColor, 1, lightColor, 0);
        GLES32.glUniform3fv(uLightPos, 1, lightPos, 0);
        float[] eye = new float[]{eyeX, eyeY, eyeZ};
        GLES32.glUniform3fv(uEyePos, 1, eye, 0);
    }

    protected void setPerspective(int width, int height, float fovyDeg, float near, float far) {
        float aspect = (float) width / (float) height;
        Matrix.perspectiveM(projMatrix, 0, fovyDeg, aspect, near, far);
    }

    protected float[] computeOrbitEye() {
        float a = degToRad(alpha);
        float b = degToRad(beta);

        float x = (float) (distance * Math.sin(b) * Math.sin(a));
        float y = (float) (-distance * Math.sin(b) * Math.cos(a));
        float z = (float) (distance * Math.cos(b));
        return new float[]{x, y, z};
    }

    protected static float clamp(float v, float lo, float hi) {
        return Math.max(lo, Math.min(hi, v));
    }

    protected static float degToRad(float deg) {
        return (float) (deg * Math.PI / 180.0);
    }

    public boolean onTouchNotUsed() { return true; }
    public boolean onActionDown(float x, float y, int cx, int cy) { return false; }
    public boolean onActionMove(float x, float y, int cx, int cy) { return false; }

    public void myCreateShaderProgram() {}
    public void myUseProgramForDrawing(int width, int height) {}
}
