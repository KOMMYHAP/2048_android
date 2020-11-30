package com.wusiko.game2048.ui.game;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.wusiko.game2048.data.game.CreatedTileLink;
import com.wusiko.game2048.data.game.GameBoard;
import com.wusiko.game2048.data.game.MergedTileLink;
import com.wusiko.game2048.data.game.MovedTileLink;
import com.wusiko.game2048.data.game.MovementState;
import com.wusiko.game2048.data.game.TileLinkContainer;

import java.util.List;

public class GameBoardViewModel extends ViewModel
{
	private final String TAG = "GameBoardViewModel";
	private final GameBoard mGameBoard;
	private final MutableLiveData<MovementState> mMovementState = new MutableLiveData<>();
	private final MutableLiveData<Integer> mScores = new MutableLiveData<>();
	private final MutableLiveData<Integer> mHighScores = new MutableLiveData<>();

	public GameBoardViewModel(GameBoard gameBoard)
	{
		mGameBoard = gameBoard;
		mScores.setValue(0);
		mHighScores.setValue(0);
	}

	public LiveData<MovementState> GetMovementState()
	{
		return mMovementState;
	}

	public LiveData<Integer> GetScores()
	{
		return mScores;
	}

	public LiveData<Integer> GetHighScores()
	{
		return mHighScores;
	}

	public void OnMoveUp()
	{
		Log.i(TAG, "Movement: up");
		mGameBoard.MoveUp();
		OnMoved();
	}

	public void OnMoveDown()
	{
		Log.i(TAG, "Movement: down");
		mGameBoard.MoveDown();
		OnMoved();
	}

	public void OnMoveLeft()
	{
		Log.i(TAG, "Movement: left");
		mGameBoard.MoveLeft();
		OnMoved();
	}

	public void OnMoveRight()
	{
		Log.i(TAG, "Movement: right");
		mGameBoard.MoveRight();
		OnMoved();
	}

	public void OnRestart()
	{
		Log.i(TAG, "State: game restarted");
		mGameBoard.StopGame();
		mGameBoard.StartGame();
		mMovementState.setValue(mGameBoard.GetMovementState());
	}

	private void OnMoved()
	{
		mMovementState.setValue(mGameBoard.GetMovementState());

		int scores = mGameBoard.GetScores();
		mScores.setValue(scores);
		if (scores > mHighScores.getValue())
		{
			mHighScores.setValue(scores);
		}
	}

}
