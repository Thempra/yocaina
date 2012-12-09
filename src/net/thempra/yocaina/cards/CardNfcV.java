package net.thempra.yocaina.cards;

import java.util.ArrayList;

import android.nfc.Tag;

public class CardNfcV extends Card {

	public CardNfcV(String string) {
		// TODO Auto-generated constructor stub
	}

	@Override
	public Boolean setType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getType(Tag tagFromIntent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Boolean setKey() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getKey() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Boolean setData() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public ArrayList<String> getData(Tag tagFromIntent) {
		// TODO Auto-generated method stub
		dump.add("Not implemented yet");
		return dump;
	}

	@Override
	public Boolean dumpToFile() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void LoadKeys(String file) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int blocksInSector() {
		// TODO Auto-generated method stub
		return 1;
	}

	@Override
	public int getLastError() {
		// TODO Auto-generated method stub
		return lasterror;
	}

}
