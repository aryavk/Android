package com.k.blockout.graphics;

import android.graphics.Color;
import android.graphics.Rect;

public class CollisionDetector
{
    public static boolean collisionDetected(GraphicInterface image1, GraphicInterface image2)
    {
        int image1x = image1.getX() - image1.getWidth() / 2;
        int image1y = image1.getY();
        int image2x = image2.getX() - image2.getWidth() / 2;
        int image2y = image2.getY();
        Rect r1 = new Rect(image1x, image1y, image1x + image1.getWidth(), image1y + image1.getHeight());
        Rect r2 = new Rect(image2x, image2y, image2x + image2.getWidth(), image2y + image2.getHeight());
        Rect r3 = new Rect(r1);
        if(r1.intersect(r2) && image1.isVisible() && image2.isVisible())
        {
            for (int i = r1.left; i < r1.right; i++)
            {
                for (int j = r1.top; j < r1.bottom; j++)
                {
                    if (image1.getBitmap().getPixel(i - r3.left, j - r3.top) != Color.TRANSPARENT)
                    {
                        if (image2.getBitmap().getPixel(i - r2.left, j - r2.top) != Color.TRANSPARENT)
                        {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }
}
