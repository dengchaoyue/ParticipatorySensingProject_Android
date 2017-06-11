/**
 * class name: 
 * class description: 
 * author: dengchaoyue 
 * version: 1.0
 */
package com.example.elaine.participatorysensingproject_android.mappage;

/**
 * @author dengchaoyue
 *
 */
public class PhotoWallElement {

	private String PhotoURL;
	private int Photofpm;
	private int PhotoActualFPM;
	private String PhotoNearStation;
	private String PhotoTime;
	private String PhotoAddress;

	public String getPhotoAddress() {
		return PhotoAddress;
	}
	public void setPhotoAddress(String photoAddress) {
		PhotoAddress = photoAddress;
	}
	public String getPhotoURL() {
		return PhotoURL;
	}
	public void setPhotoURL(String photoURL) {
		PhotoURL = photoURL;
	}
	public int getPhotofpm() {
		return Photofpm;
	}
	public void setPhotofpm(int photofpm) {
		Photofpm = photofpm;
	}
	public int getPhotoActualFPM() {
		return PhotoActualFPM;
	}
	public void setPhotoActualFPM(int photoActualFPM) {
		PhotoActualFPM = photoActualFPM;
	}
	public String getPhotoNearStation() {
		return PhotoNearStation;
	}
	public void setPhotoNearStation(String photoNearStation) {
		PhotoNearStation = photoNearStation;
	}
	public String getPhotoTime() {
		return PhotoTime;
	}
	public void setPhotoTime(String photoTime) {
		PhotoTime = photoTime;
	}

	public PhotoWallElement() {
		// TODO Auto-generated constructor stub
	}

}
