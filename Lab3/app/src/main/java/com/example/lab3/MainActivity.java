package com.example.lab3;

import android.app.Activity;
import android.graphics.Color;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.Toolbar;

public class MainActivity extends Activity implements WorkModeProvider {

    private MyGLSurfaceView glView;

    private myWorkMode wmRef = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 1) Renderer + SurfaceView
        MyGLRenderer renderer = new MyGLRenderer(this);
        glView = new MyGLSurfaceView(this, renderer, this);

        // 2) Toolbar (no XML)
        Toolbar toolbar = new Toolbar(this);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setBackgroundColor(Color.DKGRAY);

        final int ID_PYRAMID = 1;
        final int ID_CUBES = 2;

        toolbar.getMenu().add(0, ID_PYRAMID, 0, "Pyramid rotation");
        toolbar.getMenu().add(0, ID_CUBES, 1, "Nine Cubes");

        toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == ID_PYRAMID) {
                toolbar.setTitle("Pyramid rotation");
                wmRef = new ModePyramidRotation();
                glView.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY); // requirement
                return true;
            }

            if (item.getItemId() == ID_CUBES) {
                toolbar.setTitle("Nine Cubes");
                wmRef = new ModeNineCubes();
                glView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY); // requirement
                glView.requestRender();
                return true;
            }

            return false;
        });

        // 3) Layout: toolbar + glView
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);

        root.addView(toolbar, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));

        root.addView(glView, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        ));

        setContentView(root);

        // Default mode
        toolbar.setTitle("Pyramid rotation");
        wmRef = new ModePyramidRotation();
        glView.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
    }

    @Override
    public myWorkMode getWorkMode() {
        return wmRef;
    }

    @Override
    protected void onPause() {
        super.onPause();
        glView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        glView.onResume();
    }
}