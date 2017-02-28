package com.k.dodjee.graphics;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.view.View;

public interface GraphicInterface
{
    public int getX();
    public int getY();
    public void moveVertically(View view);
    public void moveHorizontally(View view);
    public void draw(Canvas canvas);

    public int getWidth();
    public int getHeight();

    public boolean isVisible();
    public void setVisible(boolean visible);

    public Bitmap getBitmap();
}
