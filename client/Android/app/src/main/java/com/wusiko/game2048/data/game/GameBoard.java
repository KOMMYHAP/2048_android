package com.wusiko.game2048.data.game;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class GameBoard
{
	private final Random mRandom = new Random();
	private final GameTileFactory mGameTileFactory = new GameTileFactory(GameConfig.TILE_PROBABILITIES, mRandom);
	private final ArrayList<GameTile> mGameField;
	private final TileLinkContainer mTileLinks = new TileLinkContainer();
	private int mTilesBitMap;
	private int mScores = 0;

	public GameBoard()
	{
		if (GameConfig.TILES_NUMBER > 32)
		{
			throw new RuntimeException("Invalid number of field size: the maximum number of tiles is 32!");
		}

		mGameField = new ArrayList<>(GameConfig.TILES_NUMBER);
	}

	public void StartGame()
	{
		for (int i = 0; i < GameConfig.TILE_SPAWN_AT_START; i++)
		{
			GameTile tile = TryToCreateTile();
			if (tile != null)
			{
				int pos = ToBitMapPosition(tile.getX(), tile.getY());
				mGameField.set(pos, tile);
			}
		}
	}

	public void StopGame()
	{
		for (int i = 0; i < GameConfig.TILES_NUMBER; ++i)
		{
			mGameField.add(null);
		}
		mTilesBitMap = 0;
		ClearUpdatedTiles();
	}

	public TileLinkContainer GetUpdatedTileLinks()
	{
		return mTileLinks;
	}

	public int GetScores()
	{
		return mScores;
	}

	private enum Direction
	{
		Left, Right, Up, Down
	}

	public void MoveLeft()
	{
		ClearUpdatedTiles();
		MoveTiles(Direction.Left);
	}

	public void MoveRight()
	{
		ClearUpdatedTiles();
		MoveTiles(Direction.Right);
	}

	public void MoveUp()
	{
		ClearUpdatedTiles();
		MoveTiles(Direction.Up);
	}

	public void MoveDown()
	{
		ClearUpdatedTiles();
		MoveTiles(Direction.Down);
	}

	private void MoveTiles(Direction direction)
	{
		ArrayList<ArrayList<GameTile>> rows = ApplyRotation(direction);
		for (ArrayList<GameTile> row : rows)
		{
			MoveRowOnLeft(row.toArray(new GameTile[0]), direction);
		}
		MoveCompleted();
	}

	@NotNull
	private ArrayList<ArrayList<GameTile>> ApplyRotation(@NotNull Direction direction)
	{
		final ArrayList<ArrayList<GameTile>> board = new ArrayList<>(GameConfig.TILES_NUMBER);
		final int maxW = GameConfig.TILES_IN_A_ROW;
		final int maxH = maxW;
		switch (direction)
		{
			case Left:
				for (int h = 0; h < maxH; ++h)
				{
					final ArrayList<GameTile> row = new ArrayList<>(GameConfig.TILES_IN_A_ROW);
					for (int w = 0; w < maxW; ++w)
					{
						int originPlace = ToBitMapPosition(w, h);
						row.add(mGameField.get(originPlace));
					}
					board.add(row);
				}
				break;
			case Right:
				for (int h = 0; h < maxH; ++h)
				{
					final ArrayList<GameTile> row = new ArrayList<>(GameConfig.TILES_IN_A_ROW);
					for (int w = 4; w > 0; --w)
					{
						int originPlace = ToBitMapPosition(w - 1, h);
						row.add(mGameField.get(originPlace));
					}
					board.add(row);
				}
				break;
			case Up:
				for (int w = 0; w < maxW; ++w)
				{
					final ArrayList<GameTile> row = new ArrayList<>(GameConfig.TILES_IN_A_ROW);
					for (int h = 0; h < 4; ++h)
					{
						int originPlace = ToBitMapPosition(w, h);
						row.add(mGameField.get(originPlace));
					}
					board.add(row);
				}
				break;
			case Down:
				for (int w = 0; w < maxW; ++w)
				{
					final ArrayList<GameTile> row = new ArrayList<>(GameConfig.TILES_IN_A_ROW);
					for (int h = 4; h > 0; --h)
					{
						int originPlace = ToBitMapPosition(w, h - 1);
						row.add(mGameField.get(originPlace));
					}
					board.add(row);
				}
				break;
		}
		return board;
	}

	private void MoveRowOnLeft(@NotNull GameTile[] row, Direction originDirection)
	{
		int l = 0, r = 0;
		while (l < (row.length - 1) && r < row.length)
		{
			// finds first not null tile
			final GameTile tileL = row[l];
			if (tileL == null)
			{
				++l;
				continue;
			}

			// finds second not null tile located to the right
			if (r <= l)
			{
				r = l + 1;
			}
			final GameTile tileR = row[r];
			if (tileR == null)
			{
				++r;
				continue;
			}

			if (tileL.getValue() != tileR.getValue())
			{
				// if values of left and right tiles are different, tries to find a next one
				l = r;
			}
			else
			{
				// in other case merges right tile to the left
				MergeTiles(tileL, tileR);
				row[r] = null;
				l = r + 1;
			}
		}

		int place = 0, x = 0;
		while (place < (row.length - 1) && x < row.length)
		{
			// finds first empty place
			if (row[place] != null)
			{
				place += 1;
				continue;
			}

			// finds first not null tile
			if (x <= place)
			{
				x = place + 1;
			}
			final GameTile rowX = row[x];
			if (rowX == null)
			{
				x += 1;
				continue;
			}

			MoveTiles(place, rowX, originDirection);
			row[place] = rowX;
			row[x] = null;
			place += 1;
		}
	}

	private void MergeTiles(@NotNull GameTile left, @NotNull GameTile right)
	{
		GameTile merged = left.Merged();
		int posL = ToBitMapPosition(left.getX(), left.getY());
		int posR = ToBitMapPosition(right.getX(), right.getY());
		mGameField.set(posL, merged);
		mGameField.set(posR, null);
		mTilesBitMap &= ~(1 >> posR);
		mTileLinks.Add(new MergedTileLink(left, right, merged));
	}

	private void MoveTiles(int place, @NotNull GameTile tile, @NotNull Direction originDirection)
	{
		int placeX = 0, placeY = 0;
		switch (originDirection)
		{
			case Left:
				// todo: placeY determined incorrect for the simplest case: position of tile is inverted (0;3) -> (3;0)
				placeX = place;
				placeY = tile.getY();
				break;
			case Right:
				placeX = GameConfig.TILES_IN_A_ROW - place - 1;
				placeY = tile.getY();
				break;
			case Up:
				placeX = tile.getX();
				placeY = place;
				break;
			case Down:
				placeX = tile.getX();
				placeY = GameConfig.TILES_IN_A_ROW - place - 1;
				break;
		}
		int newPos = ToBitMapPosition(placeX, placeY);
		int oldPos = ToBitMapPosition(tile.getX(), tile.getY());
		mGameField.set(newPos, tile);
		mGameField.set(oldPos, null);
		mTilesBitMap &= ~(1 >> oldPos);
		mTileLinks.Add(new MovedTileLink(tile, tile.getPosition().clone()));
		tile.setX(placeX);
		tile.setY(placeY);
	}


	private void MoveCompleted()
	{
		UpdateScores();
	}

	private void UpdateScores()
	{
		int scores = 0;
		List<MergedTileLink> mergedTileLinks = mTileLinks.GetMergedTiles();
		for (MergedTileLink link : mergedTileLinks)
		{
			scores += link.GetScores();
		}
		mScores += scores;
	}

	private void ClearUpdatedTiles()
	{
		mTileLinks.Clear();
	}

	@Nullable
	private GameTile TryToCreateTile()
	{
		int freePosition = GetNextFreePosition(0);
		if (freePosition == -1)
		{
			return null;
		}

		int numberOfFreePositions = NumberOfFreePositions();
		if (numberOfFreePositions != 1)
		{
			int indexOfFreePosition = mRandom.nextInt(numberOfFreePositions);
			for (int i = 0; i < indexOfFreePosition; i++)
			{
				freePosition = GetNextFreePosition(freePosition + 1);
			}
		}

		mTilesBitMap |= (1 << freePosition);
		int[] position = ToPosition(freePosition);
		GameTile tile = mGameTileFactory.Create(position[0], position[1]);
		mTileLinks.Add(new CreatedTileLink(tile));
		return tile;
	}

	private int ToBitMapPosition(int x, int y)
	{
		return y * GameConfig.TILES_IN_A_ROW + x;
	}

	@NotNull
	@Contract(value = "_ -> new", pure = true)
	private int[] ToPosition(int bitMapPosition)
	{
		return new int[]{
				bitMapPosition % 4,
				bitMapPosition / 4
		};
	}

	private boolean IsPositionFree(int position)
	{
		return ((~mTilesBitMap >> position) & 1) == 1;
	}

	private int NumberOfFreePositions()
	{
		int n = 0;
		for (int i = 0; i < GameConfig.TILES_NUMBER; i++)
		{
			n += IsPositionFree(i) ? 1 : 0;
		}
		return n;
	}

	private int GetNextFreePosition(int startPosition)
	{
		for (int i = startPosition; i < GameConfig.TILES_NUMBER; i++)
		{
			if (IsPositionFree(i))
			{
				return i;
			}
		}
		for (int i = 0; i < startPosition; ++i)
		{
			if (IsPositionFree(i))
			{
				return i;
			}
		}
		return -1;
	}
}
