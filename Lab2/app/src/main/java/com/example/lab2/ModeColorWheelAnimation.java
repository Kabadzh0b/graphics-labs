package com.example.lab2;

import android.opengl.GLES32;
import android.os.SystemClock;

public class ModeColorWheelAnimation extends ModeColorWheel {

    private final boolean animateStrip;

    public ModeColorWheelAnimation(boolean animateStrip) {
        super();
        this.animateStrip = animateStrip;
    }

    @Override
    public void myUseProgramForDrawing(int width, int height) {
        long time = SystemClock.uptimeMillis() % 2000L;
        long v = (time <= 1000) ? time : (1999 - time);
        float light = 0.001f * (float) v;

        int hLight = GLES32.glGetUniformLocation(getProgramId(), "vLight");

        GLES32.glBindVertexArray(VAO_id);

        // fan
        GLES32.glUniform1f(hLight, animateStrip ? 1.0f : light);
        GLES32.glDrawArrays(GLES32.GL_TRIANGLE_FAN, startFan, countFan);

        // strip
        GLES32.glUniform1f(hLight, animateStrip ? light : 1.0f);
        GLES32.glDrawArrays(GLES32.GL_TRIANGLE_STRIP, startStrip, countStrip);

        GLES32.glBindVertexArray(0);
    }
}