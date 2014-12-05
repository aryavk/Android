package com.k.blockout.game;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.CountDownTimer;
import android.view.SurfaceHolder;

import com.k.blockout.util.GamePanelResources;
import com.k.blockout.graphics.PlayerLibero;
import com.k.blockout.graphics.Power;
import com.k.blockout.graphics.PowerUp;
import com.k.blockout.util.ActionHandler;
import com.k.blockout.util.CollisionDetector;
import com.k.blockout.graphics.Opponent;
import com.k.blockout.graphics.Person;
import com.k.blockout.graphics.Volleyball;

import java.math.BigDecimal;
import java.util.ArrayList;

public class SpikeOutGame extends GamePanelImpl
{
    private final static int maxPersonSpeed = 60;
    private final static int maxBallSpeed = 80;
    private final static int powerUpSpeed = 20;

    // Used as a counter, game speed increases after some time
    private int i = 0;

    // This is used to disable movement of opponents and players when resuming the game.
    private boolean disableMovement = false;

    public SpikeOutGame(Context context)
    {
        super(context);

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
        getGamePanelResources().setThread(new GameThreadImpl(getHolder(), this));

        this.setInitialised(true);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height)
    {
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder)
    {
        initialise();
        getGamePanelResources().setActionHandler(new ActionHandler(this));
    }

    // Lets initialise all the on screen elements
    @Override
    public void initialise()
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
        else
        {
            disableMovement = true;
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
                {
                    getGamePanelResources().setTimeText("START");
                    disableMovement = false;
                }

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

    private void lose()
    {
        getGamePanelResources().getThread().setRunning(false);
        getGamePanelResources().setGameStarted(false);

        SpikeOutActivity activity = ((SpikeOutActivity)getContext());

        if (activity != null)
        {
            activity.setGameOver(true);
            activity.onLose(getGamePanelResources().getScore());
        }
    }

    private void win()
    {
        getGamePanelResources().getThread().setRunning(false);
        getGamePanelResources().setGameStarted(false);

        addScore(getGamePanelResources().getLevel() * 5);

        getGamePanelResources().setLevel(getGamePanelResources().getLevel() + 1);

        SpikeOutActivity activity = ((SpikeOutActivity)getContext());

        if (activity != null)
        {
            activity.setGameOver(true);
            activity.onWin(getGamePanelResources().getLevel(), getGamePanelResources().getScore());
        }
    }

    private boolean generatePowerUp()
    {
        int randomInt = getGamePanelResources().getRandom().nextInt(50);

        if (randomInt > 40)
        {
            if (getGamePanelResources().getPowerUps().size() == 0)
            {
                return true;
            }
        }
        return false;
    }

    // TODO THIS IS BASED ON 2 Types of powers. Will need to change the logic here to make it scalable for more than 2 powers
    private void createPowerUp(int x, int y)
    {
        if (getGamePanelResources().getPowerUps().size() == 0)
        {
            if (getGamePanelResources().getActivePowerUps().size() < Power.values().length)
            {
                Power power;
                // If there's no active power, lets randomise it, otherwise pick the opposite one to what is there
                if (getGamePanelResources().getActivePowerUps().size() == 0)
                {
                    int randomInt = getGamePanelResources().getRandom().nextInt(50);

                    if (randomInt <= 25)
                        power = Power.SUPER_THROW;
                    else
                        power = Power.LIBERO;
                }
                else
                {
                    Power activePower = getGamePanelResources().getActivePowerUps().get(0).getPower();
                    if (activePower.equals(Power.SUPER_THROW))
                        power = Power.LIBERO;
                    else
                        power = Power.SUPER_THROW;
                }

                PowerUp powerUp = new PowerUp(getGamePanelResources().getGameResources().getPowerUpBitmap(), x, y, powerUpSpeed, getGamePanelResources().getGameResources().getPowerUpImageIteration(), power);
                powerUp.setGameHeight(getGamePanelResources().getGameHeight());
                getGamePanelResources().getPowerUps().add(powerUp);
            }
        }
    }

    private void activatePowerUp(PowerUp powerUp)
    {
        // Clear the on screen power up
        getGamePanelResources().setPowerUps(new ArrayList<PowerUp>());

        // Now add the power up that the player got to the list of active power ups.
        getGamePanelResources().getActivePowerUps().add(powerUp);
        if (powerUp.getPower().equals(Power.SUPER_THROW))
        {
            getGamePanelResources().getPlayer().getVolleyball().setMaxCollissions(2);
            getGamePanelResources().setPowerUpText("SUPER THROW!");
        }

        if (powerUp.getPower().equals(Power.LIBERO))
        {
            getGamePanelResources().getPlayer().setHasLibero(true);
            getGamePanelResources().setPlayerLibero(new PlayerLibero(getGamePanelResources().getGameResources().getPlayerLiberoBitmap()));
            getGamePanelResources().getPlayerLibero().setY((getGamePanelResources().getGameHeight() - getGamePanelResources().getPlayer().getHeight()) - getGamePanelResources().getPlayer().getHeight() / 2);
            getGamePanelResources().setPowerUpText("LIBERO!");
        }

        getGamePanelResources().setDisplayPowerUpFrameRemaining(100);
        getGamePanelResources().setDisplayPowerUpText(true);
    }

    private void checkBallCollisions(Volleyball opponentVball)
    {
        // If balls collide then clear both balls
        if (CollisionDetector.collisionDetected(getGamePanelResources().getPlayer().getVolleyball(), opponentVball))
        {
            getGamePanelResources().getPlayer().getVolleyball().setCollided(true);

            if (getGamePanelResources().getPlayer().getVolleyball().isCollided())
            {
                getGamePanelResources().getPlayer().getVolleyball().setMovingVertically(false);
                getGamePanelResources().getPlayer().getVolleyball().reinitialise();
            }

            opponentVball.setMovingVertically(false);

            if (generatePowerUp())
            {
                createPowerUp(opponentVball.getX(), opponentVball.getY());
            }

            // Need to reinitialise after generating power up so power up falls at point of collision
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

    private void checkPlayerLiberoCollision(Volleyball opponentVball)
    {
        if (CollisionDetector.collisionDetected(getGamePanelResources().getPlayerLibero(), opponentVball))
        {
            getGamePanelResources().getPlayerLibero().setVisible(false);
            getGamePanelResources().getPlayer().setHasLibero(false);

            opponentVball.setMovingVertically(false);
            opponentVball.reinitialise();
        }
    }

    private void checkPowerUpCollision(PowerUp powerUp)
    {
        if (CollisionDetector.collisionDetected(getGamePanelResources().getPlayer(), powerUp))
        {
            activatePowerUp(powerUp);
        }
    }

    private void checkOpponentCollisions(Opponent opponent)
    {
        // If balls collides wih opponent, remove opponent and his ball (TODO keep the ball -> collision issues with just removing last line in block)
        if (CollisionDetector.collisionDetected(getGamePanelResources().getPlayer().getVolleyball(), opponent))
        {
            getGamePanelResources().getPlayer().getVolleyball().setCollided(true);

            if (getGamePanelResources().getPlayer().getVolleyball().isCollided())
            {
                getGamePanelResources().getPlayer().getVolleyball().setMovingVertically(false);
                getGamePanelResources().getPlayer().getVolleyball().reinitialise();
            }

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

            if (getGamePanelResources().getPlayer().isHasLibero())
            {
                if (opponent.getVolleyball().getY() - (opponent.getVolleyball().getHeight() * 2) < getGamePanelResources().getPlayerLibero().getY())
                    checkPlayerLiberoCollision(opponent.getVolleyball());
            }

            if (getGamePanelResources().getPlayer().getVolleyball().isInPath(opponent.getVolleyball()))
                checkBallCollisions(opponent.getVolleyball());

            if (getGamePanelResources().getPlayer().getVolleyball().isMovingVertically() &&
                    (getGamePanelResources().getPlayer().getVolleyball().getY() + (getGamePanelResources().getPlayer().getHeight() * 2) > opponent.getY()))
                checkOpponentCollisions(opponent);

            if (getGamePanelResources().getPowerUps().size() > 0)
            {
                PowerUp powerUp = getGamePanelResources().getPowerUps().get(0);
                if (powerUp.getY() - (powerUp.getHeight() * 2) < getGamePanelResources().getPlayer().getY())
                    checkPowerUpCollision(powerUp);
            }

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
                opponent.getVolleyball().spinGraphic();
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

    public void update(Canvas canvas)
    {
        if (!disableMovement)
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
                getGamePanelResources().getPlayer().getVolleyball().spinGraphic();
            }

            for (PowerUp powerUp : getGamePanelResources().getPowerUps())
            {
                powerUp.moveVertically(this);
                powerUp.spinGraphic();
            }

            boolean clearPowerUp = false;
            for (PowerUp powerUp : getGamePanelResources().getPowerUps())
            {
                if (!powerUp.isMovingVertically())
                    clearPowerUp = true;
            }

            if (clearPowerUp)
                getGamePanelResources().setPowerUps(new ArrayList<PowerUp>());

        }
        onDraw(canvas);
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

        if (getGamePanelResources().isDisplayPowerUpText())
        {
            canvas.drawText(getGamePanelResources().getPowerUpText(), getWidth() / 2, (getHeight() / 2), getGamePanelResources().getLevelTextPaint());
            getGamePanelResources().setDisplayPowerUpFrameRemaining(getGamePanelResources().getDisplayPowerUpFrameRemaining() - 1);
            if (getGamePanelResources().getDisplayPowerUpFrameRemaining() == 0)
                getGamePanelResources().setDisplayPowerUpText(false);
        }

        canvas.drawText("Score: " + getGamePanelResources().getScore(), getWidth() / 2, getGamePanelResources().getGameHeight() + 70, getGamePanelResources().getScoreTextPaint());

        getGamePanelResources().getLeftButton().draw(canvas);
        getGamePanelResources().getRightButton().draw(canvas);
        getGamePanelResources().getShootButton().draw(canvas);
        getGamePanelResources().getPlayer().draw(canvas);
        if (getGamePanelResources().getPlayer().isHasLibero())
        {
            getGamePanelResources().getPlayerLibero().setX(getGamePanelResources().getPlayer().getX());

            getGamePanelResources().getPlayerLibero().draw(canvas);
        }
        if (getGamePanelResources().getPlayer().getVolleyball().isMovingVertically())
        {
            getGamePanelResources().getPlayer().getVolleyball().draw(canvas);
        }

        for (PowerUp powerUp : getGamePanelResources().getPowerUps())
        {
            powerUp.draw(canvas);
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
