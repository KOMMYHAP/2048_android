package com.wusiko.game2048.data.login;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class LeaderBoard {
    private transient final Random mRandom;
    private ArrayList<LeaderBoardRecord> mRecords = new ArrayList<>();

    public LeaderBoard() {
        mRandom = new Random();
    }

    private LeaderBoard(Random random, List<LeaderBoardRecord> records)
    {
        mRandom = random;
        mRecords = new ArrayList<>(records);
    }

    public LeaderBoard copy()
    {
        return new LeaderBoard(mRandom, mRecords);
    }

    public List<LeaderBoardRecord> getRecords() {
        return mRecords;
    }

    public void Add(final String username, int scores) {
        if (GetByUser(username) == null) {
            mRecords.add(new LeaderBoardRecord(username, scores));
        }
    }

    public void Update(String username, int scores) {
        LeaderBoardRecord record = GetByUser(username);
        if (record != null) {
            record.setMaxScores(scores);
            Sort();
        }
    }

    public void FillWithFakes(int size) {
        int minScores = mRecords.isEmpty() ? 0 : Integer.MAX_VALUE;
        for (LeaderBoardRecord record : mRecords)
        {
            minScores = Math.min(minScores, record.getMaxScores());
        }
        for (int i = mRecords.size(); i < size; ++i) {
            mRecords.add(CreateFake(minScores));
        }
        Sort();
    }

    private void Sort()
    {
        Collections.sort(mRecords, Collections.<LeaderBoardRecord>reverseOrder());
    }

    private LeaderBoardRecord CreateFake(int scores) {
        final String[] fakePlayersArray = new String[]{
                "James", "John", "Robert",
                "Michael", "William", "David",
                "Richard", "Joseph", "Thomas",
                "Mary", "Patricia", "Jennifer",
                "Linda", "Elizabeth", "Barbara"
        };

        final int iPlayer = mRandom.nextInt(fakePlayersArray.length);
        final String fakePlayer = fakePlayersArray[iPlayer];
        scores = Math.max(scores, 100);
        final int fakeScores = (mRandom.nextInt(scores / 2)) * 2;

        return new LeaderBoardRecord(fakePlayer, fakeScores);
    }

    private LeaderBoardRecord GetByUser(String username) {
        for (LeaderBoardRecord record : mRecords) {
            if (record.getUsername().equals(username)) {
                return record;
            }
        }
        return null;
    }
}
