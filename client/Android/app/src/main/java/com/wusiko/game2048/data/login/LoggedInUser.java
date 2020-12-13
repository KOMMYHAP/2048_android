package com.wusiko.game2048.data.login;

/**
 * Data class that captures user information for logged in users retrieved from LoginRepository
 */
public class LoggedInUser {

    private String username;
    private int scores;
    private LeaderBoard leaderBoard;

    public LoggedInUser(String username, int scores, LeaderBoard leaderBoard) {
        this.username = username;
        this.scores = scores;
        this.leaderBoard = leaderBoard;
    }

    public String getUsername() {
        return username;
    }

    public int getScores() {
        return scores;
    }

    public LeaderBoard getLeaderBoard() {
        return leaderBoard;
    }

    public void UpdateScores(int scores)
    {
        if (scores > getScores())
        {
            this.scores = scores;
            leaderBoard.Update(getUsername(), getScores());
        }
    }

}