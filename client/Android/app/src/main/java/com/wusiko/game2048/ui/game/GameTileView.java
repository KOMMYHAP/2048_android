package com.wusiko.game2048.ui.game;

import android.content.Context;

import com.wusiko.game2048.R;
import com.wusiko.game2048.data.game.GameTile;

import org.jetbrains.annotations.NotNull;

public class GameTileView extends androidx.appcompat.widget.AppCompatImageView
{
	private GameTile mTile;

	public GameTileView(Context context)
	{
		super(context);
	}

	public GameTile GetTile()
	{
		return mTile;
	}

	public void SetTile(@NotNull GameTile tile)
	{
		mTile = tile;
		setImageResource(DegreeToResource(tile.getDegree()));
	}

	private static int DegreeToResource(int degree)
	{
		// stub
		return R.drawable.test;
	}
}
