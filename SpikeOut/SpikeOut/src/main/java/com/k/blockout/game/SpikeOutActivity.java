package com.k.blockout.game;

import android.content.SharedPreferences;
import android.media.AudioManager;
import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;

import com.k.blockout.R;
import com.k.blockout.graphics.GameResources;

public class SpikeOutActivity extends ActionBarActivity implements GameListener
{
    PlaceholderFragment fragment;

    private final String HIGH_SCORE_FILENAME = "highscore";
    private final String HIGH_SCORE_KEY = "score";

    private GamePanel panel = null;
    private boolean gameOver;

    private GameResources gameResources;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        // requesting to turn the title OFF
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // making it full screen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_block);

        if (savedInstanceState == null)
        {
            fragment = new PlaceholderFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, fragment)
                    .commit();
        }

        // Initialise all the resources required by the game
        gameResources = new GameResources(this, getResources());

        // Set the volume buttons to handle the media volume instead of ringtone volume
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
    }

    public void resetView()
    {
        setContentView(R.layout.activity_block);

        PlaceholderFragment fragment = new PlaceholderFragment();

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, fragment)
                .addToBackStack(null)
                .commit();
    }

    private void showScore()
    {
        String score = readScore();
        TextView scoreDisplay = (TextView) findViewById(R.id.score_display);
        if (scoreDisplay != null)
            scoreDisplay.setText("High-score: " + score);
    }

    private void showAvatars()
    {
        ImageButton avatar1 = (ImageButton) findViewById(R.id.image_1_avatar);
        ImageButton avatar2 = (ImageButton) findViewById(R.id.image_2_avatar);

        avatar1.setImageBitmap(gameResources.getAvatar1Bitmap());
        avatar2.setImageBitmap(gameResources.getAvatar2Bitmap());

        avatar1.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                gameResources.setPlayerAvatar(gameResources.getAvatar1Bitmap());
                startGameAvatar();
            }
        });

        avatar2.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                gameResources.setPlayerAvatar(gameResources.getAvatar2Bitmap());
                startGameAvatar();
            }
        });
    }

    private String readScore()
    {
        SharedPreferences prefs =  this.getSharedPreferences(HIGH_SCORE_FILENAME, MODE_PRIVATE);

        return prefs.getString(HIGH_SCORE_KEY, "0");
    }

    private void saveScore(int score, boolean override)
    {
        String currentScore = readScore();

        // Only save score if its a new score
        if (override || (score > Integer.valueOf(currentScore)))
        {
            SharedPreferences prefs = this.getSharedPreferences(HIGH_SCORE_FILENAME, 0);
            SharedPreferences.Editor editor = prefs.edit();

            editor.putString(HIGH_SCORE_KEY, String.valueOf(score));

            editor.commit();
        }

        showScore();
    }

    @Override
    public void onLose(final int score)
    {
        System.out.println("GAMEOVER: " + isGameOver());
        runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                saveScore(score, false);

                GameOverDialog dialog = new GameOverDialog(SpikeOutActivity.this, gameResources, GameState.LOSS, score);
                dialog.buildDialog();
                dialog.show();
            }
        });
    }

    @Override
    public void onWin(final int newLevel, final int score)
    {
        runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                if (newLevel <= MaxLevel)
                {
                    panel = new SpikeOutGame(SpikeOutActivity.this);
                    panel.setupResources(score, newLevel, gameResources);
                    setContentView(panel.getGamePanelView());
                } else
                {
                    saveScore(score, false);
                    GameOverDialog dialog = new GameOverDialog(SpikeOutActivity.this, gameResources, GameState.WIN, score);
                    dialog.buildDialog();
                    dialog.show();
                }
            }
        });
    }

    @Override
    public void onResume()
    {
        super.onResume();
        // TODO
        System.out.println("RESUMING");
        System.out.println("GAMEOVER: " + isGameOver());

        if (isGameOver())
            resetView();
        else if (panel != null)
        {
            panel = new SpikeOutGame(this, panel.getGamePanelResources());
            setContentView(panel.getGamePanelView());
        }
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        if (panel != null)
        {
            panel.getGamePanelResources().getThread().setRunning(false);
        }
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        if (panel != null)
        {
            panel.getGamePanelResources().getThread().setRunning(false);
        }
    }

    public void startGameAvatar()
    {
        panel = new SpikeOutGame(this);
        setGameOver(false);
        panel.setupResources(0, 1, gameResources);
        setContentView(panel.getGamePanelView());
    }

    public void tutorial(@SuppressWarnings("unused")View view)
    {
        TutorialDialog dialog = new TutorialDialog(this, gameResources);
        dialog.buildDialog();
        dialog.show();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        if (panel != null)
        {
            panel.getGamePanelResources().getThread().setRunning(false);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.block, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        return id == R.id.action_settings || super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment
    {

        public PlaceholderFragment()
        {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
        {
            return inflater.inflate(R.layout.fragment_block, container, false);
        }

        @Override
        public void onActivityCreated(Bundle savedInstanceState)
        {
            super.onActivityCreated(savedInstanceState);
            ((SpikeOutActivity)getActivity()).showScore();
            ((SpikeOutActivity)getActivity()).showAvatars();
        }
    }

    public boolean isGameOver()
    {
        return gameOver;
    }

    public void setGameOver(boolean gameOver)
    {
        this.gameOver = gameOver;
    }
}
