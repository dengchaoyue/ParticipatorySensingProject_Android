package com.example.elaine.participatorysensingproject_android.AutoCamera;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;

import com.example.elaine.participatorysensingproject_android.R;
import com.example.elaine.participatorysensingproject_android.mappage.ActivityController;

/**
 * Created by Elaine on 2017/5/2.
 */
public class AutoPicListPageTwo extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityController.addActivity(this);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.auto_piclist_page_two);
        Log.e("AutoPicList2","create");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.e("AutoPicList2","start");

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i("AutoPicList2","resume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.e("AutoPicList2","pause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.e("AutoPicList","stop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e("AutoPicList2","destroy");
    }

}
