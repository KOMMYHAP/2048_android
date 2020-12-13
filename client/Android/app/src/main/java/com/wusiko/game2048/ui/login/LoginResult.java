package com.wusiko.game2048.ui.login;

import com.wusiko.game2048.data.login.LeaderBoard;

/**
 * Authentication result : success (user details) or error message.
 */
class LoginResult {
    private final LoggedInUserView success;
    private final Integer error;

    LoginResult(Integer error) {
        this.success = null;
        this.error = error;
    }

    LoginResult(LoggedInUserView success, LeaderBoard leaderBoard) {
        this.success = success;
        this.error = null;
    }

    LoggedInUserView getSuccess() {
        return success;
    }

    Integer getError() {
        return error;
    }
}