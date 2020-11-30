package com.wusiko.game2048.ui.game;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.wusiko.game2048.R;
import com.wusiko.game2048.data.game.CreatedTileLink;
import com.wusiko.game2048.data.game.GameConfig;
import com.wusiko.game2048.data.game.MergedTileLink;
import com.wusiko.game2048.data.game.MovedTileLink;
import com.wusiko.game2048.data.game.MovementState;
import com.wusiko.game2048.ui.utils.OnSwipeTouchListener;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressLint("ViewConstructor")
public class GameBoardView extends View
{
	private final String TAG = "GameBoardView";
	private final HashMap<Integer, GameTileView> mTileViewsByIndex = new HashMap<>(GameConfig.TILES_NUMBER);
	private final FrameLayout mBoardLayout;
	private final FrameLayout mSizeHelperLayout;
	private final TextView mScoresView;
	private final TextView mHighScoresView;
	private final Context mContext;
	private final GameBoardViewModel mGameBoardViewModel;
	private int mTileSize;
	private int mTileMargin;
	private boolean mBoardLocked = false;

	public GameBoardView(Context context)
	{
		super(context);
		mContext = context;

		if (!(context instanceof AppCompatActivity))
		{
			throw new RuntimeException("Unknown context class!");
		}

		final AppCompatActivity activity = (AppCompatActivity)(context);

		mBoardLayout = activity.findViewById(R.id.boardLayout);
		mSizeHelperLayout = activity.findViewById(R.id.boardSizeHelperLayout);
		mGameBoardViewModel = new ViewModelProvider(activity, new GameBoardViewModelFactory()).get(GameBoardViewModel.class);

		activity.getWindow().getDecorView().setOnTouchListener(new OnSwipeTouchListener(mContext)
		{
			@Override
			public void onSwipeBottom()
			{
				if (!mBoardLocked)
				{
					mGameBoardViewModel.OnMoveDown();
				}
			}

			@Override
			public void onSwipeRight()
			{
				if (!mBoardLocked)
				{
					mGameBoardViewModel.OnMoveRight();
				}
			}

			@Override
			public void onSwipeLeft()
			{
				if (!mBoardLocked)
				{
					mGameBoardViewModel.OnMoveLeft();
				}
			}

			@Override
			public void onSwipeTop()
			{
				if (!mBoardLocked)
				{
					mGameBoardViewModel.OnMoveUp();
				}
			}
		});

		mGameBoardViewModel.GetMovementState().observe(activity, new Observer<MovementState>()
		{
			@Override
			public void onChanged(MovementState movementState)
			{
				try
				{
					for (MergedTileLink link : movementState.GetTileLinks().GetMergedTiles())
					{
						MergeTile(link);
					}
					for (MovedTileLink link : movementState.GetTileLinks().GetMovedTiles())
					{
						MoveTile(link);
					}
					for (CreatedTileLink link : movementState.GetTileLinks().GetCreatedTiles())
					{
						CreateTile(link);
					}
				}
				catch (RuntimeException e)
				{
					Log.e(TAG, e.getMessage() != null ? e.getMessage() : "no error");
//					e.printStackTrace();
				}
			}
		});

		mScoresView = activity.findViewById(R.id.text_scores);
		mGameBoardViewModel.GetScores().observe(activity, new Observer<Integer>()
		{
			@Override
			public void onChanged(Integer integer)
			{
				String text = activity.getResources().getString(R.string.game_text_scores);
				mScoresView.setText(text.replace("%scores%", integer.toString()));
			}
		});

		mHighScoresView = activity.findViewById(R.id.text_high_scores);
		mGameBoardViewModel.GetHighScores().observe(activity, new Observer<Integer>()
		{
			@Override
			public void onChanged(Integer integer)
			{
				String text = activity.getResources().getString(R.string.game_text_high_scores);
				mHighScoresView.setText(text.replace("%scores%", integer.toString()));
			}
		});

		// Apply resizing only when view will be created:
		mSizeHelperLayout.post(new Runnable()
		{
			@Override
			public void run()
			{
				Resize();
				mGameBoardViewModel.OnRestart();
			}
		});
	}

	public void Resize()
	{
		final int boardSize = Math.min(mSizeHelperLayout.getWidth(), mSizeHelperLayout.getHeight());
		mBoardLayout.setLayoutParams(new FrameLayout.LayoutParams(boardSize, boardSize));

		// Algorithm to determine tile's size:
		// {
		//  tile_margin + tile_n_in_a_row * (tile_margin + tile_size) = board_size
		//  tile_margin = tile_size / 8
		// }
		//   =>
		// tile_size / 8 + tile_n_in_a_row * (9 / 8 * tile_size) = board_size
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

		for (Map.Entry<Integer, GameTileView> entry: mTileViewsByIndex.entrySet())
		{
			UpdateGeometry(entry.getValue());
		}
	}

	private void CreateTile(@NotNull CreatedTileLink link)
	{
		final int[] boardPos = link.GetTile().getPosition();
		final int index = ToMappedIndex(boardPos[0], boardPos[1]);
		if (mTileViewsByIndex.containsKey(index))
		{
			throw new RuntimeException(String.format("Cannot create tile: position (%d; %d) is busy!", boardPos[0], boardPos[1]));
		}

		GameTileView tileView = new GameTileView(mContext);
		tileView.SetTile(link.GetTile());
		UpdateGeometry(tileView);

		mBoardLayout.addView(tileView);
		mTileViewsByIndex.put(index, tileView);

		Log.d(TAG, String.format("Tile created: %d: (%d; %d)",
				link.GetTile().getValue(),
				boardPos[0], boardPos[1]));
	}

	private void MoveTile(@NotNull MovedTileLink link)
	{
		final int[] oldPos = link.GetPositionFrom();
		final int oldIndex = ToMappedIndex(oldPos[0], oldPos[1]);

		GameTileView tileView = mTileViewsByIndex.remove(oldIndex);
		if (tileView == null)
		{
			throw new RuntimeException(String.format("Cannot move tile: position (%d; %d) is empty!", oldPos[0], oldPos[1]));
		}

		final int[] pos = link.GetTile().getPosition();
		final int index = ToMappedIndex(pos[0], pos[1]);
		tileView.SetTile(link.GetTile());
		UpdateGeometry(tileView);

		if (mTileViewsByIndex.containsKey(index))
		{
			throw new RuntimeException(String.format("Cannot move tile: position (%d; %d) is busy!", pos[0], pos[1]));
		}
		mTileViewsByIndex.put(index, tileView);

		int[] to = link.GetTile().getPosition();
		Log.i(TAG, String.format("Tile moved: %d (%d; %d) -> (%d; %d)",
				link.GetTile().getValue(),
				oldPos[0], oldPos[1],
				to[0], to[1]));
	}

	private void MergeTile(@NotNull MergedTileLink link)
	{
		final int[] posOrigin = link.GetOrigin().getPosition();
		final int indexOrigin = ToMappedIndex(posOrigin[0], posOrigin[1]);
		GameTileView tileView = mTileViewsByIndex.remove(indexOrigin);
		if (tileView == null)
		{
			throw new RuntimeException(String.format("Cannot merge tile: position 'origin' (%d; %d) is empty!", posOrigin[0], posOrigin[1]));
		}

		final int[] posFrom = link.GetFrom().getPosition();
		final int indexFrom = ToMappedIndex(posFrom[0], posFrom[1]);
		if (indexOrigin == indexFrom)
		{
			throw new RuntimeException(String.format("Cannot merge tile: index of tile 'from' at position (%d; %d) and 'origin' tile are equal!", posOrigin[0], posOrigin[1]));
		}

		GameTileView removedView = mTileViewsByIndex.remove(indexFrom);
		if (removedView == null)
		{
			throw new RuntimeException(String.format("Cannot merge tile: position 'from' (%d; %d) is empty!", posFrom[0], posFrom[1]));
		}

		final int[] posTo = link.GetTo().getPosition();
		final int indexTo = ToMappedIndex(posTo[0], posTo[1]);
		if (mTileViewsByIndex.put(indexTo, tileView) != null)
		{
			throw new RuntimeException(String.format("Cannot merge tile: position 'to' (%d; %d) is busy!", posTo[0], posTo[1]));
		}

		tileView.SetTile(link.GetTo());
		UpdateGeometry(tileView);
		mBoardLayout.removeView(removedView);

		int tileFromValue = link.GetFrom().getValue();
		int tileOriginValue = link.GetOrigin().getValue();
		int tileToValue = link.GetTo().getValue();

		Log.i(TAG, String.format("Tiles merged: %d (%d; %d) + %d (%d; %d) = %d (%d; %d)",
				tileFromValue, posFrom[0], posFrom[1],
				tileOriginValue, posOrigin[0], posOrigin[1],
				tileToValue, posTo[0], posTo[1]));
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

	private void UpdateGeometry(@NotNull GameTileView tileView)
	{
		final int[] boardPos = tileView.GetTile().getPosition();
		final int[] screenPos = ToScreenPosition(boardPos[0], boardPos[1]);

		tileView.setLayoutParams(new FrameLayout.LayoutParams(mTileSize, mTileSize));
		tileView.setTranslationX(screenPos[0]);
		tileView.setTranslationY(screenPos[1]);
	}
}

