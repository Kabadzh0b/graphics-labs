package com.example.lab2;

import android.opengl.GLES32;

public class ModeColorWheel extends myWorkMode {

    private static final int FAN_VERTICES = 9;
    private static final int STRIP_VERTICES = 14;

    private static final float[][] COLORS = new float[][]{
            {1f, 0f, 0f},   // Red
            {1f, 0.5f, 0f}, // Orange
            {1f, 1f, 0f},   // Yellow
            {0f, 1f, 0f},   // Green
            {0f, 1f, 1f},   // Cyan
            {0f, 0f, 1f},   // Blue
            {1f, 0f, 1f}    // Violet
    };

    public ModeColorWheel() {
        super();
        myCreateScene();
    }

    protected void myCreateScene() {
        float[] fan = buildFan();
        float[] strip = buildStrip();

        arrayVertex = new float[fan.length + strip.length];
        System.arraycopy(fan, 0, arrayVertex, 0, fan.length);
        System.arraycopy(strip, 0, arrayVertex, fan.length, strip.length);

        startFan = 0;
        countFan = FAN_VERTICES;

        startStrip = FAN_VERTICES;
        countStrip = STRIP_VERTICES;

        numVertex = FAN_VERTICES + STRIP_VERTICES;
    }

    @Override
    public void myCreateShaderProgram() {
        compileAndLinkProgram(myShadersLibrary.vertexShaderCode, myShadersLibrary.fragmentShaderCode);
        bindVertexArrayPositionColor(arrayVertex, 6, "vPosition", 0, "vColor", 3 * 4);
    }

    @Override
    public void myUseProgramForDrawing(int width, int height) {
        int hLight = GLES32.glGetUniformLocation(gl_Program, "vLight");
        GLES32.glUniform1f(hLight, 1.0f);

        GLES32.glBindVertexArray(VAO_id);
        GLES32.glDrawArrays(GLES32.GL_TRIANGLE_FAN, startFan, countFan);
        GLES32.glDrawArrays(GLES32.GL_TRIANGLE_STRIP, startStrip, countStrip);
        GLES32.glBindVertexArray(0);
    }

    private float[] buildFan() {
        float cx = 0.0f, cy = 0.25f, r = 0.55f;

        float[] v = new float[FAN_VERTICES * 6];
        int p = 0;

        // center (white)
        p = put(v, p, cx, cy, 0f, 1f, 1f, 1f);

        // 7 perimeter vertices
        for (int i = 0; i < 7; i++) {
            double a = 2.0 * Math.PI * i / 7.0 - Math.PI / 2.0;
            float x = (float) (cx + r * Math.cos(a));
            float y = (float) (cy + r * Math.sin(a));
            float[] c = COLORS[i];
            p = put(v, p, x, y, 0f, c[0], c[1], c[2]);
        }

        // repeat first perimeter vertex
        double a0 = -Math.PI / 2.0;
        float x0 = (float) (cx + r * Math.cos(a0));
        float y0 = (float) (cy + r * Math.sin(a0));
        float[] c0 = COLORS[0];
        put(v, p, x0, y0, 0f, c0[0], c0[1], c0[2]);

        return v;
    }

    private float[] buildStrip() {
        float yTop = -0.55f, yBottom = -0.85f;
        float xLeft = -0.85f, xRight = 0.85f;

        float[] v = new float[STRIP_VERTICES * 6];
        int p = 0;

        for (int i = 0; i < 7; i++) {
            float t = i / 6.0f;
            float x = xLeft + (xRight - xLeft) * t;
            float[] c = COLORS[i];

            p = put(v, p, x, yTop, 0f, c[0], c[1], c[2]);
            p = put(v, p, x, yBottom, 0f, c[0], c[1], c[2]);
        }

        return v;
    }

    private int put(float[] dst, int pos, float x, float y, float z, float r, float g, float b) {
        dst[pos++] = x; dst[pos++] = y; dst[pos++] = z;
        dst[pos++] = r; dst[pos++] = g; dst[pos++] = b;
        return pos;
    }
}