package com.wusiko.game2048.data.game;

import java.util.ArrayList;
import java.util.List;

public class MovementState
{
	private int mNewScores = 0;
	private final TileLinkContainer mTileLinks = new TileLinkContainer();

	public void Reset()
	{
		mNewScores = 0;
		mTileLinks.Clear();
	}

	public TileLinkContainer GetTileLinks()
	{
		return mTileLinks;
	}

	public void SetScores(int scores)
	{
		mNewScores += scores;
	}

	public int GetScores()
	{
		return mNewScores;
	}

	public void CleanUpTileLinks()
	{
		List<MergedTileLink> mergedTileLinks = mTileLinks.GetMergedTiles();
		List<MovedTileLink> movedTileLinks = mTileLinks.GetMovedTiles();
		List<MovedTileLink> movedTileLinksToRemove = new ArrayList<>();
		for (MovedTileLink movedTileLink : movedTileLinks)
		{
			for (MergedTileLink mergedTileLink : mergedTileLinks)
			{
				if (movedTileLink.GetTile() == mergedTileLink.GetTo())
				{
					movedTileLinksToRemove.add(movedTileLink);
					break;
				}
			}
		}
		movedTileLinks.removeAll(movedTileLinksToRemove);
	}
}
