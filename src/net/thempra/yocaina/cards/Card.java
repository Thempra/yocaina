package net.thempra.yocaina.cards;

import java.util.ArrayList;
import java.util.List;

import android.nfc.Tag;

public abstract class Card {

	public static final int AUTH = 1;
	public static final int EMPTY_BLOCK = 2;
	public static final String TAG = "yocainatags";
	public static final int NETWORK = 6;
	

	protected ArrayList<byte[]> customKeys = new ArrayList<byte[]>();
	protected int lasterror =0;
	protected ArrayList<String> dump= new ArrayList<String>();
	
	

	public abstract Boolean setType();
	public abstract String getType(Tag tagFromIntent);

	public abstract Boolean setKey();
	public abstract String getKey();
	
	public abstract Boolean setData();
	public abstract ArrayList<String> getData(Tag tagFromIntent);
	
	public abstract Boolean dumpToFile();
	protected abstract void LoadKeys(String file);
	public abstract int blocksInSector();
	public abstract int getLastError();
	
	
	

}
