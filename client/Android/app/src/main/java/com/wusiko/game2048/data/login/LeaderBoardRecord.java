package com.wusiko.game2048.data.login;

public final class LeaderBoardRecord implements Comparable<LeaderBoardRecord> {
    private String username = "";
    private int maxScores = 0;

    public LeaderBoardRecord() {
    }

    public LeaderBoardRecord(String username, int maxScores) {
        this.username = username;
        this.maxScores = maxScores;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getMaxScores() {
        return maxScores;
    }

    public void setMaxScores(int maxScores) {
        this.maxScores = maxScores;
    }

    @Override
    public int compareTo(LeaderBoardRecord o) {
        return new Integer(maxScores).compareTo(new Integer(o.maxScores));
    }
}
