package com.wusiko.game2048.data.game;

import java.util.Random;

public class GameTileFactory
{
	private final float[] mProbabilities;
	private final Random mRandom;

	public GameTileFactory(final float[] probabilities, Random random)
	{
		if (probabilities == null || probabilities.length > GameConfig.TILES_NUMBER)
		{
			throw new RuntimeException("Invalid probabilities of tile creation: too less or too much tiles!");
		}

		float sumProb = 0.0f;
		for (final float prob : probabilities)
		{
			sumProb += prob;
		}

		if (Float.isNaN(sumProb) || Float.isInfinite(sumProb) ||
				Math.abs(sumProb - 1.0f) >= GameConfig.TILE_PROBABILITIES_PRECISION)
		{
			throw new RuntimeException("Invalid probabilities of tile creation: sum of probability is not one!");
		}

		mProbabilities = probabilities;
		mRandom = random;
	}

	public GameTile Create(int x, int y)
	{
		float randomProb = mRandom.nextFloat();
		float currentProb = 0.0f;
		int degree = 1;
		for (float p : mProbabilities)
		{
			if (p != 0 && randomProb < currentProb + p)
			{
				break;
			}
			currentProb += p;
			degree += 1;
		}

		return GameTile.CreateWithDegree(x, y, degree);
	}
}
