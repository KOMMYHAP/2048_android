package com.wusiko.game2048.data.game;

public class MovedTileLink
{
	private final GameTile mTile;
	private final int[] mPositionFrom;

	public MovedTileLink(GameTile tile, int[] from)
	{
		mTile = tile;
		mPositionFrom = from;
	}

	public GameTile GetTile()
	{
		return mTile;
	}

	public int[] GetPositionFrom()
	{
		return mPositionFrom;
	}
}
