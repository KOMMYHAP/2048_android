package com.wusiko.game2048.ui.game;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.wusiko.game2048.R;
import com.wusiko.game2048.data.game.CreatedTileLink;
import com.wusiko.game2048.data.game.GameConfig;
import com.wusiko.game2048.data.game.MergedTileLink;
import com.wusiko.game2048.data.game.MovedTileLink;
import com.wusiko.game2048.ui.utils.OnSwipeTouchListener;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;

@SuppressLint("ViewConstructor")
public class GameBoardView extends View
{
	private final String TAG = "GameBoardView";
	private final HashMap<Integer, GameTileView> mTileViewsByIndex = new HashMap<>(GameConfig.TILES_NUMBER);
	private final FrameLayout mBoardLayout;
	private final FrameLayout mSizeHelperLayout;
	private final Context mContext;
	private final GameBoardViewModel mGameBoardViewModel;
	private int mTileSize;
	private int mTileMargin;

	public GameBoardView(AppCompatActivity activity)
	{
		super(activity);
		mContext = activity;
		mBoardLayout = activity.findViewById(R.id.boardLayout);
		mSizeHelperLayout = activity.findViewById(R.id.boardSizeHelperLayout);
		mGameBoardViewModel = new ViewModelProvider(activity, new GameBoardViewModelFactory()).get(GameBoardViewModel.class);

		mSizeHelperLayout.post(new Runnable()
		{
			@Override
			public void run()
			{
				Resize();
			}
		});

		activity.getWindow().getDecorView().setOnTouchListener(new OnSwipeTouchListener(mContext)
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

		mGameBoardViewModel.GetCreatedTiles().observe(activity, new Observer<List<CreatedTileLink>>()
		{
			@Override
			public void onChanged(List<CreatedTileLink> createdTileLinks)
			{
				for (CreatedTileLink link : createdTileLinks)
				{
					CreateTile(link);
				}
			}
		});

		mGameBoardViewModel.GetMergedTiles().observe(activity, new Observer<List<MergedTileLink>>()
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

		mGameBoardViewModel.GetMovedTiles().observe(activity, new Observer<List<MovedTileLink>>()
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

		mGameBoardViewModel.OnRestart();
	}

	public void Resize()
	{
		int w = mSizeHelperLayout.getWidth();
		int h = mSizeHelperLayout.getHeight();
		int size = Math.min(w, h);
		mBoardLayout.setLayoutParams(new FrameLayout.LayoutParams(size, size));

		final int boardSize = mBoardLayout.getLayoutParams().width;

		// tile_margin + tile_n_in_a_row * (tile_margin + tile_size) = board_size
		// tile_margin = tile_size / 8
		//   =>
		// tile_size / 8 + tile_n_in_a_row * (9/8 * tile_size) = board_size
		//   =>
		// tile_size + 9 * tile_n_in_a_row * tile_size = 8 * board_size
		//   =>
		// tile_sze = 8 * board_size / (1 + 9 * tile_n_in_a_row)
		//   =>
		final int tileSize = 8 * boardSize / (1 + 9 * GameConfig.TILES_IN_A_ROW);

		final float density = getResources().getDisplayMetrics().density;
		mTileSize = Math.max((int) (density * 64), tileSize);
		mTileMargin = Math.max((int) (density * 8), mTileSize / 8);

		Log.d(TAG, String.format(
				"OnResize: \n" +
						"    board size = %d\n" +
						"    tile size = %d\n" +
						"    tile margin = %d\n",
				boardSize,
				mTileSize,
				mTileMargin));
	}

	public void CreateTile(@NotNull CreatedTileLink link)
	{
		final int[] boardPos = link.GetTile().getPosition();
		final int index = ToMappedIndex(boardPos[0], boardPos[1]);
		if (mTileViewsByIndex.containsKey(index))
		{
			throw new RuntimeException(String.format("Cannot create tile: position (%d; %d) is busy!", boardPos[0], boardPos[1]));
		}

		GameTileView tileView = new GameTileView(mContext);
		tileView.setLayoutParams(new FrameLayout.LayoutParams(mTileSize, mTileSize));
		tileView.setImageResource(R.drawable.test);
		tileView.SetTile(link.GetTile());

		final int[] screenPos = ToScreenPosition(boardPos[0], boardPos[1]);
		tileView.setTranslationX(screenPos[0]);
		tileView.setTranslationY(screenPos[1]);

		mTileViewsByIndex.put(index, tileView);
		mBoardLayout.addView(tileView);

		Log.d(TAG, String.format(
				"Tile created: \n" +
						"    value = %d, \n" +
						"    board pos = (%d; %d), \n" +
						"    screen pos = (%d; %d).",
				link.GetTile().getValue(),
				boardPos[0], boardPos[1],
				screenPos[0], screenPos[1]));
	}

	private static int ToMappedIndex(int x, int y)
	{
		return x + y * GameConfig.TILES_IN_A_ROW;
	}

	@NotNull
	@Contract(value = "_, _ -> new", pure = true)
	private int[] ToScreenPosition(int x, int y)
	{
		return new int[]{
				mTileMargin + x * (mTileSize + mTileMargin),
				mTileMargin + y * (mTileSize + mTileMargin)
		};
	}
}

