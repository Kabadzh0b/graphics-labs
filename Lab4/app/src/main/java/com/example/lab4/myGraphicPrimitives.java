package com.example.lab4;

public class myGraphicPrimitives {

    public static int addVertexXYZn(float[] vdest, int pos,
                                   float x, float y, float z,
                                   float xn, float yn, float zn) {
        float N = (float) Math.sqrt(xn * xn + yn * yn + zn * zn);
        if (N <= 0) return pos;

        vdest[pos++] = x;
        vdest[pos++] = y;
        vdest[pos++] = z;

        vdest[pos++] = xn / N;
        vdest[pos++] = yn / N;
        vdest[pos++] = zn / N;
        return pos;
    }

    // Vertices should be provided in clockwise order (as in the lab handout) to get outward normals.
    public static int addTriangleXYZn(float[] vdest, int pos,
                                     float x1, float y1, float z1,
                                     float x2, float y2, float z2,
                                     float x3, float y3, float z3) {
        float xn = (z2 - z1) * (y3 - y1) - (y2 - y1) * (z3 - z1);
        float yn = (x2 - x1) * (z3 - z1) - (z2 - z1) * (x3 - x1);
        float zn = (y2 - y1) * (x3 - x1) - (x2 - x1) * (y3 - y1);

        pos = addVertexXYZn(vdest, pos, x1, y1, z1, xn, yn, zn);
        pos = addVertexXYZn(vdest, pos, x2, y2, z2, xn, yn, zn);
        pos = addVertexXYZn(vdest, pos, x3, y3, z3, xn, yn, zn);
        return pos;
    }

    public static float[] buildQuadPlane(float size, float z) {
        float s = size / 2f;
        float[] v = new float[6 * 6];
        int p = 0;

        float x1 = -s, y1 = -s;
        float x2 =  s, y2 = -s;
        float x3 =  s, y3 =  s;
        float x4 = -s, y4 =  s;

        // +Z normal: triangles clockwise from above
        p = addTriangleXYZn(v, p, x1, y1, z,  x3, y3, z,  x2, y2, z);
        p = addTriangleXYZn(v, p, x1, y1, z,  x4, y4, z,  x3, y3, z);
        return v;
    }

    public static float[] buildChessBoard(int n, float cell, float z) {
        int triangles = n * n * 2;
        float[] v = new float[triangles * 3 * 6];
        int p = 0;

        float size = n * cell;
        float x0 = -size / 2f;
        float y0 = -size / 2f;

        for (int iy = 0; iy < n; iy++) {
            for (int ix = 0; ix < n; ix++) {
                float xa = x0 + ix * cell;
                float ya = y0 + iy * cell;
                float xb = xa + cell;
                float yb = ya + cell;

                // +Z normal
                p = addTriangleXYZn(v, p, xa, ya, z,  xb, yb, z,  xb, ya, z);
                p = addTriangleXYZn(v, p, xa, ya, z,  xa, yb, z,  xb, yb, z);
            }
        }
        return v;
    }

    public static float[] buildPyramid(float base, float height, float zBase) {
        float s = base / 2f;

        float x1 = -s, y1 = -s;
        float x2 =  s, y2 = -s;
        float x3 =  s, y3 =  s;
        float x4 = -s, y4 =  s;

        float ax = 0f, ay = 0f, az = zBase + height;

        float[] v = new float[18 * 6];
        int p = 0;

        // Base (+Z normal)
        p = addTriangleXYZn(v, p, x1, y1, zBase,  x3, y3, zBase,  x2, y2, zBase);
        p = addTriangleXYZn(v, p, x1, y1, zBase,  x4, y4, zBase,  x3, y3, zBase);

        // Sides outward
        p = addTriangleXYZn(v, p, x1, y1, zBase,  x2, y2, zBase,  ax, ay, az);
        p = addTriangleXYZn(v, p, x2, y2, zBase,  x3, y3, zBase,  ax, ay, az);
        p = addTriangleXYZn(v, p, x3, y3, zBase,  x4, y4, zBase,  ax, ay, az);
        p = addTriangleXYZn(v, p, x4, y4, zBase,  x1, y1, zBase,  ax, ay, az);

        return v;
    }

    public static float[] buildCube(float half) {
        float[] v = new float[36 * 6];
        int p = 0;

        float h = half;
        float x0 = -h, x1 = h;
        float y0 = -h, y1 = h;
        float z0 = -h, z1 = h;

        // Front (z1)
        p = addTriangleXYZn(v, p, x0, y0, z1,  x1, y1, z1,  x1, y0, z1);
        p = addTriangleXYZn(v, p, x0, y0, z1,  x0, y1, z1,  x1, y1, z1);

        // Back (z0)
        p = addTriangleXYZn(v, p, x0, y0, z0,  x1, y0, z0,  x1, y1, z0);
        p = addTriangleXYZn(v, p, x0, y0, z0,  x1, y1, z0,  x0, y1, z0);

        // Left (x0)
        p = addTriangleXYZn(v, p, x0, y0, z0,  x0, y1, z1,  x0, y0, z1);
        p = addTriangleXYZn(v, p, x0, y0, z0,  x0, y1, z0,  x0, y1, z1);

        // Right (x1)
        p = addTriangleXYZn(v, p, x1, y0, z0,  x1, y0, z1,  x1, y1, z1);
        p = addTriangleXYZn(v, p, x1, y0, z0,  x1, y1, z1,  x1, y1, z0);

        // Top (y1)
        p = addTriangleXYZn(v, p, x0, y1, z0,  x1, y1, z1,  x1, y1, z0);
        p = addTriangleXYZn(v, p, x0, y1, z0,  x0, y1, z1,  x1, y1, z1);

        // Bottom (y0)
        p = addTriangleXYZn(v, p, x0, y0, z0,  x1, y0, z0,  x1, y0, z1);
        p = addTriangleXYZn(v, p, x0, y0, z0,  x1, y0, z1,  x0, y0, z1);

        return v;
    }
}
