package com.example.lab3;

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

    // Matrices
    protected final float[] modelMatrix = new float[16];
    protected final float[] viewMatrix = new float[16];
    protected final float[] projectionMatrix = new float[16];

    // Camera angles/position
    protected float alphaViewAngle = 0f;
    protected float betaViewAngle = 65f;

    protected float viewDistance = 8f;

    protected float xCamera = -4f;
    protected float yCamera = -4f;
    protected float zCamera = 2f;

    public int getProgramId() {
        return gl_Program;
    }

    protected int compileShader(int type, String code) {
        int sh = GLES32.glCreateShader(type);
        GLES32.glShaderSource(sh, code);
        GLES32.glCompileShader(sh);

        int[] ok = new int[1];
        GLES32.glGetShaderiv(sh, GLES32.GL_COMPILE_STATUS, ok, 0);
        if (ok[0] == 0) {
            Log.e("Lab3", "Shader compile error: " + GLES32.glGetShaderInfoLog(sh));
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
            Log.e("Lab3", "Program link error: " + GLES32.glGetProgramInfoLog(gl_Program));
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

    protected void bindPositionColor(float[] src) {
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

        int hCol = GLES32.glGetAttribLocation(gl_Program, "vColor");
        GLES32.glEnableVertexAttribArray(hCol);
        GLES32.glVertexAttribPointer(hCol, 3, GLES32.GL_FLOAT, false, strideBytes, 3 * 4);

        GLES32.glBindBuffer(GLES32.GL_ARRAY_BUFFER, 0);
        GLES32.glBindVertexArray(0);
    }

    protected void setUniformMatrices() {
        int hModel = GLES32.glGetUniformLocation(gl_Program, "uModelMatrix");
        int hView = GLES32.glGetUniformLocation(gl_Program, "uViewMatrix");
        int hProj = GLES32.glGetUniformLocation(gl_Program, "uProjMatrix");

        GLES32.glUniformMatrix4fv(hView, 1, false, viewMatrix, 0);
        GLES32.glUniformMatrix4fv(hProj, 1, false, projectionMatrix, 0);
        GLES32.glUniformMatrix4fv(hModel, 1, false, modelMatrix, 0);
    }

    public void myCreateShaderProgram() { }

    public void myUseProgramForDrawing(int width, int height) { }

    public boolean onTouchNotUsed() { return true; }

    public boolean onActionDown(float x, float y, int cx, int cy) { return false; }

    public boolean onActionMove(float x, float y, int cx, int cy) { return false; }

    protected static float clamp(float v, float lo, float hi) {
        return Math.max(lo, Math.min(hi, v));
    }

    protected static float degToRad(float deg) {
        return (float)(deg * Math.PI / 180.0);
    }
}