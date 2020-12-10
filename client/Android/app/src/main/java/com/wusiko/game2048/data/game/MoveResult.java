package com.wusiko.game2048.data.game;

import java.util.ArrayList;
import java.util.List;

class MoveResult {
    private int scores = 0;
    private int maxValue = 0;
    private List<Object> changes = new ArrayList<>();

    public int getScores() {
        return scores;
    }

    public int getMaxValue() {
        return maxValue;
    }

    public List<Object> getChanges() {
        return changes;
    }

    public void Moved(int place, GameTile tile) {
        getChanges().add(new MoveType(place, tile));
        maxValue = Math.max(maxValue, tile.getValue());
    }

    public void Merged(GameTile to, GameTile tile) {
        getChanges().add(new MergeType(to, tile));
        final int mergedValue = to.getValue() * 2;
        scores = getScores() + mergedValue;
        maxValue = Math.max(maxValue, mergedValue);
    }

    public boolean HasAnyChanges() {
        return !getChanges().isEmpty();
    }

    public void Add(MoveResult other) {
        getChanges().addAll(other.getChanges());
        scores = getScores() + other.getScores();
        maxValue = Math.max(maxValue, other.maxValue);
    }

    public static class MoveType {
        public final int place;
        public final GameTile tile;

        public MoveType(int place, GameTile tile) {
            this.place = place;
            this.tile = tile;
        }
    }

    public static class MergeType {
        public final GameTile to, tile;

        public MergeType(GameTile to, GameTile tile) {
            this.to = to;
            this.tile = tile;
        }
    }
}
