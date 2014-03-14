package com.k.blockout.graphics;

import android.graphics.Bitmap;
import android.view.View;

public class Player extends Person implements GraphicInterface
{
    private boolean movingVertically;

    private int Width;
    private int Height;

    public Player(Bitmap bitmap, int x, int y, int speed)
    {
        this.Width = bitmap.getWidth();
        this.Height = bitmap.getHeight();

        setBitmap(bitmap);
        setX(x);
        setY(y);
        setSpeed(speed);
    }

    public boolean isVisible()
    {
        return true;
    }

    @Override
    public void setVisible(boolean visible)
    {
        // do nothing, if player goes invisible its game over
    }

    public int getWidth()
    {
        return Width;
    }

    public int getHeight()
    {
        return Height;
    }

    @SuppressWarnings("unused") // TODO maybe implement "attacking" players
    public boolean isMovingVertically()
    {
        return movingVertically;
    }

    @SuppressWarnings("unused") // TODO maybe implement "attacking" players
    public void setMovingVertically(boolean movingVertically)
    {
        this.movingVertically = movingVertically;
    }

    @Override
    public void moveVertically(View view)
    {
        // do nothing, player shouldnt move vertically
    }
}
