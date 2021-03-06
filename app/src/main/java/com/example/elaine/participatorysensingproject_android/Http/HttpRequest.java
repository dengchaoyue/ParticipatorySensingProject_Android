package com.example.elaine.participatorysensingproject_android.Http;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by ying on 2016/6/5.
 */
public class HttpRequest {

    public static final String FIRST_PAGE = "http://182.92.116.126:8080/ps/ExistPMStation?";

    public static String sentHttpRequest(final String address){
                HttpURLConnection connection = null;
                try{
                    URL url = new URL(address);
                    connection = (HttpURLConnection)url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(8000);
                    connection.setReadTimeout(8000);
                    connection.setDoInput(true);
                    connection.setDoOutput(true);
                    InputStream in = connection.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while((line = reader.readLine()) != null){
                        response.append(line);
                    }
                    return response.toString();
                }catch(Exception e){
                    e.printStackTrace();
                    return e.getMessage();
                }finally{
                    if(connection != null){
                        connection.disconnect();
                    }
                }
    }
}
