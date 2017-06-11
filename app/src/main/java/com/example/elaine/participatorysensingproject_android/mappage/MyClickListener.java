/**
 * class name: 
 * class description: 
 * author: dengchaoyue 
 * version: 1.0
 */
package com.example.elaine.participatorysensingproject_android.mappage;

import com.baidu.mapapi.map.BaiduMap.OnMarkerClickListener;
import com.baidu.mapapi.map.Marker;

public class MyClickListener implements OnMarkerClickListener {
	public String stationAddress;
	public SubPMStationList subPMStationList;
	public MyClickListener(String stationAddress) {
		// TODO Auto-generated constructor stub
		this.stationAddress = stationAddress;
	}
	@Override
	public boolean onMarkerClick(Marker arg0) {  
	// TODO Auto-generated method stub
		//String addressName = arg0.getTitle();
		//SubPMStationItem item = subPMStationList.getItem(addressName);

				return false;  
		} 

}
