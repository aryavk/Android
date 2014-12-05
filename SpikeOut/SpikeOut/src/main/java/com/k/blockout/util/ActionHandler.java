package com.k.blockout.util;

import com.k.blockout.game.SpikeOutGame;

public class ActionHandler
{
    private SpikeOutGame gamePanel;

    public ActionHandler(SpikeOutGame panel)
    {
        this.gamePanel = panel;
    }

    public Button handleActions(float eventX, float eventY, int pointer)
    {
        if (gamePanel.getGamePanelResources().getLeftButton().eventInBounds(eventX, eventY))
        {
            gamePanel.getGamePanelResources().getPlayer().setMoveToX(0);
            gamePanel.getGamePanelResources().getPlayer().setMovingHorizontally(true);

            gamePanel.getGamePanelResources().setMovePointer(pointer);

            return Button.LEFT;
        }
        else if (gamePanel.getGamePanelResources().getRightButton().eventInBounds(eventX, eventY))
        {
            gamePanel.getGamePanelResources().getPlayer().setMoveToX(gamePanel.getWidth());
            gamePanel.getGamePanelResources().getPlayer().setMovingHorizontally(true);

            gamePanel.getGamePanelResources().setMovePointer(pointer);

            return Button.RIGHT;
        }
        else if (gamePanel.getGamePanelResources().getShootButton().eventInBounds(eventX, eventY))
        {
            if (!gamePanel.getGamePanelResources().getPlayer().getVolleyball().isMovingVertically())
            {
                if (pointer != gamePanel.getGamePanelResources().getMovePointer())
                {
                    gamePanel.getGamePanelResources().getPlayer().getVolleyball().setX(gamePanel.getGamePanelResources().getPlayer().getX());
                    gamePanel.getGamePanelResources().getPlayer().getVolleyball().setMovingVertically(gamePanel.getGamePanelResources().isGameStarted());

                    if (gamePanel.getGamePanelResources().isGameStarted())
                        gamePanel.getGamePanelResources().getGameResources().getSoundPool().play(gamePanel.getGamePanelResources().getGameResources().getSounds()[1], 100, 100, 1, 0, 1);

                    gamePanel.getGamePanelResources().setShootPointer(pointer);
                }
            }

            return Button.SHOOT;
        }
        else
        {
            // If you are not in the left or right movement squares, stop moving
            if (pointer == gamePanel.getGamePanelResources().getMovePointer())
            {
                gamePanel.getGamePanelResources().getPlayer().setMovingHorizontally(false);
                gamePanel.getGamePanelResources().setMovePointer(SpikeOutGame.NoPointer);
            }

            if (pointer == gamePanel.getGamePanelResources().getShootPointer())
                gamePanel.getGamePanelResources().setShootPointer(SpikeOutGame.NoPointer);

            return Button.NONE;
        }
    }
}
