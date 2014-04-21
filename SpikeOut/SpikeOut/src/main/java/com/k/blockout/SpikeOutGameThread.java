package com.k.blockout;

import android.graphics.Canvas;
import android.view.SurfaceHolder;

public class SpikeOutGameThread extends Thread
{
    // flag to hold game state
    private boolean running;

    // This is our target frame rate. If we cant match it lets not update. Go as much as the hardware can
    private final static int FPS = 60;
    private final static int FRAME_PERIOD = 1000 / FPS;

    public void setRunning(boolean running)
    {
        this.running = running;
    }

    @Override
    public void run()
    {
        Canvas canvas;

        long beginTime;
        long timeDiff;
        int sleepTime;

        while (running)
        {
            canvas = null;
            // try locking the canvas for exclusive pixel editing on the surface
            try
            {
                canvas = this.surfaceHolder.lockCanvas();
                synchronized (surfaceHolder)
                {
                    beginTime = System.currentTimeMillis();

                    // update game state
                    this.gamePanel.update();

                    // draws the canvas on the panel
                    this.gamePanel.onDraw(canvas);

                    timeDiff = System.currentTimeMillis() - beginTime;
                    sleepTime = (int)(FRAME_PERIOD - timeDiff);

                    // If we match the target FPS, then lets wait a bit. Don't overdo the hardware
                    if (sleepTime > 0)
                    {
                        // if sleepTime > 0 we're OK
                        try
                        {
                            // send the thread to sleep for a short period
                            // very useful for battery saving
                            Thread.sleep(sleepTime);
                        }
                        catch (InterruptedException e)
                        {
                            // do nothing
                        }
                    }
                }
            }
            finally
            {
                // in case of an exception the surface is not left in
                // an inconsistent state
                if (canvas != null) {
                    surfaceHolder.unlockCanvasAndPost(canvas);
                }
            } // end finally
        }
    }

    private final SurfaceHolder surfaceHolder;
    private SpikeOutGame gamePanel;

    public SpikeOutGameThread(SurfaceHolder surfaceHolder, SpikeOutGame gamePanel)
    {
        super();
        this.surfaceHolder = surfaceHolder;
        this.gamePanel = gamePanel;
    }
}
