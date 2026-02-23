package com.lab.lab1;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class MainApp extends Application {

    // Variant 7 (from the lab table): light-blue background, yellow objects, N = 28
    private static final int N = 28;

    // Colors picked as a "light blue" and "yellow"
    private static final Color BACKGROUND = Color.rgb(180, 220, 255); // light blue
    private static final Color OBJECTS = Color.rgb(255, 255, 0);      // yellow

    @Override
    public void start(Stage stage) {
        // Canvas is the simplest "draw directly" surface (closest to GDI/Canvas idea)
        Canvas canvas = new Canvas(800, 600);
        StackPane root = new StackPane(canvas);

        // Window
        Scene scene = new Scene(root, 800, 600);
        stage.setTitle("Lab1 (Desktop JavaFX) - Variant 7");
        stage.setScene(scene);
        stage.show();

        // Make canvas resize with window
        canvas.widthProperty().bind(root.widthProperty());
        canvas.heightProperty().bind(root.heightProperty());

        // Redraw when size changes
        canvas.widthProperty().addListener((obs, oldV, newV) -> draw(canvas));
        canvas.heightProperty().addListener((obs, oldV, newV) -> draw(canvas));

        // First draw
        draw(canvas);
    }

    private void draw(Canvas canvas) {
        double w = canvas.getWidth();
        double h = canvas.getHeight();
        GraphicsContext g = canvas.getGraphicsContext2D();

        // 1) Background fill
        g.setFill(BACKGROUND);
        g.fillRect(0, 0, w, h);

        // 2) Sun: N rays + N-gon in the center
        double cx = w * 0.5;
        double cy = h * 0.35;

        double polygonRadius = Math.min(w, h) * 0.10;
        double raysInnerRadius = polygonRadius * 1.05;
        double raysOuterRadius = polygonRadius * 1.70;

        drawSun(g, cx, cy, N, polygonRadius, raysInnerRadius, raysOuterRadius);

        // 3) Yellow triangle
        drawTriangle(g, w, h);
    }

    private void drawSun(GraphicsContext g,
                         double cx, double cy,
                         int n,
                         double polygonRadius,
                         double raysInnerRadius,
                         double raysOuterRadius) {

        // --- Rays (N lines from inner radius to outer radius) ---
        g.setStroke(OBJECTS);
        g.setLineWidth(3);

        for (int i = 0; i < n; i++) {
            // Angle in radians. "-PI/2" rotates so the first ray points up
            double angle = (2.0 * Math.PI * i) / n - Math.PI / 2.0;

            // Start point of ray
            double x1 = cx + raysInnerRadius * Math.cos(angle);
            double y1 = cy + raysInnerRadius * Math.sin(angle);

            // End point of ray
            double x2 = cx + raysOuterRadius * Math.cos(angle);
            double y2 = cy + raysOuterRadius * Math.sin(angle);

            g.strokeLine(x1, y1, x2, y2);
        }

        // --- N-gon (filled polygon with N vertices) ---
        double[] x = new double[n];
        double[] y = new double[n];

        for (int i = 0; i < n; i++) {
            double angle = (2.0 * Math.PI * i) / n - Math.PI / 2.0;
            x[i] = cx + polygonRadius * Math.cos(angle);
            y[i] = cy + polygonRadius * Math.sin(angle);
        }

        g.setFill(OBJECTS);
        g.fillPolygon(x, y, n);
    }

    private void drawTriangle(GraphicsContext g, double w, double h) {
        // Place triangle in the lower part of the window
        double baseWidth = w * 0.40;
        double triHeight = h * 0.25;

        double cx = w * 0.5;
        double baseY = h * 0.88;

        double x1 = cx - baseWidth / 2.0;
        double y1 = baseY;

        double x2 = cx + baseWidth / 2.0;
        double y2 = baseY;

        double x3 = cx;
        double y3 = baseY - triHeight;

        g.setFill(OBJECTS);
        g.fillPolygon(new double[]{x1, x2, x3}, new double[]{y1, y2, y3}, 3);
    }

    public static void main(String[] args) {
        launch(args);
    }
}