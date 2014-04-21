package com.k.blockout;

import android.graphics.Paint;
import android.graphics.Rect;

import com.k.blockout.graphics.Button;
import com.k.blockout.graphics.GameResources;
import com.k.blockout.graphics.Opponent;
import com.k.blockout.graphics.Player;
import com.k.blockout.util.ActionHandler;

import java.util.List;
import java.util.Random;

public class GamePanelResources
{
    // This is the main game loop thread.
    private SpikeOutGameThread thread;

    // These are the member variables for the player related graphics
    private Player player;

    // This is the list of opponents on screen you must beat
    private List<Opponent> opponents;

    // This is the control buttons
    private Button leftButton;
    private Button rightButton;
    private Button shootButton;

    // This class is responsible for handling the touch screen events
    private ActionHandler actionHandler;

    // These are the pointers used to control saving which on screen touch is tied to what action
    private int movePointer = SpikeOutGame.NoPointer;
    private int shootPointer = SpikeOutGame.NoPointer;

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

    // After the countdown timer finishes after each level, this gets set to true
    private boolean gameStarted = false;

    // Used to randomise the opponent direction
    private Random random = new Random();

    public SpikeOutGameThread getThread()
    {
        return thread;
    }

    public void setThread(SpikeOutGameThread thread)
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

    public List<Opponent> getOpponents()
    {
        return opponents;
    }

    public void setOpponents(List<Opponent> opponents)
    {
        this.opponents = opponents;
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
