package com.wusiko.game2048.data;

import com.wusiko.game2048.data.model.LoggedInUser;

import java.io.IOException;

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
public class LoginDataSource {

    public Result<LoggedInUser> login(String username, String password) {
//        try {
//            HttpClient client = HttpClient
//            URL url = new URL("https://google.com/");
//            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        try {
            // TODO: handle loggedInUser authentication
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