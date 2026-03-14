package com.example.lab5;

import android.opengl.GLES32;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class Mesh {

    public final int vaoId;
    public final int vboId;

    public final int primitive;
    public final int vertexCount;

    public final int[] groupStarts;
    public final int[] groupCounts;

    public Mesh(int primitive, float[] vertices, int strideFloats,
                int[] groupStarts, int[] groupCounts, int programId) {

        this.primitive = primitive;
        this.groupStarts = groupStarts;
        this.groupCounts = groupCounts;

        this.vertexCount = vertices.length / strideFloats;

        FloatBuffer fb = ByteBuffer.allocateDirect(vertices.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        fb.put(vertices).position(0);

        int[] tmp = new int[1];

        GLES32.glGenVertexArrays(1, tmp, 0);
        vaoId = tmp[0];

        GLES32.glGenBuffers(1, tmp, 0);
        vboId = tmp[0];

        GLES32.glBindVertexArray(vaoId);
        GLES32.glBindBuffer(GLES32.GL_ARRAY_BUFFER, vboId);
        GLES32.glBufferData(GLES32.GL_ARRAY_BUFFER, vertices.length * 4, fb, GLES32.GL_STATIC_DRAW);

        int strideBytes = strideFloats * 4;

        int hPos = GLES32.glGetAttribLocation(programId, "vPosition");
        int hNrm = GLES32.glGetAttribLocation(programId, "vNormal");
        int hTex = GLES32.glGetAttribLocation(programId, "vTexture");

        // vPosition: vec3 at offset 0
        GLES32.glEnableVertexAttribArray(hPos);
        GLES32.glVertexAttribPointer(hPos, 3, GLES32.GL_FLOAT, false, strideBytes, 0);

        // vNormal: vec3 at offset 3
        GLES32.glEnableVertexAttribArray(hNrm);
        GLES32.glVertexAttribPointer(hNrm, 3, GLES32.GL_FLOAT, false, strideBytes, 3 * 4);

        // vTexture: vec2 at offset 6
        GLES32.glEnableVertexAttribArray(hTex);
        GLES32.glVertexAttribPointer(hTex, 2, GLES32.GL_FLOAT, false, strideBytes, 6 * 4);

        GLES32.glBindBuffer(GLES32.GL_ARRAY_BUFFER, 0);
        GLES32.glBindVertexArray(0);
    }

    public void bind() {
        GLES32.glBindVertexArray(vaoId);
    }

    public void unbind() {
        GLES32.glBindVertexArray(0);
    }

    public void drawAll() {
        if (groupStarts != null && groupCounts != null) {
            for (int i = 0; i < groupStarts.length; i++) {
                GLES32.glDrawArrays(primitive, groupStarts[i], groupCounts[i]);
            }
        } else {
            GLES32.glDrawArrays(primitive, 0, vertexCount);
        }
    }

    public void drawGroup(int idx) {
        if (groupStarts == null || groupCounts == null) return;
        GLES32.glDrawArrays(primitive, groupStarts[idx], groupCounts[idx]);
    }
}
