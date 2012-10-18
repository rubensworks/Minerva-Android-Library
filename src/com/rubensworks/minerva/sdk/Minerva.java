package com.rubensworks.minerva.sdk;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.cookie.Cookie;
import org.apache.http.cookie.CookieOrigin;
import org.apache.http.cookie.MalformedCookieException;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.cookie.BrowserCompatSpec;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpParams;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

//FOR COOKIES: http://blog.dahanne.net/2009/08/16/how-to-access-http-resources-from-android/
public class Minerva {
	private Executor exec=Executors.newSingleThreadExecutor();

	private static final Map<String,String> DATAURLMAP=new HashMap<String,String>();
	private volatile DataHolder tmpHolder=null;
	public String data=null;
	
	public Minerva() {
		//put map data
		DATAURLMAP.put("auth.getSalt", "http://minerva.rubensworks.net/?method=auth.getSalt");
		DATAURLMAP.put("minerva.login", "https://minerva.ugent.be/secure/index.php?external=true");
		DATAURLMAP.put("minerva.index", "https://minerva.ugent.be/index.php");
		
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
		
		//TODO: Save SID (+10min Timer) and use it to perform all the API magic! + nice login form
		exec.execute(new Runnable() {
            public void run() {
            	System.out.println(getSID("rtaelman","PWDGOESHERE",tmpHolder.getData()[0].getValue()));
            }});
	}
	
	private void getSalt(ExecutionDataHolder listener) {
		this.execute(DATAURLMAP.get("auth.getSalt"), listener);
	}
	
	private String getSID(String username, String pwd, String salt) {
		Cookie sessionCookie =null;
		HttpPost httpPost = new HttpPost(DATAURLMAP.get("minerva.login")); 
		DefaultHttpClient httpclient = new DefaultHttpClient();
		HttpParams params = httpclient.getParams(); 
		HttpClientParams.setRedirecting(params, false); 
		ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();
		postParameters.add(new BasicNameValuePair("login", username));
		postParameters.add(new BasicNameValuePair("password", pwd));
		postParameters.add(new BasicNameValuePair("authentication_salt", salt));
		postParameters.add(new BasicNameValuePair("submitAuth", "Log in"));
		UrlEncodedFormEntity formEntity=null;
		try {
			formEntity = new UrlEncodedFormEntity(postParameters,"UTF-8");
		} catch (UnsupportedEncodingException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		httpPost.setEntity(formEntity);
		HttpResponse response=null;
		try {
			response = httpclient.execute(httpPost);
		} catch (ClientProtocolException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		Header[] allHeaders = response.getAllHeaders();
		CookieOrigin origin = new CookieOrigin("https://minerva.ugent.be", 80,"/secure/index.php?external=true", false);
		
		for (Header header : allHeaders) {
			System.out.println(header.getName()+":"+header.getValue()+";");
			BrowserCompatSpec cookieSpecBase=new BrowserCompatSpec();
					List<Cookie> parse=null;
					try {
						parse = cookieSpecBase.parse(header, origin);
					} catch (MalformedCookieException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					for (Cookie cookie : parse) {
						// THE cookie
						if (cookie.getName().equals("mnrv_sid")
								&& cookie.getValue() != null && cookie.getValue() != "") {
							sessionCookie = cookie;
						}
					}
		}
		return sessionCookie==null?"":sessionCookie.getValue();
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
