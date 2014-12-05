package com.k.blockout.game;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.k.blockout.R;
import com.k.blockout.graphics.GameResources;

import java.util.ArrayList;
import java.util.List;

public class TutorialDialog extends Dialog implements GameDialog
{
    List<TutorialDialogScreens> screens = new ArrayList<TutorialDialogScreens>();

    private View tutorialDialogView;
    private GameResources gameResources;
    private TutorialDialogScreens currentScreen = TutorialDialogScreens.AVATAR;

    protected TutorialDialog(Context context, GameResources gameResources)
    {
        super(context);
        this.gameResources = gameResources;
    }

    private void initialiseScreenOrder()
    {
        screens.add(TutorialDialogScreens.AVATAR);
        screens.add(TutorialDialogScreens.LEFT);
        screens.add(TutorialDialogScreens.RIGHT);
        screens.add(TutorialDialogScreens.SHOOT);
        screens.add(TutorialDialogScreens.OPPONENT);
        screens.add(TutorialDialogScreens.DODGE);
        screens.add(TutorialDialogScreens.LOSE);
        screens.add(TutorialDialogScreens.POWER_UP_1);
        screens.add(TutorialDialogScreens.POWER_UP_2);
        screens.add(TutorialDialogScreens.SCORE);
        screens.add(TutorialDialogScreens.GAMEPLAY);
    }

    public View getView()
    {
        return tutorialDialogView;
    }

    public void buildDialog()
    {
        initialiseScreenOrder();
        setCancelable(false);

        tutorialDialogView = View.inflate(getContext(), R.layout.tutorial_dialog_view, null);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(getView());

        ImageButton nextButton = (ImageButton) getView().findViewById(R.id.tutorial_next_button);
        nextButton.setOnClickListener(new android.view.View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                getNextScreen();
            }
        });

        ImageButton prevButton = (ImageButton) getView().findViewById(R.id.tutorial_prev_button);
        prevButton.setOnClickListener(new android.view.View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                getPrevScreen();
            }
        });

        ImageButton exitButton = (ImageButton) getView().findViewById(R.id.tutorial_exit_button);
        exitButton.setOnClickListener(new android.view.View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                dismiss();
            }
        });
    }

    public void show()
    {
        super.show();
        displayScreen();
    }

    private void fixTutorialDialogView(String tutorialTitleText, String tutorialText, Bitmap image)
    {
        TextView titleText = (TextView) getView().findViewById(R.id.tutorial_dialog_title_text);
        titleText.setText(tutorialTitleText);
        TextView text = (TextView) getView().findViewById(R.id.tutorial_dialog_text);
        text.setText(tutorialText);
        ImageView img = (ImageView) getView().findViewById(R.id.tutorial_dialog_image);
        if (image != null)
        {
            ViewGroup.LayoutParams params = img.getLayoutParams();
            if (params != null)
            {
                // Lets scale the image to 1.8x in the tutorial screen
                /*params.width = (int) (image.getWidth() * 1.8);
                params.height = (int) (image.getHeight() * 1.8);*/
                params.width = 150;
                params.height = 150;
            }
        }
        img.setImageBitmap(image);
    }

    public void getNextScreen()
    {
        int index = screens.indexOf(currentScreen);

        if (index != screens.size() - 1)
            currentScreen = screens.get(index + 1);
        else
            currentScreen = screens.get(0);

        displayScreen();
    }

    public void getPrevScreen()
    {
        int index = screens.indexOf(currentScreen);

        if (index != 0)
            currentScreen = screens.get(index - 1);
        else
            currentScreen = screens.get(screens.size() - 1);

        displayScreen();
    }

    private void displayScreen()
    {
        if (currentScreen.equals(TutorialDialogScreens.AVATAR))
            tutorialAvatarDialog();
        else if (currentScreen.equals(TutorialDialogScreens.LEFT))
            tutorialMoveLeftDialog();
        else if (currentScreen.equals(TutorialDialogScreens.RIGHT))
            tutorialMoveRightDialog();
        else if (currentScreen.equals(TutorialDialogScreens.SHOOT))
            tutorialShootDialog();
        else if (currentScreen.equals(TutorialDialogScreens.GAMEPLAY))
            tutorialGameplayDialog();
        else if (currentScreen.equals(TutorialDialogScreens.OPPONENT))
            tutorialOpponentDialog();
        else if (currentScreen.equals(TutorialDialogScreens.DODGE))
            tutorialDodgeDialog();
        else if (currentScreen.equals(TutorialDialogScreens.LOSE))
            tutorialLoseDialog();
        else if (currentScreen.equals(TutorialDialogScreens.POWER_UP_1))
            tutorialPowerUp1Dialog();
        else if (currentScreen.equals(TutorialDialogScreens.POWER_UP_2))
            tutorialPowerUp2Dialog();
        else if (currentScreen.equals(TutorialDialogScreens.SCORE))
            tutorialScoreDialog();

        if (screens.indexOf(currentScreen) == screens.size() - 1)
        {
            ImageButton nextButton = (ImageButton) getView().findViewById(R.id.tutorial_next_button);
            nextButton.setEnabled(false);
        }
        else if (screens.indexOf(currentScreen) == 0)
        {
            ImageButton prevButton = (ImageButton) getView().findViewById(R.id.tutorial_prev_button);
            prevButton.setEnabled(false);
        }
        else
        {
            ImageButton nextButton = (ImageButton) getView().findViewById(R.id.tutorial_next_button);
            nextButton.setEnabled(true);
            ImageButton prevButton = (ImageButton) getView().findViewById(R.id.tutorial_prev_button);
            prevButton.setEnabled(true);
        }
    }

    private void tutorialOpponentDialog()
    {
        String titleText = "Opponents";
        String tutorialText = "There are a number of opponents at the top of the screen. Your objective is to hit all the opponents with your balls.";
        Bitmap image = gameResources.getOpponent1Bitmap();
        fixTutorialDialogView(titleText, tutorialText, image);
    }

    private void tutorialDodgeDialog()
    {
        String titleText = "Dodge Balls";
        String tutorialText = "The opponent avatars will continuously shoot balls at you. You can break their balls by hitting the ball with one of yours.";
        Bitmap image = gameResources.getVolleyballBitmap();
        fixTutorialDialogView(titleText, tutorialText, image);
    }

    private void tutorialLoseDialog()
    {
        String titleText = "Lose";
        String tutorialText = "Avoid the opponents balls that are coming down. If the opponents ball hits you, you lose the game";
        Bitmap image = gameResources.getVolleyballBitmap();
        fixTutorialDialogView(titleText, tutorialText, image);
    }

    private void tutorialPowerUp1Dialog()
    {
        String titleText = "Power Ups";
        String tutorialText = "If you break an opponent ball with one of yours, there is a chance for a power up to come. To acquire the power up make sure to catch the falling power up bar.";
        Bitmap image = gameResources.getPowerUpBitmap();
        fixTutorialDialogView(titleText, tutorialText, image);
    }

    private void tutorialPowerUp2Dialog()
    {
        String titleText = "Power Ups";
        String tutorialText = "Power Ups only apply for the level you are currently on.";
        Bitmap image = gameResources.getPowerUpBitmap();
        fixTutorialDialogView(titleText, tutorialText, image);
    }

    private void tutorialScoreDialog()
    {
        String titleText = "Scoring";
        String tutorialText = "Each opponent you hit scores you 5 points. Every level you clear scores you 5 * (level).";
        fixTutorialDialogView(titleText, tutorialText, null);
    }

    private void tutorialAvatarDialog()
    {
        String titleText = "Avatar";
        String tutorialText = "Select an avatar to start the game. This is the player that will throw balls, or be hit";
        Bitmap image = gameResources.getAvatar1Bitmap();
        fixTutorialDialogView(titleText, tutorialText, image);
    }

    private void tutorialMoveLeftDialog()
    {
        String titleText = "Movement";
        String tutorialText = "To move your avatar left on screen, click this button";
        Bitmap image = gameResources.getLeftButtonBitmap();
        fixTutorialDialogView(titleText, tutorialText, image);
    }

    private void tutorialMoveRightDialog()
    {
        String titleText = "Movement";
        String tutorialText = "To move your avatar right on screen, click this button";
        Bitmap image = gameResources.getRightButtonBitmap();
        fixTutorialDialogView(titleText, tutorialText, image);
    }

    private void tutorialShootDialog()
    {
        String titleText = "Shooting";
        String tutorialText = "To shoot a volleyball from your avatar, click this button";
        Bitmap image = gameResources.getShootButtonBitmap();
        fixTutorialDialogView(titleText, tutorialText, image);
    }

    private void tutorialGameplayDialog()
    {
        String titleText = "Gameplay";
        String tutorialText = "You can only spike one ball at a time. Remember, avoid the incoming balls!";
        Bitmap image = gameResources.getVolleyballBitmap();
        fixTutorialDialogView(titleText, tutorialText, image);
    }
}
