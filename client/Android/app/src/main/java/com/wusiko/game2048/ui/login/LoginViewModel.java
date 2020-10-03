package com.wusiko.game2048.ui.login;

import android.util.Patterns;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.wusiko.game2048.R;
import com.wusiko.game2048.data.LoginRepository;
import com.wusiko.game2048.data.Result;
import com.wusiko.game2048.data.model.LoggedInUser;

import java.lang.ref.WeakReference;

public class LoginViewModel extends ViewModel {
    private WeakReference<LoginActivity> mActivity;
    private MutableLiveData<LoginFormState> loginFormState = new MutableLiveData<>();
    private MutableLiveData<LoginResult> loginResult = new MutableLiveData<>();
    private LoginRepository loginRepository;
    private Observer<Result<LoggedInUser>> mObserver = null;

    LoginViewModel(final LoginRepository loginRepository) {
        this.loginRepository = loginRepository;
    }

    public void setActivity(LoginActivity activity) {
        mActivity = new WeakReference<>(activity);
    }

    LiveData<LoginFormState> getLoginFormState() {
        return loginFormState;
    }

    LiveData<LoginResult> getLoginResult() {
        return loginResult;
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
                        loginResult.setValue(new LoginResult(new LoggedInUserView(data.getDisplayName())));
                    } else {
                        loginResult.setValue(new LoginResult(R.string.login_failed));
                    }
                    loginRepository.getLoginResult().removeObserver(mObserver);
                }
            };
        }

        loginRepository.getLoginResult().observe(activity, mObserver);
        loginRepository.login(username, password);
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

    // A placeholder username validation check
    private boolean isUserNameValid(String username) {
        if (username == null) {
            return false;
        }
        if (username.contains("@")) {
            return Patterns.EMAIL_ADDRESS.matcher(username).matches();
        } else {
            return !username.trim().isEmpty();
        }
    }

    // A placeholder password validation check
    private boolean isPasswordValid(String password) {
        return password != null && password.trim().length() > 5;
    }
}