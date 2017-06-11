package com.example.elaine.participatorysensingproject_android.usercenterpage;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.elaine.participatorysensingproject_android.firstpage.SensingApplication;


/**
 * Created by ying on 2016/7/10.
 */
public class UserUtil {
    public static boolean isUserLogin() {
        SharedPreferences sharedPreferences = SensingApplication.getInstance().getSharedPreferences("user", Context.MODE_PRIVATE);
        boolean onLine = sharedPreferences.getBoolean("online", false);
        return onLine;
    }

    public static String getUserName() {
        SharedPreferences sharedPreferences = SensingApplication.getInstance().getSharedPreferences("user", Context.MODE_PRIVATE);
        String userName = sharedPreferences.getString("username", "");
        return userName;
    }
}
