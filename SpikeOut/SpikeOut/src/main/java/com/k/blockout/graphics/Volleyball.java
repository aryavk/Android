package com.k.blockout.graphics;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class Volleyball implements GraphicInterface, Collidable
{
    private Bitmap bitmap; // the actual bitmap

    private List<Bitmap> imageIteration;
    private int arrayIndex = 0;

    private int x;   // the X coordinate
    private int y;   // the Y coordinate
    private boolean touched; // if droid is touched/picked up
    private boolean movingVertically;
    private boolean movingHorizontally;
    private int speed;
    private float moveToX;
    private Direction direction;
    private boolean collided;
    private boolean visible;

    private Person owningPlayer;

    private int Width;
    private int Height;

    public Volleyball(Bitmap bitmap, int x, int y, int speed, Direction direction, Person player)
    {
        this.bitmap = bitmap;
        this.x = x;
        this.y = y;
        this.speed = speed;
        this.Width = bitmap.getWidth();
        this.Height = bitmap.getHeight();
        this.direction = direction;
        this.owningPlayer = player;

        initialiseBitmaps();
    }

    public void reinitialise()
    {
        setX(owningPlayer.getX());
        setY(owningPlayer.getY());
    }

    private void initialiseBitmaps()
    {
        if (imageIteration == null)
        {
            Bitmap bmp0 = bitmap;
            Bitmap bmp45 = getRotatedBitmap(45);
            Bitmap bmp90 = getRotatedBitmap(90);
            Bitmap bmp135 = getRotatedBitmap(135);
            Bitmap bmp180 = getRotatedBitmap(180);
            Bitmap bmp225 = getRotatedBitmap(225);
            Bitmap bmp270 = getRotatedBitmap(270);
            Bitmap bmp315 = getRotatedBitmap(315);

            imageIteration = new ArrayList<Bitmap>();
            imageIteration.add(bmp0);
            imageIteration.add(bmp45);
            imageIteration.add(bmp90);
            imageIteration.add(bmp135);
            imageIteration.add(bmp180);
            imageIteration.add(bmp225);
            imageIteration.add(bmp270);
            imageIteration.add(bmp315);
        }
    }

    private Bitmap getRotatedBitmap(float rotate)
    {
        Matrix matrix = new Matrix();

        matrix.postRotate(rotate);

        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    public Bitmap getBitmap() {
        return bitmap;
    }
    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }
    public int getX() {
        return x;
    }
    public void setX(int x) {
        this.x = x;
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
    public boolean isMovingHorizontally()
    {
        return movingHorizontally;
    }

    public void setMovingVertically(boolean moving)
    {
        // If we are transitioning from not moving to moving, make the ball visible
        if (!isMovingVertically() && moving)
            setVisible(true);
        // If we are transitioning from moving to not moving, make the ball invisible
        else if (isMovingVertically() && !moving)
            setVisible(false);

        this.movingVertically = moving;
    }

    public int getSpeed()
    {
        return speed;
    }

    public void setSpeed(int speed)
    {
        this.speed = speed;
    }

    public void setMovingHorizontally(boolean moving)
    {
        this.movingHorizontally = moving;
    }

    public float getMoveToX()
    {
        return moveToX;
    }

    public void setMoveToX(float moveToX)
    {
        this.moveToX = moveToX;
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

    public void draw(Canvas canvas)
    {
        canvas.drawBitmap(bitmap, x - (bitmap.getWidth() / 2), y - (bitmap.getHeight() / 2), null);
    }

    @Override
    public boolean isCollided()
    {
        return collided;
    }

    @Override
    public void setCollided(boolean collided)
    {
        this.collided = collided;
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

    public void moveVertically(View view)
    {
        if (direction.equals(Direction.UP))
        {
            if (isMovingVertically() && getY() > 0)
                setY(getY() - speed);
            else
            {
                setY(view.getHeight() - 60);
                setMovingVertically(false);
            }
        }
        else
        {
            if (isMovingVertically() && getY() < view.getHeight())
                setY(getY() + speed);
            else
            {
                setY(60);
                setMovingVertically(false);
            }
        }
    }

    public void moveHorizontally(View view)
    {
        // do ntohing, ball shouldnt move horizontally
    }

    public void spinBall()
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
}
