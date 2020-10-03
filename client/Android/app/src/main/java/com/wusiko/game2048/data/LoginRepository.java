package com.wusiko.game2048.data;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.wusiko.game2048.data.model.LoggedInUser;

/**
 * Class that requests authentication and user information from the remote data source and
 * maintains an in-memory cache of login status and user credentials information.
 */
public class LoginRepository {

    private static volatile LoginRepository mInstance;
    private MutableLiveData<Result<LoggedInUser>> mLoginResult = new MutableLiveData<>();
    private LoginDataSource mDataSource;
    private boolean mIsLogging = false;

    // If user credentials will be cached in local storage, it is recommended it be encrypted
    // @see https://developer.android.com/training/articles/keystore
    private LoggedInUser user = null;

    // private constructor : singleton access
    private LoginRepository(LoginDataSource dataSource) {
        this.mDataSource = dataSource;
    }

    public static LoginRepository getInstance(LoginDataSource dataSource) {
        if (mInstance == null) {
            mInstance = new LoginRepository(dataSource);
        }
        return mInstance;
    }

    public boolean isLoggedIn() {
        return user != null;
    }

    public synchronized boolean isLogging() {
        return mIsLogging;
    }

    private synchronized void setLogging(boolean value) {
        mIsLogging = value;
    }

    public void logout() {
        user = null;
        mDataSource.logout();
    }

    private void setLoggedInUser(LoggedInUser user) {
        this.user = user;
        // If user credentials will be cached in local storage, it is recommended it be encrypted
        // @see https://developer.android.com/training/articles/keystore
    }

    public LiveData<Result<LoggedInUser>> getLoginResult() {
        return mLoginResult;
    }

    public void login(final String username, final String password) {
        if (isLoggedIn() || isLogging()) {
            return;
        }
        setLogging(true);
        { // async task here
            Result<LoggedInUser> result = mDataSource.login(username, password);
            if (result instanceof Result.Success) {
                setLoggedInUser(((Result.Success<LoggedInUser>) result).getData());
            }
            mLoginResult.setValue(result);
            setLogging(false);
        }
    }
}