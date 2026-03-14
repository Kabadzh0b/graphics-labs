package com.example.lab5;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES32;
import android.opengl.GLUtils;
import android.opengl.Matrix;
import android.util.Log;

public class myWorkMode {

    protected int gl_Program = 0;

    protected int uModel = -1;
    protected int uView = -1;
    protected int uProj = -1;

    protected int uColor = -1;
    protected int uLightColor = -1;
    protected int uLightPos = -1;
    protected int uEyePos = -1;
    protected int uUseLighting = -1;
    protected int uTexSample = -1;

    protected final float[] clearColor = new float[]{0.02f, 0.02f, 0.05f};

    // Orbit camera (alpha, beta) + zoom (distance)
    protected float alpha = 35f;
    protected float beta = 60f;
    protected float distance = 10f;

    // Touch state
    private float xPrev;
    private float yPrev;
    private boolean zoomMode;

    // Lighting
    protected final float[] lightPos = new float[]{3.0f, 2.0f, 5.0f};
    protected final float[] lightColor = new float[]{1.0f, 1.0f, 1.0f};

    public int getProgramId() {
        return gl_Program;
    }

    public float[] getClearColor() {
        return clearColor;
    }

    public boolean onTouchNotUsed() { return false; }

    public boolean onActionDown(float x, float y, int cx, int cy) {
        xPrev = x;
        yPrev = y;
        zoomMode = (y > cy * 0.75f);
        return false;
    }

    public boolean onActionMove(float x, float y, int cx, int cy) {
        float dx = x - xPrev;
        float dy = y - yPrev;

        xPrev = x;
        yPrev = y;

        alpha -= dx * 0.25f;

        if (zoomMode) {
            distance += dy * 0.02f;
            distance = clamp(distance, 3.5f, 40f);
        } else {
            beta += dy * 0.25f;
            beta = clamp(beta, 5f, 175f);
        }
        return true;
    }

    protected static float clamp(float v, float lo, float hi) {
        return Math.max(lo, Math.min(hi, v));
    }

    protected static float degToRad(float deg) {
        return (float) (deg * Math.PI / 180.0);
    }

    protected float[] computeEye() {
        float a = degToRad(alpha);
        float b = degToRad(beta);

        float x = (float) (distance * Math.sin(b) * Math.sin(a));
        float y = (float) (-distance * Math.sin(b) * Math.cos(a));
        float z = (float) (distance * Math.cos(b));

        return new float[]{x, y, z};
    }

    protected int compileShader(int type, String code) {
        int sh = GLES32.glCreateShader(type);
        GLES32.glShaderSource(sh, code);
        GLES32.glCompileShader(sh);

        int[] ok = new int[1];
        GLES32.glGetShaderiv(sh, GLES32.GL_COMPILE_STATUS, ok, 0);
        if (ok[0] == 0) {
            Log.e("Lab5", "Shader compile error: " + GLES32.glGetShaderInfoLog(sh));
            GLES32.glDeleteShader(sh);
            return 0;
        }
        return sh;
    }

    protected void compileAndLinkProgram(String vsh, String fsh) {
        int vs = compileShader(GLES32.GL_VERTEX_SHADER, vsh);
        if (vs == 0) {
            gl_Program = -1;
            return;
        }

        int fs = compileShader(GLES32.GL_FRAGMENT_SHADER, fsh);
        if (fs == 0) {
            gl_Program = -1;
            return;
        }

        gl_Program = GLES32.glCreateProgram();
        GLES32.glAttachShader(gl_Program, vs);
        GLES32.glAttachShader(gl_Program, fs);
        GLES32.glLinkProgram(gl_Program);

        int[] linked = new int[1];
        GLES32.glGetProgramiv(gl_Program, GLES32.GL_LINK_STATUS, linked, 0);
        if (linked[0] == 0) {
            Log.e("Lab5", "Program link error: " + GLES32.glGetProgramInfoLog(gl_Program));
            GLES32.glDeleteProgram(gl_Program);
            gl_Program = -1;
        }

        GLES32.glDeleteShader(vs);
        GLES32.glDeleteShader(fs);
    }

    public int myLoadTexture(Context context, int resourceId, int wrap) {
        int[] tmp = new int[1];
        GLES32.glGenTextures(1, tmp, 0);
        int handle = tmp[0];

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resourceId, options);

        GLES32.glBindTexture(GLES32.GL_TEXTURE_2D, handle);
        GLES32.glTexParameteri(GLES32.GL_TEXTURE_2D, GLES32.GL_TEXTURE_WRAP_S, wrap);
        GLES32.glTexParameteri(GLES32.GL_TEXTURE_2D, GLES32.GL_TEXTURE_WRAP_T, wrap);
        GLES32.glTexParameteri(GLES32.GL_TEXTURE_2D, GLES32.GL_TEXTURE_MIN_FILTER, GLES32.GL_LINEAR);
        GLES32.glTexParameteri(GLES32.GL_TEXTURE_2D, GLES32.GL_TEXTURE_MAG_FILTER, GLES32.GL_LINEAR);

        GLUtils.texImage2D(GLES32.GL_TEXTURE_2D, 0, bitmap, 0);

        bitmap.recycle();
        GLES32.glBindTexture(GLES32.GL_TEXTURE_2D, 0);
        return handle;
    }

    protected void cacheUniforms() {
        uModel = GLES32.glGetUniformLocation(gl_Program, "uModelMatrix");
        uView = GLES32.glGetUniformLocation(gl_Program, "uViewMatrix");
        uProj = GLES32.glGetUniformLocation(gl_Program, "uProjMatrix");

        uColor = GLES32.glGetUniformLocation(gl_Program, "vColor");
        uLightColor = GLES32.glGetUniformLocation(gl_Program, "uLightColor");
        uLightPos = GLES32.glGetUniformLocation(gl_Program, "uLightPos");
        uEyePos = GLES32.glGetUniformLocation(gl_Program, "uEyePos");
        uUseLighting = GLES32.glGetUniformLocation(gl_Program, "uUseLighting");

        uTexSample = GLES32.glGetUniformLocation(gl_Program, "vTextureSample");
    }

    protected void setCommonUniforms(float[] eye) {
        GLES32.glUniform3fv(uLightColor, 1, lightColor, 0);
        GLES32.glUniform3fv(uLightPos, 1, lightPos, 0);
        GLES32.glUniform3fv(uEyePos, 1, eye, 0);
        GLES32.glUniform1i(uTexSample, 0);
    }

    protected void setMatrices(float[] model, float[] view, float[] proj) {
        GLES32.glUniformMatrix4fv(uModel, 1, false, model, 0);
        GLES32.glUniformMatrix4fv(uView, 1, false, view, 0);
        GLES32.glUniformMatrix4fv(uProj, 1, false, proj, 0);
    }

    public void myCreateScene(Context context) {
        myInitTextures(context);
        myCreateShaderProgram();
        if (gl_Program <= 0) return;

        cacheUniforms();
        myCreateObjects();
    }

    protected void myInitTextures(Context context) {}
    protected void myCreateShaderProgram() {}
    protected void myCreateObjects() {}

    public void myUseProgramForDrawing(int width, int height) {
        myDrawing(width, height);
    }

    protected void myDrawing(int width, int height) {}

    protected void perspective(float[] proj, int w, int h, float fovyDeg, float near, float far) {
        float aspect = (float) w / (float) h;
        Matrix.perspectiveM(proj, 0, fovyDeg, aspect, near, far);
    }
}
