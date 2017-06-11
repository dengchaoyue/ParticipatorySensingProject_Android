package com.example.elaine.participatorysensingproject_android.usercenterpage;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.elaine.participatorysensingproject_android.R;

/**
 * Created by ying on 2016/7/10.
 */
public class UserSignedActivity extends Activity{
    private TextView mUsernameView;
    private Button userExit;
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usersigned);

        LinearLayout tab_home = (LinearLayout) findViewById(R.id.tab_home);
        LinearLayout tab_map = (LinearLayout) findViewById(R.id.tab_map);
        LinearLayout tab_task = (LinearLayout) findViewById(R.id.tab_task);
        LinearLayout tab_user = (LinearLayout) findViewById(R.id.tab_user);
        tab_home.setSelected(false);
        tab_map.setSelected(false);
        tab_task.setSelected(false);
        tab_user.setSelected(true);

        mUsernameView = (TextView) findViewById(R.id.userName);
        TextView tv1 = (TextView) findViewById(R.id.Button1);
        tv1.setVisibility(View.GONE);
        TextView tv2 = (TextView) findViewById(R.id.centerText);
        tv2.setText("个人");
        TextView tv3 = (TextView) findViewById(R.id.Button2);
        tv3.setVisibility(View.GONE);

        userExit = (Button) findViewById(R.id.userExit);
        preferences = getSharedPreferences("user",MODE_PRIVATE);
        mUsernameView.setText(preferences.getString("username",""));

        userExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("username","");
                editor.putString("password","");
                editor.putBoolean("online", false);
                editor.commit();
                Intent intent = new Intent(UserSignedActivity.this, UserNotSignedActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
