package com.example.elaine.participatorysensingproject_android.firstpage;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

/**
 * Created by huangying06 on 16/9/25.
 */

public class SplashActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = new Intent(this, AirQualityActivity.class);
        startActivity(intent);
        finish();
    }
}
