package com.wusiko.game2048.ui.game;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.wusiko.game2048.R;
import com.wusiko.game2048.data.game.CreatedTileLink;
import com.wusiko.game2048.data.game.GameConfig;
import com.wusiko.game2048.data.game.MergedTileLink;
import com.wusiko.game2048.data.game.MovedTileLink;
import com.wusiko.game2048.data.game.MovementState;
import com.wusiko.game2048.data.login.LeaderBoard;
import com.wusiko.game2048.data.login.LeaderBoardRecord;
import com.wusiko.game2048.data.login.LoggedInUser;
import com.wusiko.game2048.data.login.LoginRepository;
import com.wusiko.game2048.ui.login.LoginActivity;
import com.wusiko.game2048.ui.utils.OnSwipeTouchListener;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressLint("ViewConstructor")
public class GameBoardView extends View {
    private final String TAG = "GameBoardView";
    private final HashMap<Integer, GameTileView> mTileViewsByIndex = new HashMap<>(GameConfig.TILES_NUMBER);
    private final FrameLayout mBoardLayout;
    private final TextView mScoresView;
    private final TextView mHighScoresView;
    private final ArrayList<TextView> mScoresTop = new ArrayList<>();
    private final Context mContext;
    private final GameBoardViewModel mGameBoardViewModel;
    private int mTileSize;
    private int mTileMargin;

    public GameBoardView(Context context) {
        super(context);
        mContext = context;

        if (!(context instanceof AppCompatActivity)) {
            throw new RuntimeException("Unknown context class!");
        }

        final AppCompatActivity activity = (AppCompatActivity) (context);

        mBoardLayout = activity.findViewById(R.id.board_layout);
        mGameBoardViewModel = new ViewModelProvider(activity, new GameBoardViewModelFactory()).get(GameBoardViewModel.class);

        mScoresTop.add((TextView) activity.findViewById(R.id.scores_top_1));
        mScoresTop.add((TextView) activity.findViewById(R.id.scores_top_2));
        mScoresTop.add((TextView) activity.findViewById(R.id.scores_top_3));

        UpdateLeaderBoard();

        Button exitButton = activity.findViewById(R.id.button_exit);
        exitButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Exit();
            }
        });

        Button restartButton = activity.findViewById(R.id.button_restart);
        restartButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Restart();
            }
        });

        activity.getWindow().getDecorView().setOnTouchListener(new OnSwipeTouchListener(mContext) {
            @Override
            public void onSwipeBottom() {
                mGameBoardViewModel.OnMoveDown();
            }

            @Override
            public void onSwipeRight() {
                mGameBoardViewModel.OnMoveRight();
            }

            @Override
            public void onSwipeLeft() {
                mGameBoardViewModel.OnMoveLeft();
            }

            @Override
            public void onSwipeTop() {
                mGameBoardViewModel.OnMoveUp();
            }
        });

        mGameBoardViewModel.GetMovementState().observe(activity, new Observer<MovementState>() {
            @Override
            public void onChanged(MovementState movementState) {
                try {
                    for (Object entry : movementState.GetTileLinks().GetChanges()) {
                        if (entry instanceof CreatedTileLink) {
                            CreateTile(((CreatedTileLink) entry));
                        } else if (entry instanceof MovedTileLink) {
                            MoveTile(((MovedTileLink) entry));
                        } else if (entry instanceof MergedTileLink) {
                            MergeTile(((MergedTileLink) entry));
                        }
                    }

                    if (movementState.IsVictory()) {
                        OnGameFinished(true);
                    } else if (movementState.IsLose()) {
                        OnGameFinished(false);
                    }
                    PrintBoard();
                } catch (RuntimeException e) {
                    Log.e(TAG, e.getMessage() != null ? e.getMessage() : "no error");
                }
            }
        });

        mScoresView = activity.findViewById(R.id.text_scores);
        mGameBoardViewModel.GetScores().observe(activity, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                String text = activity.getResources().getString(R.string.game_text_scores);
                mScoresView.setText(text.replace("%scores%", integer.toString()));
            }
        });

        mHighScoresView = activity.findViewById(R.id.text_high_scores);
        mGameBoardViewModel.GetHighScores().observe(activity, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                int scores = integer;
                LoginRepository.getInstance().UpdateLoggedInUserData(scores);
                UpdateLeaderBoard();

                String text = activity.getResources().getString(R.string.game_text_high_scores);
                mHighScoresView.setText(text.replace("%scores%", integer.toString()));
            }
        });

        // Apply resizing only when view will be created:
        activity.findViewById(R.id.root_layout).post(new Runnable() {
            @Override
            public void run() {
                Resize();
                mGameBoardViewModel.OnRestart();
            }
        });
    }

    private static int ToMappedIndex(int x, int y) {
        return x + y * GameConfig.TILES_IN_A_ROW;
    }

    private void OnGameFinished(boolean victory) {
        String toastMsg = victory ? "Victory!" : "Lose!";
        Toast.makeText(mContext, toastMsg, Toast.LENGTH_SHORT).show();
    }

    private void Exit() {
        if (mContext instanceof AppCompatActivity) {
            AppCompatActivity activity = (AppCompatActivity) (mContext);
            activity.setResult(Activity.RESULT_OK);
            activity.finish();
            Intent intent = new Intent(activity, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            activity.startActivity(intent);
        }
    }

    private void UpdateLeaderBoard() {
        LoggedInUser thisPlayer = LoginRepository.getInstance().getLoggedInUser();
        final List<LeaderBoardRecord> records = thisPlayer.getLeaderBoard().getRecords();

        final String textScoresPattern = getResources().getString(R.string.scores_top_text);
        for (int i = 0; i < mScoresTop.size(); ++i) {
            final LeaderBoardRecord record = records.get(i);
            final String player = record.getUsername();
            final String scores = Integer.toString(record.getMaxScores());
            String textScores = textScoresPattern
                    .replace("%player%", player)
                    .replace("%scores%", scores);
            mScoresTop.get(i).setText(textScores);
        }
    }

    public void Resize() {
        final DisplayMetrics metrics = getResources().getDisplayMetrics();
        final float density = metrics.density;
        final int height = metrics.heightPixels;
        final int width = metrics.widthPixels;

        final int boardMargin = (int) (density * 10);
        final int boardMaxWidth = width - boardMargin * 2;
        final int boardMaxHeight = height - boardMargin * 2;
        final int boardSize = Math.min(boardMaxWidth, boardMaxHeight);

        mBoardLayout.setLayoutParams(new FrameLayout.LayoutParams(boardSize, boardSize));

        // Algorithm to determine tile's size:
        // {
        //  tile_margin + tile_n_in_a_row * (tile_margin + tile_size) = board_size
        //  tile_margin = tile_size / 8
        // }
        //   =>
        // tile_size / 8 + tile_n_in_a_row * (9 / 8 * tile_size) = board_size
        //   =>
        // tile_size + 9 * tile_n_in_a_row * tile_size = 8 * board_size
        //   =>
        // tile_sze = 8 * board_size / (1 + 9 * tile_n_in_a_row)
        //   =>
        final int tileSize = 8 * boardSize / (1 + 9 * GameConfig.TILES_IN_A_ROW);

        mTileSize = Math.max((int) (density * 64), tileSize);
        mTileMargin = Math.max((int) (density * 8), mTileSize / 8);

        Log.d(TAG, String.format(
                "Resize: \n" +
                        "    display height = %d\n" +
                        "    display width = %d\n" +
                        "    display density = %f\n" +
                        "    board size = %d\n" +
                        "    tile size = %d\n" +
                        "    tile margin = %d\n",
                height,
                width,
                density,
                boardSize,
                mTileSize,
                mTileMargin));

        for (Map.Entry<Integer, GameTileView> entry : mTileViewsByIndex.entrySet()) {
            UpdateGeometry(entry.getValue());
        }
    }

    private void Restart() {
        mTileViewsByIndex.clear();
        mBoardLayout.removeAllViews();
        mGameBoardViewModel.OnRestart();
    }

    private void PrintBoard() {
        StringBuilder str = new StringBuilder();
        for (int y = 0; y < 4; ++y) {
            for (int x = 0; x < 4; ++x) {
                int n = 0;
                GameTileView tileView = mTileViewsByIndex.get(ToMappedIndex(x, y));
                if (tileView != null) {
                    n = tileView.GetTile().getValue();
                }
                str.append(String.format("%4d", n));
                if (x != 3) {
                    str.append(" | ");
                }
            }
            str.append("\n-------------------------\n");
        }
        Log.d(TAG, "Board:\n" + str);
    }

    private void CreateTile(@NotNull CreatedTileLink link) {
        final int[] boardPos = link.GetTile().getPosition();
        final int index = ToMappedIndex(boardPos[0], boardPos[1]);
        if (mTileViewsByIndex.containsKey(index)) {
            throw new RuntimeException(String.format("Cannot create tile: position (%d; %d) is busy!", boardPos[0], boardPos[1]));
        }

        GameTileView tileView = new GameTileView(mContext);
        tileView.SetTile(link.GetTile());
        UpdateGeometry(tileView);

        mBoardLayout.addView(tileView);
        mTileViewsByIndex.put(index, tileView);

        Log.d(TAG, String.format("Tile created: %d: (%d; %d)",
                link.GetTile().getValue(),
                boardPos[0], boardPos[1]));
    }

    private void MoveTile(@NotNull MovedTileLink link) {
        final int[] oldPos = link.GetPositionFrom();
        final int oldIndex = ToMappedIndex(oldPos[0], oldPos[1]);

        GameTileView tileView = mTileViewsByIndex.get(oldIndex);
        if (tileView == null) {
            throw new RuntimeException(String.format("Cannot move tile: position (%d; %d) is empty!", oldPos[0], oldPos[1]));
        }

        final int[] pos = link.GetTile().getPosition();
        final int index = ToMappedIndex(pos[0], pos[1]);

        if (mTileViewsByIndex.containsKey(index)) {
            throw new RuntimeException(String.format("Cannot move tile: position (%d; %d) is busy!", pos[0], pos[1]));
        }

        mTileViewsByIndex.remove(oldIndex);
        mTileViewsByIndex.put(index, tileView);

        tileView.SetTile(link.GetTile());
        UpdateGeometry(tileView);

        int[] to = link.GetTile().getPosition();
        Log.i(TAG, String.format("Tile moved: %d (%d; %d) -> (%d; %d)",
                link.GetTile().getValue(),
                oldPos[0], oldPos[1],
                to[0], to[1]));
    }

    private void MergeTile(@NotNull MergedTileLink link) {
        final int[] posFrom = link.GetFrom().getPosition();
        final int indexFrom = ToMappedIndex(posFrom[0], posFrom[1]);
        GameTileView tileViewFrom = mTileViewsByIndex.get(indexFrom);
        if (tileViewFrom == null) {
            throw new RuntimeException(String.format("Cannot merge tile: position 'from' (%d; %d) is empty!", posFrom[0], posFrom[1]));
        }

        final int[] posTo = link.GetTo().getPosition();
        final int indexTo = ToMappedIndex(posTo[0], posTo[1]);
        final GameTileView tileViewTo = mTileViewsByIndex.get(indexTo);
        if (tileViewTo == null) {
            throw new RuntimeException(String.format("Cannot merge tile: position 'to' (%d; %d) is empty!", posTo[0], posTo[1]));
        }

        mTileViewsByIndex.remove(indexFrom);
        tileViewTo.SetTile(link.GetTo());
        mBoardLayout.removeView(tileViewFrom);

        int tileFromValue = link.GetFrom().getValue();
        int tileToValue = link.GetTo().getValue();

        Log.i(TAG, String.format("Tiles merged: %d (%d; %d) + %d (%d; %d) = %d (%d; %d)",
                tileFromValue, posFrom[0], posFrom[1],
                tileToValue / 2, posTo[0], posTo[1],
                tileToValue, posTo[0], posTo[1]));
    }

    @NotNull
    @Contract(value = "_, _ -> new", pure = true)
    private int[] ToScreenPosition(int x, int y) {
        return new int[]{
                mTileMargin + x * (mTileSize + mTileMargin),
                mTileMargin + y * (mTileSize + mTileMargin)
        };
    }

    private void UpdateGeometry(@NotNull GameTileView tileView) {
        final int[] boardPos = tileView.GetTile().getPosition();
        final int[] screenPos = ToScreenPosition(boardPos[0], boardPos[1]);

        tileView.setLayoutParams(new FrameLayout.LayoutParams(mTileSize, mTileSize));
        tileView.setTranslationX(screenPos[0]);
        tileView.setTranslationY(screenPos[1]);
    }
}

