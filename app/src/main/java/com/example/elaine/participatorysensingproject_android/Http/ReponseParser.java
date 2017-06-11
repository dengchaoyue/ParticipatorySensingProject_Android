package com.example.elaine.participatorysensingproject_android.Http;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by ying on 2016/6/4.
 */
public class ReponseParser {


    public static void parseResponseForFisrtPage(String jsonData){
        try {
            JSONObject response = new JSONObject(jsonData);
            JSONObject message = response.getJSONObject("message");
            String existPOI = message.getString("existPOI");
            if (existPOI.equals("yes")){
                JSONObject existStation = message.getJSONObject("existStation");
                String station = existStation.getString("nearPmStation");


            }else{

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


}
