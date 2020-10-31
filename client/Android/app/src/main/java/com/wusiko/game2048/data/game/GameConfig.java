package com.wusiko.game2048.data.game;

public class GameConfig {
    public static final int TILES_IN_A_ROW = 4;
    public static final int TILES_NUMBER = TILES_IN_A_ROW * TILES_IN_A_ROW;

    public static final float[] TILE_PROBABILITIES = new float[]{
            0.4500f,      // tile 2
            0.3000f,      // tile 4
            0.1590f,      // tile 8
            0.0500f,      // tile 16
            0.0200f,      // tile 32
            0.0050f,      // tile 64
            0.0025f,      // tile 128
            0.0020f,      // tile 256
            0.0010f,      // tile 512
            0.0005f,      // tile 1024
    };
    public static final float TILE_PROBABILITIES_PRECISION = 0.0001f;
    public static final int TILE_SPAWN_AT_START = 3;
}
