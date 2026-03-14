package com.example.lab5;

import android.content.Context;
import android.opengl.GLES32;
import android.opengl.Matrix;

public class ModeTorus extends myWorkMode {

    private int texBoard = 0;
    private int texTorus = 0;

    private Mesh meshBoard;
    private Mesh meshTorus;

    private int[] torusStripStarts;
    private int[] torusStripCounts;

    @Override
    protected void myInitTextures(Context context) {
        texBoard = myLoadTexture(context, R.drawable.tex_checker, GLES32.GL_REPEAT);
        texTorus = myLoadTexture(context, R.drawable.tex_torus, GLES32.GL_REPEAT);
    }

    @Override
    protected void myCreateShaderProgram() {
        compileAndLinkProgram(myShadersLibrary.VERTEX, myShadersLibrary.FRAGMENT);
    }

    @Override
    protected void myCreateObjects() {
        // Board: 4x4 repeated chess texture
        float[] board = MeshBuilder.buildBoard(9.0f, 0.0f, 4.0f);

        // Torus: triangle strips (segLat strips)
        MeshBuilder.StripData torus = MeshBuilder.buildTorus(2.6f, 0.85f, 48, 72, 1.2f);

        torusStripStarts = torus.starts;
        torusStripCounts = torus.counts;

        meshBoard = new Mesh(GLES32.GL_TRIANGLES, board, MeshBuilder.STRIDE, null, null, gl_Program);
        meshTorus = new Mesh(GLES32.GL_TRIANGLE_STRIP, torus.vertices, MeshBuilder.STRIDE, torusStripStarts, torusStripCounts, gl_Program);

        // Lighting
        lightPos[0] = 3.5f;
        lightPos[1] = 2.0f;
        lightPos[2] = 5.5f;
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
                0f, 0f, 0.8f,
                0f, 0f, 1f);

        setCommonUniforms(eye);

        // -------- Board --------
        GLES32.glActiveTexture(GLES32.GL_TEXTURE0);
        GLES32.glBindTexture(GLES32.GL_TEXTURE_2D, texBoard);

        GLES32.glUniform1i(uUseLighting, 1);
        GLES32.glUniform3f(uColor, 1.0f, 1.0f, 1.0f);

        Matrix.setIdentityM(model, 0);
        setMatrices(model, view, proj);

        meshBoard.bind();
        meshBoard.drawAll();
        meshBoard.unbind();

        // -------- Torus --------
        GLES32.glBindTexture(GLES32.GL_TEXTURE_2D, texTorus);

        Matrix.setIdentityM(model, 0);
        // small rotation for better view
        Matrix.rotateM(model, 0, 25f, 1f, 0f, 0f);
        setMatrices(model, view, proj);

        meshTorus.bind();
        for (int i = 0; i < torusStripStarts.length; i++) {
            GLES32.glDrawArrays(GLES32.GL_TRIANGLE_STRIP, torusStripStarts[i], torusStripCounts[i]);
        }
        meshTorus.unbind();

        GLES32.glBindTexture(GLES32.GL_TEXTURE_2D, 0);
    }
}
