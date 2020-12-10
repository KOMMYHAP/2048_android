package com.wusiko.game2048.data.game;

import java.util.ArrayList;
import java.util.List;

public class MovementState
{
	private enum GameState
	{
		PROCESS, LOSE, VICTORY
	}

	private int mNewScores = 0;
	private final TileLinkContainer mTileLinks = new TileLinkContainer();
	private GameState mGameState = GameState.PROCESS;

	public void Reset()
	{
		mNewScores = 0;
		mTileLinks.Clear();
		mGameState = GameState.PROCESS;
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
		return mGameState == GameState.LOSE;
	}

	public boolean IsVictory()
	{
		return mGameState == GameState.VICTORY;
	}

	public void SetVictory()
	{
		mGameState = GameState.VICTORY;
	}

	public void SetLose()
	{
		mGameState = GameState.LOSE;
	}
}
