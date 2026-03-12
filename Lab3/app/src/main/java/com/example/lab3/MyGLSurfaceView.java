package com.example.lab3;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;

public class MyGLSurfaceView extends GLSurfaceView {

    private final WorkModeProvider provider;

    public MyGLSurfaceView(Context context, MyGLRenderer renderer, WorkModeProvider provider) {
        super(context);
        this.provider = provider;

        // OpenGL ES 3.0+ because shaders use "#version 300 es"
        setEGLContextClientVersion(3);

        setRenderer(renderer);

        // Default (mode will set it anyway)
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        myWorkMode wm = provider.getWorkMode();
        if (wm == null) return false;
        if (wm.onTouchNotUsed()) return false;

        int cx = getWidth();
        int cy = getHeight();
        float x = e.getX();
        float y = e.getY();

        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (wm.onActionDown(x, y, cx, cy)) requestRender();
                break;
            case MotionEvent.ACTION_MOVE:
                if (wm.onActionMove(x, y, cx, cy)) requestRender();
                break;
            default:
                break;
        }
        return true;
    }
}