package com.wusiko.game2048.data.game;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class GameTileLink {
    private int[] mPositionFrom;
    private GameTile mTile, mTile2, mTile3;

    private GameTileLink(GameTile lhs, GameTile rhs, GameTile result) {
        mTile = lhs;
        mTile2 = rhs;
        mTile3 = result;
    }

    private GameTileLink(GameTile tile, int[] from) {
        mTile = tile;
        mPositionFrom = from;
    }

    private GameTileLink(GameTile tile) {
        mTile = tile;
        mPositionFrom = null;
    }

    @NotNull
    @Contract(value = "_ -> new", pure = true)
    public static GameTileLink NewCreatedTile(GameTile tile) {
        return new GameTileLink(tile);
    }

    @NotNull
    @Contract(value = "_, _ -> new", pure = true)
    public static GameTileLink NewMovedTile(GameTile tile, int[] from) {
        return new GameTileLink(tile, from);
    }

    @NotNull
    @Contract(value = "_, _, _ -> new", pure = true)
    public static GameTileLink NewMergedTile(GameTile lhs, GameTile rhs, GameTile result) {
        return new GameTileLink(lhs, rhs, result);
    }
}
