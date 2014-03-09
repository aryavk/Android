package com.k.blockout;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class BlockActivity extends ActionBarActivity implements GameListener
{
    private static final String TAG = BlockActivity.class.getSimpleName();
    private AlertDialog alertDialog;
    PlaceholderFragment fragment;

    private final String HIGH_SCORE_FILENAME = "highscore";
    private final String HIGH_SCORE_KEY = "score";

    private View dialogView;
    private MainGamePanel panel = null;
    private Bitmap selectedAvatar;

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

        Log.d(TAG, "View added");

        buildDialog(this);
    }

    private void resetView()
    {
        setContentView(R.layout.activity_block);

        PlaceholderFragment fragment = new PlaceholderFragment();

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, fragment)
                .addToBackStack(null)
                .commit();
    }

    private void buildDialog(Context context)
    {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

        alertDialogBuilder
                .setCancelable(false)
                .setNeutralButton("OK", new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int id)
                    {
                        // if this button is clicked, close
                        // current activity
                        dialog.dismiss();
                        resetView();
                    }
                });

        // create alert dialog
        alertDialog = alertDialogBuilder.create();

        dialogView = View.inflate(BlockActivity.this, R.layout.dialog_view, null);
        alertDialog.setView(dialogView);
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

        final Bitmap avatar1Bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.avatar1);
        final Bitmap avatar2Bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.avatar2);

        avatar1.setImageBitmap(avatar1Bitmap);
        avatar2.setImageBitmap(avatar2Bitmap);

        avatar1.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                selectedAvatar = avatar1Bitmap;
                startGameAvatar(selectedAvatar);
            }
        });

        avatar2.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                selectedAvatar = avatar2Bitmap;
                startGameAvatar(selectedAvatar);
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
        runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                saveScore(score, false);
                alertDialog.setTitle("Loser");

                final Bitmap sixPack = BitmapFactory.decodeResource(getResources(), R.drawable.six_pack);
                TextView text = (TextView) dialogView.findViewById(R.id.dialog_text);
                text.setText("You Lost!\nScore: " + score);
                ImageView img = (ImageView) dialogView.findViewById(R.id.dialog_image);
                img.setImageBitmap(sixPack);

                alertDialog.show();
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
                    panel = new MainGamePanel(BlockActivity.this, selectedAvatar, score, newLevel);
                    setContentView(panel);
                }
                else
                {
                    saveScore(score, false);
                    alertDialog.setTitle("Winner");

                    final Bitmap medal = BitmapFactory.decodeResource(getResources(), R.drawable.medal);
                    TextView text = (TextView) dialogView.findViewById(R.id.dialog_text);
                    text.setText("You Win!\nScore: " + score);
                    ImageView img = (ImageView) dialogView.findViewById(R.id.dialog_image);
                    img.setImageBitmap(medal);

                    alertDialog.show();
                }
            }
        });
    }

    /*@Override
    public void onBackPressed()
    {
        super.onBackPressed();
        panel.getThread().setRunning(false);
    }*/

    @Override
    protected void onPause()
    {
        super.onPause();
        if (panel != null)
        {
            panel.getThread().setRunning(false);
        }
    }

    public void startGameAvatar(Bitmap bmp)
    {
        panel = new MainGamePanel(this, bmp, 0, 1);
        setContentView(panel);
    }

    public void clearScore(@SuppressWarnings("unused")View view)
    {
        saveScore(0, true);
    }

    @Override
    protected void onDestroy()
    {
        Log.d(TAG, "Destroying...");
        super.onDestroy();
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        if (panel != null)
        {
            panel.getThread().setRunning(false);
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
            ((BlockActivity)getActivity()).showScore();
            ((BlockActivity)getActivity()).showAvatars();
        }
    }

}
