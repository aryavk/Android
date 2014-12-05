package com.k.blockout.game;

public interface GameThread extends Runnable
{
    public void run();

    public void setRunning(boolean running);

    public void start();

    public void join() throws InterruptedException;
}
