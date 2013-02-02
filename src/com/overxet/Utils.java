package com.overxet;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xmlpull.v1.XmlSerializer;

import android.os.Message;
import android.util.Xml;


public class Utils {
	// Hex help
	private static final byte[] HEX_CHAR_TABLE = { (byte) '0', (byte) '1',
			(byte) '2', (byte) '3', (byte) '4', (byte) '5', (byte) '6',
			(byte) '7', (byte) '8', (byte) '9', (byte) 'A', (byte) 'B',
			(byte) 'C', (byte) 'D', (byte) 'E', (byte) 'F' };

	

	
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

/*	private String writeXml(List<Message> messages){
	    XmlSerializer serializer = Xml.newSerializer();
	    StringWriter writer = new StringWriter();
	    try {
	        serializer.setOutput(writer);
	        serializer.startDocument("UTF-8", true);
	        serializer.startTag("", "messages");
	        serializer.attribute("", "number", String.valueOf(messages.size()));
	        for (Message msg: messages){
	            serializer.startTag("", "dump");
	            serializer.attribute("", "date", msg.getDate());
	            serializer.startTag("", "title");
	            serializer.text(msg.getTitle());
	            serializer.endTag("", "title");
	            serializer.startTag("", "url");
	            serializer.text(msg.getLink().toExternalForm());
	            serializer.endTag("", "url");
	            serializer.startTag("", "body");
	            serializer.text(msg.getDescription());
	            serializer.endTag("", "body");
	            serializer.endTag("", "message");
	        }
	        serializer.endTag("", "messages");
	        serializer.endDocument();
	        return writer.toString();
	    } catch (Exception e) {
	        throw new RuntimeException(e);
	    } 
	}
	*/
	
	public static String getStringFromNode(Node root) throws IOException {

        StringBuilder result = new StringBuilder();

        if (root.getNodeType() == 3)
            result.append(root.getNodeValue());
        else {
            if (root.getNodeType() != 9) {
                StringBuffer attrs = new StringBuffer();
                for (int k = 0; k < root.getAttributes().getLength(); ++k) {
                    attrs.append(" ").append(
                            root.getAttributes().item(k).getNodeName()).append(
                            "=\"").append(
                            root.getAttributes().item(k).getNodeValue())
                            .append("\" ");
                }
                result.append("<").append(root.getNodeName()).append(" ")
                        .append(attrs).append(">");
            } else {
                result.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
            }

            NodeList nodes = root.getChildNodes();
            for (int i = 0, j = nodes.getLength(); i < j; i++) {
                Node node = nodes.item(i);
                result.append(getStringFromNode(node));
            }

            if (root.getNodeType() != 9) {
                result.append("</").append(root.getNodeName()).append(">");
            }
        }
        return result.toString();
    }
	
	
	/****************************************************************************************/
	
	public static ArrayList<String> getItemsFromFile(String xmlPath, String mainNode, String[] subitems){
		
		ArrayList<HashMap<String, Object>> mylist = new ArrayList<HashMap<String, Object>>();
		HashMap<String, Object> map;
		
	
		ArrayList<String> data = new ArrayList<String>();
		ArrayList<String> datareturn = new ArrayList<String>();
		
		
		try{
			
		//URL url = new URL(urlXml);
			
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
       
        
        Document doc = db.parse( new File(xmlPath));
        doc.getDocumentElement().normalize();
		
        NodeList nodeList = doc.getElementsByTagName(mainNode);

        /** Assign textview array lenght by arraylist size */
        
        for (int i = 0; i < nodeList.getLength(); i++) {

            
            Element fstElmnt = (Element) nodeList.item(i);
            data.add (getNodeValueByTagName(fstElmnt, "keys"));
            
            map = new HashMap<String, Object>();
            
            for (String s : subitems)
            {
            	map.put(s, getNodeValueByTagName(fstElmnt, s));
            	datareturn.add(getNodeValueByTagName(fstElmnt, s));
            }
            
			mylist.add(map);

        }
		
        
		} catch (Exception e) {
			e.printStackTrace();
		}
	
		
		return datareturn;
	    }
	
	
	private static String getNodeValueByTagName(Node parentNode, String tagNameOfNode)
	{
	    String nodeValue = "";
	    if (((Element) parentNode).getElementsByTagName(tagNameOfNode).getLength() != 0)
	        if (((Element) ((Element) parentNode).getElementsByTagName(tagNameOfNode).item(0)).hasChildNodes())
	        {
	            nodeValue = ((Node) ((Element) ((Element) parentNode).getElementsByTagName(tagNameOfNode).item(0)).getChildNodes().item(0)).getNodeValue();
	        }
	    return nodeValue;
	}

	
	
	
}