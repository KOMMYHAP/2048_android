package com.wusiko.game2048.ui.game;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.wusiko.game2048.data.game.GameBoard;

public class GameViewModelFactory implements ViewModelProvider.Factory {

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(GameViewModel.class)) {
            return (T) new GameViewModel(new GameBoard());
        } else {
            throw new IllegalArgumentException("Unknown ViewModel class");
        }
    }
}