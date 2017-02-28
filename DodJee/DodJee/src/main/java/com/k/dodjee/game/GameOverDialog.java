package com.k.dodjee.game;

import android.app.Dialog;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.k.dodjee.R;
import com.k.dodjee.graphics.GameResources;

public class GameOverDialog extends Dialog implements GameDialog
{
    private View dialogView;
    private GameResources gameResources;
    private GameState gameState;
    private int score;
    private DodJeeActivity activity;

    protected GameOverDialog(DodJeeActivity context, GameResources gameResources, GameState gameState, int score)
    {
        super(context);
        this.activity = context;
        this.gameResources = gameResources;
        this.gameState = gameState;
        this.score = score;
    }

    public View getView()
    {
        return dialogView;
    }

    public void buildDialog()
    {
        setCancelable(false);

        dialogView = View.inflate(getContext(), R.layout.dialog_view, null);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(getView());

        ImageButton playAgainButton = (ImageButton) getView().findViewById(R.id.dialog_play_again_button);
        if (playAgainButton.getBackground() != null)
        {
            /*playAgainButton.getBackground().setColorFilter(Color.GREEN, PorterDuff.Mode.MULTIPLY);*/
        }

        playAgainButton.setOnClickListener(new android.view.View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                dismiss();
                activity.startGameAvatar();
            }
        });

        ImageButton closeButton = (ImageButton) getView().findViewById(R.id.dialog_close_button);

        if (closeButton.getBackground() != null)
        {
            /*closeButton.getBackground().setColorFilter(Color.GREEN, PorterDuff.Mode.MULTIPLY);*/
        }

        closeButton.setOnClickListener(new android.view.View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                dismiss();
                activity.resetView();
            }
        });

        if (gameState.equals(GameState.WIN))
        {
            TextView titleText = (TextView) getView().findViewById(R.id.dialog_title_text);
            titleText.setText("Victory");
            TextView text = (TextView) getView().findViewById(R.id.dialog_text);
            text.setText("You Win!\nScore: " + score);
            ImageView img = (ImageView) getView().findViewById(R.id.dialog_image);
            img.setImageBitmap(gameResources.getMedal());
        }
        else
        {
            TextView titleText = (TextView) getView().findViewById(R.id.dialog_title_text);
            titleText.setText("Defeat");
            TextView text = (TextView) getView().findViewById(R.id.dialog_text);
            text.setText("You Lost!\nScore: " + score);
            ImageView img = (ImageView) getView().findViewById(R.id.dialog_image);
            img.setImageBitmap(gameResources.getSixPack());
        }
    }

    @Override
    public void show()
    {
        super.show();
    }
}
