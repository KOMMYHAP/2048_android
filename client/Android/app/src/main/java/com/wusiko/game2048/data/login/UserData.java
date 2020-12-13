package com.wusiko.game2048.data.login;

public class UserData {
    private int password;
    private int maxScores;

    public UserData()
    {}

    public int getPassword() {
        return password;
    }

    public void setPassword(int password) {
        this.password = password;
    }

    public int getMaxScores() {
        return maxScores;
    }

    public void setMaxScores(int maxScores) {
        this.maxScores = maxScores;
    }
}
