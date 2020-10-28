package com.wusiko.game2048.data.game;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Random;

public class GameBoard
{
	private final Random mRandom = new Random();
	private final GameTileFactory mGameTileFactory = new GameTileFactory(GameConfig.TILE_PROBABILITIES, mRandom);
	private GameTile[] mGameField;
	private final TileLinkContainer mTileLinks = new TileLinkContainer();
	private byte mTilesBitMap;

	public GameBoard()
	{
		if (GameConfig.FIELD_SIZE > 256)
		{
			throw new RuntimeException("Invalid number of field size: the biggest size is 256!");
		}
	}

	public void StartGame()
	{
		mGameField = new GameTile[GameConfig.TILE_SPAWN_AT_START];
		for (int i = 0; i < GameConfig.TILE_SPAWN_AT_START; i++)
		{
			mGameField[i] = TryToCreateTile();
		}
	}

	public TileLinkContainer GetGameTileLinks()
	{
		return mTileLinks;
	}

	private void Move(Direction direction)
	{
		// todo
		switch (direction)
		{
			case Left:
				break;
			case Right:
				break;
			case Up:
				break;
			case Down:
				break;
		}
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
		return y * GameConfig.FIELD_SIZE_X + x;
	}

	@NotNull
	@Contract(value = "_ -> new", pure = true)
	private int[] ToPosition(int bitMapPosition)
	{
		return new int[]{
				bitMapPosition / 4,
				bitMapPosition % 4
		};
	}

	private boolean IsPositionFree(int position)
	{
		return ((~mTilesBitMap >> position) & 1) == 1;
	}

	private int NumberOfFreePositions()
	{
		int n = 0;
		for (int i = 0; i < GameConfig.FIELD_SIZE; i++)
		{
			n += IsPositionFree(i) ? 1 : 0;
		}
		return n;
	}

	private int GetNextFreePosition(int startPosition)
	{
		for (int i = startPosition; i < GameConfig.FIELD_SIZE; i++)
		{
			if (IsPositionFree(i))
			{
				return i;
			}
		}
		return -1;
	}

	private enum Direction
	{
		Left, Right, Up, Down
	}
}
