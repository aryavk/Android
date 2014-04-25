package com.k.blockout;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.CountDownTimer;
import android.support.v4.view.MotionEventCompat;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.k.blockout.graphics.Button;
import com.k.blockout.util.ActionHandler;
import com.k.blockout.util.CollisionDetector;
import com.k.blockout.graphics.Direction;
import com.k.blockout.graphics.GameResources;
import com.k.blockout.graphics.Opponent;
import com.k.blockout.graphics.Person;
import com.k.blockout.graphics.Player;
import com.k.blockout.graphics.Volleyball;

import java.math.BigDecimal;
import java.util.ArrayList;

public class SpikeOutGame extends SurfaceView implements SurfaceHolder.Callback
{
    private GamePanelResources gamePanelResources;
    private final static int maxPersonSpeed = 60;
    private final static int maxBallSpeed = 80;
    public static final int NoPointer = -1;

    private boolean initialised;

    // Used as a counter, game speed increases after some time
    private int i = 0;

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

    public SpikeOutGame(Context context, int score, int level, GameResources resources)
    {
        super(context);
        gamePanelResources = new GamePanelResources();

        int playerSpeed = 12;
        int opponentSpeed = 15;
        int opponentBallSpeed = 15;

        getGamePanelResources().setGameResources(resources);

        getGamePanelResources().setLevel(level);
        getGamePanelResources().setScore(score);

        opponentBallSpeed = opponentBallSpeed + (level);

        // adding the callback (this) to the surface holder to intercept events
        if (getHolder() != null)
            getHolder().addCallback(this);

        // Create the players, opponents and balls.
        getGamePanelResources().setPlayer(new Player(resources.getPlayerAvatar(), 0, 0, playerSpeed));
        Volleyball playerVball = new Volleyball(resources.getVolleyballBitmap(), 0, 0, playerSpeed, Direction.UP, getGamePanelResources().getPlayer(), resources.getVolleyballImageIteration());

        Opponent opponent1 = new Opponent(resources.getOpponent1Bitmap(), 0, 0, opponentSpeed);
        Opponent opponent2 = new Opponent(resources.getOpponent2Bitmap(), 0, 0, opponentSpeed);
        Opponent opponent3 = new Opponent(resources.getOpponent3Bitmap(), 0, 0, opponentSpeed);
        Opponent opponent4 = new Opponent(resources.getOpponent4Bitmap(), 0, 0, opponentSpeed);

        Volleyball opponentVball1 = new Volleyball(resources.getVolleyballBitmap(), 0, 0, opponentSpeed, Direction.DOWN, opponent1, resources.getVolleyballImageIteration());
        Volleyball opponentVball2 = new Volleyball(resources.getVolleyballBitmap(), 0, 0, opponentSpeed, Direction.DOWN, opponent2, resources.getVolleyballImageIteration());
        Volleyball opponentVball3 = new Volleyball(resources.getVolleyballBitmap(), 0, 0, opponentSpeed, Direction.DOWN, opponent3, resources.getVolleyballImageIteration());
        Volleyball opponentVball4 = new Volleyball(resources.getVolleyballBitmap(), 0, 0, opponentSpeed, Direction.DOWN, opponent4, resources.getVolleyballImageIteration());

        opponentVball1.setSpeed(opponentBallSpeed);
        opponentVball2.setSpeed(opponentBallSpeed);
        opponentVball3.setSpeed(opponentBallSpeed);
        opponentVball4.setSpeed(opponentBallSpeed);

        // Assign the balls to each person
        opponent1.setVolleyball(opponentVball1);
        opponent2.setVolleyball(opponentVball2);
        opponent3.setVolleyball(opponentVball3);
        opponent4.setVolleyball(opponentVball4);

        getGamePanelResources().getPlayer().setVolleyball(playerVball);

        // Create the action buttons for use in the button pane
        getGamePanelResources().setLeftButton(new Button(resources.getLeftButtonBitmap(), 0, 0));
        getGamePanelResources().setRightButton(new Button(resources.getRightButtonBitmap(), 0, 0));
        getGamePanelResources().setShootButton(new Button(resources.getShootButtonBitmap(), 0, 0));

        getGamePanelResources().setThread(new SpikeOutGameThread(getHolder(), this));

        getGamePanelResources().setOpponents(new ArrayList<Opponent>());

        for (int i = 1; i <= level; i++)
        {
            if (i == 1)
            {
                getGamePanelResources().getOpponents().add(opponent1);
                getGamePanelResources().getOpponents().add(opponent2);
            }
            else if (i == 3)
            {
                getGamePanelResources().getOpponents().add(opponent3);
            }
            else if (i == 5)
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

        getGamePanelResources().getButtonPanePaint().setColor(Color.BLUE);

        // make the GamePanel focusable so it can handle events
        setFocusable(true);
    }

    public SpikeOutGame(Context context, GamePanelResources gamePanelResources)
    {
        super(context);
        this.gamePanelResources = gamePanelResources;
        // adding the callback (this) to the surface holder to intercept events
        if (getHolder() != null)
            getHolder().addCallback(this);

        getGamePanelResources().setGameStarted(false);
        getGamePanelResources().setThread(new SpikeOutGameThread(getHolder(), this));

        this.setInitialised(true);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height)
    {
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder)
    {
        reinitialise();
        getGamePanelResources().setActionHandler(new ActionHandler(this));
    }

    // Lets initialise all the on screen elements
    private void reinitialise()
    {
        // Initialise the waiting text
        getGamePanelResources().setLevelText("Level " + getGamePanelResources().getLevel());

        if (!isInitialised())
        {
            int levelTextSize = getWidth() / 10;
            int scoreTextSize = getWidth() / 15;
            getGamePanelResources().getLevelTextPaint().setTextSize(levelTextSize);
            getGamePanelResources().getScoreTextPaint().setTextSize(scoreTextSize);

            // Set the game height to most of the screen (add in a layer at the bottom for a button pane
            int gameHeightToUse = (getHeight() - (getGamePanelResources().getShootButton().getHeight()));
            getGamePanelResources().setGameHeight(gameHeightToUse);
            getGamePanelResources().getPlayer().setGameWidth(getWidth());

            // Put the players avatar in the bottom of the game screen in the middle of the horizontal axis
            getGamePanelResources().getPlayer().setX(getWidth() / 2);
            getGamePanelResources().getPlayer().setY(getGamePanelResources().getGameHeight() - getGamePanelResources().getPlayer().getHeight() / 2);
            getGamePanelResources().getPlayer().getVolleyball().reinitialise();
            getGamePanelResources().getPlayer().getVolleyball().setGameHeight(getGamePanelResources().getGameHeight());

            // Initialise the location of the opponents and their volleyballs
            for (Opponent opponent : getGamePanelResources().getOpponents())
            {
                opponent.setGameWidth(getWidth());
                randomiseOpponentX(opponent);
                opponent.setY((getGamePanelResources().getOpponents().indexOf(opponent) * opponent.getHeight()) + opponent.getHeight() / 2);
                opponent.reinitialise();
                opponent.getVolleyball().setGameHeight(getGamePanelResources().getGameHeight());
            }

            // Define the button pane and the buttons within it.
            getGamePanelResources().setButtonPane(new Rect(0, getGamePanelResources().getGameHeight(), getWidth(), getHeight()));
            getGamePanelResources().setCourtPane(new Rect(0, 0, getWidth(), getGamePanelResources().getGameHeight()));
            getGamePanelResources().setCourtInitialPane(new Rect(0, 0, getGamePanelResources().getGameResources().getCourtBitmap().getWidth(), getGamePanelResources().getGameResources().getCourtBitmap().getHeight()));

            getGamePanelResources().getLeftButton().setY(getHeight() - (getGamePanelResources().getLeftButton().getHeight() / 2));
            getGamePanelResources().getLeftButton().setX((int) (getWidth() * 0.1));

            getGamePanelResources().getRightButton().setY(getHeight() - (getGamePanelResources().getRightButton().getHeight() / 2));
            getGamePanelResources().getRightButton().setX(getGamePanelResources().getLeftButton().getX() + getGamePanelResources().getLeftButton().getWidth());

            getGamePanelResources().getShootButton().setY(getHeight() - (getGamePanelResources().getShootButton().getHeight() / 2));
            getGamePanelResources().getShootButton().setX((int) (getWidth() * 0.9));
        }

        // This is the countdown timer at the start of each level
        new CountDownTimer(2100,100)
        {
            @Override
            public void onTick(long timeLeft)
            {
                double time = (double) timeLeft / (double) 1000;

                BigDecimal timeValue = new BigDecimal(String.valueOf(time));
                timeValue = timeValue.setScale(1, BigDecimal.ROUND_HALF_EVEN);

                if (time > 0.2)
                    getGamePanelResources().setTimeText("Time Left: " + timeValue.toString() + " sec");
                else
                    getGamePanelResources().setTimeText("START");

                invalidate();
            }

            @Override
            public void onFinish()
            {
                getGamePanelResources().setGameStarted(true);
            }
        }.start();

        getGamePanelResources().getThread().setRunning(true);
        getGamePanelResources().getThread().start();

        setInitialised(true);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder)
    {
        boolean retry = true;
        while (retry)
        {
            try
            {
                getGamePanelResources().getThread().join();
                retry = false;
            }
            catch (InterruptedException e)
            {
                // try again shutting down the thread
            }
        }
    }

    private void handleActions(float eventX, float eventY, int pointer)
    {
        getGamePanelResources().getActionHandler().handleActions(eventX, eventY, pointer);
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

    private void lose()
    {
        getGamePanelResources().getThread().setRunning(false);
        getGamePanelResources().setGameStarted(false);

        SpikeOutActivity activity = ((SpikeOutActivity)getContext());

        if (activity != null)
            activity.onLose(getGamePanelResources().getScore());
    }

    private void win()
    {
        getGamePanelResources().getThread().setRunning(false);
        getGamePanelResources().setGameStarted(false);

        addScore(getGamePanelResources().getLevel() * 5);

        getGamePanelResources().setLevel(getGamePanelResources().getLevel() + 1);

        SpikeOutActivity activity = ((SpikeOutActivity)getContext());

        if (activity != null)
            activity.onWin(getGamePanelResources().getLevel(), getGamePanelResources().getScore());
    }

    private void checkBallCollisions(Volleyball opponentVball)
    {
        // If balls collide then clear both balls
        if (CollisionDetector.collisionDetected(getGamePanelResources().getPlayer().getVolleyball(), opponentVball))
        {
            getGamePanelResources().getPlayer().getVolleyball().setMovingVertically(false);
            opponentVball.setMovingVertically(false);

            getGamePanelResources().getPlayer().getVolleyball().reinitialise();
            opponentVball.reinitialise();
        }
    }

    private void checkPlayerCollision(Volleyball opponentVball)
    {
        if (CollisionDetector.collisionDetected(getGamePanelResources().getPlayer(), opponentVball))
        {
            getGamePanelResources().getGameResources().getSoundPool().play(getGamePanelResources().getGameResources().getSounds()[0], 100, 100, 1, 0, 1);
            lose();
        }
    }

    private void addScore(int addedScore)
    {
        getGamePanelResources().setScore(getGamePanelResources().getScore() + addedScore);
    }

    private void checkOpponentCollisions(Opponent opponent)
    {
        // If balls collides wih opponent, remove opponent and his ball (TODO keep the ball -> collision issues with just removing last line in block)
        if (CollisionDetector.collisionDetected(getGamePanelResources().getPlayer().getVolleyball(), opponent))
        {
            getGamePanelResources().getPlayer().getVolleyball().setMovingVertically(false);
            getGamePanelResources().getPlayer().getVolleyball().reinitialise();
            addScore(5);
            opponent.setVisible(false);
            opponent.getVolleyball().setVisible(false);
        }
    }

    private void checkCollisions()
    {
        boolean opponentsLeft = false;

        for (Opponent opponent : getGamePanelResources().getOpponents())
        {
            if (opponent.getVolleyball().getY() - (opponent.getVolleyball().getHeight() * 2) < getGamePanelResources().getPlayer().getY())
                checkPlayerCollision(opponent.getVolleyball());

            if (getGamePanelResources().getPlayer().getVolleyball().isInPath(opponent.getVolleyball()))
                checkBallCollisions(opponent.getVolleyball());

            if (getGamePanelResources().getPlayer().getVolleyball().isMovingVertically() &&
                    (getGamePanelResources().getPlayer().getVolleyball().getY() + (getGamePanelResources().getPlayer().getHeight() * 2) > opponent.getY()))
                checkOpponentCollisions(opponent);

            if (opponent.isVisible())
                opponentsLeft = true;
        }

        if (!opponentsLeft)
        {
            win();
        }
    }

    private void randomiseOpponentX(Opponent opponent)
    {
        if (!opponent.isMovingHorizontally())
        {
            int newX = getGamePanelResources().getRandom().nextInt((getWidth() - opponent.getWidth() / 2) - opponent.getWidth() / 2) + opponent.getWidth() / 2;
            opponent.setMoveToX(newX);
            opponent.setMovingHorizontally(true);
        }
    }

    private void randomiseOpponent(Opponent opponent)
    {
        if (opponent.isVisible())
        {
            if (!opponent.getVolleyball().isMovingVertically())
            {
                opponent.getVolleyball().reinitialise();
                opponent.getVolleyball().setMovingVertically(getGamePanelResources().isGameStarted());
            }

            randomiseOpponentX(opponent);

            if (opponent.getVolleyball().isMovingVertically() && getGamePanelResources().isGameStarted())
            {
                opponent.getVolleyball().moveVertically(this);
                opponent.getVolleyball().spinBall();
            }

            if (opponent.isMovingHorizontally())
            {
                opponent.moveHorizontally(this);
                if (!opponent.getVolleyball().isMovingVertically())
                    opponent.getVolleyball().reinitialise();
            }
        }
    }

    private void incrementSpeed(Person person)
    {
        if (i % 100 == 0)
        {
            if (person.getVolleyball().getSpeed() < maxBallSpeed)
                person.getVolleyball().setSpeed(person.getVolleyball().getSpeed() + 1);
        }

        if (i % 200 == 0)
        {
            if (person.getSpeed() < maxPersonSpeed)
                person.setSpeed(person.getSpeed() + 1);
        }
    }

    public void update()
    {
        checkCollisions();

        i++;

        for (Opponent opponent : getGamePanelResources().getOpponents())
        {
            randomiseOpponent(opponent);
            incrementSpeed(opponent);
        }

        incrementSpeed(getGamePanelResources().getPlayer());

        if (getGamePanelResources().getPlayer().isMovingHorizontally())
        {
            getGamePanelResources().getPlayer().moveHorizontally(this);
            if (!getGamePanelResources().getPlayer().getVolleyball().isMovingVertically())
                getGamePanelResources().getPlayer().getVolleyball().reinitialise();
        }

        if (getGamePanelResources().getPlayer().getVolleyball().isMovingVertically() && getGamePanelResources().isGameStarted())
        {
            getGamePanelResources().getPlayer().getVolleyball().moveVertically(this);
            getGamePanelResources().getPlayer().getVolleyball().spinBall();
        }
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        // Lets clear the canvas
        canvas.drawColor(Color.BLACK);

        // Now lets draw the button pane in the specified Rect at the bottom of the screen
        canvas.drawRect(getGamePanelResources().getButtonPane(), getGamePanelResources().getButtonPanePaint());

        // Now draw the court in the main game area
        canvas.drawBitmap(getGamePanelResources().getGameResources().getCourtBitmap(), getGamePanelResources().getCourtInitialPane(), getGamePanelResources().getCourtPane(), null);

        // If its the start of the level, display the level/time text
        if (!getGamePanelResources().isGameStarted())
        {
            canvas.drawText(getGamePanelResources().getLevelText(), getWidth() / 2, (getHeight() / 2) - 120, getGamePanelResources().getLevelTextPaint());
            canvas.drawText(getGamePanelResources().getTimeText(), getWidth() / 2, (getHeight() / 2) + 120, getGamePanelResources().getLevelTextPaint());
        }

        canvas.drawText("Score: " + getGamePanelResources().getScore(), getWidth() / 2, getGamePanelResources().getGameHeight() + 70, getGamePanelResources().getScoreTextPaint());

        getGamePanelResources().getLeftButton().draw(canvas);
        getGamePanelResources().getRightButton().draw(canvas);
        getGamePanelResources().getShootButton().draw(canvas);
        getGamePanelResources().getPlayer().draw(canvas);
        if (getGamePanelResources().getPlayer().getVolleyball().isMovingVertically())
        {
            getGamePanelResources().getPlayer().getVolleyball().draw(canvas);
        }

        for (Opponent opponent : getGamePanelResources().getOpponents())
        {
            if (opponent.isVisible())
            {
                opponent.draw(canvas);
                if (opponent.getVolleyball().isMovingVertically())
                {
                    opponent.getVolleyball().draw(canvas);
                }
            }
        }
    }
}
