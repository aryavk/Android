package com.k.dodjee.game;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v4.view.MotionEventCompat;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.k.dodjee.graphics.Ball;
import com.k.dodjee.util.GamePanelResources;
import com.k.dodjee.graphics.Button;
import com.k.dodjee.graphics.Direction;
import com.k.dodjee.graphics.GameResources;
import com.k.dodjee.graphics.Opponent;
import com.k.dodjee.graphics.Player;

import java.util.ArrayList;

public abstract class GamePanelImpl extends SurfaceView implements GamePanel
{
    public static final int NoPointer = -1;

    protected GamePanelResources gamePanelResources;
    private boolean initialised;

    public SurfaceView getGamePanelView()
    {
        return this;
    }

    public GamePanelImpl(Context context)
    {
        super(context);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder)
    {

    }

    @Override
    public abstract void update(Canvas canvas);

    public GamePanelResources getGamePanelResources()
    {
        return gamePanelResources;
    }

    public boolean isInitialised()
    {
        return initialised;
    }

    public void setInitialised(boolean initialised)
    {
        this.initialised = initialised;
    }

    protected void addScore(int addedScore)
    {
        getGamePanelResources().setScore(getGamePanelResources().getScore() + addedScore);
    }

    @Override
    public void setupResources(int score, int level, GameResources resources)
    {
        gamePanelResources = new GamePanelResources();

        int playerSpeed = 12;
        int opponentSpeed = 15;
        int opponentBallSpeed = opponentSpeed;

        getGamePanelResources().setGameResources(resources);

        getGamePanelResources().setLevel(level);
        getGamePanelResources().setScore(score);

        opponentBallSpeed = opponentBallSpeed + (level);

        // adding the callback (this) to the surface holder to intercept events
        if (getHolder() != null)
            getHolder().addCallback(this);

        // Create the players, opponents and balls.
        getGamePanelResources().setPlayer(new Player(resources.getPlayerAvatar(), 0, 0, playerSpeed));
        Ball playerVball = new Ball(resources.getVolleyballBitmap(), 0, 0, playerSpeed, Direction.UP, getGamePanelResources().getPlayer(), resources.getVolleyballImageIteration());

        Opponent opponent1 = new Opponent(resources.getOpponent1Bitmap(), 0, 0, opponentSpeed);
        Opponent opponent2 = new Opponent(resources.getOpponent2Bitmap(), 0, 0, opponentSpeed);
        Opponent opponent3 = new Opponent(resources.getOpponent3Bitmap(), 0, 0, opponentSpeed);
        Opponent opponent4 = new Opponent(resources.getOpponent4Bitmap(), 0, 0, opponentSpeed);

        Ball opponentVball1 = new Ball(resources.getVolleyballBitmap(), 0, 0, opponentSpeed, Direction.DOWN, opponent1, resources.getVolleyballImageIteration());
        Ball opponentVball2 = new Ball(resources.getVolleyballBitmap(), 0, 0, opponentSpeed, Direction.DOWN, opponent2, resources.getVolleyballImageIteration());
        Ball opponentVball3 = new Ball(resources.getVolleyballBitmap(), 0, 0, opponentSpeed, Direction.DOWN, opponent3, resources.getVolleyballImageIteration());
        Ball opponentVball4 = new Ball(resources.getVolleyballBitmap(), 0, 0, opponentSpeed, Direction.DOWN, opponent4, resources.getVolleyballImageIteration());

        opponentVball1.setSpeed(opponentBallSpeed);
        opponentVball2.setSpeed(opponentBallSpeed);
        opponentVball3.setSpeed(opponentBallSpeed);
        opponentVball4.setSpeed(opponentBallSpeed);

        // Assign the balls to each person
        opponent1.setBall(opponentVball1);
        opponent2.setBall(opponentVball2);
        opponent3.setBall(opponentVball3);
        opponent4.setBall(opponentVball4);

        getGamePanelResources().getPlayer().setBall(playerVball);

        // Create the action buttons for use in the button pane
        getGamePanelResources().setLeftButton(new Button(resources.getLeftButtonBitmap(), 0, 0));
        getGamePanelResources().setRightButton(new Button(resources.getRightButtonBitmap(), 0, 0));
        getGamePanelResources().setShootButton(new Button(resources.getShootButtonBitmap(), 0, 0));

        getGamePanelResources().setThread(new GameThreadImpl(getHolder(), this));

        getGamePanelResources().setOpponents(new ArrayList<Opponent>());

        for (int i = 1; i <= level; i++)
        {
            if (i == 1)
            {
                getGamePanelResources().getOpponents().add(opponent1);
                getGamePanelResources().getOpponents().add(opponent2);
            }
            else if (i == 5)
            {
                getGamePanelResources().getOpponents().add(opponent3);
            }
            else if (i == 10)
            {
                getGamePanelResources().getOpponents().add(opponent4);
            }
        }

        // Initialise the paint settings for the level starting text displaying time and level
        getGamePanelResources().getLevelTextPaint().setTextSize(120);
        getGamePanelResources().getLevelTextPaint().setTextAlign(Paint.Align.CENTER);
        getGamePanelResources().getLevelTextPaint().setColor(Color.YELLOW);

        // Initialise the paint settings for the score text displaying the current score
        getGamePanelResources().getScoreTextPaint().setTextSize(70);
        getGamePanelResources().getScoreTextPaint().setTextAlign(Paint.Align.LEFT);
        getGamePanelResources().getScoreTextPaint().setColor(Color.YELLOW);

        getGamePanelResources().getButtonPanePaint().setColor(Color.BLACK);
    }

    protected com.k.dodjee.util.Button handleActions(float eventX, float eventY, int pointer)
    {
        return getGamePanelResources().getActionHandler().handleActions(eventX, eventY, pointer);
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        int action = MotionEventCompat.getActionMasked(event);
        int pointerCount = event.getPointerCount();
        int actionIndex = MotionEventCompat.getActionIndex(event);

        for (int i = 0; i < pointerCount; i++)
        {
            int pointer = MotionEventCompat.getPointerId(event, i);
            float eventX = event.getX(i);
            float eventY = event.getY(i);

            if (action == MotionEvent.ACTION_DOWN)
            {
                handleActions(eventX, eventY, pointer);
            }
            else if (action == MotionEvent.ACTION_UP)
            {
                if (pointer == getGamePanelResources().getMovePointer())
                {
                    getGamePanelResources().setMovePointer(NoPointer);
                    getGamePanelResources().getPlayer().setMovingHorizontally(false);
                }
                else if (pointer == getGamePanelResources().getShootPointer())
                {
                    getGamePanelResources().setShootPointer(NoPointer);
                }
            }
            else if (action == MotionEvent.ACTION_POINTER_UP)
            {
                if (i == actionIndex)
                {
                    if (pointer == getGamePanelResources().getMovePointer())
                    {
                        getGamePanelResources().setMovePointer(NoPointer);
                        getGamePanelResources().getPlayer().setMovingHorizontally(false);
                    }
                    else if (pointer == getGamePanelResources().getShootPointer())
                    {
                        getGamePanelResources().setShootPointer(NoPointer);
                    }
                }
            }
            else if(action == MotionEvent.ACTION_POINTER_DOWN)
            {
                handleActions(eventX, eventY, pointer);
            }
            else if (action == MotionEvent.ACTION_MOVE)
            {
                // In the move action, we need to define the pointer differently, as it always uses index 0
                // when doing MotionEventCompat.getActionIndex(event);. This is a workaround
                pointer = event.getPointerId(i);
                handleActions(eventX, eventY, pointer);
            }
            else if (action == MotionEvent.ACTION_CANCEL)
            {
                if (pointer == getGamePanelResources().getMovePointer())
                {
                    getGamePanelResources().setMovePointer(NoPointer);
                    getGamePanelResources().getPlayer().setMovingHorizontally(false);
                }
                else if (pointer == getGamePanelResources().getShootPointer())
                {
                    getGamePanelResources().setShootPointer(NoPointer);
                }
            }
        }

        return true;
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i2, int i3)
    {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder)
    {

    }
}
