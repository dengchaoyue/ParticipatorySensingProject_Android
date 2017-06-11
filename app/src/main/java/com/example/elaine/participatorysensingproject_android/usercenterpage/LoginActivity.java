package com.example.elaine.participatorysensingproject_android.usercenterpage;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.elaine.participatorysensingproject_android.Http.HttpUtil;
import com.example.elaine.participatorysensingproject_android.Http.Networker;
import com.example.elaine.participatorysensingproject_android.R;
import com.example.elaine.participatorysensingproject_android.mappage.ActivityController;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Created by ying on 2016/7/9.
 */
public class LoginActivity extends Activity {

    private static final int SHOW_LOGIN = 0;

    private EditText mUsernameView;
    private EditText mPasswordView;
    private String mUsername;
    private String mPassword;
    private Button login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityController.addActivity(this);
        setContentView(R.layout.activity_login);

        LinearLayout tab_home = (LinearLayout) findViewById(R.id.tab_home);
        LinearLayout tab_map = (LinearLayout) findViewById(R.id.tab_map);
        LinearLayout tab_task = (LinearLayout) findViewById(R.id.tab_task);
        LinearLayout tab_user = (LinearLayout) findViewById(R.id.tab_user);
        tab_home.setSelected(false);
        tab_map.setSelected(false);
        tab_task.setSelected(false);
        tab_user.setSelected(true);

        TextView goBack = (TextView) findViewById(R.id.Button1);
        Typeface iconfont = Typeface.createFromAsset(getAssets(), "iconfont.ttf");
        goBack.setTypeface(iconfont);
        goBack.setText(R.string.icon_back);
        TextView title = (TextView) findViewById(R.id.centerText);
        title.setText("登录");
        TextView blank = (TextView) findViewById(R.id.Button2);
        blank.setText("");
        mUsernameView = (EditText) findViewById(R.id.userName);
        mPasswordView = (EditText) findViewById(R.id.passWord);
        login = (Button) findViewById(R.id.login);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptLogin();
            }
        });
        goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


    private void attemptLogin() {
        // Reset errors.
        mUsernameView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        mUsername = mUsernameView.getText().toString();
        mPassword = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(mPassword) && !isPasswordValid(mPassword)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid username.
        if (TextUtils.isEmpty(mUsername)) {
            mUsernameView.setError(getString(R.string.error_field_required));
            focusView = mUsernameView;
            cancel = true;
        } else if (!isUsernameValid(mUsername)) {
            mUsernameView.setError(getString(R.string.error_invalid_email));
            focusView = mUsernameView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            StringBuilder reqLogin = new StringBuilder();
            reqLogin.append(HttpUtil.LOGIN);
            reqLogin.append("userName=");
            reqLogin.append(Uri.encode(mUsername, "utf-8"));
            reqLogin.append("&passWord=");
            reqLogin.append(mPassword);

            Networker.HttpRequest imageRequest = Networker.HttpRequest.newBuilder()
                    .method(Networker.HttpMethod.GET)
                    .url(reqLogin.toString())
                    .build();
            Networker.get().submit(imageRequest, new Networker.Callback() {
                @Override
                public void onResponse(Networker.HttpResponse result) {
                    if (result.statusCode == 200) {
                        Log.i("ying", new String(result.body));
                        Message message = new Message();
                        message.what = SHOW_LOGIN;
                        message.obj = new String(result.body);
                        registerHandler.sendMessage(message);
                    }
                }

                @Override
                public void onFailure(IOException e) {
                    // Let Stetho demonstrate the errors :)
                }
            });


        }
    }

    private Handler registerHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SHOW_LOGIN:
                    String jsonData = (String) msg.obj;
                    try {
                        JSONObject response = new JSONObject(jsonData);
                        String stateCode = response.getString("stateCode");
                        if (stateCode.equals("000")) {
                            Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_LONG).show();
                            SharedPreferences.Editor editor = getSharedPreferences("user", MODE_PRIVATE).edit();
                            editor.putString("username", mUsername);
                            editor.putString("password", mPassword);
                            editor.putBoolean("online", true);
                            editor.commit();
                            UserCenterPage.userName = mUsername;
                            Intent intent = new Intent(LoginActivity.this, UserCenterPage.class);
                            intent.putExtra("username", mUsername);
                            startActivity(intent);
                            finish();
                        } else if ((stateCode.equals("001")) || (stateCode.equals("002"))) {
                            Toast.makeText(LoginActivity.this, "登录失败，用户名或密码错误", Toast.LENGTH_LONG).show();
                            mPasswordView.requestFocus();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                default:
                    break;
            }
        }
    };

    private boolean isPasswordValid(String password) {
        return password.length() > 0;
    }

    private boolean isUsernameValid(String username) {
        return username.length() > 0;
    }


}
