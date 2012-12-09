package net.thempra.yocaina;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import net.thempra.yocaina.cards.CardMifare;

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
import com.overxet.*;

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

/*	// Just for alerts

	private static final int AUTH = 1;
	private static final int EMPTY_BLOCK_0 = 2;
	private static final int EMPTY_BLOCK_1 = 3;
	private static final int EMPTY_BLOCK_2 = 4;
	private static final int EMPTY_BLOCK_3 = 5;
	private static final int NETWORK = 6;
	private static final String TAG = "purchtagscanact";

*/
	
	private CardMifare crdMifare;
	

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
			Button btn_dump = (Button) findViewById(R.id.btn_dump);
			// Register the onClick listener with the implementation above
			btn_clear.setOnClickListener(this);
			btn_dump.setOnClickListener(dumpToFile());

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

	private OnClickListener dumpToFile() {
		// TODO Auto-generated method stub
		//crdMifare.dumpToFile();
		
		return null;
	}

	void resolveIntent(Intent intent) {

		// Parse the intent
		String action = intent.getAction();
		if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)) {
			// status_Data.setText("Discovered tag with intent: " + intent);
			Tag tagFromIntent = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
			
			
	/*		Ndef ndefTag = Ndef.get(tagFromIntent);
			int size = ndefTag.getMaxSize();         // tag size
			String NFCType =ndefTag.getType(); 
			boolean writable = ndefTag.isWritable(); // is tag writable?

			status_Data.setText("Type: "+NFCType);
*/			

			status_Data.setText("Reading the Tag..");
			crdMifare = new CardMifare(cmbCards.getSelectedItem().toString());
			status_Data.setText("Tag read.");

			ArrayList<String> dump = crdMifare.getData(tagFromIntent);
			for (int i = 0; i < dump.size() ; i++)
			{

				if (i%crdMifare.blocksInSector()==0)
				{
				tl = (TableLayout) findViewById(R.id.purchScanTable1);

				TableRow trTitle = new TableRow(this);
				trTitle.setLayoutParams(new LayoutParams(
						LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));

				TextView tvSect = new TextView(this);
				tvSect.setText("\nSection " + i/crdMifare.blocksInSector());
				tvSect.setTextColor(Color.YELLOW);

				trTitle.addView(tvSect);
				tl.addView(trTitle,
						new TableLayout.LayoutParams(
								LayoutParams.FILL_PARENT,
								LayoutParams.WRAP_CONTENT));
				}
			
			
					TableRow tr1 = new TableRow(this);
					tr1.setLayoutParams(new LayoutParams(
							LayoutParams.FILL_PARENT,
							LayoutParams.WRAP_CONTENT));

					TextView tvBlk = new TextView(this);
					tvBlk.setText("BLOCK " + i +":   ");

					TextView textview = new TextView(this);
					textview.setText(dump.get(i));

					tr1.addView(tvBlk);
					tr1.addView(textview);
					tl.addView(tr1, new TableLayout.LayoutParams(
							LayoutParams.FILL_PARENT,
							LayoutParams.WRAP_CONTENT));
					
				
	
			}
		} else {
			status_Data.setText("Online + Scan a tag");
		}
	}
/*
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
*/
	
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

/*	private byte[] GetKey(int n, int sector) {
		return customKeys.get(sector);
	}
*/
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


}
