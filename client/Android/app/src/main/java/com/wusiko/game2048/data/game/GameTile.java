package com.wusiko.game2048.data.game;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class GameTile
{
	private int mDegree = 1;
	private final int[] mPosition = new int[2];

	public GameTile(int x, int y, int value)
	{
		if (x < 0 || x >= GameConfig.TILES_IN_A_ROW)
		{
			throw new RuntimeException(String.format("Incorrect position for a tile: must be positive and less than %d!", GameConfig.TILES_IN_A_ROW));
		}
		mPosition[0] = x;

		if (y < 0 || y >= GameConfig.TILES_IN_A_ROW)
		{
			throw new RuntimeException(String.format("Incorrect position for a tile: must be positive and less than %d!", GameConfig.TILES_IN_A_ROW));
		}
		mPosition[1] = y;

		int degree = convertValueToDegree(value);
		if (degree < 1 || (2 << degree - 1) != value)
		{
			throw new RuntimeException("Incorrect value for a tile: only power of two is available!");
		}
		mDegree = degree;
	}

	@NotNull
	@Contract("_, _, _ -> new")
	public static GameTile CreateWithDegree(int x, int y, int degree)
	{
		return new GameTile(x, y, 1 << degree);
	}

	public GameTile Merged()
	{
		return new GameTile(getX(), getY(), getValue() * 2);
	}

	private static int convertValueToDegree(int value)
	{
		int degree = 0;
		while (value > 1)
		{
			value /= 2;
			degree += 1;
		}
		return degree;
	}

	public int getValue()
	{
		return 1 << mDegree;
	}

	public int getDegree()
	{
		return mDegree;
	}

	public int getX()
	{
		return mPosition[0];
	}

	public void setX(int x)
	{
		if (x >= 0 && x < GameConfig.TILES_IN_A_ROW)
		{
			mPosition[0] = x;
		} else
		{
			throw new RuntimeException("Invalid x position of tile!");
		}
	}

	public int getY()
	{
		return mPosition[1];
	}

	public void setY(int y)
	{
		if (y >= 0 && y < GameConfig.TILES_IN_A_ROW)
		{
			mPosition[1] = y;
		} else
		{
			throw new RuntimeException("Invalid x position of tile!");
		}
	}

	public int[] getPosition()
	{
		return mPosition;
	}
}
