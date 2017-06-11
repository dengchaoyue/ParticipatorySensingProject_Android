/**
 * class name: 
 * class description: 
 * author: dengchaoyue 
 * version: 1.0
 */
package com.example.elaine.participatorysensingproject_android.mappage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author dengchaoyue
 *
 */
public class NewPOIStationID {

	public static String computePOI(double lon, double lat) {
		QueryString qs = new QueryString("ak","XsGuv1HvMgeOFjy1P4C4RAQH6RkLTBMR");
		qs.add("latitude", String.valueOf(lat));
		qs.add("longitude", String.valueOf(lon));
		qs.add("geotable_id", "98977");
		qs.add("coord_type", "3");

		System.out.println(qs.toString());
		
		String string = sendPost("http://api.map.baidu.com/geodata/v3/poi/create",qs.toString() );
		try {
			JSONObject object = new JSONObject(string);
			int state = object.getInt("status");
			String POI_id = object.getString("id");
			System.out.println(POI_id);
			return POI_id;
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
		
	}


	public static String sendPost(String url, String param) {  
        PrintWriter out = null;  
        BufferedReader in = null;  
        String result = "";  
        try {  
            URL realUrl = new URL(url);  
            
            URLConnection conn = realUrl.openConnection();  
           
            conn.setRequestProperty("accept", "*/*");  
            conn.setRequestProperty("connection", "Keep-Alive");  
            conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1)");  
              
            conn.setDoOutput(true);
            conn.setDoInput(true);  
              
            out = new PrintWriter(conn.getOutputStream());
            out.print(param);
            out.flush();
            in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;  
            while ((line = in.readLine()) != null) {  
                result += "\n" + line;  
            }  
        } 
        catch (Exception e) {  
            System.out.println("");  
            e.printStackTrace();  
        }  
        // 
        finally {  
            try {  
                if (out != null) {  
                    out.close();  
                }  
                if (in != null) {  
                    in.close();  
                }  
            } catch (IOException ex) {  
                ex.printStackTrace();  
            }  
        }  
        return result;  
    }  

}
