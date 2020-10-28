package com.wusiko.game2048.ui.game;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.wusiko.game2048.R;
import com.wusiko.game2048.ui.utils.OnSwipeTouchListener;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class GameActivity extends AppCompatActivity {
    private GameViewModel mGameViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        mGameViewModel = new ViewModelProvider(this, new GameViewModelFactory()).get(GameViewModel.class);

        getWindow().getDecorView().setOnTouchListener(new OnSwipeTouchListener(this)
        {
            @Override
            public void onSwipeBottom()
            {
                mGameViewModel.OnMoveDown();
            }

            @Override
            public void onSwipeRight()
            {
                mGameViewModel.OnMoveRight();
            }

            @Override
            public void onSwipeLeft()
            {
                mGameViewModel.OnMoveLeft();
            }

            @Override
            public void onSwipeTop()
            {
                mGameViewModel.OnMoveUp();
            }
        });

        // Note that some of these constants are new as of API 16 (Jelly Bean)
        // and API 19 (KitKat). It is safe to use them, as they are inlined
        // at compile-time and do nothing on earlier devices.
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);

        mGameViewModel.OnRestart();
    }

    @Override
    protected void onStart() {
        super.onStart();
        getWindow().getDecorView().setSystemUiVisibility(getWindow().getDecorView().getSystemUiVisibility()
                | View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
    }

}