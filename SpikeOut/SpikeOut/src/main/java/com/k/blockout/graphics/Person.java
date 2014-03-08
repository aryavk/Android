package com.k.blockout.graphics;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.view.View;

public abstract class Person implements GraphicInterface
{
    private Bitmap bitmap;
    private int x;
    private int y;
    private float moveToX;
    private int speed;
    private boolean movingHorizontally;

    public Bitmap getBitmap()
    {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap)
    {
        this.bitmap = bitmap;
    }

    public int getX()
    {
        return x;
    }

    public void setX(int x)
    {
        this.x = x;
    }

    public int getY()
    {
        return y;
    }

    public void setY(int y)
    {
        this.y = y;
    }

    public float getMoveToX()
    {
        return moveToX;
    }

    public void setMoveToX(float moveToX)
    {
        this.moveToX = moveToX;
    }

    public void setSpeed(int speed)
    {
        this.speed = speed;
    }

    public int getSpeed()
    {
        return speed;
    }

    public boolean isMovingHorizontally()
    {
        return movingHorizontally;
    }

    public void setMovingHorizontally(boolean movingHorizontally)
    {
        this.movingHorizontally = movingHorizontally;
    }

    public void moveHorizontally(View view)
    {
        if (getMoveToX() < getX())
        {
            if (getX() - getMoveToX() < getSpeed())
            {
                setX((int) getMoveToX());
                setMovingHorizontally(false);
            }
            else if (getX() - speed > 60)
            {
                setX(getX() - speed);
            }
            else
            {
                setX(60);
                setMovingHorizontally(false);
            }
        }
        else
        {
            if (getMoveToX() - getX() < speed)
            {
                setX((int) getMoveToX());
                setMovingHorizontally(false);
            }
            else if (getX() + speed < view.getWidth() - 60)
            {
                setX(getX() + speed);
            }
            else
            {
                setX(view.getWidth() - 60);
                setMovingHorizontally(false);
            }
        }
    }

    public void draw(Canvas canvas)
    {
        canvas.drawBitmap(getBitmap(), getX() - (getBitmap().getWidth() / 2), getY() - (getBitmap().getHeight() / 2), null);
    }
}
