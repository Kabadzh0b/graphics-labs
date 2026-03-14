package com.example.lab5;

import android.content.Context;
import android.opengl.GLES32;
import android.opengl.Matrix;

public class ModeSeaview extends myWorkMode {

    private int texBoard = 0;
    private int texTorus = 0;
    private final int[] texSky = new int[6];

    private Mesh meshBoard;
    private Mesh meshTorus;
    private Mesh meshSkybox;

    private int[] torusStripStarts;
    private int[] torusStripCounts;

    // Skybox faces: +X, -X, +Y, -Y, +Z, -Z
    @Override
    protected void myInitTextures(Context context) {
        texBoard = myLoadTexture(context, R.drawable.tex_checker, GLES32.GL_REPEAT);
        texTorus = myLoadTexture(context, R.drawable.tex_torus, GLES32.GL_REPEAT);

        texSky[0] = myLoadTexture(context, R.drawable.sky_right, GLES32.GL_CLAMP_TO_EDGE);
        texSky[1] = myLoadTexture(context, R.drawable.sky_left,  GLES32.GL_CLAMP_TO_EDGE);
        texSky[2] = myLoadTexture(context, R.drawable.sky_front, GLES32.GL_CLAMP_TO_EDGE);
        texSky[3] = myLoadTexture(context, R.drawable.sky_back,  GLES32.GL_CLAMP_TO_EDGE);
        texSky[4] = myLoadTexture(context, R.drawable.sky_top,   GLES32.GL_CLAMP_TO_EDGE);
        texSky[5] = myLoadTexture(context, R.drawable.sky_bottom,GLES32.GL_CLAMP_TO_EDGE);
    }

    @Override
    protected void myCreateShaderProgram() {
        compileAndLinkProgram(myShadersLibrary.VERTEX, myShadersLibrary.FRAGMENT);
    }

    @Override
    protected void myCreateObjects() {
        float[] board = MeshBuilder.buildBoard(9.0f, -0.9f, 4.0f);

        MeshBuilder.StripData torus = MeshBuilder.buildTorus(2.6f, 0.85f, 48, 72, 0.7f);
        torusStripStarts = torus.starts;
        torusStripCounts = torus.counts;

        MeshBuilder.GroupData sky = MeshBuilder.buildSkybox(60.0f);

        meshBoard = new Mesh(GLES32.GL_TRIANGLES, board, MeshBuilder.STRIDE, null, null, gl_Program);
        meshTorus = new Mesh(GLES32.GL_TRIANGLE_STRIP, torus.vertices, MeshBuilder.STRIDE, torusStripStarts, torusStripCounts, gl_Program);
        meshSkybox = new Mesh(GLES32.GL_TRIANGLES, sky.vertices, MeshBuilder.STRIDE, sky.starts, sky.counts, gl_Program);

        clearColor[0] = 0.0f;
        clearColor[1] = 0.0f;
        clearColor[2] = 0.0f;

        distance = 10.5f;
        alpha = 40f;
        beta = 65f;
    }

    @Override
    protected void myDrawing(int width, int height) {
        float[] proj = new float[16];
        float[] view = new float[16];
        float[] model = new float[16];

        perspective(proj, width, height, 60f, 0.1f, 200f);

        float[] eye = computeEye();
        Matrix.setLookAtM(view, 0,
                eye[0], eye[1], eye[2],
                0f, 0f, 0.0f,
                0f, 0f, 1f);

        setCommonUniforms(eye);

        // -------- Skybox FIRST --------
        // Do not write depth for skybox
        GLES32.glDepthMask(false);

        GLES32.glUniform1i(uUseLighting, 0);
        GLES32.glUniform3f(uColor, 1f, 1f, 1f);

        // Move skybox center to camera position so it "follows" the camera
        Matrix.setIdentityM(model, 0);
        Matrix.translateM(model, 0, eye[0], eye[1], eye[2]);
        setMatrices(model, view, proj);

        meshSkybox.bind();
        for (int i = 0; i < 6; i++) {
            GLES32.glActiveTexture(GLES32.GL_TEXTURE0);
            GLES32.glBindTexture(GLES32.GL_TEXTURE_2D, texSky[i]);
            meshSkybox.drawGroup(i);
        }
        meshSkybox.unbind();
        GLES32.glBindTexture(GLES32.GL_TEXTURE_2D, 0);

        // Restore depth writes
        GLES32.glDepthMask(true);

        // -------- Objects inside skybox (board + torus) --------
        GLES32.glUniform1i(uUseLighting, 1);
        GLES32.glUniform3f(uColor, 1f, 1f, 1f);

        // board
        GLES32.glBindTexture(GLES32.GL_TEXTURE_2D, texBoard);
        Matrix.setIdentityM(model, 0);
        setMatrices(model, view, proj);

        meshBoard.bind();
        meshBoard.drawAll();
        meshBoard.unbind();

        // torus
        GLES32.glBindTexture(GLES32.GL_TEXTURE_2D, texTorus);
        Matrix.setIdentityM(model, 0);
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
