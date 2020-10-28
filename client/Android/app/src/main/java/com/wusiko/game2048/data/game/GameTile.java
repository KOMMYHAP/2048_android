package com.wusiko.game2048.data.game;

public class GameTile
{
	private int mDegree = 1;
	private final int[] mPosition = new int[2];

	public GameTile(int x, int y, int value)
	{
		if (x < 0 || x >= GameConfig.FIELD_SIZE_X)
		{
			throw new RuntimeException("Incorrect position for a tile: must be positive and less than {}!");
		}
		mPosition[0] = x;

		if (y < 0 || y >= GameConfig.FIELD_SIZE_Y)
		{
			throw new RuntimeException("Incorrect position for a tile: must be positive and less than {}!");
		}
		mPosition[1] = y;

		int degree = convertValueToDegree(value);
		if (degree < 1 || (2 << degree - 1) != value)
		{
			throw new RuntimeException("Incorrect value for a tile: only power of two is available!");
		}
		mDegree = degree;
	}

	public static GameTile CreateWithDegree(int x, int y, int degree)
	{
		return new GameTile(x, y, 2 << degree);
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
		return 2 << mDegree;
	}

	public int getDegree()
	{
		return mDegree;
	}

	public void moveUp()
	{
		mPosition[1] = Math.min(mPosition[1] + 1, GameConfig.FIELD_SIZE_Y - 1);
	}

	public void moveDown()
	{
		mPosition[1] = Math.max(mPosition[1] - 1, 0);
	}

	public void moveLeft()
	{
		mPosition[0] = Math.max(mPosition[0] - 1, 0);
	}

	public void moveRight()
	{
		mPosition[0] = Math.min(mPosition[0] + 1, GameConfig.FIELD_SIZE_X - 1);
	}

	public void Merged()
	{
		mDegree += 1;
	}

	public int getX()
	{
		return mPosition[0];
	}

	public void setX(int x)
	{
		if (x >= 0 && x < GameConfig.FIELD_SIZE_X)
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
		if (y >= 0 && y < GameConfig.FIELD_SIZE_Y)
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
