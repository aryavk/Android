package com.k.dodjee.graphics;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.view.View;

public class Button implements GraphicInterface, ButtonListener
{
    private int x;
    private int y;
    private int Height;
    private int Width;
    private Bitmap bitmap;

    public Button(Bitmap bitmap, int x, int y)
    {
        this.bitmap = bitmap;
        this.x = x;
        this.y = y;
        this.Width = bitmap.getWidth();
        this.Height = bitmap.getHeight();
    }

    @Override
    public int getX()
    {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    @Override
    public int getY()
    {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    @Override
    public void moveVertically(View view)
    {
        // Buttons dont move
    }

    @Override
    public void moveHorizontally(View view)
    {
        // Buttons dont move
    }

    @Override
    public int getWidth()
    {
        return Width;
    }

    @Override
    public int getHeight()
    {
        return Height;
    }

    @Override
    public boolean isVisible()
    {
        return true;
    }

    @Override
    public void setVisible(boolean visible)
    {
        // Do nothing
    }

    @Override
    public Bitmap getBitmap()
    {
        return bitmap;
    }

    @Override
    public boolean eventInBounds(float x, float y)
    {
        int left = (getX() - getWidth() / 2);
        int right = (getX() + getWidth() / 2);

        int top = (getY() - getHeight() / 2);
        int bottom = (getY() + getHeight() / 2);

        return x > left && x < right
        && y > top && y < bottom;
    }

    @Override
    public void draw(Canvas canvas)
    {
        canvas.drawBitmap(bitmap, x - (bitmap.getWidth() / 2), y - (bitmap.getHeight() / 2), null);
    }
}
