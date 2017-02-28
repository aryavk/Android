package com.k.dodjee.graphics;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.view.View;

import java.util.List;

public class Ball implements GraphicInterface, Collidable, Spinnable
{
    private Bitmap bitmap; // the actual bitmap

    private List<Bitmap> imageIteration;
    private int arrayIndex = 0;

    private int x;   // the X coordinate
    private int y;   // the Y coordinate
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

    private int gameHeight;

    int numberOfCollisions = 0;
    int maxCollissions = 1;

    public Ball(Bitmap bitmap, int x, int y, int speed, Direction direction, Person player, List<Bitmap> imageIteration)
    {
        this.bitmap = bitmap;
        this.x = x;
        this.y = y;
        this.speed = speed;
        this.Width = bitmap.getWidth();
        this.Height = bitmap.getHeight();
        this.direction = direction;
        this.owningPlayer = player;
        this.imageIteration = imageIteration;
    }

    public void reinitialise()
    {
        setX(owningPlayer.getX());
        setY(owningPlayer.getY());
        numberOfCollisions = 0;
    }

    public Bitmap getBitmap() {
        return bitmap;
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
    @SuppressWarnings("unused") // TODO maybe implement "curve" balls
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

    private int getRelativeSpeed()
    {
        return (getGameHeight() / 1000) * getSpeed();
    }

    public void setSpeed(int speed)
    {
        this.speed = speed;
    }

    @SuppressWarnings("unused") // TODO maybe implement "curve" balls
    public void setMovingHorizontally(boolean moving)
    {
        this.movingHorizontally = moving;
    }

    @SuppressWarnings("unused") // TODO maybe implement "curve" balls
    public float getMoveToX()
    {
        return moveToX;
    }

    @SuppressWarnings("unused") // TODO maybe implement "curve" balls
    public void setMoveToX(float moveToX)
    {
        this.moveToX = moveToX;
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
    public boolean isCollided()
    {
        return collided;
    }

    @Override
    public void setCollided(boolean collided)
    {
        if (collided)
        {
            if ((numberOfCollisions + 1) < getMaxCollissions())
            {
                this.collided = false;
                numberOfCollisions++;
            }
            else
            {
                this.collided = true;
                numberOfCollisions = 0;
            }
        }
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
                setY(getY() - getRelativeSpeed());
            else
            {
                setY(getGameHeight() - getHeight() / 2);
                setMovingVertically(false);
            }
        }
        else
        {
            if (isMovingVertically() && getY() < getGameHeight())
                setY(getY() + getRelativeSpeed());
            else
            {
                setY(getHeight() / 2);
                setMovingVertically(false);
            }
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

    public boolean isInPath(GraphicInterface graphic)
    {
        return (isMovingVertically() &&
                ((getX() >= graphic.getX() && getX() <= (graphic.getX() + graphic.getWidth())) ||
                (getX() <= graphic.getX() && (getX() + getWidth()) >= graphic.getX())));
    }

    public int getMaxCollissions()
    {
        return maxCollissions;
    }

    public void setMaxCollissions(int maxCollissions)
    {
        this.maxCollissions = maxCollissions;
    }
}
