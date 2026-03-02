package com.example.lab1_canvas;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

public class CustomView extends View {

    private static final int N = 28;

    private Paint paintFill;
    private Paint paintStroke;

    public CustomView(Context context) {
        super(context);

        paintFill = new Paint();
        paintFill.setColor(Color.YELLOW);
        paintFill.setStyle(Paint.Style.FILL);
        paintFill.setAntiAlias(true);

        paintStroke = new Paint();
        paintStroke.setColor(Color.YELLOW);
        paintStroke.setStyle(Paint.Style.STROKE);
        paintStroke.setStrokeWidth(6);
        paintStroke.setAntiAlias(true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int w = getWidth();
        int h = getHeight();

        // 1) Background (light blue)
        canvas.drawColor(Color.rgb(180, 220, 255));

        // 2) Draw sun
        float cx = w * 0.5f;
        float cy = h * 0.35f;

        float polygonRadius = Math.min(w, h) * 0.10f;
        float raysInner = polygonRadius * 1.05f;
        float raysOuter = polygonRadius * 1.70f;

        drawSun(canvas, cx, cy, polygonRadius, raysInner, raysOuter);

        // 3) Draw triangle
        drawTriangle(canvas, w, h);
    }

    private void drawSun(Canvas canvas, float cx, float cy,
                         float polygonRadius,
                         float raysInnerRadius,
                         float raysOuterRadius) {

        // Rays
        for (int i = 0; i < N; i++) {

            double angle = (2.0 * Math.PI * i) / N - Math.PI / 2.0;

            float x1 = (float) (cx + raysInnerRadius * Math.cos(angle));
            float y1 = (float) (cy + raysInnerRadius * Math.sin(angle));

            float x2 = (float) (cx + raysOuterRadius * Math.cos(angle));
            float y2 = (float) (cy + raysOuterRadius * Math.sin(angle));

            canvas.drawLine(x1, y1, x2, y2, paintStroke);
        }

        // Polygon (N-gon)
        android.graphics.Path path = new android.graphics.Path();

        for (int i = 0; i < N; i++) {

            double angle = (2.0 * Math.PI * i) / N - Math.PI / 2.0;

            float x = (float) (cx + polygonRadius * Math.cos(angle));
            float y = (float) (cy + polygonRadius * Math.sin(angle));

            if (i == 0) {
                path.moveTo(x, y);
            } else {
                path.lineTo(x, y);
            }
        }

        path.close();
        canvas.drawPath(path, paintFill);
    }

    private void drawTriangle(Canvas canvas, int w, int h) {

        float baseWidth = w * 0.40f;
        float triHeight = h * 0.25f;

        float cx = w * 0.5f;
        float baseY = h * 0.88f;

        float x1 = cx - baseWidth / 2f;
        float y1 = baseY;

        float x2 = cx + baseWidth / 2f;
        float y2 = baseY;

        float x3 = cx;
        float y3 = baseY - triHeight;

        android.graphics.Path triangle = new android.graphics.Path();
        triangle.moveTo(x1, y1);
        triangle.lineTo(x2, y2);
        triangle.lineTo(x3, y3);
        triangle.close();

        canvas.drawPath(triangle, paintFill);
    }
}