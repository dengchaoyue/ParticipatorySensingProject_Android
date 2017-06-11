package com.example.elaine.participatorysensingproject_android.usercenterpage;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.elaine.participatorysensingproject_android.R;

/**
 * Created by ying on 2016/7/9.
 */
public class UserNotSignedActivity extends Activity implements View.OnClickListener {
    private Button toLogin;
    private Button toRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usernotsigned);

        LinearLayout tab_home = (LinearLayout) findViewById(R.id.tab_home);
        LinearLayout tab_map = (LinearLayout) findViewById(R.id.tab_map);
        LinearLayout tab_task = (LinearLayout) findViewById(R.id.tab_task);
        LinearLayout tab_user = (LinearLayout) findViewById(R.id.tab_user);
        tab_home.setSelected(false);
        tab_map.setSelected(false);
        tab_task.setSelected(false);
        tab_user.setSelected(true);

        Typeface iconfont = Typeface.createFromAsset(getAssets(), "iconfont.ttf");
        TextView button1 = (TextView) findViewById(R.id.Button1);
        button1.setTypeface(iconfont);
        button1.setText(R.string.icon_back);
        button1.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                UserNotSignedActivity.this.finish();
            }
        });
        TextView tv = (TextView) findViewById(R.id.centerText);
        tv.setText("登录/注册");
        TextView button2 = (TextView) findViewById(R.id.Button2);
        button2.setText("");
        toLogin = (Button) findViewById(R.id.toLogin);
        toRegister = (Button) findViewById(R.id.toRegister);
        toLogin.setOnClickListener(this);
        toRegister.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.toLogin:
                Intent intent = new Intent(UserNotSignedActivity.this, LoginActivity.class);
                startActivity(intent);
                break;
            case R.id.toRegister:
                Intent intent1 = new Intent(UserNotSignedActivity.this, RegisterActivity.class);
                startActivity(intent1);
                break;
            default:
                break;
        }
    }
}
