package com.example.lab5;

import android.content.Context;
import android.opengl.GLES32;
import android.opengl.Matrix;

public class ModeEarth extends myWorkMode {

    private int texEarth = 0;

    private Mesh meshSphere;
    private int[] sphereStripStarts;
    private int[] sphereStripCounts;

    @Override
    protected void myInitTextures(Context context) {
        texEarth = myLoadTexture(context, R.drawable.tex_earth, GLES32.GL_REPEAT);
    }

    @Override
    protected void myCreateShaderProgram() {
        compileAndLinkProgram(myShadersLibrary.VERTEX, myShadersLibrary.FRAGMENT);
    }

    @Override
    protected void myCreateObjects() {
        MeshBuilder.StripData sphere = MeshBuilder.buildSphere(2.2f, 48, 72, 0.0f);

        sphereStripStarts = sphere.starts;
        sphereStripCounts = sphere.counts;

        meshSphere = new Mesh(GLES32.GL_TRIANGLE_STRIP, sphere.vertices, MeshBuilder.STRIDE, sphereStripStarts, sphereStripCounts, gl_Program);

        lightPos[0] = 5.0f;
        lightPos[1] = 1.5f;
        lightPos[2] = 6.5f;

        distance = 9.5f;
        alpha = 30f;
        beta = 60f;
    }

    @Override
    protected void myDrawing(int width, int height) {
        float[] proj = new float[16];
        float[] view = new float[16];
        float[] model = new float[16];

        perspective(proj, width, height, 55f, 0.1f, 120f);

        float[] eye = computeEye();
        Matrix.setLookAtM(view, 0,
                eye[0], eye[1], eye[2],
                0f, 0f, 0f,
                0f, 0f, 1f);

        setCommonUniforms(eye);

        GLES32.glActiveTexture(GLES32.GL_TEXTURE0);
        GLES32.glBindTexture(GLES32.GL_TEXTURE_2D, texEarth);

        GLES32.glUniform1i(uUseLighting, 1);
        GLES32.glUniform3f(uColor, 1.0f, 1.0f, 1.0f);

        Matrix.setIdentityM(model, 0);
        Matrix.rotateM(model, 0, -25f, 0f, 0f, 1f);
        setMatrices(model, view, proj);

        meshSphere.bind();
        for (int i = 0; i < sphereStripStarts.length; i++) {
            GLES32.glDrawArrays(GLES32.GL_TRIANGLE_STRIP, sphereStripStarts[i], sphereStripCounts[i]);
        }
        meshSphere.unbind();

        GLES32.glBindTexture(GLES32.GL_TEXTURE_2D, 0);
    }
}
