package com.example.elaine.participatorysensingproject_android.AutoCamera;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.elaine.participatorysensingproject_android.AutoCamera.MyHorizontalScrollView.CurrentImageChangeListener;
import com.example.elaine.participatorysensingproject_android.AutoCamera.MyHorizontalScrollView.OnItemClickListener;
import com.example.elaine.participatorysensingproject_android.R;
import com.example.elaine.participatorysensingproject_android.mappage.ActivityController;
import com.example.elaine.participatorysensingproject_android.mappage.SubPMStationItem;

import java.util.ArrayList;
import java.util.List;

public class AutoCameraActivity extends Activity {

    private ArrayList<String> PeriodTimesData;
    private ArrayList<String> PeriodDuringData;
    private MyHorizontalScrollView PeriodTimesScrollView;
    private MyHorizontalScrollViewBelow PeriodDuringScrollView;
    private HorizontalScrollViewAdapter PeriodTimesAdapter;
    private HorizontalScrollViewAdapterBelow PeriodDuringAdapter;
    private LinearLayout linearLayoutAbove, linearLayoutBelow;
    private TextView fdTv, bssTv, tips;
    private int PeriodTimesPos = 0;
    private int PeriodDuringPos = 0;
    private double PeriodDuringNO = 0.0;
    private double PeriodTimesNO = 0.0;
    private List<Double> PeriodDuringNumber = new ArrayList<Double>();
    private List<Double> PeriodTimesNumber = new ArrayList<Double>();
    private static int languageType = 1; // 1 means English, 0 means Chinese;
    private static double result = 0.0;
    private TextView button2;
    public static String singlePicPath, username, station;
    public static SubPMStationItem aStation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityController.addActivity(this);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.auto_camera_config);

        TextView pageTitle = (TextView) findViewById(R.id.centerText);
        pageTitle.setText("拍照设置");
        button2 = (TextView) findViewById(R.id.Button2);
        button2.setText("确定");
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (PeriodDuringNO == 0.0 && PeriodTimesNO == 0.0) {
                    AlertDialog.Builder al = new AlertDialog.Builder(AutoCameraActivity.this);
                    al.setTitle("Note:");
                    al.setMessage("请至少选择一个配置参数");
                    al.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
                    al.show();
                } else {
                    if (PeriodDuringNO == 0.0 && PeriodTimesNO != 0.0) {
                        if (PeriodTimesNO - 1 == 0.0) {
                            PeriodDuringNO = 10000;
                            tips.setText("选定拍摄照片数:" + PeriodTimesNO + "\n选定拍摄间隔:" + PeriodDuringNO);
                        } else {
                            PeriodDuringNO = 24 / (PeriodTimesNO - 1);
                            tips.setText("选定拍摄照片数:" + PeriodTimesNO + "\n选定拍摄间隔:" + PeriodDuringNO);
                        }
                    } else if (PeriodDuringNO != 0.0 && PeriodTimesNO == 0.0) {
                        PeriodTimesNO = (24 / PeriodDuringNO) + 1;
                        tips.setText("选定拍摄照片数:" + PeriodTimesNO + "\n选定拍摄间隔:" + PeriodDuringNO);
                    }

                    button2.setClickable(false);
                    MyThread myThread = new MyThread();
                    myThread.start();
                    Log.e("设置界面","onCreate");
                    Intent jumpToUploadPage = new Intent(AutoCameraActivity.this, AutoPicListPage.class);
                    startActivity(jumpToUploadPage);
                    //AutoCameraActivity.this.finish();
                }
            }
        });
        TextView goBack = (TextView) findViewById(R.id.Button1);
        goBack.setText("");

        initData();
        initView();

    }

    private class MyThread extends Thread {
        @Override
        public void run() {
            Intent alarmActivity = new Intent("ELITOR_CLOCK");
            alarmActivity.putExtra("PeriodTimesNumber", PeriodTimesNO);
            alarmActivity.putExtra("PeriodDuringNumber", PeriodDuringNO);
            PendingIntent pi = PendingIntent.getBroadcast(AutoCameraActivity.this, 0, alarmActivity, 0);
            AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
            am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), (long) (PeriodDuringNO * 60 * 1000), pi);
            //am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), PeriodDuringPos * 3600 * 1000, pi);//第二个参数表示开始时间，第三个参数表示间隔时间
        }
    }

    public void initData() {
        PeriodTimesData = new ArrayList<String>() {{
            add("不定义");
            add("1");
            add("2");
            add("3");
            add("4");
            add("5");
            add("7");
            add("9");
            add("13");
            add("25");
        }};
        PeriodTimesNumber.add(0.0);
        PeriodTimesNumber.add(1.0);
        PeriodTimesNumber.add(2.0);
        PeriodTimesNumber.add(3.0);
        PeriodTimesNumber.add(4.0);
        PeriodTimesNumber.add(5.0);
        PeriodTimesNumber.add(7.0);
        PeriodTimesNumber.add(9.0);
        PeriodTimesNumber.add(13.0);
        PeriodTimesNumber.add(25.0);


        PeriodDuringData = new ArrayList<String>();
        PeriodDuringData.add("不定义");
        PeriodDuringData.add("1.00");
        PeriodDuringData.add("2.00");
        PeriodDuringData.add("3.00");
        PeriodDuringData.add("4.00");
        PeriodDuringData.add("6.00");
        PeriodDuringData.add("8.00");
        PeriodDuringData.add("12.00");
        PeriodDuringData.add("24.00");

        PeriodDuringNumber.add(0.0);
        PeriodDuringNumber.add(1.0);
        PeriodDuringNumber.add(2.0);
        PeriodDuringNumber.add(3.0);
        PeriodDuringNumber.add(4.0);
        PeriodDuringNumber.add(6.0);
        PeriodDuringNumber.add(8.0);
        PeriodDuringNumber.add(12.0);
        PeriodDuringNumber.add(24.0);

        PeriodTimesPos = PeriodTimesNumber.size() - 1;
        PeriodDuringPos = PeriodDuringNumber.size() - 1;
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.e("设置界面","onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e("设置界面","onResume");
    }

    public void onLowMemory() {
        super.onLowMemory();
        Log.e("1111111111","onLowMemory");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.e("设置界面","onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.e("设置界面","onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityController.removeActivity(this);
        Log.e("设置界面","onDestroy");
    }

    public void initView() {

        fdTv = (TextView) findViewById(R.id.FD);
        bssTv = (TextView) findViewById(R.id.BSS);
        tips = (TextView) findViewById(R.id.tips);
        tips.setText("自动相机运行时间超过1天，请适当缩短");
        tips.setVisibility(View.GONE);
        PeriodTimesScrollView = (MyHorizontalScrollView) findViewById(R.id.FDScrollView);
        PeriodDuringScrollView = (MyHorizontalScrollViewBelow) findViewById(R.id.BSSScrollView);
        PeriodTimesAdapter = new HorizontalScrollViewAdapter(this, PeriodTimesData);
        PeriodDuringAdapter = new HorizontalScrollViewAdapterBelow(this, PeriodDuringData);
        PeriodTimesScrollView.initDatas(PeriodTimesAdapter);
        PeriodDuringScrollView.initDatas(PeriodDuringAdapter);

        //添加滚动回调
        PeriodTimesScrollView.setCurrentImageChangeListener(new CurrentImageChangeListener() {
            @Override
            public void onCurrentImgChanged(int position, View viewIndicator) {
                PeriodTimesPos = position;
                calculateTotalTime(PeriodTimesPos, PeriodDuringPos);
            }
        });
        //添加点击回调

        PeriodTimesScrollView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onClick(View view, int position) {
            }
        });

        //添加滚动回调
        PeriodDuringScrollView.setCurrentImageChangeListener(new MyHorizontalScrollViewBelow.CurrentImageChangeListener() {
            @Override
            public void onCurrentImgChanged(int position, View viewIndicator) {
                PeriodDuringPos = position;
                calculateTotalTime(PeriodTimesPos, PeriodDuringPos);
            }
        });
        //添加点击回调
        PeriodDuringScrollView.setOnItemClickListener(new MyHorizontalScrollViewBelow.OnItemClickListener() {

            @Override
            public void onClick(View view, int position) {
            }
        });
    }

    private void calculateTotalTime(int periodTimesPos, int periodDuringPos) {
        int realptp = (periodTimesPos + 1) % PeriodTimesNumber.size();
        int realpdp = (periodDuringPos + 1) % PeriodDuringNumber.size();
        PeriodTimesNO = PeriodTimesNumber.get(realptp);
        PeriodDuringNO = PeriodDuringNumber.get(realpdp);
        result = (PeriodTimesNO - 1) * PeriodDuringNO;
        if (result > 24) {
            tips.setVisibility(View.VISIBLE);
            tips.setText("选定拍摄照片数:" + PeriodTimesData.get(realptp) + "\n选定拍摄间隔:" + PeriodDuringData.get(realpdp));
            button2.setClickable(false);
        } else {
            tips.setVisibility(View.VISIBLE);
            tips.setText("选定拍摄照片数:" + PeriodTimesData.get(realptp) + "\n选定拍摄间隔:" + PeriodDuringData.get(realpdp));
            button2.setClickable(true);
        }
    }
}