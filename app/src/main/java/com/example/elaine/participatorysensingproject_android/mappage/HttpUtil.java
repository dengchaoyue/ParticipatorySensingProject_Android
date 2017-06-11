/**
 * class name: 
 * class description: 
 * author: dengchaoyue 
 * version: 1.0
 */
package com.example.elaine.participatorysensingproject_android.mappage;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import com.example.elaine.participatorysensingproject_android.Http.HttpCallbackListener;
import android.annotation.SuppressLint;
import android.os.Message;
import android.util.Log;

public class HttpUtil {

	public static Message msg = new Message();
	
	public static void sentHttpData(final String address, final String uploadFilePath, final String extraMsg, final HttpCallbackListener httpCallbackListener){
		new Thread(new Runnable() {
			@SuppressLint("NewApi")
			public void run() {
				File file = new File(uploadFilePath);
				System.out.println(file.length());
				
				final String TAG = "uploadFile";
				final String CHARSET = "utf-8";
				HttpURLConnection connection = null;
				int res = 0;
				String result = null;
				

				try {
					// int length = Integer.parseInt(file.length()+"");
					URL url = new URL(address);
					connection = (HttpURLConnection) url.openConnection();
					//connection.setChunkedStreamingMode(0);
					connection.setFixedLengthStreamingMode(file.length());
					connection.setRequestMethod("POST");
					connection.setConnectTimeout(8000000);
					connection.setReadTimeout(8000000);
					connection.setDoInput(true);
					connection.setDoOutput(true);
					connection.setUseCaches(false);
					connection.setRequestProperty("Content-Length", String.valueOf(file.length()));

					/*connection.setRequestProperty("Accept", "image/jpeg");
					connection.setRequestProperty("Connection", "Keep-Alive");
					connection.setRequestProperty("Charset", "UTF-8");
					connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
					connection.setRequestProperty("Accept", "application/json");*/

					DataOutputStream dos = new DataOutputStream(connection.getOutputStream());
					if (file != null) {

						InputStream is = new FileInputStream(file);
						byte[] bytes = new byte[1024];
						int len = 0;
						while ((len = is.read(bytes)) != -1) {
							dos.write(bytes, 0, len);
						}
						is.close();
					} else {
						Log.e(TAG, "file not exits");
					}
					// dos.writeBytes(extraMsg);
					dos.flush();
					dos.close();


					res = connection.getResponseCode();
					Log.e(TAG, "response code:" + res);
					if (res == 200) {
						Log.e(TAG, "request success");
						InputStream in = connection.getInputStream();
						BufferedReader reader = new BufferedReader(new InputStreamReader(in));
						StringBuilder response  = new StringBuilder();
						String line;
						while ((line = reader.readLine()) != null) {
							response.append(line);
						}
						result = response.toString();
						Log.e(TAG, "result : " + result);
						in.close();
						if (httpCallbackListener != null) {
							httpCallbackListener.onFinish(result);
						}
					} else if (res == 500) {
						Log.e(TAG, connection.getErrorStream().toString());
						if (httpCallbackListener != null) {
							httpCallbackListener.onFinish("500");
						}
					} else {
						Log.e(TAG, "request error");
						Log.e(TAG, connection.getErrorStream().toString());
					}
				} catch (Exception e) {
					if (httpCallbackListener != null) {
						httpCallbackListener.onError(e);
					}
					e.printStackTrace();
				} finally {
					if (connection != null) {
						connection.disconnect();
					}
				}
			}
		}).start();
	}

	public static void sentHttpRequest(final String address, final HttpCallbackListener listener) {
		new Thread(new Runnable() {
			public void run() {
				StringBuilder response = null;
				HttpURLConnection connection = null;
				try {
					URL url = new URL(address);
					connection = (HttpURLConnection) url.openConnection();
					connection.setRequestMethod("GET");
					connection.setConnectTimeout(8000);
					connection.setReadTimeout(8000);
					connection.setDoInput(true);
					connection.setDoOutput(true);
					InputStream in = connection.getInputStream();
					BufferedReader reader = new BufferedReader(new InputStreamReader(in));
					response = new StringBuilder();
					String line;
					while ((line = reader.readLine()) != null) {
						response.append(line);
					}
					in.close();
				} catch (Exception e) {
					if (listener != null) {
						listener.onError(e);
					}
					e.printStackTrace();
				} finally {
					if (connection != null) {
						connection.disconnect();
					}
					if (listener != null) {
						listener.onFinish(response+"");
					} else {
						System.out.printf("NO RESPONSE");
					}
				}
			}
		}).start();
	}

	public static void requestCreateNewStation(final String address, final HttpCallbackListener httpCallbackListener){
		new Thread(new Runnable() {
			@SuppressLint("NewApi")
			public void run() {

				final String TAG = "Create New Station:";
				final String CHARSET = "utf-8";
				HttpURLConnection connection = null;
				int res = 0;
				String result = null;

				try {
					// int length = Integer.parseInt(file.length()+"");
					URL url = new URL(address);
					connection = (HttpURLConnection) url.openConnection();
					connection.setChunkedStreamingMode(1000);
					//connection.setFixedLengthStreamingMode(contentLength);
					connection.setRequestMethod("POST");
					connection.setConnectTimeout(8000000);
					connection.setReadTimeout(8000000);
					connection.setDoInput(true);
					connection.setDoOutput(true);
					connection.setUseCaches(false);
					connection.setRequestProperty("Connection", "Keep-Alive");
					connection.setRequestProperty("Charset", "UTF-8");
					connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
					connection.setRequestProperty("Content-Length", null);
					connection.setRequestProperty("Accept", "application/json");


					res = connection.getResponseCode();
					Log.e(TAG, "response code:" + res);
					if (res == 200) {
						Log.e(TAG, "request success");
						InputStream in = connection.getInputStream();
						BufferedReader reader = new BufferedReader(new InputStreamReader(in));
						StringBuilder response  = new StringBuilder();
						String line;
						while ((line = reader.readLine()) != null) {
							response.append(line);
						}
						result = response.toString();
						Log.e(TAG, "result : " + result);
						in.close();
						if (httpCallbackListener != null) {
							httpCallbackListener.onFinish(result);
						}
					} else if (res == 500) {
						Log.e(TAG, connection.getErrorStream().toString());
						if (httpCallbackListener != null) {
							httpCallbackListener.onFinish("500");
						}
					} else {
						Log.e(TAG, "request error");
						Log.e(TAG, connection.getErrorStream().toString());
					}
				} catch (Exception e) {
					if (httpCallbackListener != null) {
						httpCallbackListener.onError(e);
					}
					e.printStackTrace();
				} finally {
					if (connection != null) {
						connection.disconnect();
					}
				}
			}
		}).start();
	}

}
