package com.k.blockout.graphics;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.AudioManager;
import android.media.SoundPool;

import com.k.blockout.R;

import java.util.ArrayList;
import java.util.List;

public class GameResources
{
    private Bitmap volleyballBitmap;
    private List<Bitmap> imageIteration;

    private Bitmap opponent1Bitmap;
    private Bitmap opponent2Bitmap;
    private Bitmap opponent3Bitmap;
    private Bitmap opponent4Bitmap;
    private Bitmap avatar1Bitmap;
    private Bitmap avatar2Bitmap;
    private Bitmap leftButtonBitmap;
    private Bitmap rightButtonBitmap;
    private Bitmap shootButtonBitmap;
    private Bitmap courtBitmap;

    private Bitmap sixPack;
    private Bitmap medal;

    private Bitmap playerAvatar;

    private SoundPool soundPool;
    private int[] sounds;

    public GameResources(Context context, Resources resources)
    {
        volleyballBitmap = BitmapFactory.decodeResource(resources, R.drawable.ball);
        initialiseVolleyballBitmaps();
        opponent1Bitmap = BitmapFactory.decodeResource(resources, R.drawable.opponent1);
        opponent2Bitmap = BitmapFactory.decodeResource(resources, R.drawable.opponent2);
        opponent3Bitmap = BitmapFactory.decodeResource(resources, R.drawable.opponent3);
        opponent4Bitmap = BitmapFactory.decodeResource(resources, R.drawable.opponent4);

        avatar1Bitmap = BitmapFactory.decodeResource(resources, R.drawable.avatar1);
        avatar2Bitmap = BitmapFactory.decodeResource(resources, R.drawable.avatar2);

        leftButtonBitmap = BitmapFactory.decodeResource(resources, R.drawable.back2);
        rightButtonBitmap = BitmapFactory.decodeResource(resources, R.drawable.forward2);
        shootButtonBitmap = BitmapFactory.decodeResource(resources, R.drawable.spike2);

        courtBitmap = BitmapFactory.decodeResource(resources, R.drawable.court2);

        sixPack = BitmapFactory.decodeResource(resources, R.drawable.six_pack);
        medal = BitmapFactory.decodeResource(resources, R.drawable.medal);

        soundPool = new SoundPool(2, AudioManager.STREAM_MUSIC, 0);
        sounds = new int[2];
        sounds[0] = soundPool.load(context, R.raw.hit, 1);
        sounds[1] = soundPool.load(context, R.raw.spike, 1);
    }

    private void initialiseVolleyballBitmaps()
    {
        if (imageIteration == null)
        {
            Bitmap bmp0 = getVolleyballBitmap();
            Bitmap bmp45 = getRotatedBitmap(getVolleyballBitmap(), 45);
            Bitmap bmp90 = getRotatedBitmap(getVolleyballBitmap(), 90);
            Bitmap bmp135 = getRotatedBitmap(getVolleyballBitmap(), 135);
            Bitmap bmp180 = getRotatedBitmap(getVolleyballBitmap(), 180);
            Bitmap bmp225 = getRotatedBitmap(getVolleyballBitmap(), 225);
            Bitmap bmp270 = getRotatedBitmap(getVolleyballBitmap(), 270);
            Bitmap bmp315 = getRotatedBitmap(getVolleyballBitmap(), 315);

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

    private Bitmap getRotatedBitmap(Bitmap bitmap, float rotate)
    {
        Matrix matrix = new Matrix();

        matrix.postRotate(rotate);

        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    public Bitmap getVolleyballBitmap()
    {
        return volleyballBitmap;
    }

    public List<Bitmap> getVolleyballImageIteration()
    {
        return imageIteration;
    }

    public Bitmap getOpponent1Bitmap()
    {
        return opponent1Bitmap;
    }

    public Bitmap getOpponent2Bitmap()
    {
        return opponent2Bitmap;
    }

    public Bitmap getOpponent3Bitmap()
    {
        return opponent3Bitmap;
    }

    public Bitmap getOpponent4Bitmap()
    {
        return opponent4Bitmap;
    }

    public Bitmap getAvatar1Bitmap()
    {
        return avatar1Bitmap;
    }

    public Bitmap getAvatar2Bitmap()
    {
        return avatar2Bitmap;
    }

    public Bitmap getLeftButtonBitmap()
    {
        return leftButtonBitmap;
    }

    public Bitmap getRightButtonBitmap()
    {
        return rightButtonBitmap;
    }

    public Bitmap getShootButtonBitmap()
    {
        return shootButtonBitmap;
    }

    public Bitmap getCourtBitmap()
    {
        return courtBitmap;
    }

    public Bitmap getSixPack()
    {
        return sixPack;
    }

    public Bitmap getMedal()
    {
        return medal;
    }

    public Bitmap getPlayerAvatar()
    {
        return playerAvatar;
    }

    public void setPlayerAvatar(Bitmap playerAvatar)
    {
        this.playerAvatar = playerAvatar;
    }

    public SoundPool getSoundPool()
    {
        return soundPool;
    }

    public int[] getSounds()
    {
        return sounds;
    }
}
