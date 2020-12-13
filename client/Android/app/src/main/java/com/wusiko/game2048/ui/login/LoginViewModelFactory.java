package com.wusiko.game2048.ui.login;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.wusiko.game2048.data.login.LoginDataSource;
import com.wusiko.game2048.data.login.LoginRepository;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * ViewModel provider factory to instantiate LoginViewModel.
 * Required given LoginViewModel has a non-empty constructor
 */
public class LoginViewModelFactory implements ViewModelProvider.Factory {

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(LoginViewModel.class)) {
            Executor loginExecutor = Executors.newSingleThreadExecutor();
            LoginRepository loginRepository = LoginRepository.createInstance(new LoginDataSource(), loginExecutor);
            return (T) new LoginViewModel(loginRepository);
        } else {
            throw new IllegalArgumentException("Unknown ViewModel class");
        }
    }
}