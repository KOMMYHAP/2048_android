package com.wusiko.game2048.ui.game;

import android.content.Context;
import com.wusiko.game2048.data.game.GameTile;

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
	public void SetTile(GameTile tile)
	{
		mTile = tile;
	}
}
