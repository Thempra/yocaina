package net.thempra.yocaina;

import java.util.ArrayList;

import net.thempra.yocaina.cards.Card;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class DataActivity extends Activity {

	//private Card currentCard;
	
	private static TableLayout tl;
	
	// UI Elements
	private static TextView status_Data;
		
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.dataactivity);
		status_Data = (TextView) findViewById(R.id.status_data);
		
		Bundle extras = getIntent().getExtras();
		//Tag tag = (Tag) extras.get("TAG");
		ArrayList<String> dump= (ArrayList<String>) extras.get("DUMP");
		int bbs=  (Integer) extras.get("blocksBySector");
		
		showCardData(dump,bbs);
		
	}
	
	
	private void showCardData(ArrayList<String> dump, Integer bbs) {
		
		if ( dump.size() ==0)
		{
			status_Data.setText(R.string.errorReading);
		}
		else
		{
			status_Data.setText(R.string.tagRead);
			//Publish data to Activity
			for (int i = 0; i < dump.size() ; i++)
			{

				if (i%bbs==0)
				{
				tl = (TableLayout) findViewById(R.id.tblGeneral);

				//TableRow trTitle = new TableRow(this);
			    TableRow trTitle = (TableRow)LayoutInflater.from(this).inflate(R.layout.table_section_header, null);
				trTitle.setLayoutParams(new LayoutParams(
						LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));

				
				
				TextView tvSect = new TextView(this);
				tvSect.setText("\n" + getString(R.string.section) +" " + i/bbs);
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
}
