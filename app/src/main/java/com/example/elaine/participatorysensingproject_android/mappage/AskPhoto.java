/**
 * class name: 
 * class description: 
 * author: dengchaoyue 
 * version: 1.0
 */
package com.example.elaine.participatorysensingproject_android.mappage;

import android.content.Context;
import android.net.Uri;
import android.widget.Toast;

import com.example.elaine.participatorysensingproject_android.Http.HttpCallbackListener;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * @author dengchaoyue
 *
 */
public class AskPhoto {
	private String photoStationAddress;
	public boolean flag = false;
	Context parentContext;
	public AskPhoto(Context c, long l, String stationAddress) {
		// TODO Auto-generated constructor stub
		photoStationAddress = stationAddress;
		String BASE_URL = "http://182.92.116.126:8080/ps/PMStationDetail?stationID="+l+"&beginNum=1&count=10000";
		parentContext = c;
		try {
			HttpUtil.sentHttpRequest(BASE_URL, new HttpCallbackListener(){
				@Override
				public void onFinish(String response) {
					// TODO Auto-generated method stub
					parseJSONWithJSONObject(response);
					flag = true;
					/*Message msgLoadingPhotoWall = new Message();
					msgLoadingPhotoWall.what = 103;	
					loadingPhotoWall.sendMessage(msgLoadingPhotoWall);*/
					Thread.currentThread().interrupt();		
				}
				@Override
				public void onError(Exception e) {
					// TODO Auto-generated method stub
					e.printStackTrace();
				}
			});
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}
	
	
/*	public static Handler loadingPhotoWall = new Handler(){
		 public void handleMessage(Message msg) {  
	            switch (msg.what) {  
	            case 103:  
	            	MapPage.loadingFinish = false;
	            	cameraDIY.loadingFinish = false;
	            }
		 }
	};*/
	
	
	PhotoWallList picsArrayList = new PhotoWallList();
	private void parseJSONWithJSONObject(String jsonData){
		try{
			JSONObject json =  new JSONObject(jsonData);
			JSONObject msg =  json.getJSONObject("message");
			JSONArray picsArray = null;
			if(msg.getJSONArray("pics") != null){
				picsArray = msg.getJSONArray("pics");
			}else{
				Toast.makeText(parentContext, "No station has been established nearby", Toast.LENGTH_LONG).show();
			}
			for(int i = 0 ; i < picsArray.length() ; i++){				
				PhotoWallElement item = new PhotoWallElement();
				JSONObject jsonObject = picsArray.getJSONObject(i);

				item.setPhotoAddress(photoStationAddress);
				item.setPhotofpm(Integer.parseInt(jsonObject.getString("fpm")));
				item.setPhotoNearStation(jsonObject.getString("nearPmStation"));
				item.setPhotoTime(jsonObject.getString("time"));
				item.setPhotoURL(jsonObject.getString("url"));
				item.setPhotoActualFPM(Integer.parseInt(jsonObject.getString("actual_fpm")));
				picsArrayList.add(item);

				
				String decodePicName = Uri.encode(item.getPhotoURL(),"utf-8");
				String decodeUrl = new String("http://182.92.116.126:8080/"+decodePicName);
	            //String strUTF8 = URLDecoder.decode(originUrl, "UTF-8");

				Images.address.add(photoStationAddress);
				Images.imageUrls.add(decodeUrl);
				Images.fpm.add(item.getPhotofpm());
				Images.actual_fpm.add(item.getPhotoActualFPM());
				Images.nearStation.add(item.getPhotoNearStation());
				Images.time.add(item.getPhotoTime());
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
