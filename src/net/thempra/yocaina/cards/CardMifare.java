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


public class CardMifare extends Card {

	
	public CardMifare (String filecfg)
	{
		this.LoadKeys(filecfg);
		
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


	
	protected void LoadKeys(String file) {
		customKeys.clear();

		try {
			File f = new File(Environment.getExternalStorageDirectory()
					+ "/yocaina/" + file);
			FileInputStream fileIS = new FileInputStream(f);
			BufferedReader buf = new BufferedReader(new InputStreamReader(
					fileIS));
			String readString = new String();

			// just reading each line and pass it on the debugger

			while ((readString = buf.readLine()) != null) {
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
	
}
