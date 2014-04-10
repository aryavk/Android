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
import com.k.blockout.graphics.CollisionDetector;
import com.k.blockout.graphics.Direction;
import com.k.blockout.graphics.GameResources;
import com.k.blockout.graphics.Opponent;
import com.k.blockout.graphics.Person;
import com.k.blockout.graphics.Player;
import com.k.blockout.graphics.Volleyball;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainGamePanel extends SurfaceView implements SurfaceHolder.Callback
{
    // This is the main game loop thread.
    private MainThread thread;

    // These are the member variables for the player related graphics
    private Player player;

    private final static int maxPersonSpeed = 40;
    private final static int maxBallSpeed = 80;

    // This is the list of opponents on screen you must beat
    private List<Opponent> opponents;

    // This is the control buttons
    private Button leftButton;
    private Button rightButton;
    private Button shootButton;

    private int score;
    private int level;

    private int gameHeight;

    private GameResources resources;

    private Rect courtPane;
    private Rect courtInitialPane;
    private Rect buttonPane;
    private Paint buttonPanePaint = new Paint();
    private Paint levelTextPaint = new Paint();
    private Paint scoreTextPaint = new Paint();

    private boolean gameStarted = false;
    private String levelText = "";
    private String timeText = "";

    // TODO maybe i dont need this, added in for the balls able to keep going, but it shouldn't occur anymore, was wrong code
    private final Object mutex = new Object();

    public MainGamePanel(Context context, int score, int level, GameResources resources)
    {
        super(context);

        int playerSpeed = 15;
        int opponentSpeed = 15;
        int opponentBallSpeed = 15;

        this.resources = resources;

        this.level = level;
        this.score = score;

        opponentBallSpeed = opponentBallSpeed + (level);

        // adding the callback (this) to the surface holder to intercept events
        if (getHolder() != null)
            getHolder().addCallback(this);

        // Create the players, opponents and balls.
        player = new Player(resources.getPlayerAvatar(), 0, 0, playerSpeed);
        Volleyball playerVball = new Volleyball(resources.getVolleyballBitmap(), 0, 0, playerSpeed, Direction.UP, player);

        player.setVolleyball(playerVball);

        Opponent opponent1 = new Opponent(resources.getOpponent1Bitmap(), 0, 0, opponentSpeed);
        Opponent opponent2 = new Opponent(resources.getOpponent2Bitmap(), 0, 0, opponentSpeed);
        Opponent opponent3 = new Opponent(resources.getOpponent3Bitmap(), 0, 0, opponentSpeed);
        Opponent opponent4 = new Opponent(resources.getOpponent4Bitmap(), 0, 0, opponentSpeed);

        Volleyball opponentVball1 = new Volleyball(resources.getVolleyballBitmap(), 0, 0, opponentSpeed, Direction.DOWN, opponent1);
        Volleyball opponentVball2 = new Volleyball(resources.getVolleyballBitmap(), 0, 0, opponentSpeed, Direction.DOWN, opponent2);
        Volleyball opponentVball3 = new Volleyball(resources.getVolleyballBitmap(), 0, 0, opponentSpeed, Direction.DOWN, opponent3);
        Volleyball opponentVball4 = new Volleyball(resources.getVolleyballBitmap(), 0, 0, opponentSpeed, Direction.DOWN, opponent3);

        opponentVball1.setSpeed(opponentBallSpeed);
        opponentVball2.setSpeed(opponentBallSpeed);
        opponentVball3.setSpeed(opponentBallSpeed);
        opponentVball4.setSpeed(opponentBallSpeed);

        // Assign the opponent ball to each opponent
        opponent1.setVolleyball(opponentVball1);
        opponent2.setVolleyball(opponentVball2);
        opponent3.setVolleyball(opponentVball3);
        opponent4.setVolleyball(opponentVball4);

        // Create the action buttons for use in the button pane
        leftButton = new Button(resources.getLeftButtonBitmap(), 0, 0);
        rightButton = new Button(resources.getRightButtonBitmap(), 0, 0);
        shootButton = new Button(resources.getShootButtonBitmap(), 0, 0);

        thread = new MainThread(getHolder(), this);

        opponents = new ArrayList<Opponent>();

        for (int i = 1; i <= level; i++)
        {
            if (i == 1)
            {
                opponents.add(opponent1);
                opponents.add(opponent2);
            }
            else if (i == 3)
            {
                opponents.add(opponent3);
            }
            else if (i == 5)
            {
                opponents.add(opponent4);
            }
        }

        // Initialise the paint settings for the level starting text displaying time and level
        levelTextPaint.setTextSize(120);
        levelTextPaint.setTextAlign(Paint.Align.CENTER);
        levelTextPaint.setColor(Color.YELLOW);

        // Initialise the paint settings for the score text displaying the current score
        scoreTextPaint.setTextSize(70);
        scoreTextPaint.setTextAlign(Paint.Align.LEFT);
        scoreTextPaint.setColor(Color.YELLOW);

        buttonPanePaint.setColor(Color.BLUE);

        // make the GamePanel focusable so it can handle events
        setFocusable(true);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height)
    {
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder)
    {
        reinitialise();
    }

    // Lets initialise all the on screen elements
    private void reinitialise()
    {
        // Initialise the waiting text
        levelText = "Level " + level;

        // This is the countdown timer at the start of each level
        new CountDownTimer(3100,100)
        {
            @Override
            public void onTick(long timeLeft)
            {
                double time = (double) timeLeft / (double) 1000;

                BigDecimal timeValue = new BigDecimal(String.valueOf(time));
                timeValue = timeValue.setScale(1, BigDecimal.ROUND_HALF_EVEN);

                if (time > 0.2)
                    timeText = "Time Left: " + timeValue.toString() + " sec";
                else
                    timeText = "START";

                invalidate();
            }

            @Override
            public void onFinish()
            {
                gameStarted = true;
            }
        }.start();

        // Set the game height to most of the screen (add in a layer at the bottom for a button pane
        int gameHeightToUse = (getHeight() - (shootButton.getHeight()));
        setGameHeight(gameHeightToUse);

        // Put the players avatar in the bottom of the game screen in the middle of the horizontal axis
        player.setX(getWidth() / 2);
        player.setY(getGameHeight() - player.getHeight() / 2);
        player.getVolleyball().reinitialise();
        player.getVolleyball().setGameHeight(getGameHeight());

        // Initialise the location of the opponents and their volleyballs
        for (Opponent opponent : opponents)
        {
            randomiseOpponentX(opponent);
            opponent.setY((opponents.indexOf(opponent) * opponent.getHeight()) + opponent.getHeight() / 2);
            opponent.reinitialise();
            opponent.getVolleyball().setGameHeight(getGameHeight());
        }

        // Define the button pane and the buttons within it.
        buttonPane = new Rect(0, getGameHeight(), getWidth(), getHeight());
        courtPane = new Rect(0, 0, getWidth(), getGameHeight());
        courtInitialPane = new Rect(0, 0, resources.getCourtBitmap().getWidth(), resources.getCourtBitmap().getHeight());

        leftButton.setY(getHeight() - (leftButton.getHeight() / 2));
        leftButton.setX((int) (getWidth() * 0.1));

        rightButton.setY(getHeight() - (rightButton.getHeight() / 2));
        rightButton.setX((int)(getWidth() * 0.35));

        shootButton.setY(getHeight() - (shootButton.getHeight() / 2));
        shootButton.setX((int)(getWidth() * 0.9));

        thread.setRunning(true);
        thread.start();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder)
    {
        boolean retry = true;
        while (retry)
        {
            try
            {
                thread.join();
                retry = false;
            }
            catch (InterruptedException e)
            {
                // try again shutting down the thread
            }
        }
    }

    public MainThread getThread()
    {
        return thread;
    }

    public boolean isGameStarted() { return gameStarted; }

    public int getGameHeight()
    {
        return gameHeight;
    }

    public void setGameHeight(int gameHeight)
    {
        this.gameHeight = gameHeight;
    }

    private final int NoPointer = -1;

    private int movePointer = NoPointer;
    private int shootPointer = NoPointer;

    private void handleActions(float eventX, float eventY, int pointer)
    {
        if (leftButton.eventInBounds(eventX, eventY))
        {
            if (!player.isMovingHorizontally())
            {
                player.setMoveToX(0);
                player.setMovingHorizontally(true);
            }

            /*if (movePointer == NoPointer)*/
                movePointer = pointer;
        }
        else if (rightButton.eventInBounds(eventX, eventY))
        {
            if (!player.isMovingHorizontally())
            {
                player.setMoveToX(getWidth());
                player.setMovingHorizontally(true);
            }

            /*if (movePointer == NoPointer)*/
                movePointer = pointer;
        }
        else if (shootButton.eventInBounds(eventX, eventY))
        {
            synchronized (mutex)
            {
                if (!player.getVolleyball().isMovingVertically())
                {
                    player.getVolleyball().setX(player.getX());
                    player.getVolleyball().setMovingVertically(isGameStarted());
                    if (isGameStarted())
                        resources.getSoundPool().play(resources.getSounds()[1], 100, 100, 1, 0, 1);
                }
                shootPointer = pointer;
            }
        }
        else
        {
            // If you are not in the left or right movement squares, stop moving
            if (pointer == movePointer)
            {
                player.setMovingHorizontally(false);
                movePointer = NoPointer;
            }

            if (pointer == shootPointer)
                shootPointer = NoPointer;
        }
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        int action = MotionEventCompat.getActionMasked(event);
        int pointerCount = event.getPointerCount();

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
                if (pointer == movePointer)
                {
                    movePointer = NoPointer;
                    player.setMovingHorizontally(false);
                }
                else if (pointer == shootPointer)
                {
                    shootPointer = NoPointer;
                }
            }
            else if (action == MotionEvent.ACTION_POINTER_UP)
            {
                if (pointer == movePointer)
                {
                    movePointer = NoPointer;
                    player.setMovingHorizontally(false);
                }
                else if (pointer == shootPointer)
                {
                    shootPointer = NoPointer;
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
                if (pointer == movePointer)
                {
                    movePointer = NoPointer;
                    player.setMovingHorizontally(false);
                }
                else if (pointer == shootPointer)
                {
                    shootPointer = NoPointer;
                }
            }
        }

        return true;
    }

    private int i = 0;
    Random random = new Random();

    private void lose()
    {
        thread.setRunning(false);
        gameStarted = false;

        BlockActivity activity = ((BlockActivity)getContext());

        if (activity != null)
            activity.onLose(score);
    }

    private void win()
    {
        thread.setRunning(false);
        gameStarted = false;

        addScore(level * 5);

        level++;

        BlockActivity activity = ((BlockActivity)getContext());

        if (activity != null)
            activity.onWin(level, score);
    }

    private void checkBallCollisions(Volleyball opponentVball)
    {
        // If balls collide then clear both balls
        if (CollisionDetector.collisionDetected(player.getVolleyball(), opponentVball))
        {
            player.getVolleyball().setMovingVertically(false);
            opponentVball.setMovingVertically(false);

            player.getVolleyball().reinitialise();
            opponentVball.reinitialise();
        }
    }

    private void checkPlayerCollision(Volleyball opponentVball)
    {
        if (CollisionDetector.collisionDetected(player, opponentVball))
        {
            resources.getSoundPool().play(resources.getSounds()[0], 100, 100, 1, 0, 1);
            lose();
        }
    }

    private void addScore(int addedScore)
    {
        score = score + addedScore;
    }

    private void checkOpponentCollisions(Opponent opponent)
    {
        // If balls collides wih opponent, remove opponent and his ball (TODO keep the ball -> collision issues with just removing last line in block)
        if (CollisionDetector.collisionDetected(player.getVolleyball(), opponent))
        {
            player.getVolleyball().setMovingVertically(false);
            player.getVolleyball().reinitialise();
            addScore(5);
            opponent.setVisible(false);
            opponent.getVolleyball().setVisible(false);
        }
    }

    private void checkCollisions()
    {
        boolean opponentsLeft = false;

        synchronized (mutex)
        {
            for (Opponent opponent : opponents)
            {
                checkPlayerCollision(opponent.getVolleyball());

                checkBallCollisions(opponent.getVolleyball());

                checkOpponentCollisions(opponent);

                if (opponent.isVisible())
                    opponentsLeft = true;
            }
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
            int newX = random.nextInt((getWidth() - opponent.getWidth() / 2) - opponent.getWidth() / 2) + opponent.getWidth() / 2;
            opponent.setMoveToX(newX);
            opponent.setMovingHorizontally(true);
        }
    }

    private void randomiseOpponent(Opponent opponent, Canvas canvas)
    {
        if (opponent.isVisible())
        {
            opponent.draw(canvas);

            if (!opponent.getVolleyball().isMovingVertically())
            {
                opponent.getVolleyball().reinitialise();
                opponent.getVolleyball().setMovingVertically(isGameStarted());
            }

            randomiseOpponentX(opponent);

            if (opponent.getVolleyball().isMovingVertically())
            {
                opponent.getVolleyball().draw(canvas);
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

    @Override
    protected void onDraw(Canvas canvas)
    {
        // Lets clear the canvas
        canvas.drawColor(Color.BLACK);

        // Now lets draw the button pane in the specified Rect at the bottom of the screen
        canvas.drawRect(buttonPane, buttonPanePaint);

        // Now draw the court in the main game area
        canvas.drawBitmap(resources.getCourtBitmap(), courtInitialPane, courtPane, null);

        // If its the start of the level, display the level/time text
        if (!isGameStarted())
        {
            canvas.drawText(levelText, getWidth() / 2, (getHeight() / 2) - 120, levelTextPaint);
            canvas.drawText(timeText, getWidth() / 2, (getHeight() / 2) + 120, levelTextPaint);
        }

        canvas.drawText("Score: " + score, getWidth() / 2, getGameHeight() + 70, scoreTextPaint);

        leftButton.draw(canvas);
        rightButton.draw(canvas);
        shootButton.draw(canvas);
        player.draw(canvas);

        checkCollisions();

        i++;

        for (Opponent opponent : opponents)
        {
            randomiseOpponent(opponent, canvas);
            incrementSpeed(opponent);
        }

        incrementSpeed(player);

        if (player.isMovingHorizontally())
        {
            player.moveHorizontally(this);
            if (!player.getVolleyball().isMovingVertically())
                player.getVolleyball().reinitialise();
        }

        if (player.getVolleyball().isMovingVertically())
        {
            player.getVolleyball().draw(canvas);
            player.getVolleyball().moveVertically(this);
            player.getVolleyball().spinBall();
        }
    }
}
