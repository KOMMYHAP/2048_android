package com.wusiko.game2048.ui.game;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

import com.wusiko.game2048.R;
import com.wusiko.game2048.data.game.CreatedTileLink;
import com.wusiko.game2048.data.game.GameConfig;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

@SuppressLint("ViewConstructor")
public class GameBoardView extends View
{
	private final String TAG = "GameBoardView";
	private final HashMap<Integer, GameTileView> mTileViewsByIndex = new HashMap<>(GameConfig.TILES_NUMBER);
	private final FrameLayout mLayout;
	private final Context mContext;
	private int mTileSize;
	private int mTileMargin;

	public GameBoardView(Context context, FrameLayout layout)
	{
		super(context);
		mContext = context;
		mLayout = layout;
	}

	public void OnResized()
	{
		final int boardSize = mLayout.getLayoutParams().width;

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
		mTileSize = Math.max((int)(density * 64), tileSize);
		mTileMargin = Math.max((int)(density * 8), mTileSize / 8);

		Log.d(TAG, String.format("OnResize: \n" +
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
		mLayout.addView(tileView);

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

