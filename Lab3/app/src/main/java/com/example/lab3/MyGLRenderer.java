package com.example.lab3;

import android.opengl.GLES32;
import android.opengl.GLSurfaceView;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class MyGLRenderer implements GLSurfaceView.Renderer {

    private final WorkModeProvider provider;
    private int renderWidth = 1;
    private int renderHeight = 1;

    public MyGLRenderer(WorkModeProvider provider) {
        this.provider = provider;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        // Depth test is required for correct 3D visibility
        GLES32.glEnable(GLES32.GL_DEPTH_TEST);
        GLES32.glClearColor(0f, 0f, 0f, 1f);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES32.glViewport(0, 0, width, height);
        renderWidth = width;
        renderHeight = height;
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        // Clear color + depth buffers
        GLES32.glClear(GLES32.GL_COLOR_BUFFER_BIT | GLES32.GL_DEPTH_BUFFER_BIT);

        myWorkMode wm = provider.getWorkMode();
        if (wm == null) return;

        if (wm.getProgramId() == 0) wm.myCreateShaderProgram();
        if (wm.getProgramId() <= 0) return;

        GLES32.glUseProgram(wm.getProgramId());
        wm.myUseProgramForDrawing(renderWidth, renderHeight);
    }
}