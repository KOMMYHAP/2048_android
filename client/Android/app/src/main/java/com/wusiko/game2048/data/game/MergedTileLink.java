package com.wusiko.game2048.data.game;

public class MergedTileLink
{
	// mTo = mOrigin + mFrom
	// mOrigin	- copy of real Tile
	// mTo		- result of tile where two tiles were merged
	// mFrom	- tile which was destroyed to obtain merged tile
	//
	// In example, lets move first row to left:
	// Row: 0   2   0   2
	//         |       |
	//     mOrigin     |
	//              mFrom
	//
	// Result of moving:
	//      4   0   0   0
	//      |   |      |
	//    mTo   |      |
	//       mOrigin   |
	//               mFrom

	private final GameTile mTo, mOrigin, mFrom;

	public MergedTileLink(GameTile origin, GameTile to, GameTile from)
	{
		mTo = to;
		mOrigin = origin;
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

	public GameTile GetOrigin()
	{
		return mOrigin;
	}

	public int GetScores()
	{
		return mTo.getValue() + mOrigin.getValue();
	}
}
