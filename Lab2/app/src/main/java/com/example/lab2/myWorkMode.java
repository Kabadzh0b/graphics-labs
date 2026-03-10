package com.example.lab2;

import android.opengl.GLES32;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class myWorkMode {

    protected int gl_Program = 0;
    protected int VAO_id = 0;
    protected int VBO_id = 0;

    // Interleaved vertices: [x,y,z,r,g,b] per vertex
    protected float[] arrayVertex = null;
    protected int numVertex = 0;

    // Two objects inside one array:
    protected int startFan = 0, countFan = 0;
    protected int startStrip = 0, countStrip = 0;

    public int getProgramId() {
        return gl_Program;
    }

    protected int compileShader(int type, String code) {
        int shader = GLES32.glCreateShader(type);
        GLES32.glShaderSource(shader, code);
        GLES32.glCompileShader(shader);

        int[] ok = new int[1];
        GLES32.glGetShaderiv(shader, GLES32.GL_COMPILE_STATUS, ok, 0);
        if (ok[0] == 0) {
            Log.e("Lab2_GLES", "Shader compile error: " + GLES32.glGetShaderInfoLog(shader));
            GLES32.glDeleteShader(shader);
            return 0;
        }
        return shader;
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
            Log.e("Lab2_GLES", "Program link error: " + GLES32.glGetProgramInfoLog(gl_Program));
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

    // strideFloats = 6 (x,y,z,r,g,b)
    // offsetColorBytes = 3*4
    protected void bindVertexArrayPositionColor(float[] src,
                                                int strideFloats,
                                                String attribPos, int offsetPosBytes,
                                                String attribColor, int offsetColorBytes) {
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

        int strideBytes = strideFloats * 4;

        int hPos = GLES32.glGetAttribLocation(gl_Program, attribPos);
        GLES32.glEnableVertexAttribArray(hPos);
        GLES32.glVertexAttribPointer(hPos, 3, GLES32.GL_FLOAT, false, strideBytes, offsetPosBytes);

        int hCol = GLES32.glGetAttribLocation(gl_Program, attribColor);
        GLES32.glEnableVertexAttribArray(hCol);
        GLES32.glVertexAttribPointer(hCol, 3, GLES32.GL_FLOAT, false, strideBytes, offsetColorBytes);

        GLES32.glBindBuffer(GLES32.GL_ARRAY_BUFFER, 0);
        GLES32.glBindVertexArray(0);
    }

    public void myCreateShaderProgram() { }

    public void myUseProgramForDrawing(int width, int height) {
        GLES32.glBindVertexArray(VAO_id);
        GLES32.glDrawArrays(GLES32.GL_TRIANGLES, 0, numVertex);
        GLES32.glBindVertexArray(0);
    }
}