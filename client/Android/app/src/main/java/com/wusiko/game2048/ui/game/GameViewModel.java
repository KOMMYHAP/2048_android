package com.wusiko.game2048.ui.game;

import android.util.Log;

import androidx.lifecycle.ViewModel;

import com.wusiko.game2048.data.game.GameBoard;
import com.wusiko.game2048.data.game.TileLinkContainer;

public class GameViewModel extends ViewModel
{
	private final String TAG = "GameViewModel";
	private final GameBoard mGameBoard;

	public GameViewModel(GameBoard gameBoard)
	{
		mGameBoard = gameBoard;
	}

	public void OnMoveUp()
	{
		Log.i(TAG, "OnMoveRight: moved up");
		mGameBoard.MoveUp();
		OnMoved();
	}

	public void OnMoveDown()
	{
		Log.i(TAG, "OnMoveRight: moved down");
		mGameBoard.MoveDown();
		OnMoved();
	}

	public void OnMoveLeft()
	{
		Log.i(TAG, "OnMoveRight: moved left");
		mGameBoard.MoveLeft();
		OnMoved();
	}

	public void OnMoveRight()
	{
		Log.i(TAG, "OnMoveRight: moved right");
		mGameBoard.MoveRight();
		OnMoved();
	}

	public void OnRestart()
	{
		Log.i(TAG, "OnRestart: game started");
		mGameBoard.StartGame();

		// todo: animate it
		TileLinkContainer links = mGameBoard.GetGameTileLinks();
		Log.i(TAG, "OnMoved: created tiles " + links.GetCreatedTiles().size());
	}

	private void OnMoved()
	{
		// todo: animate it
		TileLinkContainer links = mGameBoard.GetGameTileLinks();
		Log.i(TAG, "OnMoved: created tiles " + links.GetCreatedTiles().size());
		Log.i(TAG, "OnMoved: merged tiles " + links.GetMergedTiles().size());
		Log.i(TAG, "OnMoved: moved tiles " + links.GetMovedTiles().size());
	}

}
