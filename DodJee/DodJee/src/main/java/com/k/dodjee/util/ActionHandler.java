package com.k.dodjee.util;

import com.k.dodjee.game.DodJeeGame;

public class ActionHandler
{
    private DodJeeGame gamePanel;

    public ActionHandler(DodJeeGame panel)
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
            if (!gamePanel.getGamePanelResources().getPlayer().getBall().isMovingVertically())
            {
                if (pointer != gamePanel.getGamePanelResources().getMovePointer())
                {
                    gamePanel.getGamePanelResources().getPlayer().getBall().setX(gamePanel.getGamePanelResources().getPlayer().getX());
                    gamePanel.getGamePanelResources().getPlayer().getBall().setMovingVertically(gamePanel.getGamePanelResources().isGameStarted());

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
                gamePanel.getGamePanelResources().setMovePointer(DodJeeGame.NoPointer);
            }

            if (pointer == gamePanel.getGamePanelResources().getShootPointer())
                gamePanel.getGamePanelResources().setShootPointer(DodJeeGame.NoPointer);

            return Button.NONE;
        }
    }
}
