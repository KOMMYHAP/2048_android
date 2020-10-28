package com.wusiko.game2048.data.game;

public class CreatedTileLink
{
	private final GameTile mTile;

	public CreatedTileLink(GameTile tile)
	{
		mTile = tile;
	}

	public GameTile GetTile()
	{
		return mTile;
	}
}
