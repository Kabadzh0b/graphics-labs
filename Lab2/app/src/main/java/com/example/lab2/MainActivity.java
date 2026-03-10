package com.example.lab2;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.Toolbar;
import android.graphics.Color;
import android.opengl.GLSurfaceView;
import android.widget.LinearLayout;
import android.widget.Toolbar;

public class MainActivity extends Activity {

    private MyGLSurfaceView glView;
    private MyGLRenderer renderer;

    // Lab2 variants depend on parity of your record book number:
    // even -> animate heptagon (set ANIMATE_STRIP=false)
    // odd  -> animate strip   (set ANIMATE_STRIP=true)
    private static final boolean ANIMATE_STRIP = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        renderer = new MyGLRenderer();
        glView = new MyGLSurfaceView(this, renderer);

        Toolbar toolbar = new Toolbar(this);

        // зробимо бар видимим і читабельним
        toolbar.setTitle("Color Wheel");
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setBackgroundColor(Color.DKGRAY);

        // ДОДАЄМО МЕНЮ ПРЯМО В TOOLBAR
        final int ID_STATIC = 1;
        final int ID_ANIM = 2;

        toolbar.getMenu().add(0, ID_STATIC, 0, "Color Weel");
        toolbar.getMenu().add(0, ID_ANIM, 1, "Color Weel animation");

        // Обробка кліків по меню
        toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == ID_STATIC) {
                toolbar.setTitle("Color Weel");
                renderer.setMode(new ModeColorWheel());
                glView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
                glView.requestRender();
                return true;
            }

            if (item.getItemId() == ID_ANIM) {
                toolbar.setTitle("Color Weel animation");
                renderer.setMode(new ModeColorWheelAnimation(ANIMATE_STRIP));
                glView.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
                return true;
            }

            return false;
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

        // стартовий режим
        renderer.setMode(new ModeColorWheel());
        glView.requestRender();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Names are required by the lab
        menu.add(0, 1, 0, "Color Weel");
        menu.add(0, 2, 0, "Color Weel animation");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        setTitle(item.getTitle());
        switch (item.getItemId()) {
            case 1:
                renderer.setMode(new ModeColorWheel());
                glView.setRenderMode(MyGLSurfaceView.RENDERMODE_WHEN_DIRTY);
                glView.requestRender();
                return true;

            case 2:
                renderer.setMode(new ModeColorWheelAnimation(ANIMATE_STRIP));
                glView.setRenderMode(MyGLSurfaceView.RENDERMODE_CONTINUOUSLY);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
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