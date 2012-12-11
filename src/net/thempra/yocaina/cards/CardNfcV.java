package net.thempra.yocaina.cards;

import java.io.IOException;
import java.util.ArrayList;

import com.overxet.Utils;

import android.nfc.Tag;
import android.nfc.TagLostException;
import android.nfc.tech.NfcV;
import android.util.Log;

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
		
		NfcV nfcV = NfcV.get(tagFromIntent);
		//byte[] dataSend = null;
		byte[] dataRecieve;
		String cardData = null;
		
       // if ( nfcV == null ) throw new Exception("tag is not ISO15693(NFC-V) ");
        try {
            nfcV.connect();
            try {
            	dataRecieve=nfcV.transceive(new byte[] {0x11, 0x24, 0x11});
            	cardData = Utils.getHexString(dataRecieve, dataRecieve.length);

				if (cardData != null) {
					dump.add(cardData);

				} else {
					lasterror=EMPTY_BLOCK;
				}
            } finally {
                nfcV.close();
            }
        } catch (TagLostException e) {
            return null; //Tag Lost
        } catch (IOException e) {
            //throw new Exception(e);
            Log.e(TAG, e.getMessage());
        }
        
		
		
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
