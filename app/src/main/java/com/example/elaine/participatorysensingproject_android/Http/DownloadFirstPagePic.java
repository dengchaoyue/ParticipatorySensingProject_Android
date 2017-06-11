/**
 * class name: 
 * class description: 
 * author: dengchaoyue 
 * version: 1.0
 */
package com.example.elaine.participatorysensingproject_android.Http;

import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class DownloadFirstPagePic {

	public static final String ROOT_URL = "http://182.92.116.126:8080/";
	private static BufferedInputStream bis;
	private final static String ALBUM_PATH = Environment.getExternalStorageDirectory() + "/firstPagePic/";


	public static String getAlbumPath() {
		return ALBUM_PATH;
	}

	public static void download(final ArrayList<String> picNameArray, final DownloadCallbackListener listener){
			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						File dirFile = new File(ALBUM_PATH);
						if (!dirFile.exists()) {
							dirFile.mkdir();
						}
						for (String picName : picNameArray) {
							Log.i("ying","6666666666666666666");
							Log.i("ying",DownloadFirstPagePic.ROOT_URL + Uri.encode(picName,"utf-8"));
							bis = new BufferedInputStream(getImageStream(DownloadFirstPagePic.ROOT_URL + Uri.encode(picName,"utf-8")));
							File myPic = new File(ALBUM_PATH + picName);
							BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(myPic));
							byte buffer[] = new byte[4 * 1024];
							int hasRead = 0;
							while ((hasRead = bis.read(buffer)) > -1) {
								bos.write(buffer, 0, hasRead);
								bos.flush();
							}
							bos.flush();
							bos.close();
							bis.close();
						}
						Log.i("ying","22222222222222222222222");
						if (listener != null) {
							listener.onFinish(picNameArray);
						} else {
							System.out.printf("NO RESPONSE");
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}).start();
		}

    /**
     * Get image from newwork
     * @param path The path of image
     * @return InputStream
     * @throws Exception
     */
    public static InputStream getImageStream(String path) throws Exception{
        URL url = new URL(path);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setConnectTimeout(50 * 1000);
        conn.setRequestMethod("GET");
        int code = conn.getResponseCode();
        if(conn.getResponseCode() == HttpURLConnection.HTTP_OK){
            return conn.getInputStream();
        }
        return null;
    } 

}

