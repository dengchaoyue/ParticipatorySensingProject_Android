/**
 * class name: 
 * class description: 
 * author: dengchaoyue 
 * version: 1.0
 */
package com.example.elaine.participatorysensingproject_android.mappage;

import java.io.Serializable;

public class SubPMStationItem implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private long stationID;
	private String stationAddress;
	private String latestPic;
	private int FPM;
	private int actual_fpm;
	private String nearPmStation;
	private int reputation;
	private String latestTime;
	private double longitude;
	private double latitude;
	private double blongitude;
	private double blatitude;


	public SubPMStationItem() {
		// TODO Auto-generated constructor stub
	}
	public long getStationID() {
		return stationID;
	}
	public void setStationID(long stationID) {
		this.stationID = stationID;
	}
	public String getStationAddress() {
		return stationAddress;
	}
	public void setStationAddress(String stationAddress) {
		this.stationAddress = stationAddress;
	}
	public String getLatestPic() {
		return latestPic;
	}
	public void setLatestPic(String latestPic) {
		this.latestPic = latestPic;
	}
	public int getFPM() {
		return FPM;
	}
	public void setFPM(int FPM) {
		this.FPM = FPM;
	}
	public int getActual_fpm() {
		return actual_fpm;
	}
	public void setActual_fpm(int actual_fpm) {
		this.actual_fpm = actual_fpm;
	}
	public String getNearPmStation() {
		return nearPmStation;
	}
	public void setNearPmStation(String nearPmStation) {
		this.nearPmStation = nearPmStation;
	}
	public int getReputation() {
		return reputation;
	}
	public void setReputation(int reputation) {
		this.reputation = reputation;
	}
	public String getLatestTime() {
		return latestTime;
	}
	public void setLatestTime(String latestTime) {
		this.latestTime = latestTime;
	}
	public double getLongitude() {
		return longitude;
	}
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}
	public double getLatitude() {
		return latitude;
	}
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}
	public double getBlongitude() {
		return blongitude;
	}
	public void setBlongitude(double blongitude) {
		this.blongitude = blongitude;
	}
	public double getBlatitude() {
		return blatitude;
	}
	public void setBlatitude(double blatitude) {
		this.blatitude = blatitude;
	}
}
