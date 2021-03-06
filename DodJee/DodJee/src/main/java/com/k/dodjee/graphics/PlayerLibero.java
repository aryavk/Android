package com.k.dodjee.graphics;

import android.graphics.Bitmap;
import android.view.View;

public class PlayerLibero extends Person implements GraphicInterface
{
    private boolean movingVertically;

    private int Width;
    private int Height;

    public PlayerLibero(Bitmap bitmap)
    {
        this.Width = bitmap.getWidth();
        this.Height = bitmap.getHeight();

        setBitmap(bitmap);
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
