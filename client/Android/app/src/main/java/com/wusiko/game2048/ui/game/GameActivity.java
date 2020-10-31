package com.wusiko.game2048.ui.game;

import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.wusiko.game2048.R;
import com.wusiko.game2048.data.game.CreatedTileLink;
import com.wusiko.game2048.data.game.MergedTileLink;
import com.wusiko.game2048.data.game.MovedTileLink;
import com.wusiko.game2048.ui.utils.OnSwipeTouchListener;

import java.util.List;
import java.lang.Math;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class GameActivity extends AppCompatActivity
{
	private final String TAG = "GameActivity";
	private GameBoardViewModel mGameBoardViewModel;
	private GameBoardView mGameBoardView;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_game);
		mGameBoardViewModel = new ViewModelProvider(this, new GameBoardViewModelFactory()).get(GameBoardViewModel.class);

		getWindow().getDecorView().setOnTouchListener(new OnSwipeTouchListener(this)
		{
			@Override
			public void onSwipeBottom()
			{
				mGameBoardViewModel.OnMoveDown();
			}

			@Override
			public void onSwipeRight()
			{
				mGameBoardViewModel.OnMoveRight();
			}

			@Override
			public void onSwipeLeft()
			{
				mGameBoardViewModel.OnMoveLeft();
			}

			@Override
			public void onSwipeTop()
			{
				mGameBoardViewModel.OnMoveUp();
			}
		});

		mGameBoardViewModel.GetCreatedTiles().observe(this, new Observer<List<CreatedTileLink>>()
		{
			@Override
			public void onChanged(List<CreatedTileLink> createdTileLinks)
			{
				for (CreatedTileLink link : createdTileLinks)
				{
					mGameBoardView.CreateTile(link);
				}
			}
		});

		mGameBoardViewModel.GetMergedTiles().observe(this, new Observer<List<MergedTileLink>>()
		{
			@Override
			public void onChanged(List<MergedTileLink> mergedTileLinks)
			{
				for (MergedTileLink link : mergedTileLinks)
				{
					int firstTileValue = link.GetFirstTile().getValue();
					int secondTileValue = link.GetSecondTile().getValue();
					int resultTileValue = link.GetFirstTile().getValue();

					int[] firstTilePos = link.GetFirstTile().getPosition();
					int[] secondTilePos = link.GetSecondTile().getPosition();
					int[] resultPos = link.GetResultTile().getPosition();
					Log.i(TAG, String.format("Tiles merged: %d (%d; %d) -> %d (%d; %d) = %d (%d; %d)",
							firstTileValue, firstTilePos[0], firstTilePos[1],
							secondTileValue, secondTilePos[0], secondTilePos[1],
							resultTileValue, resultPos[0], resultPos[1]));

					// todo: animate it
				}
			}
		});

		mGameBoardViewModel.GetMovedTiles().observe(this, new Observer<List<MovedTileLink>>()
		{
			@Override
			public void onChanged(List<MovedTileLink> movedTileLinks)
			{
				for (MovedTileLink link : movedTileLinks)
				{
					int[] from = link.GetPositionFrom();
					int[] to = link.GetTile().getPosition();
					int value = link.GetTile().getValue();
					Log.i(TAG, String.format("Tile moved: %d (%d; %d) -> %d (%d; %d)",
							value, from[0], from[1],
							value, to[0], to[1]));

					// todo: animate it
				}
			}
		});

		FrameLayout boardLayout = findViewById(R.id.boardLayout);
		mGameBoardView = new GameBoardView(this, boardLayout);

		// Apply resizing only when view will be created:
		FrameLayout boardSizeHelperLayout = findViewById(R.id.boardSizeHelperLayout);
		boardSizeHelperLayout.post(new Runnable()
		{
			@Override
			public void run()
			{
				ResizeBoardView();
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

		mGameBoardViewModel.OnRestart();
	}

	@Override
	public void onConfigurationChanged(@NonNull Configuration newConfig)
	{
		super.onConfigurationChanged(newConfig);

		if (mGameBoardView != null)
		{
			ResizeBoardView();
		}
	}

	private void ResizeBoardView()
	{
		FrameLayout boardSizeHelperLayout = findViewById(R.id.boardSizeHelperLayout);
		int w = boardSizeHelperLayout.getWidth();
		int h = boardSizeHelperLayout.getHeight();
		int size = Math.min(w, h);
		FrameLayout boardLayout = findViewById(R.id.boardLayout);
		boardLayout.setLayoutParams(new FrameLayout.LayoutParams(size, size));
		mGameBoardView.OnResized();
	}

	@Override
	protected void onStart()
	{
		super.onStart();
		getWindow().getDecorView().setSystemUiVisibility(getWindow().getDecorView().getSystemUiVisibility()
				| View.SYSTEM_UI_FLAG_LOW_PROFILE
				| View.SYSTEM_UI_FLAG_FULLSCREEN
				| View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
	}

}