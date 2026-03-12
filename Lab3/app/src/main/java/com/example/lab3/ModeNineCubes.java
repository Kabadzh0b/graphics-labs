package com.example.lab3;

import android.opengl.GLES32;
import android.opengl.Matrix;

public class ModeNineCubes extends myWorkMode {

    private int startBoard, countBoard;
    private int startCube, countCube;

    private float xDown, yDown;
    private float alphaPrev, betaPrev;
    private float camXPrev, camYPrev, camZPrev;

    private boolean moveMode = false;

    private static final float Kalpha = 0.15f;
    private static final float Kbeta  = 0.12f;
    private static final float Speed  = 0.004f;

    private static final float FOVY = 55f;

    private static final float CUBE_HALF = 0.30f;
    private static final float CAM_RADIUS = 0.25f;

    private final float[][] cubeCenters;

    public ModeNineCubes() {
        super();

        alphaViewAngle = 45f;
        betaViewAngle = 65f;

        xCamera = -4f;
        yCamera = -4f;
        zCamera = 2.2f;

        buildScene();
        cubeCenters = buildCubeCenters();
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
        betaPrev = betaViewAngle;

        camXPrev = xCamera;
        camYPrev = yCamera;
        camZPrev = zCamera;

        // Bottom part of the screen -> movement
        moveMode = (y > cy * 0.60f);
        return false;
    }

    @Override
    public boolean onActionMove(float x, float y, int cx, int cy) {
        // Yaw always from horizontal movement
        alphaViewAngle = alphaPrev + Kalpha * (xDown - x);

        if (!moveMode) {
            // Top area: pitch control
            betaViewAngle = clamp(betaPrev + Kbeta * (yDown - y), 15f, 165f);
            return true;
        }

        // Bottom area: move forward/back along view direction
        float step = Speed * (yDown - y);

        float a = degToRad(alphaViewAngle);
        float b = degToRad(betaViewAngle);

        float dx = (float) (-step * Math.sin(a));
        float dy = (float) ( step * Math.cos(a));
        float dz = (float) (-step * Math.cos(b));

        float nx = xCamera + dx;
        float ny = yCamera + dy;
        float nz = zCamera + dz;

        if (!collides(nx, ny, nz)) {
            xCamera = nx;
            yCamera = ny;
            zCamera = nz;
        }

        // Make movement incremental
        yDown = y;

        return true;
    }

    @Override
    public void myCreateShaderProgram() {
        compileAndLinkProgram(myShadersLibrary.VERTEX, myShadersLibrary.FRAGMENT);
        bindPositionColor(arrayVertex);
    }

    @Override
    public void myUseProgramForDrawing(int width, int height) {
        // View matrix: camera at (xCamera,yCamera,zCamera) + angles
        Matrix.setIdentityM(viewMatrix, 0);
        Matrix.rotateM(viewMatrix, 0, -betaViewAngle, 1, 0, 0);
        Matrix.rotateM(viewMatrix, 0, -alphaViewAngle, 0, 0, 1);
        Matrix.translateM(viewMatrix, 0, -xCamera, -yCamera, -zCamera);

        float aspect = (float) width / height;
        Matrix.perspectiveM(projectionMatrix, 0, FOVY, aspect, 0.1f, 80f);

        GLES32.glBindVertexArray(VAO_id);

        // Board
        Matrix.setIdentityM(modelMatrix, 0);
        setUniformMatrices();
        GLES32.glDrawArrays(GLES32.GL_TRIANGLES, startBoard, countBoard);

        // 27 cubes
        for (float[] c : cubeCenters) {
            Matrix.setIdentityM(modelMatrix, 0);
            Matrix.translateM(modelMatrix, 0, c[0], c[1], c[2]);
            setUniformMatrices();
            GLES32.glDrawArrays(GLES32.GL_TRIANGLES, startCube, countCube);
        }

        GLES32.glBindVertexArray(0);
    }

    private void buildScene() {
        float[] board = buildChessBoard(10, 1.0f);
        float[] cube = buildCubeMesh(CUBE_HALF);

        startBoard = 0;
        countBoard = board.length / 6;

        startCube = countBoard;
        countCube = cube.length / 6;

        arrayVertex = new float[board.length + cube.length];
        System.arraycopy(board, 0, arrayVertex, 0, board.length);
        System.arraycopy(cube, 0, arrayVertex, board.length, cube.length);
    }

    private float[] buildChessBoard(int n, float cell) {
        float size = n * cell;
        float x0 = -size / 2f;
        float y0 = -size / 2f;

        float[] dark = {0.10f, 0.10f, 0.10f};
        float[] light = {0.65f, 0.65f, 0.65f};

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

                p = put(v, p, xa, ya, z, c);
                p = put(v, p, xb, ya, z, c);
                p = put(v, p, xb, yb, z, c);

                p = put(v, p, xa, ya, z, c);
                p = put(v, p, xb, yb, z, c);
                p = put(v, p, xa, yb, z, c);
            }
        }
        return v;
    }

    private float[] buildCubeMesh(float h) {
        // 6 faces * 2 triangles * 3 vertices = 36 vertices
        // Cube centered at origin
        float[] v = new float[36 * 6];
        int p = 0;

        // Face colors
        float[] c1 = {1f, 0.2f, 0.2f};
        float[] c2 = {0.2f, 1f, 0.2f};
        float[] c3 = {0.2f, 0.2f, 1f};
        float[] c4 = {1f, 1f, 0.2f};
        float[] c5 = {0.2f, 1f, 1f};
        float[] c6 = {1f, 0.2f, 1f};

        // Helper: add face as two triangles (a,b,c) (a,c,d)
        p = face(v, p, -h, -h, -h,   h, -h, -h,   h,  h, -h,  -h,  h, -h, c1); // back
        p = face(v, p, -h, -h,  h,  -h,  h,  h,   h,  h,  h,   h, -h,  h, c2); // front
        p = face(v, p, -h, -h, -h,  -h,  h, -h,  -h,  h,  h,  -h, -h,  h, c3); // left
        p = face(v, p,  h, -h, -h,   h, -h,  h,   h,  h,  h,   h,  h, -h, c4); // right
        p = face(v, p, -h,  h, -h,   h,  h, -h,   h,  h,  h,  -h,  h,  h, c5); // top
        p = face(v, p, -h, -h, -h,  -h, -h,  h,   h, -h,  h,   h, -h, -h, c6); // bottom

        return v;
    }

    private int face(float[] dst, int pos,
                     float ax, float ay, float az,
                     float bx, float by, float bz,
                     float cx, float cy, float cz,
                     float dx, float dy, float dz,
                     float[] c) {
        pos = put(dst, pos, ax, ay, az, c);
        pos = put(dst, pos, bx, by, bz, c);
        pos = put(dst, pos, cx, cy, cz, c);

        pos = put(dst, pos, ax, ay, az, c);
        pos = put(dst, pos, cx, cy, cz, c);
        pos = put(dst, pos, dx, dy, dz, c);

        return pos;
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

    private float[][] buildCubeCenters() {
        float[] xs = {-1.6f, 0f, 1.6f};
        float[] ys = {-1.6f, 0f, 1.6f};
        float[] zs = {0.35f, 1.35f, 2.35f};

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