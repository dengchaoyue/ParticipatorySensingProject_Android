/**
 * class name: 
 * class description: 
 * author: dengchaoyue 
 * version: 1.0
 */
package com.example.elaine.participatorysensingproject_android.mappage;

import java.util.ArrayList;

public class SubPMStationList extends ArrayList<SubPMStationItem> {
	
	private static final long serialVersionUID = 1L;
	public SubPMStationItem getItem(String stationID){
		for(int i = 0; i < this.size(); i++){
			String s = this.get(i).getStationID()+"";
			if(s.equals(stationID)){
				return this.get(i);
			}
		}
		return this.get(0);
	}
}
