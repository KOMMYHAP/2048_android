package com.wusiko.game2048.data.login;

import android.content.Context;
import android.content.SharedPreferences;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.HashMap;

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
public class LoginDataSource {
    private WeakReference<Context> mContext;
    private final String PREFERENCES = "LoginDataSource";
    private final HashMap<String, LoginDataSource> mLogins = new HashMap<>();

    public void SetContext(Context context)
    {
        mContext = new WeakReference<>(context);
    }

    public Result<LoggedInUser> login(String username, String password) {
        try {
            Context context = mContext.get();
            if (context == null)
            {
                return new Result.Error(new RuntimeException("Context is empty!"));
            }

//            SharedPreferences prefs = context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
//            SharedPreferences.Editor editor = prefs.edit();
//            editor.putStringSet()

            LoggedInUser fakeUser =
                    new LoggedInUser(
                            java.util.UUID.randomUUID().toString(),
                            "Jane Doe");
            return new Result.Success<>(fakeUser);
        } catch (Exception e) {
            return new Result.Error(new IOException("Error logging in", e));
        }
    }

    public void logout() {
        // TODO: revoke authentication
    }
}