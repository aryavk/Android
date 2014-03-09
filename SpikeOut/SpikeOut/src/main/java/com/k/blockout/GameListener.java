package com.k.blockout;

public interface GameListener
{
    int MaxLevel = 7;

    public void onLose(final int score);
    public void onWin(final int level, final int score);
}
