package net.thempra.yocaina.cards;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import com.overxet.Utils;

import android.nfc.Tag;
import android.nfc.tech.MifareClassic;
import android.nfc.tech.Ndef;
import android.os.Environment;
import android.util.Log;


public class CardMifareClassic extends Card {

	boolean keysloaded=false;
	
	public CardMifareClassic (String filecfg)
	{
		if (this.LoadKeys(filecfg)==1)
			keysloaded= true;
	}
	
	@Override
	public Boolean setType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getType(Tag tagFromIntent) {
		// TODO Auto-generated method stub
		Ndef ndefTag = Ndef.get(tagFromIntent);
		return ndefTag.getType(); 
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
		return null;
	}

	@Override
	public ArrayList<String> getData(Tag tagFromIntent) {
		
		MifareClassic mfc = MifareClassic.get(tagFromIntent);
		
		byte[] data;
		try {
			
			mfc.connect();
			boolean auth = false;
			String cardData = null;
			
			for (int i = 0; i < mfc.getSectorCount(); i++)
			{

				// Authenticating and reading Block 0 /Sector 1
				auth = mfc.authenticateSectorWithKeyA(i, customKeys.get(i));
				
				if (auth) {

					
					// If authenticated
					for (int j = mfc.sectorToBlock(i); j < mfc
							.sectorToBlock(i)
							+ mfc.getBlockCountInSector(i); j++) {

						/* ············································· */
						data = mfc.readBlock(j);
						cardData = Utils.getHexString(data, data.length);

						if (cardData != null) {
							dump.add(cardData);

						} else {
							lasterror=EMPTY_BLOCK;
						}
					}
					

				} else {
					lasterror=AUTH;
					break;
				}
			}
		} catch (IOException e) {
			Log.e(TAG, e.getLocalizedMessage());
			lasterror=NETWORK;

		}
		return dump;
	}

	@Override
	public Boolean dumpToFile() {
		// TODO Auto-generated method stub
		return null;
	}


	
	protected int LoadKeys(String file) {
		customKeys.clear();

		try {
			File f = new File(Environment.getExternalStorageDirectory()
					+ "/yocaina/" + file);
			FileInputStream fileIS = new FileInputStream(f);
			BufferedReader buf = new BufferedReader(new InputStreamReader(
					fileIS));
			//String readString = new String();

			
			// just reading each line and pass it on the debugger
			String[] items = {"keyA0","keyA1","keyA2","keyA3","keyA4","keyA5","keyA6","keyA7","keyA8","keyA9","keyA10","keyA11","keyA12","keyA13","keyA14","keyA15",
							"keyB0","keyB1","keyB2","keyB3","keyB4","keyB5","keyB6","keyB7","keyB8","keyB9","keyB10","keyB11","keyB12","keyB13","keyB14","keyB15"};
			
			ArrayList<String> arrayKeys= Utils.getItemsFromFile(Environment.getExternalStorageDirectory()
					+ "/yocaina/" + file,"card",items);
			
			if (arrayKeys.size()== 0) return -1;
			//while ((readString = buf.readLine()) != null) {
			for ( String readString: arrayKeys){
				
				if (readString==null) break;
				
				String[] separated = readString.split(" ");

				// byte n = (byte)(Integer.parseInt(separated[0].replace("0x",
				// ""), 16) & 0xFF);
				// byte[] k1 ={ (byte) 0xa0, (byte) 0xa1, (byte) 0xa2, (byte)
				// 0xa3, (byte) 0xa4, (byte) 0xa5};

				byte[] kn = {
						(byte) (Integer.parseInt(
								separated[0].replace("0x", ""), 16) & 0xFF),
						(byte) (Integer.parseInt(
								separated[1].replace("0x", ""), 16) & 0xFF),
						(byte) (Integer.parseInt(
								separated[2].replace("0x", ""), 16) & 0xFF),
						(byte) (Integer.parseInt(
								separated[3].replace("0x", ""), 16) & 0xFF),
						(byte) (Integer.parseInt(
								separated[4].replace("0x", ""), 16) & 0xFF),
						(byte) (Integer.parseInt(
								separated[5].replace("0x", ""), 16) & 0xFF) };

				customKeys.add(kn);

			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();

		}
		return 1;
		
	}

	@Override
	public int blocksInSector() {
		// TODO Auto-generated method stub
		return 4;
	}

	@Override
	public int getLastError() {
		// TODO Auto-generated method stub
		return lasterror;
	}
	
	public boolean iskeysLoaded() {
		// TODO Auto-generated method stub
		return keysloaded;
	}
	
	
}
