/**
 * class name:
 * class description:
 * author: dengchaoyue
 * version: 1.0
 */
package com.example.elaine.participatorysensingproject_android.Http;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;

import com.example.elaine.participatorysensingproject_android.mappage.ImageLoader;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;


/**
 * @author dengchaoyue
 */
public class DownloadSinglePic {


    public static final String ROOT_URL = "http://182.92.116.126:8080/";
    private static BufferedInputStream bis;
    private final static String ALBUM_PATH = Environment.getExternalStorageDirectory() + "/singlePic/";
    private static HttpURLConnection conn;

    public static String getAlbumPath() {
        return ALBUM_PATH;
    }

    public static void download(final Context context, final String picName, final HttpCallbackListener httpCallbackListener) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection con = null;
                FileOutputStream fos = null;
                BufferedOutputStream bos = null;
                BufferedInputStream bis = null;
                File imageDir = null;
                File imageFile = null;
                try {
                    URL url = new URL(DownloadSinglePic.ROOT_URL + picName);
                    con = (HttpURLConnection) url.openConnection();
                    con.setConnectTimeout(50 * 1000);
                    con.setReadTimeout(15 * 1000);
                    con.setDoInput(true);
                    con.setDoOutput(true);
                    con.setRequestProperty("Charset", "UTF-8");
                    bis = new BufferedInputStream(con.getInputStream());
                    imageDir = new File(ALBUM_PATH);
                    imageFile = new File(ALBUM_PATH + picName);
                    if (!imageDir.exists()) {
                        imageDir.mkdirs();
                    }
                    if (!imageFile.exists()) {
                        imageFile.createNewFile();
                    }
                    fos = new FileOutputStream(imageFile);
                    bos = new BufferedOutputStream(fos);
                    byte[] b = new byte[1024];
                    int length;
                    while ((length = bis.read(b)) != -1) {
                        bos.write(b, 0, length);
                        bos.flush();
                    }
                    if (httpCallbackListener != null) {
                        System.out.println("单张照片下载成功");

                        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(imageFile)));

                        ImageLoader imageLoader = ImageLoader.getInstance();
                        if (imageFile != null) {
                            Bitmap bitmap = ImageLoader.decodeSampledBitmapFromResource(imageFile.getPath(), 200);
                            imageLoader.addBitmapToMemoryCache(imageFile.getPath(), bitmap);
                            Bitmap bitmapa = imageLoader.getBitmapFromMemoryCache(imageFile.getPath());
                        }
                        httpCallbackListener.onFinish(ALBUM_PATH + picName);
                    } else {
                        System.out.printf("NO RESPONSE");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    if (httpCallbackListener != null) {
                        System.out.println("单张照片下载失败");
                        httpCallbackListener.onFinish("Failed");
                    }
                } finally {
                    try {
                        if (bis != null) {
                            bis.close();
                        }
                        if (bos != null) {
                            bos.close();
                        }
                        if (con != null) {
                            con.disconnect();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

/*                try {
                    File dirFile = new File(ALBUM_PATH);
                    if (!dirFile.exists()) {
                        dirFile.mkdir();
                    }
                    if (getImageStream(DownloadSinglePic.ROOT_URL + picName) == null) {
                        if (httpCallbackListener != null) {
                            System.out.println("单张照片下载失败");
                            httpCallbackListener.onFinish("Failed");
                        }
                    } else {
                        bis = new BufferedInputStream(getImageStream(DownloadSinglePic.ROOT_URL + picName));
                        File singlePic = new File(ALBUM_PATH + picName);
                        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(singlePic));
                        byte buffer[] = new byte[4 * 1024];
                        int hasRead = 0;
                        while ((hasRead = bis.read(buffer)) != -1) {
                            bos.write(buffer, 0, hasRead);
                        }
                        bos.flush();
                        bos.close();
                        bis.close();
                        conn.disconnect();


                        if (httpCallbackListener != null) {
                            System.out.println("单张照片下载成功");
                            httpCallbackListener.onFinish(singlePic.getAbsolutePath());
                        } else {
                            System.out.printf("NO RESPONSE");
                        }
                    }
                } catch (Exception e) {
                    if (httpCallbackListener != null) {
                        httpCallbackListener.onError(e);
                    }
                    e.printStackTrace();
                }*/
            }
}).start();
        }


    /**
     * Get image from newwork
     *
     * @param path The path of image
     * @return InputStream
     * @throws Exception
     */
    public static InputStream getImageStream(String path) throws Exception {
        URL url = new URL(path);
        conn = (HttpURLConnection) url.openConnection();
        conn.setConnectTimeout(50 * 1000);
        conn.setReadTimeout(15 * 1000);
        conn.setDoInput(true);
        conn.setDoOutput(true);
        conn.setRequestProperty("Charset", "UTF-8");
        conn.setRequestMethod("GET");
        int code = conn.getResponseCode();
        if (code == HttpURLConnection.HTTP_OK) {
            return conn.getInputStream();
        } else {
            return null;
        }
    }

}

