package com.wusiko.game2048.ui.game;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.wusiko.game2048.data.game.CreatedTileLink;
import com.wusiko.game2048.data.game.GameBoard;
import com.wusiko.game2048.data.game.MergedTileLink;
import com.wusiko.game2048.data.game.MovedTileLink;
import com.wusiko.game2048.data.game.TileLinkContainer;

import java.util.List;

public class GameBoardViewModel extends ViewModel
{
	private final String TAG = "GameViewModel";
	private final GameBoard mGameBoard;
	private final MutableLiveData<List<CreatedTileLink>> mCreatedTiles = new MutableLiveData<>();
	private final MutableLiveData<List<MovedTileLink>> mMovedTiles = new MutableLiveData<>();
	private final MutableLiveData<List<MergedTileLink>> mMergedTiles = new MutableLiveData<>();

	private final MutableLiveData<Integer> mScores = new MutableLiveData<>();
	private final MutableLiveData<Integer> mHighScores = new MutableLiveData<>();

	public GameBoardViewModel(GameBoard gameBoard)
	{
		mGameBoard = gameBoard;
		mScores.setValue(0);
		mHighScores.setValue(0);
	}

	public LiveData<List<CreatedTileLink>> GetCreatedTiles()
	{
		return mCreatedTiles;
	}

	public LiveData<List<MovedTileLink>> GetMovedTiles()
	{
		return mMovedTiles;
	}

	public LiveData<List<MergedTileLink>> GetMergedTiles()
	{
		return mMergedTiles;
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
		Log.i(TAG, "moved up");
		mGameBoard.MoveUp();
		OnMoved();
	}

	public void OnMoveDown()
	{
		Log.i(TAG, "moved down");
		mGameBoard.MoveDown();
		OnMoved();
	}

	public void OnMoveLeft()
	{
		Log.i(TAG, "moved left");
		mGameBoard.MoveLeft();
		OnMoved();
	}

	public void OnMoveRight()
	{
		Log.i(TAG, "moved right");
		mGameBoard.MoveRight();
		OnMoved();
	}

	public void OnRestart()
	{
		Log.i(TAG, "game started");
		mGameBoard.StopGame();
		mGameBoard.StartGame();
		mCreatedTiles.setValue(mGameBoard.GetUpdatedTileLinks().GetCreatedTiles());
	}

	private void OnMoved()
	{
		TileLinkContainer links = mGameBoard.GetUpdatedTileLinks();
		mCreatedTiles.setValue(links.GetCreatedTiles());
		mMovedTiles.setValue(links.GetMovedTiles());
		mMergedTiles.setValue(links.GetMergedTiles());
		mScores.setValue(mGameBoard.GetScores());
	}

}
