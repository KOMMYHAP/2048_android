package com.wusiko.game2048.data.login;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
public class LoginDataSource {
    private final String TAG = "LoginDataSource";
    private final String PREFERENCES = "LoginDataSource";
    private final HashMap<String, UserData> mLogins = new HashMap<>();
    private final LeaderBoard mLeaderBoard = new LeaderBoard();
    private Context mContext;

    public void SetContext(Context context) {
        if (context == null) {
            throw new RuntimeException("Context must be non empty!");
        }

        mContext = context;
        LoadData();
    }

    public LeaderBoard GetLeaderBoard() {
        return mLeaderBoard;
    }

    public UserData GetUserData(String username) {
        return mLogins.get(username);
    }

    public Result<LoggedInUser> login(String username, String password) {
        try {
            UserData userData = mLogins.get(username);
            if (userData == null) {
                userData = RegisterNewUser(username, password);
            } else if (userData.getPassword() != password.hashCode()) {
                return new Result.Error(new RuntimeException("Incorrect password"));
            }
            LeaderBoard leaderBoard = mLeaderBoard.copy();
            leaderBoard.FillWithFakes(userData.getMaxScores(), 5);
            LoggedInUser user = new LoggedInUser(username, userData.getMaxScores(), leaderBoard);
            return new Result.Success<>(user);
        } catch (Exception e) {
            return new Result.Error(new IOException("Error logging in", e));
        }
    }

    private UserData RegisterNewUser(String username, String password) {
        UserData userData = new UserData();
        userData.setPassword(password.hashCode());
        userData.setMaxScores(0);
        mLogins.put(username, userData);

        if (mContext != null) {
            SharedPreferences prefs = mContext.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            Gson gson = new Gson();
            String data = gson.toJson(userData);
            editor.putString(username, data);
            editor.apply();
        }

        return userData;
    }

    private void LoadData() {
        if (mContext == null) {
            return;
        }

        SharedPreferences prefs = mContext.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
        Map<String, ?> objects = prefs.getAll();
        for (Map.Entry<String, ?> entry : objects.entrySet()) {
            if (entry.getValue() instanceof String) {
                try {
                    String username = entry.getKey();
                    Gson gson = new Gson();
                    UserData userData = gson.fromJson((String) entry.getValue(), UserData.class);
                    mLogins.put(username, userData);

                    if (userData.getMaxScores() != 0)
                    {
                        mLeaderBoard.Add(username, userData.getMaxScores());
                    }

                } catch (Exception e) {
                    Log.e("LoginDataSource", e.toString());
                }
            }
        }
    }

    public void ForgetUser(String username) {
        if (mContext == null) {
            return;
        }
        SharedPreferences prefs = mContext.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove(username);
        editor.apply();
    }

    public void UpdateLeaderBoard(String username, int scores) {
        UserData userData = mLogins.get(username);
        if (userData == null) {
            return;
        }
        if (userData.getMaxScores() >= scores) {
            return;
        }
        Context context = mContext;
        if (context == null) {
            return;
        }

        mLeaderBoard.Update(username, scores);
        userData.setMaxScores(scores);

        SharedPreferences prefs = context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();
        editor.putString(username, gson.toJson(userData));
        editor.apply();

        Log.d(TAG, String.format("User %s has broken his record! Now it's %d scores!", username, scores));
    }
}