/**
 * class name: 
 * class description: 
 * author: dengchaoyue 
 * version: 1.0
 */
package com.example.elaine.participatorysensingproject_android.mappage;

import java.util.ArrayList;

/**
 * @author dengchaoyue
 *
 */
public class PhotoWallList extends ArrayList<PhotoWallElement> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public PhotoWallElement getItem(String photoUrl){
		for(int i = 0; i < this.size(); i++){
			String s = this.get(i).getPhotoURL();
			if(s.equals(photoUrl)){
				return this.get(i);
			}
		}
		return this.get(0);
	}
}
