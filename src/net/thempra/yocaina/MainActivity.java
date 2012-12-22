package net.thempra.yocaina;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import net.thempra.yocaina.cards.Card;
import net.thempra.yocaina.cards.CardMifare;
import net.thempra.yocaina.cards.CardNfcV;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentFilter.MalformedMimeTypeException;
import android.content.res.Resources;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.MifareClassic;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class MainActivity extends Activity implements OnClickListener {

	// UI Elements
	private static TextView status_Data;
	
	private static Button btn_clear;
	private static Button btnDecode;
	private static Button btnDumpToFile;
	private static Button btnOther;
	
	private static  List<String> cmbCards;
	private static TableLayout tl;

	// NFC parts
	private static NfcAdapter mAdapter;
	private static PendingIntent mPendingIntent;
	private static IntentFilter[] mFilters;
	private static String[][] mTechLists;


	private Card currentCard;
	

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		/* Codigo para no poner el titulo */
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		
		status_Data = (TextView) findViewById(R.id.status_data);
		
		btn_clear = (Button) findViewById(R.id.btn_clear);
		btnDecode = (Button) findViewById(R.id.btn_decode);
		btnDumpToFile = (Button) findViewById(R.id.btn_dump);
		btnOther = (Button) findViewById(R.id.btn_other);
		
		btnDecode.setEnabled(false);
		btnDumpToFile.setEnabled(false);
		btnOther.setEnabled(false);

		if (!LoadCards()) {
			// Show message no cards
			Resources res = getResources();
			status_Data.setText(R.string.noCards + Environment.getExternalStorageDirectory().toString() + "/"
					+ res.getString(R.string.app_name).toLowerCase() +"\n\n" +
					"Format file (keys):\n" +
					"0xa0 0xa1 0xa2 0xa3 0xa4 0xa5\n" +
					"0xa6 0xa7 0xa8 0xa9 0x14 0x10\n" +
					"......");
			

		} else {

			// Capture Purchase button from layout
			
			// Register the onClick listener with the implementation above
			btn_clear.setOnClickListener(this);
			btnDumpToFile.setOnClickListener(dumpToFile());

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
			final Tag tagFromIntent = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
			
			status_Data.setText(R.string.reading);
			
			clearFields();
			
			//Detecting card
			if (tagFromIntent.getTechList()[0].equals("android.nfc.tech.MifareClassic"))
			{
				  AlertDialog d;
				  int default_value=0;
				 
				     //c.show();
				     AlertDialog.Builder builder=new AlertDialog.Builder(this).setSingleChoiceItems(getCards(), default_value,new  DialogInterface.OnClickListener() {
				    	 @Override
				         public void onClick(DialogInterface dialog, int position) 
				         {
				    		 dialog.dismiss(); 
				        	 currentCard = new CardMifare(cmbCards.get(position).toString());
				        	 showCardData(tagFromIntent);
				        	
				         }
				     }).setTitle(R.string.selectCard);
				     
				     d=builder.create();
				     d.show();
				 
				
				
				//currentCard = new CardMifare(cmbCards.getSelectedItem().toString());
			}
			else if (tagFromIntent.getTechList()[0].equals("android.nfc.tech.NfcV"))
				currentCard = new CardNfcV("");
			
			
		} else {
			status_Data.setText(getString(R.string.online)+" + " + getString(R.string.readyScan));
		}
	}

	
	
	private void showCardData(Tag tagFromIntent) {
		ArrayList<String> dump;
		//Reading card
		
		dump = currentCard.getData(tagFromIntent);
		if ( dump.size() ==0)
		{
			status_Data.setText(R.string.errorReading);
			showAlert(currentCard.getLastError());
		}
		else
		{
			status_Data.setText(R.string.tagRead);
			//Publish data to Activity
			for (int i = 0; i < dump.size() ; i++)
			{

				if (i%currentCard.blocksInSector()==0)
				{
				tl = (TableLayout) findViewById(R.id.tblGeneral);

				//TableRow trTitle = new TableRow(this);
			    TableRow trTitle = (TableRow)LayoutInflater.from(this).inflate(R.layout.table_section_header, null);
				trTitle.setLayoutParams(new LayoutParams(
						LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));

				
				
				TextView tvSect = new TextView(this);
				tvSect.setText("\n" + getString(R.string.section) +" " + i/currentCard.blocksInSector());
				//tvSect.setTextColor(Color.YELLOW);

				trTitle.addView(tvSect);
				tl.addView(trTitle,
						new TableLayout.LayoutParams(
								LayoutParams.FILL_PARENT,
								LayoutParams.WRAP_CONTENT));
				}
				
				TableRow tr1 = (TableRow)LayoutInflater.from(this).inflate(R.layout.table_section_data, null);
				//TableRow tr1 = new TableRow(this);
				tr1.setLayoutParams(new LayoutParams(
						LayoutParams.FILL_PARENT,
						LayoutParams.WRAP_CONTENT));
				
				

				TextView tvBlk = new TextView(this);
				tvBlk.setText(getString(R.string.block) +" " + i +":   ");

				TextView textview = new TextView(this);
				textview.setText(dump.get(i));

				tr1.addView(tvBlk);
				tr1.addView(textview);
				tl.addView(tr1, new TableLayout.LayoutParams(
						LayoutParams.FILL_PARENT,
						LayoutParams.WRAP_CONTENT));
				
			}
		}
	}

	private void showAlert(int alertCase) {
		// prepare the alert box
		AlertDialog.Builder alertbox = new AlertDialog.Builder(this);
		switch (alertCase) {

		case Card.AUTH:// Card Authentication Error
			alertbox.setMessage(R.string.authFailed);
			break;
		case Card.EMPTY_BLOCK: // Block 0 Empty
			alertbox.setMessage(R.string.blockFailed);
			break;
		case Card.NETWORK: // Communication Error
			alertbox.setMessage(R.string.errorReading);
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
		status_Data.setText(R.string.readyScan);
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
	
	private CharSequence[] getCards() {
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
		CharSequence[] cards = new CharSequence[files.length];;
		
		for (int i = 0; i< files.length; i++)
			cards[i]=files[i].getName();	
		return cards;
			
			
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
			
			 cmbCards = new ArrayList<String>();

			// Fill items
			for (File inFile : files) {
				cmbCards.add(inFile.getName());

			}

			if (cmbCards.isEmpty())
				return false;
			else
				return true;
	
	}


}
