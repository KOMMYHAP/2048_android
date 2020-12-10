package com.wusiko.game2048.data.game;

public class MergedTileLink
{
	private final GameTile mTo, mFrom;

	public MergedTileLink(GameTile to, GameTile from)
	{
		mTo = to;
		mFrom = from;
	}

	public GameTile GetTo()
	{
		return mTo;
	}

	public GameTile GetFrom()
	{
		return mFrom;
	}
}
