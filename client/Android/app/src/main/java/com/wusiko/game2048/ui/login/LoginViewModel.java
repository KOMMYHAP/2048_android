package com.wusiko.game2048.ui.login;

import android.os.Bundle;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.google.gson.Gson;
import com.wusiko.game2048.R;
import com.wusiko.game2048.data.login.LeaderBoard;
import com.wusiko.game2048.data.login.LoggedInUser;
import com.wusiko.game2048.data.login.LoginRepository;
import com.wusiko.game2048.data.login.Result;

import java.lang.ref.WeakReference;

public class LoginViewModel extends ViewModel {
    private final String TAG = "LoginViewModel";
    private final MutableLiveData<LoginFormState> loginFormState = new MutableLiveData<>();
    private final MutableLiveData<LoginResult> mLoginResult = new MutableLiveData<>();
    private final MutableLiveData<LeaderBoard> mLeaderBoard = new MutableLiveData<>();
    private final LoginRepository mLoginRepository;
    private WeakReference<LoginActivity> mActivity;
    private Observer<Result<LoggedInUser>> mObserver = null;

    LoginViewModel(final LoginRepository loginRepository) {
        this.mLoginRepository = loginRepository;
    }

    public void setActivity(LoginActivity activity) {
        mActivity = new WeakReference<>(activity);
        mLoginRepository.SetContext(activity);
    }

    LiveData<LoginFormState> getLoginFormState() {
        return loginFormState;
    }

    LiveData<LoginResult> getLoginResult() {
        return mLoginResult;
    }

    LiveData<LeaderBoard> getLeaderBoard() {
        return mLeaderBoard;

    }

    public boolean isLoggedIn() {
        return mLoginRepository.isLoggedIn();
    }

    public void logout()
    {
        mLoginRepository.logout();
    }

    public void login(String username, String password) {
        LoginActivity activity = null;
        if (mActivity != null) {
            activity = mActivity.get();
        }
        if (activity == null) {
            return;
        }

        if (mObserver == null) {
            mObserver = new Observer<Result<LoggedInUser>>() {
                @Override
                public void onChanged(Result<LoggedInUser> result) {
                    if (result instanceof Result.Success) {
                        LoggedInUser data = ((Result.Success<LoggedInUser>) result).getData();
                        LoggedInUserView userView = new LoggedInUserView(data.getUsername());
                        LoginResult loginResult = new LoginResult(userView, data.getLeaderBoard());
                        mLoginResult.setValue(loginResult);
                    } else {
                        Exception error = ((Result.Error) result).getError();
                        String message = error != null ? error.toString() : "Login failed!";
                        Log.e("LoginViewModel", message);
                        mLoginResult.setValue(new LoginResult(R.string.login_failed));
                    }
                    mLoginRepository.getLoginResult().removeObserver(mObserver);
                }
            };
        }

        mLoginRepository.getLoginResult().observe(activity, mObserver);
        mLoginRepository.login(username, password);
    }

    public void loginDataChanged(String username, String password) {
        if (!isUserNameValid(username)) {
            loginFormState.setValue(new LoginFormState(R.string.invalid_username, null));
        } else if (!isPasswordValid(password)) {
            loginFormState.setValue(new LoginFormState(null, R.string.invalid_password));
        } else {
            loginFormState.setValue(new LoginFormState(true));
        }
    }

    private boolean isUserNameValid(String username) {
        return username != null && username.length() > 0 && username.length() < 16;
    }

    private boolean isPasswordValid(String password) {
        return password != null && password.length() > 5;
    }
}