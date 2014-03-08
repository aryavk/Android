package com.k.blockout.graphics;

import android.graphics.Bitmap;
import android.view.View;

public class Opponent extends Person implements GraphicInterface
{
    private boolean movingVertically;

    private int Width;
    private int Height;

    private boolean visible;

    private Volleyball volleyball;

    public Opponent(Bitmap bitmap, int x, int y, int speed)
    {
        this.Width = bitmap.getWidth();
        this.Height = bitmap.getHeight();
        this.visible = true;

        setBitmap(bitmap);
        setX(x);
        setY(y);
        setSpeed(speed);
    }

    @Override
    public boolean isVisible()
    {
        return visible;
    }

    @Override
    public void setVisible(boolean visible)
    {
        this.visible = visible;
    }

    public int getWidth()
    {
        return Width;
    }

    public void setWidth(int width)
    {
        Width = width;
    }

    public int getHeight()
    {
        return Height;
    }

    public void setHeight(int height)
    {
        Height = height;
    }

    public boolean isMovingVertically()
    {
        return movingVertically;
    }

    public void setMovingVertically(boolean movingVertically)
    {
        this.movingVertically = movingVertically;
    }

    public Volleyball getVolleyball()
    {
        return volleyball;
    }

    public void setVolleyball(Volleyball volleyball)
    {
        this.volleyball = volleyball;
    }

    @Override
    public void moveVertically(View view)
    {
        // do nothing, player shouldn't move vertically
    }

    public void reinitialise()
    {
        setVisible(true);
        getVolleyball().reinitialise();
    }
}
