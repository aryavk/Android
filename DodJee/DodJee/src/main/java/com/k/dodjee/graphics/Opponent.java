package com.k.dodjee.graphics;

import android.graphics.Bitmap;
import android.view.View;

public class Opponent extends Person implements GraphicInterface
{
    private boolean movingVertically;

    private int Width;
    private int Height;

    private boolean visible;

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
        // do nothing, player shouldn't move vertically
    }

    public void reinitialise()
    {
        setVisible(true);
        getBall().reinitialise();
    }
}
