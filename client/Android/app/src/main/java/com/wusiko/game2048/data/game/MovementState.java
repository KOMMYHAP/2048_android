package com.wusiko.game2048.data.game;

import java.util.ArrayList;
import java.util.List;

public class MovementState
{
	private int mNewScores = 0;
	private final TileLinkContainer mTileLinks = new TileLinkContainer();
	private boolean mLose = false;

	public void Reset()
	{
		mNewScores = 0;
		mTileLinks.Clear();
		mLose = false;
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

	public boolean IsLose()
	{
		return mLose;
	}

	public void SetLose()
	{
		mLose = true;
	}
}