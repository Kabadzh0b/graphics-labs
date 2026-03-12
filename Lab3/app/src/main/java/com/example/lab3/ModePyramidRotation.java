package com.example.lab3;

import android.opengl.GLES32;
import android.opengl.Matrix;
import android.os.SystemClock;

public class ModePyramidRotation extends myWorkMode {

    private int startBoard, countBoard;
    private int startPyr, countPyr;

    private float xDown, yDown;
    private float alphaPrev, distPrev;

    private static final float Kalpha = 0.15f;
    private static final float Kdist = 0.02f;

    private static final float FOVY = 50f;

    public ModePyramidRotation() {
        super();
        alphaViewAngle = 0f;
        betaViewAngle = 65f;
        viewDistance = 10f;
        buildScene();
    }

    @Override
    public boolean onTouchNotUsed() {
        return false;
    }

    @Override
    public boolean onActionDown(float x, float y, int cx, int cy) {
        xDown = x;
        yDown = y;
        alphaPrev = alphaViewAngle;
        distPrev = viewDistance;
        return false;
    }

    @Override
    public boolean onActionMove(float x, float y, int cx, int cy) {
        alphaViewAngle = alphaPrev + Kalpha * (xDown - x);
        viewDistance = clamp(distPrev + Kdist * (yDown - y), 4f, 30f);
        return true;
    }

    @Override
    public void myCreateShaderProgram() {
        compileAndLinkProgram(myShadersLibrary.VERTEX, myShadersLibrary.FRAGMENT);
        bindPositionColor(arrayVertex);
    }

    @Override
    public void myUseProgramForDrawing(int width, int height) {
        // View matrix: camera orbit around scene center (method from manual)
        Matrix.setIdentityM(viewMatrix, 0);
        Matrix.translateM(viewMatrix, 0, 0, 0, -viewDistance);
        Matrix.rotateM(viewMatrix, 0, -betaViewAngle, 1, 0, 0);
        Matrix.rotateM(viewMatrix, 0, -alphaViewAngle, 0, 0, 1);

        float aspect = (float) width / height;
        Matrix.perspectiveM(projectionMatrix, 0, FOVY, aspect, 0.1f, 50f);

        GLES32.glBindVertexArray(VAO_id);

        // 1) Board
        Matrix.setIdentityM(modelMatrix, 0);
        setUniformMatrices();
        GLES32.glDrawArrays(GLES32.GL_TRIANGLES, startBoard, countBoard);

        // 2) Pyramid rotation (around Z axis)
        Matrix.setIdentityM(modelMatrix, 0);

        long t = SystemClock.uptimeMillis();
        float angle = (t % 8000L) * (360f / 8000f);
        Matrix.rotateM(modelMatrix, 0, angle, 0, 0, 1);

        setUniformMatrices();
        GLES32.glDrawArrays(GLES32.GL_TRIANGLES, startPyr, countPyr);

        GLES32.glBindVertexArray(0);
    }

    private void buildScene() {
        float[] board = buildChessBoard(8, 1.0f);
        float[] pyr = buildPyramid();

        startBoard = 0;
        countBoard = board.length / 6;

        startPyr = countBoard;
        countPyr = pyr.length / 6;

        arrayVertex = new float[board.length + pyr.length];
        System.arraycopy(board, 0, arrayVertex, 0, board.length);
        System.arraycopy(pyr, 0, arrayVertex, board.length, pyr.length);
    }

    private float[] buildChessBoard(int n, float cell) {
        // Board in XY plane, Z=0
        float size = n * cell;
        float x0 = -size / 2f;
        float y0 = -size / 2f;

        float[] dark = {0.15f, 0.15f, 0.15f};
        float[] light = {0.75f, 0.75f, 0.75f};

        float z = 0f;
        float[] v = new float[n * n * 6 * 6];
        int p = 0;

        for (int iy = 0; iy < n; iy++) {
            for (int ix = 0; ix < n; ix++) {
                float xa = x0 + ix * cell;
                float ya = y0 + iy * cell;
                float xb = xa + cell;
                float yb = ya + cell;

                float[] c = ((ix + iy) % 2 == 0) ? light : dark;

                // Triangle 1: (xa,ya) (xb,ya) (xb,yb)
                p = put(v, p, xa, ya, z, c);
                p = put(v, p, xb, ya, z, c);
                p = put(v, p, xb, yb, z, c);

                // Triangle 2: (xa,ya) (xb,yb) (xa,yb)
                p = put(v, p, xa, ya, z, c);
                p = put(v, p, xb, yb, z, c);
                p = put(v, p, xa, yb, z, c);
            }
        }
        return v;
    }

    private float[] buildPyramid() {
        // Square pyramid: base on Z=0, apex on +Z
        float s = 1.2f;
        float h = 1.4f;

        float zBase = 0.01f; // avoid z-fighting with board

        float ax = 0, ay = 0, az = h;

        float x1 = -s / 2, y1 = -s / 2;
        float x2 =  s / 2, y2 = -s / 2;
        float x3 =  s / 2, y3 =  s / 2;
        float x4 = -s / 2, y4 =  s / 2;

        float[] baseC = {0.9f, 0.8f, 0.1f};
        float[] sideC1 = {0.9f, 0.2f, 0.2f};
        float[] sideC2 = {0.2f, 0.9f, 0.2f};
        float[] sideC3 = {0.2f, 0.2f, 0.9f};
        float[] sideC4 = {0.9f, 0.2f, 0.9f};

        // 6 (base) + 12 (sides) = 18 vertices, each vertex 6 floats
        float[] v = new float[18 * 6];
        int p = 0;

        // Base (2 triangles): (1,2,3) and (1,3,4)
        p = put(v, p, x1, y1, zBase, baseC);
        p = put(v, p, x2, y2, zBase, baseC);
        p = put(v, p, x3, y3, zBase, baseC);

        p = put(v, p, x1, y1, zBase, baseC);
        p = put(v, p, x3, y3, zBase, baseC);
        p = put(v, p, x4, y4, zBase, baseC);

        // Side 1: (1,2,A)
        p = put(v, p, x1, y1, zBase, sideC1);
        p = put(v, p, x2, y2, zBase, sideC1);
        p = put(v, p, ax, ay, az, sideC1);

        // Side 2: (2,3,A)
        p = put(v, p, x2, y2, zBase, sideC2);
        p = put(v, p, x3, y3, zBase, sideC2);
        p = put(v, p, ax, ay, az, sideC2);

        // Side 3: (3,4,A)
        p = put(v, p, x3, y3, zBase, sideC3);
        p = put(v, p, x4, y4, zBase, sideC3);
        p = put(v, p, ax, ay, az, sideC3);

        // Side 4: (4,1,A)
        p = put(v, p, x4, y4, zBase, sideC4);
        p = put(v, p, x1, y1, zBase, sideC4);
        p = put(v, p, ax, ay, az, sideC4);

        return v;
    }

    private int put(float[] dst, int pos, float x, float y, float z, float[] c) {
        dst[pos++] = x;
        dst[pos++] = y;
        dst[pos++] = z;
        dst[pos++] = c[0];
        dst[pos++] = c[1];
        dst[pos++] = c[2];
        return pos;
    }
}