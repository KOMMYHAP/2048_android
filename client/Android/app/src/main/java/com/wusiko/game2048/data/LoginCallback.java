package com.wusiko.game2048.data;

import com.wusiko.game2048.data.model.LoggedInUser;

public abstract class LoginCallback {
    public abstract void run(Result<LoggedInUser> result);
}
