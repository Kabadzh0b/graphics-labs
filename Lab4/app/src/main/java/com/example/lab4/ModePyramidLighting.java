package com.example.lab4;

import android.opengl.GLES32;
import android.opengl.Matrix;
import android.os.SystemClock;

public class ModePyramidLighting extends myWorkMode {

    private int startBoard, countBoard;
    private int startPyr, countPyr;

    public ModePyramidLighting() {
        super();

        lightPos[0] = 3.0f;
        lightPos[1] = 1.5f;
        lightPos[2] = 3.5f;

        lightColor[0] = 1.0f;
        lightColor[1] = 0.3f;
        lightColor[2] = 1.0f;

        alpha = 35f;
        beta = 65f;
        distance = 12f;

        clearColor[0] = 0.02f;
        clearColor[1] = 0.02f;
        clearColor[2] = 0.12f;

        buildScene();
    }

    @Override
    public boolean onTouchNotUsed() { return false; }

    @Override
    public boolean onActionDown(float x, float y, int cx, int cy) { return updateLightFromTouch(x, y, cx, cy); }

    @Override
    public boolean onActionMove(float x, float y, int cx, int cy) { return updateLightFromTouch(x, y, cx, cy); }

    private boolean updateLightFromTouch(float x, float y, int cx, int cy) {
        float nx = (x / (float) cx) * 2f - 1f;
        float ny = 1f - (y / (float) cy) * 2f;

        lightPos[0] = nx * 4.0f;
        lightPos[1] = ny * 4.0f;
        lightPos[2] = 3.5f;
        return true;
    }

    @Override
    public void myCreateShaderProgram() {
        compileAndLinkProgram(myShadersLibrary.VERTEX, myShadersLibrary.FRAG_PHONG_ATTEN);
        if (gl_Program <= 0) return;
        cacheUniformLocations();
        bindPositionNormal(arrayVertex);
    }

    @Override
    public void myUseProgramForDrawing(int width, int height) {
        setPerspective(width, height, 50f, 0.1f, 80f);

        float[] eye = computeOrbitEye();
        Matrix.setLookAtM(viewMatrix, 0,
                eye[0], eye[1], eye[2],
                0f, 0f, 1.0f,
                0f, 0f, 1f);

        setCommonUniforms(eye[0], eye[1], eye[2]);

        GLES32.glBindVertexArray(VAO_id);

        Matrix.setIdentityM(modelMatrix, 0);
        GLES32.glUniform1i(uIsLight, 0);
        GLES32.glUniform3f(uColor, 0.75f, 0.25f, 0.75f);
        setMatrices();
        GLES32.glDrawArrays(GLES32.GL_TRIANGLES, startBoard, countBoard);

        Matrix.setIdentityM(modelMatrix, 0);
        Matrix.translateM(modelMatrix, 0, 0f, 0f, 0.02f);

        long t = SystemClock.uptimeMillis();
        float angle = (t % 7000L) * (360f / 7000f);
        Matrix.rotateM(modelMatrix, 0, angle, 0f, 0f, 1f);

        GLES32.glUniform3f(uColor, 0.85f, 0.70f, 0.10f);
        setMatrices();
        GLES32.glDrawArrays(GLES32.GL_TRIANGLES, startPyr, countPyr);

        Matrix.setIdentityM(modelMatrix, 0);
        Matrix.translateM(modelMatrix, 0, lightPos[0], lightPos[1], lightPos[2]);
        GLES32.glUniform1i(uIsLight, 1);
        setMatrices();
        GLES32.glDrawArrays(GLES32.GL_POINTS, 0, 1);

        GLES32.glBindVertexArray(0);
    }

    private void buildScene() {
        float[] board = myGraphicPrimitives.buildChessBoard(8, 1.0f, 0.0f);
        float[] pyr = myGraphicPrimitives.buildPyramid(2.0f, 2.2f, 0.02f);

        int totalVerts = 1 + board.length / 6 + pyr.length / 6;
        arrayVertex = new float[totalVerts * 6];

        int p = 0;
        p = myGraphicPrimitives.addVertexXYZn(arrayVertex, p, 0f, 0f, 0f, 0f, 0f, 1f);

        startBoard = 1;
        countBoard = board.length / 6;
        System.arraycopy(board, 0, arrayVertex, p, board.length);
        p += board.length;

        startPyr = startBoard + countBoard;
        countPyr = pyr.length / 6;
        System.arraycopy(pyr, 0, arrayVertex, p, pyr.length);
    }
}
