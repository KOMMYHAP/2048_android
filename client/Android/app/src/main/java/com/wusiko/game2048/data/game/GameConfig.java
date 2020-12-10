package com.wusiko.game2048.data.game;

public class GameConfig {
    public static final int TILES_IN_A_ROW = 4;
    public static final int TILES_NUMBER = TILES_IN_A_ROW * TILES_IN_A_ROW;

    public static final float[] TILE_PROBABILITIES = new float[]{
            0.9f,      // tile 2
            0.1f,      // tile 4
    };
    public static final float TILE_PROBABILITIES_PRECISION = 0.1f;
    public static final int TILE_SPAWN_AT_START = 2;

    public static final int[] GAME_FIELD_PRESET = {
            0,    0,    0,    0,
            0,    0,    0,    0,
            0,    0,    0,    0,
            0,    0,    0,    0,
    };
    public static final int GAME_TILE_MAX_VALUE = 2048;
}
