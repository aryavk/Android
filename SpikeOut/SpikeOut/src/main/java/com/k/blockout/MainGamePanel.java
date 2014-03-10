package com.k.blockout;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.CountDownTimer;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewConfiguration;

import com.k.blockout.graphics.CollisionDetector;
import com.k.blockout.graphics.Direction;
import com.k.blockout.graphics.Opponent;
import com.k.blockout.graphics.Player;
import com.k.blockout.graphics.Volleyball;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainGamePanel extends SurfaceView implements SurfaceHolder.Callback
{
    private MainThread thread;
    private Volleyball playerVball;
    private Player player;
    private int playerSpeed = 15;
    private int opponentSpeed = 15;

    private List<Opponent> opponents;

    private final static int maxSpeed = 50;

    private int score;
    private int level;

    private Paint paint = new Paint();

    public MainGamePanel(Context context, Bitmap playerAvatar, int score, int level)
    {
        super(context);

        this.level = level;
        this.score = score;

        Opponent opponent1;
        Opponent opponent2;
        Opponent opponent3;
        Opponent opponent4;

        Volleyball opponentVball1;
        Volleyball opponentVball2;
        Volleyball opponentVball3;
        Volleyball opponentVball4;

        opponentSpeed = (opponentSpeed * level / 2);

        // adding the callback (this) to the surface holder to intercept events
        if (getHolder() != null)
            getHolder().addCallback(this);

        // Get the bitmap resources from drawable-mdpi
        Bitmap volleyballBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ball);
        Bitmap opponent1Bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.opponent1);
        Bitmap opponent2Bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.opponent2);
        Bitmap opponent3Bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.opponent3);
        Bitmap opponent4Bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.opponent4);

        // Create the players, opponents and balls.
        player = new Player(playerAvatar, 0, 0, playerSpeed);
        opponent1 = new Opponent(opponent1Bitmap, 0, 0, opponentSpeed);
        opponent2 = new Opponent(opponent2Bitmap, 0, 0, opponentSpeed);
        opponent3 = new Opponent(opponent3Bitmap, 0, 0, opponentSpeed);
        opponent4 = new Opponent(opponent4Bitmap, 0, 0, opponentSpeed);

        playerVball = new Volleyball(volleyballBitmap, 0, 0, playerSpeed, Direction.UP, player);

        opponentVball1 = new Volleyball(volleyballBitmap, 0, 0, opponentSpeed, Direction.DOWN, opponent1);
        opponentVball2 = new Volleyball(volleyballBitmap, 0, 0, opponentSpeed, Direction.DOWN, opponent2);
        opponentVball3 = new Volleyball(volleyballBitmap, 0, 0, opponentSpeed, Direction.DOWN, opponent3);
        opponentVball4 = new Volleyball(volleyballBitmap, 0, 0, opponentSpeed, Direction.DOWN, opponent3);

        // Assign the opponent ball to each opponent
        opponent1.setVolleyball(opponentVball1);
        opponent2.setVolleyball(opponentVball2);
        opponent3.setVolleyball(opponentVball3);
        opponent4.setVolleyball(opponentVball4);

        thread = new MainThread(getHolder(), this);

        opponents = new ArrayList<Opponent>();
        opponents.add(opponent1);
        opponents.add(opponent2);


        if (level < 3)
        {
            // ignore
        }
        else if (level < 5)
        {
            opponents.add(opponent3);
        }
        else
        {
            opponents.add(opponent4);
        }

        // Initialise the paint settings for the level starting text displaying time and level
        paint.setTextSize(120);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setColor(Color.CYAN);

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

    private boolean gameStarted = false;
    private String levelText = "";
    private String timeText = "";

    private void reinitialise()
    {
        levelText = "Level " + level;

        new CountDownTimer(3000,1000)
        {
            @Override
            public void onTick(long timeLeft)
            {
                timeText = "Time Left: " + String.valueOf(timeLeft / 1000);
                invalidate();
            }

            @Override
            public void onFinish()
            {
                timeText = "START";
                gameStarted = true;
            }
        }.start();

        player.setX(getWidth() / 2);
        player.setY(getHeight() - player.getHeight() / 2);

        for (Opponent opponent : opponents)
        {
            randomiseOpponentX(opponent);
            opponent.setY((opponents.indexOf(opponent) * opponent.getHeight()) + opponent.getHeight() / 2);
            opponent.reinitialise();
        }

        playerVball.reinitialise();

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

    // This is a variable to help determine double touch when wanting to shoot a ball
    private long lastTouchTime = -1;

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        float eventX = event.getX();

        if (event.getAction() == MotionEvent.ACTION_DOWN)
        {
            long thisTime = System.currentTimeMillis();

            if (thisTime - lastTouchTime < ViewConfiguration.getDoubleTapTimeout())
            {
                lastTouchTime = -1;

                if (!playerVball.isMovingVertically())
                    playerVball.setX(player.getX());

                playerVball.setMovingVertically(isGameStarted());

            }
            else
            {
                lastTouchTime = thisTime;
                player.setMovingHorizontally(true);
                player.setMoveToX(eventX);
            }
        }
        else if (event.getAction() == MotionEvent.ACTION_UP)
        {
            player.setMovingHorizontally(false);
        }
        else if (event.getAction() == MotionEvent.ACTION_MOVE)
        {
            player.setMovingHorizontally(true);
            player.setMoveToX(eventX);
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
        if (CollisionDetector.collisionDetected(playerVball, opponentVball))
        {
            playerVball.setMovingVertically(false);
            opponentVball.setMovingVertically(false);

            playerVball.reinitialise();
            opponentVball.reinitialise();
        }
    }

    private void checkPlayerCollision(Volleyball opponentVball)
    {
        if (CollisionDetector.collisionDetected(player, opponentVball))
        {
            lose();
        }
    }

    private void addScore(int addedScore)
    {
        score = score + addedScore;
    }

    private void checkOpponentCollisions(Opponent opponent)
    {
        // If balls collide then clear both balls
        if (CollisionDetector.collisionDetected(playerVball, opponent))
        {
            playerVball.setMovingVertically(false);
            addScore(5);
            opponent.setVisible(false);
            opponent.getVolleyball().setVisible(false);
        }
    }

    private void checkCollisions()
    {
        boolean opponentsLeft = false;

        for (Opponent opponent : opponents)
        {
            checkPlayerCollision(opponent.getVolleyball());

            checkBallCollisions(opponent.getVolleyball());

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
            int newX = random.nextInt((getWidth() - 60) - 60) + 60;
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

            opponent.getVolleyball().setSpeed(opponentSpeed);
            opponent.setSpeed(opponentSpeed);

            if (opponent.isMovingHorizontally())
            {
                opponent.moveHorizontally(this);
                if (!opponent.getVolleyball().isMovingVertically())
                    opponent.getVolleyball().reinitialise();
            }
        }
    }

    private void incrementSpeed()
    {
        if (i % 100 == 0)
        {
            if (playerSpeed < maxSpeed)
                playerSpeed++;
            if (opponentSpeed < (maxSpeed * 2))
                opponentSpeed++;
        }
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        canvas.drawColor(Color.BLACK);
        if (!isGameStarted())
        {
            canvas.drawText(levelText, getWidth() / 2, (getHeight() / 2) - 120, paint);
            canvas.drawText(timeText, getWidth() / 2, (getHeight() / 2) + 120, paint);
        }
        player.draw(canvas);

        checkCollisions();

        for (Opponent opponent : opponents)
        {
            randomiseOpponent(opponent, canvas);
        }

        playerVball.setSpeed(playerSpeed);
        player.setSpeed(playerSpeed);

        i++;

        incrementSpeed();

        if (player.isMovingHorizontally())
        {
            player.moveHorizontally(this);
            if (!playerVball.isMovingVertically())
                playerVball.reinitialise();
        }

        if (playerVball.isMovingVertically())
        {
            playerVball.draw(canvas);
            playerVball.moveVertically(this);
            playerVball.spinBall();
        }
    }
}
