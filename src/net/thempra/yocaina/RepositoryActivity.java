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
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


public class RepositoryActivity extends Activity

{
	private static final String ACTION_GET_CARDS = "Get_cards";

	private static EditText txtURL;
	private static Button btnDownload;
	private static TextView txtDebug;


	ArrayList<String> card;

	ArrayList<HashMap<String, Object>> mylist = new ArrayList<HashMap<String, Object>>();
	HashMap<String, Object> map;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.repositoryactivity);
		
		txtURL= (EditText) findViewById(R.id.txtURL);
		btnDownload= (Button) findViewById(R.id.btnDownload);
		
	
			
		card = new ArrayList<String>();
	    
	    
	//	btnDownload.setOnClickListener(new OnClickListener() {
	//		public void onClick(View view) {
				
				new AsyncTask<Activity, Void, String>() {
			        @Override
			        public String doInBackground(Activity... activities) {
			        	card= getItemsFromURL("http://"+txtURL.getText().toString()+"/cards.xml","card");
			     
			        	//Send to Activity
			    		Intent conectedIntent = new Intent();
			    		conectedIntent.setAction(ACTION_GET_CARDS);
			            sendBroadcast(conectedIntent);
			            return ""; //return Utilities.DBGetOnlineVersionNumber(activities[0]);
			        }
			        
			    }.execute(this);
	    
	    
		//	 }

	    //});

			    
	    
	    if (activityReceiver != null) {
        	//Create an intent filter to listen to the broadcast sent with the action "ACTION_STRING_ACTIVITY"
        	            IntentFilter intentFilter = new IntentFilter(ACTION_GET_CARDS);
        	//Map the intent filter to the receiver
        	            registerReceiver(activityReceiver, intentFilter);
        	        }
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
            
     
            //getResources().getResourceName(resid)
			mylist.add(map);

        }
		
        
		} catch (Exception e) {
			// TODO Auto-generated catch block
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

	private String getNodeAttributeByTagName(Node parentNode, String tagNameOfAttr)
	{
	    String nodeValue = "";

	    NamedNodeMap questNodeAttr = parentNode.getAttributes();

	    if (questNodeAttr.getLength() != 0)
	        nodeValue = questNodeAttr.getNamedItem(tagNameOfAttr).getTextContent();

	    return nodeValue;
	}
	
	
	private void manageNewCards() {
		
		txtDebug = (TextView) findViewById(R.id.txtDebug);
	    
	    
		for (HashMap<String, Object> item : mylist)
		{
			txtDebug.setText(txtDebug.getText()+"\n\t- "+item.get("name").toString());
			try {
				DownloadFile(item.get("name").toString(),new URL(item.get("url").toString()));
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
			
	}

	private BroadcastReceiver activityReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
        	manageNewCards();
        }
    };


	void DownloadFile (String fileName, URL url) {
		
		try {
           
            File file = new File(fileName);

            long startTime = System.currentTimeMillis();
            Log.d("Manage", "download begining");
            Log.d("Manage", "download url:" + url);
            Log.d("Manage", "downloaded file name:" + fileName);
            /* Open a connection to that URL. */
            URLConnection ucon = url.openConnection();

            
            ucon.setReadTimeout(5000);
            ucon.setConnectTimeout(30000);
            
            /*
             * Define InputStreams to read from the URLConnection.
             */
            
            
            /*************************************************************************/
            //FIXME: Error al hacer el getInputStream 
            /*************************************************************************/
            
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
            Log.d("Manage", "download ready in"
                            + ((System.currentTimeMillis() - startTime) / 1000)
                            + " sec");

    } catch (IOException e) {
            Log.d("Manage", "Error: " + e);
    }
		
	/*	try {
	        //set the download URL, a url that points to a file on the internet
	        //this is the file to be downloaded
	       
	
	        //create the new connection
	        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
	
	        //set up some things on the connection
	        urlConnection.setRequestMethod("GET");
	        urlConnection.setDoOutput(true);
	
	        //and connect!
	        urlConnection.connect();
	
	        //set the path where we want to save the file
	        //in this case, going to save it on the root directory of the
	        //sd card.
	        File SDCardRoot = Environment.getExternalStorageDirectory();
	        //create a new file, specifying the path, and the filename
	        //which we want to save the file as.
	        File file = new File(SDCardRoot,"/yocaina/"+name);
	
	        //this will be used to write the downloaded data into the file we created
	        FileOutputStream fileOutput = new FileOutputStream(file);
	
	        //this will be used in reading the data from the internet
	        InputStream inputStream = urlConnection.getInputStream();
	
	        //this is the total size of the file
	        int totalSize = urlConnection.getContentLength();
	        //variable to store total downloaded bytes
	        int downloadedSize = 0;
	
	        //create a buffer...
	        byte[] buffer = new byte[1024];
	        int bufferLength = 0; //used to store a temporary size of the buffer
	
	        //now, read through the input buffer and write the contents to the file
	        while ( (bufferLength = inputStream.read(buffer)) > 0 ) {
	                //add the data in the buffer to the file in the file output stream (the file on the sd card
	                fileOutput.write(buffer, 0, bufferLength);
	                //add up the size so we know how much is downloaded
	                downloadedSize += bufferLength;
	                //this is where you would do something to report the prgress, like this maybe
	               // updateProgress(downloadedSize, totalSize);
	
	        }
	        //close the output stream when done
	        fileOutput.close();
	
	//catch some possible errors...
	} catch (MalformedURLException e) {
	        e.printStackTrace();
	} catch (IOException e) {
	        e.printStackTrace();
	}
	*/
	}
	
}