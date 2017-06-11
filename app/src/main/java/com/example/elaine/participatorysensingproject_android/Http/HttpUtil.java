/**
 * class name: 
 * class description: 
 * author: dengchaoyue 
 * version: 1.0
 */
package com.example.elaine.participatorysensingproject_android.Http;

import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpUtil {

	public static final String FIRST_PAGE = "http://182.92.116.126:8080/ps/ExistPMStation?";
	public static final String WEATHER_API = "https://api.heweather.com/x3/weather?";
	public static final String WEATHER_KEY = "798cf89748404030b059057d14f79703";
	public static final String REGISTER = "http://182.92.116.126:8080/ps/user/registry?";
	public static final String LOGIN = "http://182.92.116.126:8080/ps/user/login?";

	public static void sentHttpData(final String address, final String uploadFilePath){		
		new Thread(new Runnable(){
			public void run(){
				File file = new File(uploadFilePath);
				final String TAG = "uploadFile";
				final String CHARSET = "utf-8";
				HttpURLConnection connection = null;
				int res=0;
				String result = null;
					try{
						URL url = new URL(address);
						connection = (HttpURLConnection)url.openConnection();
						connection.setRequestMethod("POST");
						connection.setConnectTimeout(80000);
						connection.setReadTimeout(80000);
						connection.setDoInput(true);
						connection.setDoOutput(true);
						connection.setRequestProperty("Accept","image/jpeg");
						connection.setRequestProperty("Connection", "Keep-Alive");
            			connection.setRequestProperty("Charset", "UTF-8");
            			connection.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
            			connection.setRequestProperty("Accept", "application/json");
            			connection.setChunkedStreamingMode(0);
            
							if (file != null) {
								DataOutputStream dos = new DataOutputStream(connection.getOutputStream());
								InputStream is = new FileInputStream(file);
								byte[] bytes = new byte[1024];
								int len = 0;
								while ((len = is.read(bytes)) != -1) {
									dos.write(bytes, 0, len);
								}
								dos.flush();
								is.close();
								dos.close();

								res = connection.getResponseCode();
								Log.e(TAG, "response code:" + res);

								if (res == 200) {
									Log.e(TAG, "request success");
									InputStream in = connection.getInputStream();
									BufferedReader reader = new BufferedReader(new InputStreamReader(in));
									StringBuilder response = new StringBuilder();
									String line;
									while((line = reader.readLine()) != null){
										response.append(line);
									}
									result = response.toString();
									Log.e(TAG, "result : " + result);
								} else {
									Log.e(TAG, "request error");
									Log.e(TAG, connection.getErrorStream().toString());
								}
							}else{
								Log.e(TAG, "file not exits");
							}
					}catch(Exception e){
						e.printStackTrace();
					}
			}
		}).start();
	}
			
	
	public static void sentHttpRequest(final String address, final HttpCallbackListener listener){
		new Thread(new Runnable(){
			public void run(){
				HttpURLConnection connection = null;
				try{
					URL url = new URL(address);
					connection = (HttpURLConnection)url.openConnection();
					connection.setRequestMethod("GET");
					connection.setConnectTimeout(80000);
					connection.setReadTimeout(80000);
					connection.setDoInput(true);
					connection.setDoOutput(true);
					InputStream in = connection.getInputStream();
					BufferedReader reader = new BufferedReader(new InputStreamReader(in));
					StringBuilder response = new StringBuilder();
					String line;
					while((line = reader.readLine()) != null){
						response.append(line);
					}
					if(listener != null){
						listener.onFinish(response.toString());
					}else{
						System.out .printf("NO RESPONSE");
					}					
				}catch(Exception e){
					if(listener != null){
						listener.onError(e);
					}
				}finally{
					if(connection != null){
						connection.disconnect();
					}
				}
			}
		}).start();
	}

}
