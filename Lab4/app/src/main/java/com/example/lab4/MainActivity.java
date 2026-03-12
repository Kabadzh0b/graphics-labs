package com.example.lab4;

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

        MyGLRenderer renderer = new MyGLRenderer(this);
        glView = new MyGLSurfaceView(this, renderer, this);

        Toolbar toolbar = new Toolbar(this);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setBackgroundColor(Color.DKGRAY);

        final int ID_DIFFUSE = 1;
        final int ID_SPECULAR = 2;
        final int ID_PYRAMID = 3;
        final int ID_CUBES = 4;

        toolbar.getMenu().add(0, ID_DIFFUSE, 0, "Diffuse lighting");
        toolbar.getMenu().add(0, ID_SPECULAR, 1, "Specular lighting");
        toolbar.getMenu().add(0, ID_PYRAMID, 2, "Pyramid");
        toolbar.getMenu().add(0, ID_CUBES, 3, "Nine Cubes");

        toolbar.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case ID_DIFFUSE:
                    toolbar.setTitle("Diffuse lighting");
                    wmRef = new ModeDiffuseLighting();
                    glView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
                    glView.requestRender();
                    return true;
                case ID_SPECULAR:
                    toolbar.setTitle("Specular lighting");
                    wmRef = new ModeSpecularLighting();
                    glView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
                    glView.requestRender();
                    return true;
                case ID_PYRAMID:
                    toolbar.setTitle("Pyramid");
                    wmRef = new ModePyramidLighting();
                    glView.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
                    return true;
                case ID_CUBES:
                    toolbar.setTitle("Nine Cubes");
                    wmRef = new ModeNineCubesLighting();
                    glView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
                    glView.requestRender();
                    return true;
                default:
                    return false;
            }
        });

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

        toolbar.setTitle("Diffuse lighting");
        wmRef = new ModeDiffuseLighting();
        glView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
        glView.requestRender();
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
