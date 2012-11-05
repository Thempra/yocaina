package net.thempra.yocaina;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentFilter.MalformedMimeTypeException;
import android.content.res.Resources;
import android.graphics.Color;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.MifareClassic;
import android.nfc.tech.Ndef;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class MainActivity extends Activity implements OnClickListener {

	// UI Elements
	private static TextView status_Data;
	private static Spinner cmbCards;
	private static TableLayout tl;

	// NFC parts
	private static NfcAdapter mAdapter;
	private static PendingIntent mPendingIntent;
	private static IntentFilter[] mFilters;
	private static String[][] mTechLists;
	// Hex help
	private static final byte[] HEX_CHAR_TABLE = { (byte) '0', (byte) '1',
			(byte) '2', (byte) '3', (byte) '4', (byte) '5', (byte) '6',
			(byte) '7', (byte) '8', (byte) '9', (byte) 'A', (byte) 'B',
			(byte) 'C', (byte) 'D', (byte) 'E', (byte) 'F' };

	// Just for alerts

	private static final int AUTH = 1;
	private static final int EMPTY_BLOCK_0 = 2;
	private static final int EMPTY_BLOCK_1 = 3;
	private static final int EMPTY_BLOCK_2 = 4;
	private static final int EMPTY_BLOCK_3 = 5;
	private static final int NETWORK = 6;
	private static final String TAG = "purchtagscanact";

	private ArrayList<byte[]> customKeys = new ArrayList<byte[]>();

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		status_Data = (TextView) findViewById(R.id.status_data);

		if (!LoadCards()) {
			// Show message no cards
			Resources res = getResources();
			status_Data.setText("No cards in your device\n\n" +
					"To add new card create a new file in " + Environment.getExternalStorageDirectory() + "/"
					+ res.getString(R.string.app_name).toLowerCase() +"\n\n" +
					"Format file (keys):\n" +
					"0xa0 0xa1 0xa2 0xa3 0xa4 0xa5\n" +
					"0xa6 0xa7 0xa8 0xa9 0x14 0x10\n" +
					"......");
			cmbCards.setEnabled(false);

		} else {

			// Capture Purchase button from layout
			Button btn_clear = (Button) findViewById(R.id.btn_clear);
			// Register the onClick listener with the implementation above
			btn_clear.setOnClickListener(this);

			mAdapter = NfcAdapter.getDefaultAdapter(this);
			if (mAdapter != null) {
				// Create a generic PendingIntent that will be deliver to this
				// activity.
				// The NFC stack
				// will fill in the intent with the details of the discovered
				// tag before
				// delivering to
				// this activity.
				mPendingIntent = PendingIntent.getActivity(this, 0, new Intent(
						this, getClass())
						.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
				// Setup an intent filter for all MIME based dispatches
				IntentFilter ndef = new IntentFilter(
						NfcAdapter.ACTION_TECH_DISCOVERED);

				try {
					ndef.addDataType("*/*");
				} catch (MalformedMimeTypeException e) {
					throw new RuntimeException("fail", e);
				}
				mFilters = new IntentFilter[] { ndef, };

				// Setup a tech list for all NfcF tags
				mTechLists = new String[][] { new String[] { MifareClassic.class
						.getName() } };

				Intent intent = getIntent();
				resolveIntent(intent);
			} else {
				status_Data.setText("No NFC device");

			}

		}
	}

	void resolveIntent(Intent intent) {

		// Parse the intent
		String action = intent.getAction();
		if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)) {
			// status_Data.setText("Discovered tag with intent: " + intent);
			Tag tagFromIntent = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
			
/*			Ndef ndefTag = Ndef.get(tagFromIntent);
			String NFCType =ndefTag.getType(); 
			status_Data.setText("Type: "+NFCType);
	*/		
			
			MifareClassic mfc = MifareClassic.get(tagFromIntent);
			byte[] data;
			try {
				mfc.connect();
				boolean auth = false;
				String cardData = null;
				status_Data.setText("Authenticating the Tag..");

				for (int i = 0; i < mfc.getSectorCount(); i++)
				// for(int i=0 ;i<NUM_SECTORS; i++)
				{

					tl = (TableLayout) findViewById(R.id.purchScanTable1);

					TableRow trTitle = new TableRow(this);
					trTitle.setLayoutParams(new LayoutParams(
							LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));

					TextView tvSect = new TextView(this);
					tvSect.setText("\nSection " + i);
					tvSect.setTextColor(Color.YELLOW);

					trTitle.addView(tvSect);
					tl.addView(trTitle,
							new TableLayout.LayoutParams(
									LayoutParams.FILL_PARENT,
									LayoutParams.WRAP_CONTENT));

					// Authenticating and reading Block 0 /Sector 1
					auth = mfc.authenticateSectorWithKeyA(i, customKeys.get(i));
					
					if (auth) {

						
						// If authenticated
						for (int j = mfc.sectorToBlock(i); j < mfc
								.sectorToBlock(i)
								+ mfc.getBlockCountInSector(i); j++) {

							/* ············································· */
							data = mfc.readBlock(j);
							cardData = getHexString(data, data.length);

							if (cardData != null) {
								// block_0_Data.setText(cardData);

								TableRow tr1 = new TableRow(this);
								tr1.setLayoutParams(new LayoutParams(
										LayoutParams.FILL_PARENT,
										LayoutParams.WRAP_CONTENT));

								TextView tvBlk = new TextView(this);
								tvBlk.setText("BLOCK " + j);

								TextView textview = new TextView(this);
								textview.setText(cardData);

								tr1.addView(tvBlk);
								tr1.addView(textview);
								tl.addView(tr1, new TableLayout.LayoutParams(
										LayoutParams.FILL_PARENT,
										LayoutParams.WRAP_CONTENT));

							} else {
								showAlert(EMPTY_BLOCK_0);
							}
						}
						status_Data.setText("Read ok");


					} else {
						showAlert(AUTH);
						break;
					}
				}
			} catch (IOException e) {
				Log.e(TAG, e.getLocalizedMessage());
				showAlert(NETWORK);
			}
		} else {
			status_Data.setText("Online + Scan a tag");
		}
	}

	private void showAlert(int alertCase) {
		// prepare the alert box
		AlertDialog.Builder alertbox = new AlertDialog.Builder(this);
		switch (alertCase) {

		case AUTH:// Card Authentication Error
			alertbox.setMessage("Authentication Failed on Block 0");
			break;
		case EMPTY_BLOCK_0: // Block 0 Empty
			alertbox.setMessage("Failed reading Block 0");
			break;
		case EMPTY_BLOCK_1:// Block 1 Empty
			alertbox.setMessage("Failed reading Block 1");
			break;
		case EMPTY_BLOCK_2:// Block 1 Empty
			alertbox.setMessage("Failed reading Block 2");
			break;
		case EMPTY_BLOCK_3:// Block 1 Empty
			alertbox.setMessage("Failed reading Block 3");
			break;
		case NETWORK: // Communication Error
			alertbox.setMessage("Tag reading error");
			break;
		}
		// set a positive/yes button and create a listener
		alertbox.setPositiveButton("OK", new DialogInterface.OnClickListener() {

			// Save the data from the UI to the database - already done
			public void onClick(DialogInterface arg0, int arg1) {
				clearFields();
			}
		});
		// display box
		alertbox.show();

	}

	@Override
	public void onClick(View v) {
		clearFields();
	}

	private static void clearFields() {
		// View purchScanES1 = (View) findViewById(R.id.purchScanES1);
		if (tl != null) {
			int count = tl.getChildCount();
			for (int i = 0; i < count; i++) {
				View child = tl.getChildAt(i);
				if (child instanceof TableRow)
					((ViewGroup) child).removeAllViews();
			}
		}
		status_Data.setText("Ready for Scan");
	}

	public static String getHexString(byte[] raw, int len) {
		byte[] hex = new byte[2 * len];
		int index = 0;
		int pos = 0;

		for (byte b : raw) {
			if (pos >= len)
				break;

			pos++;
			int v = b & 0xFF;
			hex[index++] = HEX_CHAR_TABLE[v >>> 4];
			hex[index++] = HEX_CHAR_TABLE[v & 0xF];
		}

		return new String(hex);
	}

	@Override
	public void onResume() {
		super.onResume();
		if (mAdapter != null)
			mAdapter.enableForegroundDispatch(this, mPendingIntent, mFilters,
					mTechLists);
	}

	@Override
	public void onNewIntent(Intent intent) {
		Log.i("Foreground dispatch", "Discovered tag with intent: " + intent);
		resolveIntent(intent);
		// mText.setText("Discovered tag " + ++mCount + " with intent: " +
		// intent);
	}

	@Override
	public void onPause() {
		super.onPause();
		if (mAdapter != null)
			mAdapter.disableForegroundDispatch(this);
	}

	private byte[] GetKey(int n, int sector) {
		return customKeys.get(sector);
	}

	private boolean LoadCards() {
		// /sdcard/yocaina
		Resources res = getResources();
		File f = new File(Environment.getExternalStorageDirectory() + "/"
				+ res.getString(R.string.app_name).toLowerCase());
		
		if (!f.isDirectory()) {
			File cardsPath = new File(Environment.getExternalStorageDirectory()
					+ "/" + res.getString(R.string.app_name).toLowerCase());
			cardsPath.mkdirs();
		}

			File[] files = f.listFiles();
			cmbCards = (Spinner) findViewById(R.id.cmbCards);
			final List<String> strValues = new ArrayList<String>();

			// Fill items
			for (File inFile : files) {
				strValues.add(inFile.getName());

			}

			if (strValues.isEmpty())
				return false;

			// Define array
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
					android.R.layout.simple_spinner_item, strValues);

			adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			cmbCards.setAdapter(adapter);

			// Add listeners
			cmbCards.setOnItemSelectedListener(new OnItemSelectedListener() {
				@Override
				public void onItemSelected(AdapterView<?> parentView,
						View selectedItemView, int position, long id) {
					// your code here
					status_Data.setText("Selected "
							+ cmbCards.getSelectedItem().toString());
					LoadKeys(cmbCards.getSelectedItem().toString());
					clearFields();
				}

				@Override
				public void onNothingSelected(AdapterView<?> parentView) {
					// your code here
					status_Data.setText("Selected "
							+ cmbCards.getSelectedItem().toString());
				}

			});
			return true;
		
	}

	private void LoadKeys(String file) {
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

}
