package com.example.lab4;

import android.opengl.GLES32;
import android.opengl.Matrix;

public class ModeDiffuseLighting extends myWorkMode {

    private int startQuad;
    private int countQuad;

    public ModeDiffuseLighting() {
        super();

        lightPos[0] = 1.5f;
        lightPos[1] = 1.0f;
        lightPos[2] = 2.5f;

        lightColor[0] = 1.0f;
        lightColor[1] = 0.2f;
        lightColor[2] = 1.0f;

        clearColor[0] = 0.02f;
        clearColor[1] = 0.02f;
        clearColor[2] = 0.10f;

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

        lightPos[0] = nx * 3.0f;
        lightPos[1] = ny * 3.0f;
        lightPos[2] = 2.8f;
        return true;
    }

    @Override
    public void myCreateShaderProgram() {
        compileAndLinkProgram(myShadersLibrary.VERTEX, myShadersLibrary.FRAG_DIFFUSE_ATTEN);
        if (gl_Program <= 0) return;
        cacheUniformLocations();
        bindPositionNormal(arrayVertex);
    }

    @Override
    public void myUseProgramForDrawing(int width, int height) {
        setPerspective(width, height, 55f, 0.1f, 60f);

        float[] eye = computeOrbitEye();
        Matrix.setLookAtM(viewMatrix, 0,
                eye[0], eye[1], eye[2],
                0f, 0f, 0f,
                0f, 0f, 1f);

        setCommonUniforms(eye[0], eye[1], eye[2]);

        GLES32.glBindVertexArray(VAO_id);

        Matrix.setIdentityM(modelMatrix, 0);
        GLES32.glUniform1i(uIsLight, 0);
        GLES32.glUniform3f(uColor, 0.6f, 0.6f, 0.6f);
        setMatrices();
        GLES32.glDrawArrays(GLES32.GL_TRIANGLES, startQuad, countQuad);

        Matrix.setIdentityM(modelMatrix, 0);
        Matrix.translateM(modelMatrix, 0, lightPos[0], lightPos[1], lightPos[2]);
        GLES32.glUniform1i(uIsLight, 1);
        setMatrices();
        GLES32.glDrawArrays(GLES32.GL_POINTS, 0, 1);

        GLES32.glBindVertexArray(0);
    }

    private void buildScene() {
        float[] quad = myGraphicPrimitives.buildQuadPlane(6.0f, 0.0f);

        arrayVertex = new float[(1 + quad.length / 6) * 6];
        int p = 0;

        p = myGraphicPrimitives.addVertexXYZn(arrayVertex, p, 0f, 0f, 0f, 0f, 0f, 1f);

        startQuad = 1;
        countQuad = quad.length / 6;
        System.arraycopy(quad, 0, arrayVertex, p, quad.length);
    }
}
