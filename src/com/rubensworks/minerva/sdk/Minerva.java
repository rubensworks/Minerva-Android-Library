package com.rubensworks.minerva.sdk;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public class Minerva {
	private Executor exec=Executors.newSingleThreadExecutor();

	private static final Map<String,String> DATAURLMAP=new HashMap<String,String>();
	private volatile DataHolder tmpHolder=null;
	
	public Minerva() {
		//put map data
		DATAURLMAP.put("auth.getSalt", "http://minerva.rubensworks.net/?method=auth.getSalt");
		
		getSalt(new ExecutionDataHolder() {

			@Override
			public void onError(Exception e) {
				tmpHolder=new DataHolder("error",e.getMessage());
			}

			@Override
			public void onComplete(DataHolder data) {
				tmpHolder=data;
				
			}
			
		});
		while (tmpHolder==null) {
			try {
				Thread.sleep(10);
			} catch (InterruptedException e1) {
				//do nothing
			}
		}
		
		if(!tmpHolder.isError())
			System.out.println("SALT: "+tmpHolder.getData()[0].getValue());
		else
			System.out.println("An error occured: "+tmpHolder.getValue());
		
		//TODO: Get auth cookie + use that to auth with the xml server
	}
	
	private void getSalt(ExecutionDataHolder listener) {
		this.execute(DATAURLMAP.get("auth.getSalt"), listener);
	}
	
	private void execute(final String dataUrl, final ExecutionDataHolder listener) {
		exec.execute(new Runnable() {
            public void run() {
            	//starting data load
            	
            	HttpGet uri = new HttpGet(dataUrl);    

            	DefaultHttpClient client = new DefaultHttpClient();
            	HttpResponse resp = null;
        		try {
        			resp = client.execute(uri);
        		} catch (ClientProtocolException e) {
        			listener.onError(e);
        			e.printStackTrace();
        		} catch (IOException e) { 
        			listener.onError(e);
        			e.printStackTrace();
        		}
        		if(resp!=null) {
	            	DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	            	DocumentBuilder builder;
	            	Document doc=null;
	        		try {
	        			builder = factory.newDocumentBuilder();
	        			doc = builder.parse(resp.getEntity().getContent());
	        		} catch (ParserConfigurationException e) {
	        			e.printStackTrace();
	        			listener.onError(e);
	        		} catch (IllegalStateException e) {
	        			e.printStackTrace();
	        			listener.onError(e);
	        		} catch (SAXException e) {
	        			e.printStackTrace();
	        			listener.onError(e);
	        		} catch (IOException e) {
	        			e.printStackTrace();
	        			listener.onError(e);
	        		}
	        		
	        		if(doc!=null) {
		        		try {
		        			resp.getEntity().consumeContent();
		        			DataHolder data=DataHolder.addNodes(doc.getDocumentElement());
		        			listener.onComplete(data);
		        		} catch (IOException e) {
		        			e.printStackTrace();
		        		}
	        		}
        		}
            }
          });
	}
}
