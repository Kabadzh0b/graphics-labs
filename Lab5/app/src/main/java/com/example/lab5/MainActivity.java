package com.example.lab5;

import android.app.Activity;
import android.graphics.Color;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.Toolbar;

public class MainActivity extends Activity implements WorkModeProvider {

    private MyGLSurfaceView glView;
    private myWorkMode wmRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MyGLRenderer renderer = new MyGLRenderer(this, this);
        glView = new MyGLSurfaceView(this, renderer, this);

        Toolbar toolbar = new Toolbar(this);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setBackgroundColor(Color.DKGRAY);

        final int ID_TORUS = 1;
        final int ID_EARTH = 2;
        final int ID_SEAVIEW = 3;

        toolbar.getMenu().add(0, ID_TORUS, 0, "Torus");
        toolbar.getMenu().add(0, ID_EARTH, 1, "Earth");
        toolbar.getMenu().add(0, ID_SEAVIEW, 2, "Seaview");

        toolbar.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case ID_TORUS:
                    toolbar.setTitle("Torus");
                    startMode(new ModeTorus());
                    return true;
                case ID_EARTH:
                    toolbar.setTitle("Earth");
                    startMode(new ModeEarth());
                    return true;
                case ID_SEAVIEW:
                    toolbar.setTitle("Seaview");
                    startMode(new ModeSeaview());
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

        toolbar.setTitle("Torus");
        startMode(new ModeTorus());
    }

    private void startMode(myWorkMode mode) {
        wmRef = mode;
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
