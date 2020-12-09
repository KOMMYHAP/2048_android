package com.wusiko.game2048.data.game;

import androidx.core.util.Pair;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Random;

public class GameBoard {
    private final Random mRandom = new Random();
    private final GameTileFactory mGameTileFactory = new GameTileFactory(GameConfig.TILE_PROBABILITIES, mRandom);
    private final ArrayList<GameTile> mGameField;
    private final MovementState mMovementState;
    private int mTilesBitMap;
    private boolean mGameWasOver = false;

    public GameBoard() {
        if (GameConfig.TILES_NUMBER > 32) {
            throw new RuntimeException("Invalid number of field size: the maximum number of tiles is 32!");
        }

        mGameField = new ArrayList<>(GameConfig.TILES_NUMBER);
        mMovementState = new MovementState();
    }

    public void StartGame() {
        for (int i = 0; i < GameConfig.GAME_FIELD_PRESET.length; ++i)
        {
            int value = GameConfig.GAME_FIELD_PRESET[i];
            if (value != 0)
            {
                int[] pos = ToPosition(i);
                CreateTile(pos[0], pos[1], value);
            }
        }
        for (int i = 0; i < GameConfig.TILE_SPAWN_AT_START; i++) {
            TryToCreateTile(null);
        }
    }

    public void StopGame() {
        for (int i = 0; i < GameConfig.TILES_NUMBER; ++i) {
            mGameField.add(null);
        }
        mTilesBitMap = 0;
        mGameWasOver = false;
        mMovementState.Reset();
    }

    public MovementState GetMovementState() {
        return mMovementState;
    }

    public void MoveLeft() {
        TryMoveTiles(Direction.Left);
    }

    public void MoveRight() {
        TryMoveTiles(Direction.Right);
    }

    public void MoveUp() {
        TryMoveTiles(Direction.Up);
    }

    public void MoveDown() {
        TryMoveTiles(Direction.Down);
    }

    private void TryMoveTiles(Direction direction) {
        if (mGameWasOver) {
            return;
        }

        mMovementState.Reset();
        MoveResult moveResult = MoveTiles(direction);
        final boolean boardChanged = ApplyMoveResult(moveResult, direction);
        mMovementState.SetScores(moveResult.getScores());

        if (boardChanged) {
            GameTile tileCreated = TryToCreateTile(null);

            if (tileCreated == null) {
                throw new RuntimeException("Invalid code flow: board has been changed, but tile cannot be created!");
            }
            if (!HasAnyMove()) {
                mGameWasOver = true;
                mMovementState.SetLose();
            }
        }
    }

    private boolean HasAnyMove() {
        for (Direction dir : Direction.values()) {
            if (MoveTiles(dir).HasAnyChanges()) {
                return true;
            }
        }
        return false;
    }

    private MoveResult MoveTiles(Direction direction) {
        MoveResult moveResult = new MoveResult();
        ArrayList<ArrayList<GameTile>> rows = ApplyRotation(direction);
        for (ArrayList<GameTile> row : rows) {
            MoveResult result = MoveRowOnLeft(row.toArray(new GameTile[0]));
            moveResult.Add(result);
        }
        return moveResult;
    }

    private GameTile CopyTile(int mappedPos) {
        GameTile tile = mGameField.get(mappedPos);
        return tile != null ? tile.clone() : null;
    }

    @NotNull
    private ArrayList<ArrayList<GameTile>> ApplyRotation(@NotNull Direction direction) {
        final ArrayList<ArrayList<GameTile>> board = new ArrayList<>(GameConfig.TILES_NUMBER);
        final int maxW = GameConfig.TILES_IN_A_ROW;
        final int maxH = maxW;
        switch (direction) {
            case Left:
                for (int h = 0; h < maxH; ++h) {
                    final ArrayList<GameTile> row = new ArrayList<>(GameConfig.TILES_IN_A_ROW);
                    for (int w = 0; w < maxW; ++w) {
                        int originPlace = ToBitMapPosition(w, h);
                        row.add(CopyTile(originPlace));
                    }
                    board.add(row);
                }
                break;
            case Right:
                for (int h = 0; h < maxH; ++h) {
                    final ArrayList<GameTile> row = new ArrayList<>(GameConfig.TILES_IN_A_ROW);
                    for (int w = 4; w > 0; --w) {
                        int originPlace = ToBitMapPosition(w - 1, h);
                        row.add(CopyTile(originPlace));
                    }
                    board.add(row);
                }
                break;
            case Up:
                for (int w = 0; w < maxW; ++w) {
                    final ArrayList<GameTile> row = new ArrayList<>(GameConfig.TILES_IN_A_ROW);
                    for (int h = 0; h < 4; ++h) {
                        int originPlace = ToBitMapPosition(w, h);
                        row.add(CopyTile(originPlace));
                    }
                    board.add(row);
                }
                break;
            case Down:
                for (int w = 0; w < maxW; ++w) {
                    final ArrayList<GameTile> row = new ArrayList<>(GameConfig.TILES_IN_A_ROW);
                    for (int h = 4; h > 0; --h) {
                        int originPlace = ToBitMapPosition(w, h - 1);
                        row.add(CopyTile(originPlace));
                    }
                    board.add(row);
                }
                break;
        }
        return board;
    }

    private MoveResult MoveRowOnLeft(@NotNull GameTile[] row) {
        MoveResult moveResult = new MoveResult();
		/*	place - индекс для вставки плитки,
		x     - индекс рассматриваемой плитки */
        int place = 0, x = 1;
        for (; x < row.length; ++x) {
            if (row[x] == null)
                continue;

            final GameTile rowP = row[place];
            final GameTile rowX = row[x];

            final int valueP = rowP != null ? rowP.getValue() : 0;
            final int valueX = rowX.getValue();
            if (valueP == valueX && rowP != null) {
                moveResult.Merged(rowP.clone(), rowX.clone());
                rowP.Merged();
                row[x] = null;
                place++;
            } else {
                if (place + 1 == x && rowP != null) {
                    place += 1;
                    continue;
                }
                if (rowP != null) {
                    place++;
                }

                moveResult.Moved(place, rowX.clone());
                row[place] = rowX;
                row[x] = null;
            }
        }

        return moveResult;
    }

    private boolean ApplyMoveResult(MoveResult moveResult, Direction originDirection) {
        for (Object change : moveResult.getChanges()) {
            if (change instanceof MoveResult.MoveType) {
                MoveResult.MoveType move = ((MoveResult.MoveType) change);
                MoveTiles(move.place, move.tile, originDirection);
            } else if (change instanceof MoveResult.MergeType) {
                MoveResult.MergeType merge = ((MoveResult.MergeType) change);
                MergeTiles(merge.to, merge.tile);
            }
        }
        return moveResult.HasAnyChanges();
    }

    private void MergeTiles(@NotNull GameTile to, @NotNull GameTile from) {
        GameTile origin = to.clone();
        to.Merged();
        int posL = ToBitMapPosition(to.getX(), to.getY());
        int posR = ToBitMapPosition(from.getX(), from.getY());
        mGameField.set(posL, to);
        mGameField.set(posR, null);
        mTilesBitMap &= ~(1 << posR);
        mMovementState.GetTileLinks().Add(new MergedTileLink(origin, to.clone(), from.clone()));
    }

    private void MoveTiles(int place, @NotNull GameTile tile, @NotNull Direction originDirection) {
        int placeX = 0, placeY = 0;
        switch (originDirection) {
            case Left:
                placeX = place;
                placeY = tile.getY();
                break;
            case Right:
                placeX = GameConfig.TILES_IN_A_ROW - place - 1;
                placeY = tile.getY();
                break;
            case Up:
                placeX = tile.getX();
                placeY = place;
                break;
            case Down:
                placeX = tile.getX();
                placeY = GameConfig.TILES_IN_A_ROW - place - 1;
                break;
        }
        int newPos = ToBitMapPosition(placeX, placeY);
        int oldPos = ToBitMapPosition(tile.getX(), tile.getY());
        mGameField.set(newPos, tile);
        mGameField.set(oldPos, null);
        mTilesBitMap &= ~(1 << oldPos);
        mTilesBitMap |= (1 << newPos);

        int[] posFrom = tile.getPosition().clone();
        tile.setX(placeX);
        tile.setY(placeY);
        mMovementState.GetTileLinks().Add(new MovedTileLink(tile.clone(), posFrom));
    }

    @Nullable
    private GameTile TryToCreateTile(Integer value) {
        int freePosition = GetNextFreePosition(0);
        if (freePosition == -1) {
            return null;
        }

        int numberOfFreePositions = NumberOfFreePositions();
        if (numberOfFreePositions != 1) {
            int indexOfFreePosition = mRandom.nextInt(numberOfFreePositions);
            for (int i = 0; i < indexOfFreePosition; i++) {
                freePosition = GetNextFreePosition(freePosition + 1);
            }
        }

        int[] position = ToPosition(freePosition);
        return CreateTile(position[0], position[1], value);
    }

    private GameTile CreateTile(int x, int y, Integer value)
    {
        GameTile tile;
        if (value != null)
        {
            tile = new GameTile(x, y, value);
        }
        else
        {
            tile = mGameTileFactory.Create(x, y);
        }
        PlaceTile(tile);
        return tile;
    }

    private void PlaceTile(GameTile tile)
    {
        int mappedPos = ToBitMapPosition(tile.getX(), tile.getY());
        mTilesBitMap |= (1 << mappedPos);
        mGameField.set(mappedPos, tile);
        mMovementState.GetTileLinks().Add(new CreatedTileLink(tile.clone()));
    }

    private int ToBitMapPosition(int x, int y) {
        return y * GameConfig.TILES_IN_A_ROW + x;
    }

    @NotNull
    @Contract(value = "_ -> new", pure = true)
    private int[] ToPosition(int bitMapPosition) {
        return new int[]{
                bitMapPosition % 4,
                bitMapPosition / 4
        };
    }

    private boolean IsPositionFree(int position) {
        return ((~mTilesBitMap >> position) & 1) == 1;
    }

    private int NumberOfFreePositions() {
        int n = 0;
        for (int i = 0; i < GameConfig.TILES_NUMBER; i++) {
            n += IsPositionFree(i) ? 1 : 0;
        }
        return n;
    }

    private int GetNextFreePosition(int startPosition) {
        for (int i = startPosition; i < GameConfig.TILES_NUMBER; i++) {
            if (IsPositionFree(i)) {
                return i;
            }
        }
        for (int i = 0; i < startPosition; ++i) {
            if (IsPositionFree(i)) {
                return i;
            }
        }
        return -1;
    }

    private enum Direction {
        Left, Right, Up, Down
    }
}
