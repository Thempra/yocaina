package net.thempra.yocaina.cards;

import java.util.ArrayList;
import java.util.List;

import android.nfc.Tag;

public abstract class Card {

	protected static final int AUTH = 1;
	protected static final int EMPTY_BLOCK = 2;
	protected static final String TAG = "yocainatags";
	protected static final int NETWORK = 6;

	protected ArrayList<byte[]> customKeys = new ArrayList<byte[]>();

	protected String type;
	protected List<String> data;
	

	public abstract Boolean setType();
	public abstract String getType(Tag tagFromIntent);

	public abstract Boolean setKey();
	public abstract String getKey();
	
	public abstract Boolean setData();
	public abstract ArrayList<String> getData(Tag tagFromIntent);
	
	public abstract Boolean dumpToFile();
	protected abstract void LoadKeys(String file);
	public abstract int blocksInSector();
	
	
	

}
