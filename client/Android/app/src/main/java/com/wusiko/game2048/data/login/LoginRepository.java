package com.wusiko.game2048.data.login;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.FutureTask;

/**
 * Class that requests authentication and user information from the remote data source and
 * maintains an in-memory cache of login status and user credentials information.
 */
public class LoginRepository {
    private static volatile LoginRepository mInstance;
    private MutableLiveData<Result<LoggedInUser>> mLoginResult = new MutableLiveData<>();
    private LoginDataSource mDataSource;
    private Executor mLoginExecutor = null;
    private FutureTask<?> mLoginTask = null;
    // If user credentials will be cached in local storage, it is recommended it be encrypted
    // @see https://developer.android.com/training/articles/keystore
    private LoggedInUser mUser = null;

    // private constructor : singleton access
    private LoginRepository(LoginDataSource dataSource, Executor loginExecutor) {
        mDataSource = dataSource;
        mLoginExecutor = loginExecutor;
    }

    public static LoginRepository createInstance(LoginDataSource dataSource, Executor loginExecutor) {
        if (mInstance == null) {
            mInstance = new LoginRepository(dataSource, loginExecutor);
        }
        return mInstance;
    }

    public static LoginRepository getInstance() {
        return mInstance;
    }

    public LeaderBoard GetLeaderBoard() {
        return mDataSource.GetLeaderBoard();
    }

    public void SetContext(Context context) {
        mDataSource.SetContext(context);
    }

    public void UpdateLoggedInUserData(int scores) {
        if (mUser.getScores() < scores)
        {
            mDataSource.UpdateLeaderBoard(mUser.getUsername(), scores);
            mUser.UpdateScores(scores);
        }
    }

    public boolean isLoggedIn() {
        return mUser != null;
    }

    public boolean isLogging() {
        return mLoginTask != null && !mLoginTask.isDone() && !mLoginTask.isCancelled();
    }

    public void logout() {
        if (!isLoggedIn() || isLogging()) {
            return;
        }

        mLoginResult = new MutableLiveData<>();
        mUser = null;
    }

    public LoggedInUser getLoggedInUser() {
        return mUser;
    }

    private void setLoggedInUser(LoggedInUser user) {
        this.mUser = user;
    }

    public LiveData<Result<LoggedInUser>> getLoginResult() {
        return mLoginResult;
    }

    public void login(final String username, final String password) {
        if (isLoggedIn() || isLogging()) {
            return;
        }

        mLoginTask = new FutureTask<>(new Callable<Void>() {
            @Override
            public Void call() {
                Result<LoggedInUser> result = mDataSource.login(username, password);
                if (result instanceof Result.Success) {
                    setLoggedInUser(((Result.Success<LoggedInUser>) result).getData());
                }
                mLoginResult.postValue(result);
                return null;
            }
        });
        mLoginExecutor.execute(mLoginTask);
    }
}