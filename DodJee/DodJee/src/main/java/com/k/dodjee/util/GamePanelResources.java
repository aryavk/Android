package com.k.dodjee.util;

import android.graphics.Paint;
import android.graphics.Rect;

import com.k.dodjee.game.DodJeeGame;
import com.k.dodjee.game.GameThread;
import com.k.dodjee.graphics.Button;
import com.k.dodjee.graphics.GameResources;
import com.k.dodjee.graphics.Opponent;
import com.k.dodjee.graphics.Player;
import com.k.dodjee.graphics.PlayerLibero;
import com.k.dodjee.graphics.PowerUp;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GamePanelResources
{
    // This is the main game loop thread.
    private GameThread thread;

    // These are the member variables for the player related graphics
    private Player player;

    private PlayerLibero playerLibero;

    // This is the list of opponents on screen you must beat
    private List<Opponent> opponents;

    // This is the list of falling power ups (not ones that are active)
    private List<PowerUp> powerUps = new ArrayList<PowerUp>();

    // This is the list of power ups that are active for the player.
    private List<PowerUp> activePowerUps = new ArrayList<PowerUp>();

    // This is the control buttons
    private Button leftButton;
    private Button rightButton;
    private Button shootButton;

    // This class is responsible for handling the touch screen events
    private ActionHandler actionHandler;

    // These are the pointers used to control saving which on screen touch is tied to what action
    private int movePointer = DodJeeGame.NoPointer;
    private int shootPointer = DodJeeGame.NoPointer;

    private int score;
    private int level;

    // This is the game height, after the button bane has been set
    private int gameHeight;

    // Game resources are initialised and controlled from here
    private GameResources resources;

    // The various drawn rectangles on screen for the background, button pane, etc
    private Rect courtPane;
    private Rect courtInitialPane;
    private Rect buttonPane;
    private Paint buttonPanePaint = new Paint();
    private Paint levelTextPaint = new Paint();
    private Paint scoreTextPaint = new Paint();
    private String levelText = "";
    private String timeText = "";
    private String powerUpText = "";

    // After the countdown timer finishes after each level, this gets set to true
    private boolean gameStarted = false;

    // A flag to indicate how long we should display the power up text for
    private boolean displayPowerUpText = false;

    private int displayPowerUpFrameRemaining = 0;

    // Used to randomise the opponent direction
    private Random random = new Random();

    public GameThread getThread()
    {
        return thread;
    }

    public void setThread(GameThread thread)
    {
        this.thread = thread;
    }

    public Player getPlayer()
    {
        return player;
    }

    public void setPlayer(Player player)
    {
        this.player = player;
    }

    public PlayerLibero getPlayerLibero()
    {
        return playerLibero;
    }

    public void setPlayerLibero(PlayerLibero playerLibero)
    {
        this.playerLibero = playerLibero;
    }

    public List<Opponent> getOpponents()
    {
        return opponents;
    }

    public void setOpponents(List<Opponent> opponents)
    {
        this.opponents = opponents;
    }

    public List<PowerUp> getPowerUps()
    {
        return powerUps;
    }

    public void setPowerUps(List<PowerUp> powerUps)
    {
        this.powerUps = powerUps;
    }

    public List<PowerUp> getActivePowerUps()
    {
        return activePowerUps;
    }

    public Button getLeftButton()
    {
        return leftButton;
    }

    public void setLeftButton(Button leftButton)
    {
        this.leftButton = leftButton;
    }

    public Button getRightButton()
    {
        return rightButton;
    }

    public void setRightButton(Button rightButton)
    {
        this.rightButton = rightButton;
    }

    public Button getShootButton()
    {
        return shootButton;
    }

    public void setShootButton(Button shootButton)
    {
        this.shootButton = shootButton;
    }

    public ActionHandler getActionHandler()
    {
        return actionHandler;
    }

    public void setActionHandler(ActionHandler actionHandler)
    {
        this.actionHandler = actionHandler;
    }

    public int getMovePointer()
    {
        return movePointer;
    }

    public void setMovePointer(int movePointer)
    {
        this.movePointer = movePointer;
    }

    public int getShootPointer()
    {
        return shootPointer;
    }

    public void setShootPointer(int shootPointer)
    {
        this.shootPointer = shootPointer;
    }

    public int getScore()
    {
        return score;
    }

    public void setScore(int score)
    {
        this.score = score;
    }

    public int getLevel()
    {
        return level;
    }

    public void setLevel(int level)
    {
        this.level = level;
    }

    public int getGameHeight()
    {
        return gameHeight;
    }

    public void setGameHeight(int gameHeight)
    {
        this.gameHeight = gameHeight;
    }

    public GameResources getGameResources()
    {
        return resources;
    }

    public void setGameResources(GameResources resources)
    {
        this.resources = resources;
    }

    public Rect getCourtPane()
    {
        return courtPane;
    }

    public void setCourtPane(Rect courtPane)
    {
        this.courtPane = courtPane;
    }

    public Rect getCourtInitialPane()
    {
        return courtInitialPane;
    }

    public void setCourtInitialPane(Rect courtInitialPane)
    {
        this.courtInitialPane = courtInitialPane;
    }

    public Rect getButtonPane()
    {
        return buttonPane;
    }

    public void setButtonPane(Rect buttonPane)
    {
        this.buttonPane = buttonPane;
    }

    public Paint getButtonPanePaint()
    {
        return buttonPanePaint;
    }

    public Paint getLevelTextPaint()
    {
        return levelTextPaint;
    }

    public Paint getScoreTextPaint()
    {
        return scoreTextPaint;
    }

    public String getLevelText()
    {
        return levelText;
    }

    public void setLevelText(String levelText)
    {
        this.levelText = levelText;
    }

    public String getTimeText()
    {
        return timeText;
    }

    public void setTimeText(String timeText)
    {
        this.timeText = timeText;
    }

    public String getPowerUpText()
    {
        return powerUpText;
    }

    public void setPowerUpText(String powerUpText)
    {
        this.powerUpText = powerUpText;
    }

    public boolean isDisplayPowerUpText()
    {
        return displayPowerUpText;
    }

    public void setDisplayPowerUpText(boolean displayPowerUpText)
    {
        this.displayPowerUpText = displayPowerUpText;
    }

    public int getDisplayPowerUpFrameRemaining()
    {
        return displayPowerUpFrameRemaining;
    }

    public void setDisplayPowerUpFrameRemaining(int displayPowerUpFrameRemaining)
    {
        this.displayPowerUpFrameRemaining = displayPowerUpFrameRemaining;
    }

    public boolean isGameStarted()
    {
        return gameStarted;
    }

    public void setGameStarted(boolean gameStarted)
    {
        this.gameStarted = gameStarted;
    }

    public Random getRandom()
    {
        return random;
    }
}
