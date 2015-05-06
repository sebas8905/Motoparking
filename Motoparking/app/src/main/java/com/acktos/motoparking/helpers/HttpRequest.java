package com.acktos.motoparking.helpers;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;

import android.net.http.AndroidHttpClient;


public class HttpRequest {
	
	private String url;
	private HttpURLConnection conection;
	private MultipartEntity paramsEntity;

	
	
	public HttpRequest(String url) {
		setUrl(url);
		paramsEntity=new MultipartEntity();
	}
	
	public String request(){
		String result="";
		InputStream in=connect();
		result=readStream(in);
		return result;
	}
	
	public InputStream connect(){
		try {
			URL url =new URL(this.url);
			conection=(HttpURLConnection) url.openConnection();
			conection.connect();
			InputStream in =new BufferedInputStream(url.openStream()); 
			return in;
			
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
	public String postRequest(){
		
		String responseData=null;
		HttpParams httpParams = new BasicHttpParams();
	    HttpConnectionParams.setConnectionTimeout(httpParams, 20000);
	    HttpConnectionParams.setSoTimeout(httpParams, 20000);
	    HttpClient httpclient = new DefaultHttpClient(httpParams);
	    
	 
	    try {
	    	HttpPost httpPost=new HttpPost(url);
	        httpPost.setEntity(paramsEntity);
	    	//httpPost.setHeader("Accept", "*/*");
	        //httpPost.setHeader("Content-type", "application/x-www-form-urlencoded");
	        //httpPost.setHeader("Content-Encoding", "gzip, deflate, compress");
	        //httpPost.setHeader("Accept-Encoding", "gzip,deflate");
	        
	    	HttpResponse response = httpclient.execute(httpPost);
	    	if(response!=null){
                InputStream in = response.getEntity().getContent();
                responseData=readStream(in);
            }
	    	
	    } catch (UnsupportedEncodingException e) {
	    	e.printStackTrace();
	    } catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
       

	    return responseData;
	}
	
	
	public static String httpPostRequest(String url, ArrayList<NameValuePair> params){
		try {
			AndroidHttpClient httpClient = AndroidHttpClient.newInstance("ANDROID-ACKTOS");
			System.out.println("url:"+url);
			URL urlObj = new URL(url);
			HttpPost httpPostRequest = new HttpPost(url);
			if(!params.isEmpty()){
				HttpEntity httpEntity = new UrlEncodedFormEntity(params,HTTP.UTF_8);
			    httpPostRequest.setEntity(httpEntity);		    
			}
			
			//AuthScope scope = new AuthScope(urlObj.getHost(), urlObj.getPort());
			//UsernamePasswordCredentials creds = new UsernamePasswordCredentials(username, password);

			//CredentialsProvider cp = new BasicCredentialsProvider();
			//cp.setCredentials(scope, creds);
			//HttpContext credContext = new BasicHttpContext();
			//credContext.setAttribute(ClientContext.CREDS_PROVIDER, cp);


			//HttpResponse response = httpClient.execute(httpPostRequest, credContext);
			HttpResponse response = httpClient.execute(httpPostRequest);

			HttpEntity httpEntity = response.getEntity();

			InputStream is = httpEntity.getContent();

			BufferedReader reader = new BufferedReader(new InputStreamReader(
					is, "iso-8859-1"), 8);
			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
			is.close();
			String json = sb.toString();
			System.out.println("httpPost:" + json);
			StatusLine status = response.getStatusLine();
			System.out.println("httpPost staus:" + status.toString());

			httpClient.close();
			
			if(status.getStatusCode()!=200)
				return "500";
			else
				return json;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}
	
	public void setParam(String name,String value){
		try {
			if(value!=null){
				paramsEntity.addPart(name, new StringBody(value));
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
	
	
	public String readStream(InputStream in){
		
		String result="";
		BufferedReader buffer=new  BufferedReader(new InputStreamReader(in));
		
		
		String s="";
		try {
			while((s=buffer.readLine())!=null){
				result+=s;
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			if(buffer!=null){
				try {
					buffer.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return result;
	}
	
	public void setUrl(String url){
		this.url=url;
	}
	

}
