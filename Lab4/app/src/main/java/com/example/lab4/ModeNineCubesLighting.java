package com.example.lab4;

import android.opengl.GLES32;
import android.opengl.Matrix;

public class ModeNineCubesLighting extends myWorkMode {

    private int startCube, countCube;

    private float xCamera = -2.8f;
    private float yCamera = -2.8f;
    private float zCamera = 2.0f;

    private float xDown, yDown;
    private float alphaPrev, betaPrev;

    private boolean moveMode = false;

    private static final float Kalpha = 0.15f;
    private static final float Kbeta  = 0.12f;
    private static final float Speed  = 0.004f;

    private static final float CUBE_HALF = 0.35f;
    private static final float CAM_RADIUS = 0.25f;

    private final float[][] cubeCenters;

    public ModeNineCubesLighting() {
        super();

        clearColor[0] = 0.02f;
        clearColor[1] = 0.02f;
        clearColor[2] = 0.12f;

        alpha = 45f;
        beta = 65f;

        buildScene();
        cubeCenters = buildCubeCenters();
    }

    @Override
    public boolean onTouchNotUsed() { return false; }

    @Override
    public boolean onActionDown(float x, float y, int cx, int cy) {
        xDown = x;
        yDown = y;

        alphaPrev = alpha;
        betaPrev = beta;

        moveMode = (y > cy * 0.60f);
        return false;
    }

    @Override
    public boolean onActionMove(float x, float y, int cx, int cy) {
        alpha = alphaPrev + Kalpha * (xDown - x);

        if (!moveMode) {
            beta = clamp(betaPrev + Kbeta * (yDown - y), 15f, 165f);
            return true;
        }

        float step = Speed * (yDown - y);

        float a = degToRad(alpha);
        float b = degToRad(beta);

        float nx = xCamera - step * (float) Math.sin(a);
        float ny = yCamera + step * (float) Math.cos(a);
        float nz = zCamera - step * (float) Math.cos(b);

        if (!collides(nx, ny, nz)) {
            xCamera = nx;
            yCamera = ny;
            zCamera = nz;
        }

        yDown = y;
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
        setPerspective(width, height, 55f, 0.1f, 80f);

        Matrix.setIdentityM(viewMatrix, 0);
        Matrix.rotateM(viewMatrix, 0, -beta, 1f, 0f, 0f);
        Matrix.rotateM(viewMatrix, 0, -alpha, 0f, 0f, 1f);
        Matrix.translateM(viewMatrix, 0, -xCamera, -yCamera, -zCamera);

        // Light at camera position; do NOT draw it
        lightPos[0] = xCamera;
        lightPos[1] = yCamera;
        lightPos[2] = zCamera;

        lightColor[0] = 1.0f;
        lightColor[1] = 1.0f;
        lightColor[2] = 1.0f;

        setCommonUniforms(xCamera, yCamera, zCamera);

        GLES32.glBindVertexArray(VAO_id);
        GLES32.glUniform1i(uIsLight, 0);
        GLES32.glUniform3f(uColor, 0.45f, 0.55f, 0.95f);

        for (float[] c : cubeCenters) {
            Matrix.setIdentityM(modelMatrix, 0);
            Matrix.translateM(modelMatrix, 0, c[0], c[1], c[2]);
            setMatrices();
            GLES32.glDrawArrays(GLES32.GL_TRIANGLES, startCube, countCube);
        }

        GLES32.glBindVertexArray(0);
    }

    private void buildScene() {
        float[] cube = myGraphicPrimitives.buildCube(CUBE_HALF);
        arrayVertex = new float[cube.length];
        System.arraycopy(cube, 0, arrayVertex, 0, cube.length);

        startCube = 0;
        countCube = cube.length / 6;
    }

    private float[][] buildCubeCenters() {
        float[] xs = {-1.8f, 0f, 1.8f};
        float[] ys = {-1.8f, 0f, 1.8f};
        float[] zs = {0.4f, 1.6f, 2.8f};

        float[][] centers = new float[27][3];
        int k = 0;
        for (float z : zs) {
            for (float y : ys) {
                for (float x : xs) {
                    centers[k][0] = x;
                    centers[k][1] = y;
                    centers[k][2] = z;
                    k++;
                }
            }
        }
        return centers;
    }

    private boolean collides(float x, float y, float z) {
        float r = CUBE_HALF + CAM_RADIUS;
        for (float[] c : cubeCenters) {
            if (Math.abs(x - c[0]) < r &&
                Math.abs(y - c[1]) < r &&
                Math.abs(z - c[2]) < r) {
                return true;
            }
        }
        return false;
    }
}
