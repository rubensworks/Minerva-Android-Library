package com.rubensworks.minerva.sdk;

import java.io.IOException;
import java.io.Serializable;
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

import com.rubensworks.minerva.sdk.fetch.Fetcher;

//FOR COOKIES: http://blog.dahanne.net/2009/08/16/how-to-access-http-resources-from-android/
public class Minerva implements Serializable{
	private transient Executor exec=Executors.newSingleThreadExecutor();

	private static final Map<String,String> DATAURLMAP=new HashMap<String,String>();//to save the urls
	private volatile DataHolder tmpHolder=null;										//temp dataholder
	private volatile String salt=null;												//salt
	private volatile String sid=null;												//session id from cookie
	private String username=null;													//username after logging in
	private boolean loggedIn=false;													//if user is logged in
	private boolean error=false;													//if an error occured
	private volatile boolean fetching=false;										//if the executor is busy fetching something
	//public String data=null;	
	private Fetcher fetcher=new Fetcher();
	
	public Minerva() {
		
		//put map data
		DATAURLMAP.put("auth.getSalt", 			"http://minerva.rubensworks.net/v1/xml?method=auth.getSalt");
		DATAURLMAP.put("courses.getCourses", 	"http://minerva.rubensworks.net/v1/xml?method=courses.getCourses");
		DATAURLMAP.put("minerva.login", 		"https://minerva.ugent.be/secure/index.php?external=true");
		DATAURLMAP.put("minerva.index", 		"https://minerva.ugent.be/index.php");
		
		//immediatly start fetching the salt
		getSalt(new ExecutionDataHolder() {

			@Override
			public void onError(Exception e) {
				tmpHolder=new DataHolder("error",e.getMessage());
				error=true;
			}

			@Override
			public void onComplete(DataHolder data) {
				tmpHolder=data;
				salt=tmpHolder.getData()[0].getValue();
				System.out.println("FETCHED SALT");
			}
			
		});
	}
	
	private String makeParams(String[] names, String[] params) {
		String string="";
		for(int i=0;i<params.length;i++) {
			string+="&"+names[i]+"="+params[i];
		}
		return "&username="+username+"&cookie="+sid+string;
	}
	
	public boolean login(final String username, final String pwd) {
		this.username=username;
		this.fetcher=new Fetcher();//resets the fetched data from the previous session
		if(error) {
			return false;
		}
		
		while (salt==null) {
			if(error) {
				return false;
			}
			try {
				Thread.sleep(10);
			} catch (InterruptedException e1) {
				//do nothing
			}
		}
		
		exec.execute(new Runnable() {
			public void run() {
				sid=getSID(username,pwd,salt);
		        loggedIn=true;
		    }});
		while (sid==null) {
			if(error) {
				return false;
			}
			try {
				Thread.sleep(10);
			} catch (InterruptedException e1) {
				//do nothing
			}
		}
		
		//FETCH COURSES & CHECK IF VALID
		return this.getFetcher().fetchCourses(this)!=null;
		
		//return !("".equals(sid));
	}
	
	public boolean isLoggedIn() {
		return loggedIn;
	}
	
	public void getCourses(ExecutionDataHolder listener) {//add check! & state update
		this.execute(DATAURLMAP.get("courses.getCourses")+this.makeParams(new String[0],new String[0]), listener);
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
			error=true;
		}
		httpPost.setEntity(formEntity);
		HttpResponse response=null;
		try {
			response = httpclient.execute(httpPost);
		} catch (ClientProtocolException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			error=true;
			
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			error=true;
			
		}
		Header[] allHeaders = response.getAllHeaders();
		CookieOrigin origin = new CookieOrigin("https://minerva.ugent.be", 80,"/secure/index.php?external=true", false);
		
		for (Header header : allHeaders) {
			BrowserCompatSpec cookieSpecBase=new BrowserCompatSpec();
					List<Cookie> parse=null;
					try {
						parse = cookieSpecBase.parse(header, origin);
					} catch (MalformedCookieException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						error=true;
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
		if(exec==null)
			exec=Executors.newSingleThreadExecutor();
		exec.execute(new Runnable() {
            public void run() {
            	fetching=true;
            	//starting data load
            	
            	HttpGet uri = new HttpGet(dataUrl);    

            	DefaultHttpClient client = new DefaultHttpClient();
            	HttpResponse resp = null;
        		try {
        			resp = client.execute(uri);
        		} catch (ClientProtocolException e) {
        			listener.onError(e);
        			e.printStackTrace();
        			error=true;
        		} catch (IOException e) { 
        			listener.onError(e);
        			e.printStackTrace();
        			error=true;
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
	        			error=true;
	        		} catch (IllegalStateException e) {
	        			e.printStackTrace();
	        			listener.onError(e);
	        			error=true;
	        		} catch (SAXException e) {
	        			e.printStackTrace();
	        			listener.onError(e);
	        			error=true;
	        		} catch (IOException e) {
	        			e.printStackTrace();
	        			listener.onError(e);
	        			error=true;
	        		}
	        		
	        		if(doc!=null) {
		        		try {
		        			resp.getEntity().consumeContent();
		        			DataHolder data=DataHolder.addNodes(doc.getDocumentElement());
		        			listener.onComplete(data);
		        		} catch (IOException e) {
		        			e.printStackTrace();
		        			error=true;
		        		}
	        		}
        		}
        		fetching=false;
            }
          });
	}
	
	public boolean isFetching() {
		return this.fetching;
	}
	
	public boolean isError() {
		return this.error;
	}
	
	public void resetError() {
		this.error=false;
	}
	
	public Fetcher getFetcher() {
		return this.fetcher;
	}
}
