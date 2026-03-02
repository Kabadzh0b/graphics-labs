package com.example.lab1_gles;

import android.opengl.GLES30;
import android.opengl.GLSurfaceView;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class MyRenderer implements GLSurfaceView.Renderer {

    private static final int N = 28;

    private int program;

    private FloatBuffer triangleBuffer;
    private FloatBuffer sunBuffer;
    private FloatBuffer raysBuffer;

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {

        GLES30.glClearColor(0.7f, 0.85f, 1.0f, 1.0f);

        String vertexShader =
                "#version 300 es\n" +
                        "layout(location = 0) in vec2 aPosition;\n" +
                        "void main() {\n" +
                        "    gl_Position = vec4(aPosition, 0.0, 1.0);\n" +
                        "}";

        String fragmentShader =
                "#version 300 es\n" +
                        "precision mediump float;\n" +
                        "out vec4 fragColor;\n" +
                        "void main() {\n" +
                        "    fragColor = vec4(1.0, 1.0, 0.0, 1.0);\n" +
                        "}";

        program = ShaderUtils.createProgram(vertexShader, fragmentShader);

        setupTriangle();
        setupSun();
        setupRays();
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES30.glViewport(0, 0, width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT);

        GLES30.glUseProgram(program);

        drawShape(triangleBuffer, 3, GLES30.GL_TRIANGLES);
        drawShape(sunBuffer, N, GLES30.GL_TRIANGLE_FAN);
        drawShape(raysBuffer, N * 2, GLES30.GL_LINES);
    }

    private void drawShape(FloatBuffer buffer, int count, int mode) {
        GLES30.glEnableVertexAttribArray(0);
        GLES30.glVertexAttribPointer(0, 2, GLES30.GL_FLOAT, false, 0, buffer);
        GLES30.glDrawArrays(mode, 0, count);
    }

    private void setupTriangle() {
        float[] triangle = {
                -0.4f, -0.8f,
                0.4f, -0.8f,
                0.0f, -0.4f
        };
        triangleBuffer = createBuffer(triangle);
    }

    private void setupSun() {
        float radius = 0.2f;
        float cx = 0f;
        float cy = 0.4f;

        float[] vertices = new float[N * 2];

        for (int i = 0; i < N; i++) {
            double angle = 2 * Math.PI * i / N;
            vertices[i * 2] = (float)(cx + radius * Math.cos(angle));
            vertices[i * 2 + 1] = (float)(cy + radius * Math.sin(angle));
        }

        sunBuffer = createBuffer(vertices);
    }

    private void setupRays() {
        float inner = 0.22f;
        float outer = 0.35f;
        float cx = 0f;
        float cy = 0.4f;

        float[] vertices = new float[N * 4];

        for (int i = 0; i < N; i++) {
            double angle = 2 * Math.PI * i / N;

            vertices[i * 4]     = (float)(cx + inner * Math.cos(angle));
            vertices[i * 4 + 1] = (float)(cy + inner * Math.sin(angle));

            vertices[i * 4 + 2] = (float)(cx + outer * Math.cos(angle));
            vertices[i * 4 + 3] = (float)(cy + outer * Math.sin(angle));
        }

        raysBuffer = createBuffer(vertices);
    }

    private FloatBuffer createBuffer(float[] data) {
        ByteBuffer bb = ByteBuffer.allocateDirect(data.length * 4);
        bb.order(ByteOrder.nativeOrder());
        FloatBuffer buffer = bb.asFloatBuffer();
        buffer.put(data);
        buffer.position(0);
        return buffer;
    }
}