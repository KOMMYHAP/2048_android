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
		final int value = 1 << degree;
		switch (value)
		{
			case 2:
				return R.drawable.t2;
			case 4:
				return R.drawable.t4;
			case 8:
				return R.drawable.t8;
			case 16:
				return R.drawable.t16;
			case 32:
				return R.drawable.t32;
			case 64:
				return R.drawable.t64;
			case 128:
				return R.drawable.t128;
			case 256:
				return R.drawable.t256;
			case 512:
				return R.drawable.t512;
			case 1024:
				return R.drawable.t1024;
			case 2048:
				return R.drawable.t2048;
			// todo: add all possible value
		}
		// todo: add image to indicate invalid value
		return R.drawable.t2;
	}
}
