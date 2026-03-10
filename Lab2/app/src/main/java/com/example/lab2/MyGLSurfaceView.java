package com.example.lab2;

import android.content.Context;
import android.opengl.GLSurfaceView;

public class MyGLSurfaceView extends GLSurfaceView {

    public MyGLSurfaceView(Context context, MyGLRenderer renderer) {
        super(context);

        // OpenGL ES 3.0+ for GLSL "#version 300 es"
        setEGLContextClientVersion(3);

        setRenderer(renderer);

        // Default: draw once when requested
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }
}