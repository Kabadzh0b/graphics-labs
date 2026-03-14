package com.example.lab5;

import android.content.Context;
import android.opengl.GLES32;
import android.opengl.GLSurfaceView;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class MyGLRenderer implements GLSurfaceView.Renderer {

    private final Context context;
    private final WorkModeProvider provider;

    private int renderWidth = 1;
    private int renderHeight = 1;

    public MyGLRenderer(Context context, WorkModeProvider provider) {
        this.context = context;
        this.provider = provider;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        GLES32.glEnable(GLES32.GL_DEPTH_TEST);
        GLES32.glDisable(GLES32.GL_CULL_FACE);
        GLES32.glClearColor(0.02f, 0.02f, 0.05f, 1f);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES32.glViewport(0, 0, width, height);
        renderWidth = width;
        renderHeight = height;
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        myWorkMode wm = provider.getWorkMode();
        if (wm == null) return;

        float[] cc = wm.getClearColor();
        GLES32.glClearColor(cc[0], cc[1], cc[2], 1f);
        GLES32.glClear(GLES32.GL_COLOR_BUFFER_BIT | GLES32.GL_DEPTH_BUFFER_BIT);

        if (wm.getProgramId() == 0) {
            wm.myCreateScene(context);
        }
        if (wm.getProgramId() <= 0) return;

        GLES32.glUseProgram(wm.getProgramId());
        wm.myUseProgramForDrawing(renderWidth, renderHeight);
    }
}
