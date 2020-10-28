package com.wusiko.game2048.ui.game;

import android.content.Context;
import android.widget.ImageView;

import com.wusiko.game2048.data.game.GameTile;

public class GameTileView
{
	private GameTile mTile;
	private final ImageView mImageView;

	public GameTileView(Context context)
	{
		mImageView = new ImageView(context);
	}

	public ImageView GetImageView()
	{
		return mImageView;
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
