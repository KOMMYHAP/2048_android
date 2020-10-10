package com.wusiko.game2048.ui.game;

import androidx.lifecycle.ViewModel;

import com.wusiko.game2048.data.game.GameBoard;

public class GameViewModel extends ViewModel {
    private GameBoard mGameBoard;

    public GameViewModel(GameBoard gameBoard)
    {
        mGameBoard = gameBoard;
    }

}
