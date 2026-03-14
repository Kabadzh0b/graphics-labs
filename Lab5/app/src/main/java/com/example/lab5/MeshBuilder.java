package com.example.lab5;

import java.util.ArrayList;

public class MeshBuilder {

    // Common vertex format: position(3) + normal(3) + texcoord(2) => 8 floats
    public static final int STRIDE = 8;

    public static float[] buildBoard(float size, float z, float repeat) {
        float s = size / 2f;

        float x0 = -s, y0 = -s;
        float x1 =  s, y1 =  s;

        float u0 = 0.01f;
        float v0 = 0.01f;
        float u1 = repeat - 0.01f;
        float v1 = repeat - 0.01f;

        // Two triangles
        return new float[] {
                //  x,  y,  z,   nx,ny,nz,   u,  v
                x0, y0, z,      0, 0, 1,    u0, v1,
                x0, y1, z,      0, 0, 1,    u0, v0,
                x1, y1, z,      0, 0, 1,    u1, v0,

                x0, y0, z,      0, 0, 1,    u0, v1,
                x1, y1, z,      0, 0, 1,    u1, v0,
                x1, y0, z,      0, 0, 1,    u1, v1
        };
    }

    public static StripData buildTorus(float majorR, float minorR, int segLat, int segLong, float zCenter) {
        ArrayList<Float> out = new ArrayList<>();
        int[] starts = new int[segLat];
        int[] counts = new int[segLat];

        int cursor = 0;
        for (int bb = 0; bb < segLat; bb++) {
            starts[bb] = cursor;

            float B0 = (float) (2.0 * Math.PI * bb / segLat);
            float B1 = (float) (2.0 * Math.PI * (bb + 1) / segLat);

            for (int ll = 0; ll <= segLong; ll++) {
                float L = (float) (2.0 * Math.PI * ll / segLong);

                addTorusVertex(out, majorR, minorR, B0, L, zCenter, (float) ll / segLong, (float) bb / segLat);
                addTorusVertex(out, majorR, minorR, B1, L, zCenter, (float) ll / segLong, (float) (bb + 1) / segLat);

                cursor += 2;
            }
            counts[bb] = (segLong + 1) * 2;
        }

        float[] v = toArray(out);
        return new StripData(v, starts, counts);
    }

    private static void addTorusVertex(ArrayList<Float> out, float majorR, float minorR,
                                       float B, float L, float zCenter, float xt, float yt) {
        float x = (float) (majorR * Math.cos(L));
        float y = (float) (majorR * Math.sin(L));

        float xn = (float) (Math.cos(B) * Math.cos(L));
        float yn = (float) (Math.cos(B) * Math.sin(L));
        float zn = (float) (Math.sin(B));

        float px = x + xn * minorR;
        float py = y + yn * minorR;
        float pz = zCenter + zn * minorR;

        out.add(px); out.add(py); out.add(pz);
        out.add(xn); out.add(yn); out.add(zn);
        out.add(xt); out.add(yt);
    }

    public static StripData buildSphere(float radius, int segLat, int segLong, float zCenter) {
        ArrayList<Float> out = new ArrayList<>();
        int[] starts = new int[segLat];
        int[] counts = new int[segLat];

        float startB = (float) (-0.5 * Math.PI);

        int cursor = 0;
        for (int bb = 0; bb < segLat; bb++) {
            starts[bb] = cursor;

            float B0 = startB + (float) (Math.PI * bb / segLat);
            float B1 = startB + (float) (Math.PI * (bb + 1) / segLat);

            for (int ll = 0; ll <= segLong; ll++) {
                float L = (float) (2.0 * Math.PI * ll / segLong);

                addSphereVertex(out, radius, B0, L, zCenter);
                addSphereVertex(out, radius, B1, L, zCenter);

                cursor += 2;
            }
            counts[bb] = (segLong + 1) * 2;
        }

        float[] v = toArray(out);
        return new StripData(v, starts, counts);
    }

    private static void addSphereVertex(ArrayList<Float> out, float radius, float B, float L, float zCenter) {
        float x = (float) (Math.cos(B) * Math.cos(L));
        float y = (float) (Math.cos(B) * Math.sin(L));
        float z = (float) (Math.sin(B));

        float xt = (float) (L / (2.0 * Math.PI));
        float yt = 0.5f + (float) (B / Math.PI);
        yt = 1.0f - yt; // invert Y for most raster formats

        out.add(radius * x);
        out.add(radius * y);
        out.add(zCenter + radius * z);

        out.add(x); out.add(y); out.add(z);

        out.add(xt); out.add(yt);
    }

    // Skybox cube from INSIDE. 6 faces, each face is 2 triangles (6 vertices).
    // We keep face groups to bind 6 different textures.
    public static GroupData buildSkybox(float size) {
        float s = size / 2f;

        ArrayList<Float> out = new ArrayList<>();
        int[] starts = new int[6];
        int[] counts = new int[6];

        // Helper for adding one face: two triangles from 4 corners (a,b,c,d)
        // with texture coords (0,0)-(1,1).
        class Face {
            void add(float ax, float ay, float az,
                     float bx, float by, float bz,
                     float cx, float cy, float cz,
                     float dx, float dy, float dz) {
                // fake normal (not used for skybox)
                float nx = 0, ny = 0, nz = 1;

                // tri 1: a,b,c
                addV(ax, ay, az, nx, ny, nz, 0f, 1f);
                addV(bx, by, bz, nx, ny, nz, 0f, 0f);
                addV(cx, cy, cz, nx, ny, nz, 1f, 0f);

                // tri 2: a,c,d
                addV(ax, ay, az, nx, ny, nz, 0f, 1f);
                addV(cx, cy, cz, nx, ny, nz, 1f, 0f);
                addV(dx, dy, dz, nx, ny, nz, 1f, 1f);
            }

            void addV(float x, float y, float z, float nx, float ny, float nz, float u, float v) {
                out.add(x); out.add(y); out.add(z);
                out.add(nx); out.add(ny); out.add(nz);
                out.add(u); out.add(v);
            }
        }

        Face face = new Face();

        int cursor = 0;

        // +X (right)
        starts[0] = cursor; counts[0] = 6;
        face.add( s, -s, -s,   s, -s,  s,   s,  s,  s,   s,  s, -s);
        cursor += 6;

        // -X (left)
        starts[1] = cursor; counts[1] = 6;
        face.add(-s, -s,  s,  -s, -s, -s,  -s,  s, -s,  -s,  s,  s);
        cursor += 6;

        // +Y (front)
        starts[2] = cursor; counts[2] = 6;
        face.add(-s,  s, -s,  -s,  s,  s,   s,  s,  s,   s,  s, -s);
        cursor += 6;

        // -Y (back)
        starts[3] = cursor; counts[3] = 6;
        face.add( s, -s, -s,   s, -s,  s,  -s, -s,  s,  -s, -s, -s);
        cursor += 6;

        // +Z (top)
        starts[4] = cursor; counts[4] = 6;
        face.add(-s, -s,  s,  -s,  s,  s,   s,  s,  s,   s, -s,  s);
        cursor += 6;

        // -Z (bottom)
        starts[5] = cursor; counts[5] = 6;
        face.add(-s,  s, -s,  -s, -s, -s,   s, -s, -s,   s,  s, -s);
        cursor += 6;

        float[] v = toArray(out);
        return new GroupData(v, starts, counts);
    }

    private static float[] toArray(ArrayList<Float> out) {
        float[] v = new float[out.size()];
        for (int i = 0; i < out.size(); i++) v[i] = out.get(i);
        return v;
    }

    public static class StripData {
        public final float[] vertices;
        public final int[] starts;
        public final int[] counts;

        public StripData(float[] vertices, int[] starts, int[] counts) {
            this.vertices = vertices;
            this.starts = starts;
            this.counts = counts;
        }
    }

    public static class GroupData {
        public final float[] vertices;
        public final int[] starts;
        public final int[] counts;

        public GroupData(float[] vertices, int[] starts, int[] counts) {
            this.vertices = vertices;
            this.starts = starts;
            this.counts = counts;
        }
    }
}
