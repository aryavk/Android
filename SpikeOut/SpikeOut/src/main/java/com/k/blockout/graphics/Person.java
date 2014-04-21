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
    private Volleyball volleyball;

    private int gameWidth;

    public Bitmap getBitmap()
    {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap)
    {
        this.bitmap = bitmap;
    }

    public void setGameWidth(int width)
    {
        this.gameWidth = width;
    }

    public int getGameWidth()
    {
        return gameWidth;
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

    private int getRelativeSpeed()
    {
        return (int) (((double) getGameWidth() / (double) 1080) * (double) getSpeed());
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
            if (getX() - getMoveToX() < getRelativeSpeed())
            {
                setX((int) getMoveToX());
                setMovingHorizontally(false);
            }
            else if (getX() - getRelativeSpeed() > getWidth() / 2)
            {
                setX(getX() - getRelativeSpeed());
            }
            else
            {
                setX(60);
                setMovingHorizontally(false);
            }
        }
        else
        {
            if (getMoveToX() - getX() < getRelativeSpeed())
            {
                setX((int) getMoveToX());
                setMovingHorizontally(false);
            }
            else if (getX() + getRelativeSpeed() < view.getWidth() - (getWidth() / 2))
            {
                setX(getX() + getRelativeSpeed());
            }
            else
            {
                setX(view.getWidth() - (getWidth() / 2));
                setMovingHorizontally(false);
            }
        }
    }

    public Volleyball getVolleyball()
    {
        return volleyball;
    }

    public void setVolleyball(Volleyball volleyball)
    {
        this.volleyball = volleyball;
    }

    public void draw(Canvas canvas)
    {
        canvas.drawBitmap(getBitmap(), getX() - (getBitmap().getWidth() / 2), getY() - (getBitmap().getHeight() / 2), null);
    }
}
