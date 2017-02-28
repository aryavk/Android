package com.k.dodjee.graphics;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.view.View;

import java.util.List;

public class PowerUp implements GraphicInterface, Spinnable
{
    private Bitmap bitmap; // the actual bitmap

    private List<Bitmap> imageIteration;
    private int arrayIndex = 0;

    private int x;   // the X coordinate
    private int y;   // the Y coordinate
    private boolean movingVertically;
    private boolean movingHorizontally;
    private int speed;

    private int Width;
    private int Height;

    private int gameHeight;

    private Power power;

    public PowerUp(Bitmap bitmap, int x, int y, int speed, List<Bitmap> imageIteration, Power power)
    {
        this.bitmap = bitmap;
        this.x = x;
        this.y = y;
        this.speed = speed;
        this.Width = bitmap.getWidth();
        this.Height = bitmap.getHeight();
        this.imageIteration = imageIteration;

        this.power = power;
        this.setMovingVertically(true);
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public int getX() {
        return x;
    }
    public int getY() {
        return y;
    }
    public void setY(int y) {
        this.y = y;
    }

    public boolean isMovingVertically()
    {
        return movingVertically;
    }

    public void setMovingVertically(boolean movingVertically)
    {
        this.movingVertically = movingVertically;
    }

    @SuppressWarnings("unused") // TODO maybe implement "curve" balls
    public boolean isMovingHorizontally()
    {
        return movingHorizontally;
    }

    @SuppressWarnings("unused") // TODO maybe implement "curve" balls
    public void setMovingHorizontally(boolean moving)
    {
        this.movingHorizontally = moving;
    }

    public int getSpeed()
    {
        return speed;
    }

    private int getRelativeSpeed()
    {
        return (getGameHeight() / 1000) * getSpeed();
    }

    public int getWidth()
    {
        return Width;
    }

    public int getHeight()
    {
        return Height;
    }

    public int getGameHeight()
    {
        return gameHeight;
    }

    public void setGameHeight(int gameHeight)
    {
        this.gameHeight = gameHeight;
    }

    public void draw(Canvas canvas)
    {
        canvas.drawBitmap(bitmap, x - (bitmap.getWidth() / 2), y - (bitmap.getHeight() / 2), null);
    }

    @Override
    public boolean isVisible()
    {
        return true;
    }

    @Override
    public void setVisible(boolean visible)
    {
        // do nothing, powerup should always be visible
    }

    public void moveVertically(View view)
    {
        if (isMovingVertically() && getY() < getGameHeight())
            setY(getY() + getRelativeSpeed());
        else
        {
            setY(getHeight() / 2);
            setMovingVertically(false);
        }
    }

    public void moveHorizontally(View view)
    {
        // do nothing, ball shouldn't move horizontally
    }

    public void spinGraphic()
    {
        if (arrayIndex < imageIteration.size() - 1)
        {
            arrayIndex++;
        }
        else
        {
            arrayIndex = 0;
        }

        bitmap = imageIteration.get(arrayIndex);
    }

    public Power getPower()
    {
        return power;
    }
}
