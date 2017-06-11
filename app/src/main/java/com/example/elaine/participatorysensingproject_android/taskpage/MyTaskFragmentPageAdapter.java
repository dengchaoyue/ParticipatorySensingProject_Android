/**
 * class name: 
 * class description: 
 * author: dengchaoyue 
 * version: 1.0
 */
package com.example.elaine.participatorysensingproject_android.taskpage;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * @author dengchaoyue
 *
 */
public class MyTaskFragmentPageAdapter extends FragmentPagerAdapter {

	/**
	 * @param fm
	 */
	public MyTaskFragmentPageAdapter(FragmentManager fm) {
		super(fm);
		// TODO Auto-generated constructor stub
	}

	@Override
    public int getCount() {
        return 4;
    }
    @Override
    public Fragment getItem(int position) {
        switch (position) {
         	case 0:
                return TaskActivity.mMyTaskFragmentList.get(0);
            case 1:
                return TaskActivity.mMyTaskFragmentList.get(1);
            case 2:
                return TaskActivity.mMyTaskFragmentList.get(2);
            case 3:
                return TaskActivity.mMyTaskFragmentList.get(3);
            default:
                return null;
            }
    }

}
