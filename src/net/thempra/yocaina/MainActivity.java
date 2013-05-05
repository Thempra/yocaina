package net.thempra.yocaina;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


import net.thempra.yocaina.cards.Card;
import net.thempra.yocaina.cards.CardMifareClassic;
import net.thempra.yocaina.cards.CardNfcV;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentFilter.MalformedMimeTypeException;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.MifareClassic;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity implements OnClickListener {

	// UI Elements
	private static TextView status_Data;
	
	private static Button btn_scan;
	private static Button btnDecode;
	private static Button btnDumpToFile;
	private static Button btnClone;
	private static Button btnRepo;
	private static Button btnOther;
	
	
	private static  List<String> cmbCards;

	// NFC parts
	private static NfcAdapter mAdapter;
	private static PendingIntent mPendingIntent;
	private static IntentFilter[] mFilters;
	private static String[][] mTechLists;


	private Card currentCard;
	
    Dialog dialogToScan ;


	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		/* Codigo para no poner el titulo */
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mainactivity);

		AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
		status_Data = (TextView) findViewById(R.id.status_data);
		
		btn_scan = (Button) findViewById(R.id.btn_scan);
		btnDecode = (Button) findViewById(R.id.btn_decode);
		btnDumpToFile = (Button) findViewById(R.id.btn_dump);
		btnClone = (Button) findViewById(R.id.btn_clone);
		btnRepo = (Button) findViewById(R.id.btn_repository);
		btnOther= (Button) findViewById(R.id.btn_other);
		
		
		btnRepo.setOnClickListener(new OnClickListener() {
			public void onClick(View view) {
				
	            Intent repo = new Intent(MainActivity.this, RepositoryActivity.class);
	            startActivity(repo);
	        }

	    });
		
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
			//btn_scan.setOnClickListener(this);
			

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
			
			
			
			btn_scan.setOnClickListener(new OnClickListener() {
				public void onClick(View view) {
					dialogToScan = new Dialog(MainActivity.this);
					dialogToScan.setTitle(getString(R.string.neardevice));
					dialogToScan.show();
			        
				}
			});

		}
		
		
		btnDecode.setOnClickListener(new OnClickListener() {
			public void onClick(View view) {
				AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
				dialog.setTitle(getString(R.string.developing));
				dialog.setMessage(getString(R.string.notImplemented));
				dialog.setPositiveButton("OK", null);
				dialog.show();
				
			}
		});
		
		btnDumpToFile.setOnClickListener(new OnClickListener() {
			public void onClick(View view) {

				if (currentCard != null)
				{
					String path=currentCard.dumpToFile("dumped");
					
					AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
					dialog.setTitle(getString(R.string.dump));
					dialog.setPositiveButton(getString(R.string.ok), null);
					
					if (!path.isEmpty() )
						dialog.setMessage(getString(R.string.dumped)+path);
					else
						dialog.setMessage(getString(R.string.dumped_error));

					dialog.show();
				}
				else
				{
					AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
					dialog.setTitle(getString(R.string.dump));
					dialog.setMessage(getString(R.string.neardevice)+"\n"+getString(R.string.and_try_again));
					dialog.setPositiveButton(getString(R.string.ok), null);
					dialog.show();
				}
				
			}
		});
		
		btnClone.setOnClickListener(new OnClickListener() {
			public void onClick(View view) {
				AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
				dialog.setTitle(getString(R.string.developing));
				dialog.setMessage(getString(R.string.notImplemented));
				dialog.setPositiveButton("OK", null);
				dialog.show();
				
			}
		});
		
		btnOther.setOnClickListener(new OnClickListener() {
			public void onClick(View view) {
				AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
				dialog.setTitle(getString(R.string.developing));
				dialog.setMessage(getString(R.string.notImplemented));
				dialog.setPositiveButton("OK", null);
				dialog.show();
				
			}
		});
		
		
	}

	
	
	@Override
	public void onClick(View v) {
		//clearFields();
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
		if ((dialogToScan!=null) && (dialogToScan.isShowing()))
			dialogToScan.dismiss();
		
	}

	@Override
	public void onPause() {
		super.onPause();
		if (mAdapter != null)
			mAdapter.disableForegroundDispatch(this);
	}

	
	void resolveIntent(Intent intent) {

		// Parse the intent
		String action = intent.getAction();
		if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)) {
			// status_Data.setText("Discovered tag with intent: " + intent);
			final Tag tagFromIntent = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
			
			status_Data.setText(R.string.reading);
			
			//clearFields();
			
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
				        	 currentCard = new CardMifareClassic(cmbCards.get(position).toString());
				        	 
				        	 if (!((CardMifareClassic) currentCard).iskeysLoaded())
				        	 {
				        		 
				        		 AlertDialog alert = new AlertDialog.Builder(MainActivity.this).create();
				                 alert.setTitle("Files out of date");
				                 alert.setMessage("Your files are out of date.\n Please update new format files from the repository");
				                 alert.setButton("OK", new DialogInterface.OnClickListener() {

				                     public void onClick(DialogInterface dialog, int which) {
				                         // TODO Auto-generated method stub

				                     }
				                 });
				                 alert.show();
				                 
				        	 }else
				        	 {
				        		// showCardData(tagFromIntent);
				        		 ArrayList<String> dump;
				        			//Reading card
				        			
				        		dump = currentCard.getData(tagFromIntent);
				        		status_Data.setText(R.string.tagRead);
				        		
				        		if ( dump.size() ==0)
				        		{
				        			showAlert(currentCard.getLastError());
				        		}else
				        		{
				        			
					        		Intent showScan = new Intent(MainActivity.this, DataActivity.class);
					        		//Send data to other Activity
					        		showScan.putExtra("DUMP",dump);
					        		showScan.putExtra("blocksBySector", currentCard.blocksInSector());
					        		//showScan.putExtra("CARD",currentCard);
					        		
					 	            startActivity(showScan);
				        		}
				        	 }
				        	
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
		
		//FIXME: Patch before to make key dir, and migrate all filess
		int totalKeys=0;
		for (int i = 0; i< files.length; i++)	
		{
			if (files[i].isFile())
				totalKeys++;
		}
		
		CharSequence[] cards = new CharSequence[totalKeys];
		
		for (int i = 0; i< files.length; i++)	
		{
			if (files[i].isFile())
				cards[i]=files[i].getName();	
		
		}
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
				if (inFile.isFile())
					cmbCards.add(inFile.getName());

			}

			if (cmbCards.isEmpty())
				return false;
			else
				return true;
	
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
				//clearFields();
			}
		});
		// display box
		alertbox.show();

	}	
	
}
