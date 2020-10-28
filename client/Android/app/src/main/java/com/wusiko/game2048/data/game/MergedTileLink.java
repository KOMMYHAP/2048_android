package com.wusiko.game2048.data.game;

public class MergedTileLink
{
	private final GameTile mTile1;
	private final GameTile mTile2;
	private final GameTile mTileResult;

	public MergedTileLink(GameTile lhs, GameTile rhs, GameTile result)
	{
		mTile1 = lhs;
		mTile2 = rhs;
		mTileResult = result;
	}

	public GameTile GetFirstTile()
	{
		return mTile1;
	}

	public GameTile GetSecondTile()
	{
		return mTile2;
	}

	public GameTile GetResultTile()
	{
		return mTileResult;
	}
}
