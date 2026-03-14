package com.example.lab5;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;

public class MyGLSurfaceView extends GLSurfaceView {

    private final WorkModeProvider provider;

    public MyGLSurfaceView(Context context, MyGLRenderer renderer, WorkModeProvider provider) {
        super(context);
        this.provider = provider;

        setEGLContextClientVersion(3);
        setRenderer(renderer);
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

        boolean changed = false;
        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN:
                changed = wm.onActionDown(x, y, cx, cy);
                break;
            case MotionEvent.ACTION_MOVE:
                changed = wm.onActionMove(x, y, cx, cy);
                break;
            default:
                break;
        }
        if (changed) requestRender();
        return true;
    }
}
