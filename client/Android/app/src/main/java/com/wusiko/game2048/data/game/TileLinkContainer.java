package com.wusiko.game2048.data.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TileLinkContainer
{
	private final ArrayList<CreatedTileLink> mCreated = new ArrayList<CreatedTileLink>();
	private final ArrayList<MergedTileLink> mMerged = new ArrayList<MergedTileLink>();
	private final ArrayList<MovedTileLink> mMoved = new ArrayList<MovedTileLink>();

	public void Add(CreatedTileLink link)
	{
		mCreated.add(link);
	}

	public void Add(MergedTileLink link)
	{
		mMerged.add(link);
	}

	public void Add(MovedTileLink link)
	{
		mMoved.add(link);
	}

	public List<CreatedTileLink> GetCreatedTiles()
	{
		return Collections.unmodifiableList(mCreated);
	}

	public List<MergedTileLink> GetMergedTiles()
	{
		return Collections.unmodifiableList(mMerged);
	}

	public List<MovedTileLink> GetMovedTiles()
	{
		return Collections.unmodifiableList(mMoved);
	}

}
