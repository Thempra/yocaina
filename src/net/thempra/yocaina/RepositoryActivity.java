package net.thempra.yocaina;


import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import java.util.ArrayList;
import java.util.HashMap;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.http.util.ByteArrayBuffer;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


public class RepositoryActivity extends Activity

{

	private static EditText txtURL;
	private static Button btnDownload;
	private static TextView txtDebug;


	ArrayList<String> card;

	ArrayList<HashMap<String, Object>> mylist = new ArrayList<HashMap<String, Object>>();
	HashMap<String, Object> map;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		 requestWindowFeature(Window.FEATURE_NO_TITLE);
	     getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
	     setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
	        
		
		setContentView(R.layout.repositoryactivity);
		
		txtURL= (EditText) findViewById(R.id.txtURL);
		btnDownload= (Button) findViewById(R.id.btnDownload);
		txtDebug= (TextView) findViewById(R.id.txtDebug);

		
	
			
		card = new ArrayList<String>();

		btnDownload.setOnClickListener(new OnClickListener() {
			public void onClick(View view) {
					
				new AsyncTask<Activity, Void, String>() {
					@Override
					protected String doInBackground(Activity... arg0) {
						
						card= getItemsFromURL("http://"+txtURL.getText().toString()+"/cards.xml","card");						 
						manageNewCards();
						
						
						
						return null;
					}
				}.execute();
				RefreshLocalCards();
			 }

	    });
		
		RefreshLocalCards();
	

	}

	private void RefreshLocalCards() {
		String[] fileList = new File(Environment.getExternalStorageDirectory().toString()+"/yocaina/").list();
		
		txtDebug.setText(getString(R.string.cardsAvailable));
		for (String f : fileList)
	    	txtDebug.setText(txtDebug.getText()+"\n - "+ f);
	}
	
private ArrayList<String> getItemsFromURL(String urlXml, String mainNode){
		
		ArrayList<String> data = new ArrayList<String>();
		
		try{
			
		URL url = new URL(urlXml);
			
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
       
        
        Document doc = db.parse(new InputSource(url.openStream()));
        doc.getDocumentElement().normalize();
		
        NodeList nodeList = doc.getElementsByTagName(mainNode);

        /** Assign textview array lenght by arraylist size */
        
        for (int i = 0; i < nodeList.getLength(); i++) {

            
            Element fstElmnt = (Element) nodeList.item(i);
            data.add (getNodeValueByTagName(fstElmnt, "card"));
            
            map = new HashMap<String, Object>();
            map.put("name", getNodeValueByTagName(fstElmnt, "name"));
            map.put("url", getNodeValueByTagName(fstElmnt, "url"));
            
     
			mylist.add(map);

        }
		
        
		} catch (Exception e) {
			e.printStackTrace();
		}
	   
	
		
		
		return data;
	    }
	
	
	private String getNodeValueByTagName(Node parentNode, String tagNameOfNode)
	{
	    String nodeValue = "";
	    if (((Element) parentNode).getElementsByTagName(tagNameOfNode).getLength() != 0)
	        if (((Element) ((Element) parentNode).getElementsByTagName(tagNameOfNode).item(0)).hasChildNodes())
	        {
	            nodeValue = ((Node) ((Element) ((Element) parentNode).getElementsByTagName(tagNameOfNode).item(0)).getChildNodes().item(0)).getNodeValue();
	        }
	    return nodeValue;
	}

	
	
	
	private void manageNewCards() {
		
		txtDebug = (TextView) findViewById(R.id.txtDebug);
	    
	    
		for (final HashMap<String, Object> item : mylist)
		{
			//txtDebug.setText(txtDebug.getText()+"\n\t- "+item.get("name").toString());
			new AsyncTask<Activity, Void, String>() {
			    

				@Override
				protected String doInBackground(Activity... arg0) {
					
					try {
						DownloadFile(item.get("name").toString(),new URL (item.get("url").toString()));
					} catch (MalformedURLException e) {
						e.printStackTrace();
					}
					return null;
				}
			}.execute(this);
		}
		
			
	}



	void DownloadFile (String fileName, URL url) {
		
		try {
           
            File file = new File(fileName);

            /* Open a connection to that URL. */
            URLConnection ucon = url.openConnection();

            
            ucon.setReadTimeout(5000);
            ucon.setConnectTimeout(30000);
            
            /*
             * Define InputStreams to read from the URLConnection.
             */
      
            InputStream is = ucon.getInputStream();
            BufferedInputStream bis = new BufferedInputStream(is);

            /*
             * Read bytes to the Buffer until there is nothing more to read(-1).
             */
            ByteArrayBuffer baf = new ByteArrayBuffer(50);
            int current = 0;
            while ((current = bis.read()) != -1) {
                    baf.append((byte) current);
            }

            /* Convert the Bytes read to a String. */
            FileOutputStream fos = new FileOutputStream(Environment.getExternalStorageDirectory().toString()+"/yocaina/"+file);
            fos.write(baf.toByteArray());
            fos.close();

    } catch (IOException e) {
            Log.d("Manage", "Error: " + e);
    }
		
	
	}
	
}



