package com.example.elaine.participatorysensingproject_android.Layout;

/**
 * Created by lenovo on 2016/5/6 0006.
 */

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.elaine.participatorysensingproject_android.firstpage.AirQualityActivity;
import com.example.elaine.participatorysensingproject_android.R;
import com.example.elaine.participatorysensingproject_android.usercenterpage.UserCenterPage;
import com.example.elaine.participatorysensingproject_android.mappage.MapPage;

public class MenuLayout extends LinearLayout {
    static LinearLayout tab_home, tab_map, tab_task, tab_user;
    public MenuLayout(final Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
        LayoutInflater.from(context).inflate(R.layout.menu_layout, this);
        tab_home = (LinearLayout) findViewById(R.id.tab_home);
        tab_map = (LinearLayout) findViewById(R.id.tab_map);
        tab_task = (LinearLayout) findViewById(R.id.tab_task);
        tab_user = (LinearLayout)findViewById(R.id.tab_user);
        tab_home.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent firstPage=  new Intent(getContext(), AirQualityActivity.class);
                firstPage.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                getContext().startActivity(firstPage);
                ((Activity) getContext()).overridePendingTransition(0, 0);
            }
        });
        tab_map.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent mapPage = new Intent(getContext(), MapPage.class);
                mapPage.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                getContext().startActivity(mapPage);
                ((Activity) getContext()).overridePendingTransition(0, 0);
            }
        });
        tab_user.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                SharedPreferences sharedPreferences = getContext().getSharedPreferences("user", Context.MODE_PRIVATE);
                String userName = sharedPreferences.getString("username", "");
                String passWord = sharedPreferences.getString("password", "");
                SharedPreferences sharedPreference = getContext().getSharedPreferences("userIcon", Context.MODE_PRIVATE);
                String userIcon = sharedPreference.getString("picPath", "");
                boolean onLine = sharedPreferences.getBoolean("online", false);
                if (isStringValid(userName) && isStringValid(passWord) && onLine) {
                    Intent usercenter =  new Intent(getContext(),UserCenterPage.class);
                    usercenter.putExtra("username",userName);
                    usercenter.putExtra("userIcon",userIcon);
                    usercenter.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    getContext().startActivity(usercenter);
                    ((Activity) getContext()).overridePendingTransition(0, 0);
                } else {
                    Intent usercenter =  new Intent(getContext(),UserCenterPage.class);
                    usercenter.putExtra("username","");
                    usercenter.putExtra("userIcon","");
                    usercenter.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    getContext().startActivity(usercenter);
                    ((Activity) getContext()).overridePendingTransition(0, 0);
                }
            }
        });
        tab_task.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Toast.makeText(context,"任务界面被点击", Toast.LENGTH_LONG).show();
                /*Intent taskPage = new Intent(getContext(), TaskActivity.class);
                taskPage.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                getContext().startActivity(taskPage);
                ((Activity) getContext()).overridePendingTransition(0, 0);*/
            }
        });
    }

    private boolean isStringValid(String string) {
        if (string != null && string.length() != 0) {
            return true;
        } else {
            return  false;
        }
    }

}

