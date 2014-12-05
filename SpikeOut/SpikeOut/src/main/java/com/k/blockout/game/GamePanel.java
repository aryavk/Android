package com.k.blockout.game;

import android.graphics.Canvas;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.k.blockout.util.GamePanelResources;
import com.k.blockout.graphics.GameResources;

public interface GamePanel extends SurfaceHolder.Callback
{
    @Override
    public void surfaceCreated(SurfaceHolder holder);

    public SurfaceView getGamePanelView();

    @SuppressWarnings("unused")
    public boolean onTouchEvent(MotionEvent event);

    public void update(Canvas canvas);

    public void setupResources(int score, int level, GameResources resources);

    public void initialise();

    public GamePanelResources getGamePanelResources();
}
