package com.k.blockout;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewConfiguration;

import com.k.blockout.graphics.CollisionDetector;
import com.k.blockout.graphics.Direction;
import com.k.blockout.graphics.Opponent;
import com.k.blockout.graphics.Player;
import com.k.blockout.graphics.Volleyball;

import java.util.Random;

public class MainGamePanel extends SurfaceView implements SurfaceHolder.Callback
{
    private MainThread thread;
    private Volleyball playerVball;
    private Player player;
    private Opponent opponent1;
    private Opponent opponent2;
    private Opponent opponent3;
    private Volleyball opponentVball1;
    private Volleyball opponentVball2;
    private Volleyball opponentVball3;

    private int playerSpeed = 15;
    private int opponentSpeed = 10;

    private final static int maxSpeed = 50;
    private int maxLevel = 5;

    private int score;
    private int level = 1;

    public MainGamePanel(Context context, Bitmap playerAvatar)
    {
        super(context);

        score = 0;
        // adding the callback (this) to the surface holder to intercept events
        getHolder().addCallback(this);

        Bitmap volleyballBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ball);
        Bitmap opponent1Bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.opponent1);
        Bitmap opponent2Bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.opponent2);
        Bitmap opponent3Bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.opponent3);

        player = new Player(playerAvatar, 0, 0, playerSpeed);
        opponent1 = new Opponent(opponent1Bitmap, 0, 0, opponentSpeed);
        opponent2 = new Opponent(opponent2Bitmap, 0, 0, opponentSpeed);
        opponent3 = new Opponent(opponent3Bitmap, 0, 0, opponentSpeed);

        playerVball = new Volleyball(volleyballBitmap, 0, 0, playerSpeed, Direction.UP, player);

        opponentVball1 = new Volleyball(volleyballBitmap, 0, 0, opponentSpeed, Direction.DOWN, opponent1);
        opponentVball2 = new Volleyball(volleyballBitmap, 0, 0, opponentSpeed, Direction.DOWN, opponent2);
        opponentVball3 = new Volleyball(volleyballBitmap, 0, 0, opponentSpeed, Direction.DOWN, opponent3);

        opponent1.setVolleyball(opponentVball1);
        opponent2.setVolleyball(opponentVball2);
        opponent3.setVolleyball(opponentVball3);

        thread = new MainThread(getHolder(), this);

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

    private void reinitialise()
    {
        player.setX(getWidth() / 2);
        player.setY(getHeight() - opponent1.getHeight() / 2);

        randomiseOpponentX(opponent1);
        randomiseOpponentX(opponent2);
        randomiseOpponentX(opponent3);

        opponent1.setY(opponent1.getHeight() / 2);
        opponent2.setY(opponent1.getHeight() + (opponent2.getHeight() / 2));
        opponent3.setY(opponent1.getHeight() + opponent2.getHeight() + (opponent3.getHeight() / 2));

        playerVball.reinitialise();

        opponent1.reinitialise();
        opponent2.reinitialise();
        opponent3.reinitialise();

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

                playerVball.setMovingVertically(true);

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

        ((BlockActivity)getContext()).onLose(String.valueOf(score));
    }

    private void win()
    {
        thread.setRunning(false);

        ((BlockActivity)getContext()).onWin(String.valueOf(score));
    }

    private void checkBallCollisions(Volleyball opponentVball)
    {
        // If balls collide then clear both balls
        if (CollisionDetector.collisionDetected(playerVball, opponentVball))
        {
            score++;
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

    private void checkOpponentCollisions(Opponent opponent)
    {
        // If balls collide then clear both balls
        if (CollisionDetector.collisionDetected(playerVball, opponent))
        {
            playerVball.setMovingVertically(false);
            score = score + 5;
            opponent.setVisible(false);
        }
    }

    private void checkCollisions()
    {
        checkPlayerCollision(opponentVball1);
        checkPlayerCollision(opponentVball2);
        checkPlayerCollision(opponentVball3);

        checkBallCollisions(opponentVball1);
        checkBallCollisions(opponentVball2);
        checkBallCollisions(opponentVball3);

        checkOpponentCollisions(opponent1);
        checkOpponentCollisions(opponent2);
        checkOpponentCollisions(opponent3);

        if (!opponent1.isVisible() && !opponent2.isVisible() && !opponent3.isVisible())
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
                opponent.getVolleyball().setMovingVertically(true);
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
        player.draw(canvas);

        checkCollisions();

        randomiseOpponent(opponent1, canvas);
        randomiseOpponent(opponent2, canvas);
        randomiseOpponent(opponent3, canvas);

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
