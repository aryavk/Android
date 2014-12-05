package com.k.blockout.game;

public interface GameListener
{
    int MaxLevel = 20;

    public void onLose(final int score);
    public void onWin(final int level, final int score);
}
