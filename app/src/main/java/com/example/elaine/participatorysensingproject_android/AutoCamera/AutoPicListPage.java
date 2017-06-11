package com.example.elaine.participatorysensingproject_android.AutoCamera;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.example.elaine.participatorysensingproject_android.Http.HttpCallbackListener;
import com.example.elaine.participatorysensingproject_android.R;
import com.example.elaine.participatorysensingproject_android.mappage.ActivityController;
import com.example.elaine.participatorysensingproject_android.mappage.HttpUtil;
import com.example.elaine.participatorysensingproject_android.mappage.SubPMStationItem;

/**
 * Created by Elaine on 2017/5/2.
 */
public class AutoPicListPage extends Activity {
    public static String singlePicPath, username, station;
    public static SubPMStationItem aStation;
    private String[] filelist;
    private TextView tv;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityController.addActivity(this);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.auto_piclist_page);
        Log.e("AutoPicList", "create");

        TextView pageTitle = (TextView) findViewById(R.id.centerText);
        pageTitle.setText("已拍摄照片");
        TextView button1 = (TextView) findViewById(R.id.Button1);
        Typeface iconfont = Typeface.createFromAsset(getAssets(), "iconfont.ttf");
        button1.setTypeface(iconfont);
        button1.setText(R.string.icon_back);
        button1.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                //Images.imageUrls.clear();
                /*Intent it = new Intent(AutoPicListPage.this, MapPage.class);
                startActivity(it);
                overridePendingTransition(R.anim.in_from_left, 0);*/
                AutoPicListPage.this.finish();
            }
        });
        TextView button2 = (TextView) findViewById(R.id.Button2);
        button2.setTypeface(iconfont);
        button2.setText(R.string.icon_camera);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
            }

        });

        tv = (TextView) findViewById(R.id.my_text_view);
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendfiles(v);
            }

        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.e("AutoPicList", "start");

    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences preferences = AutoPicListPage.this.getSharedPreferences("storePath", Context.MODE_ENABLE_WRITE_AHEAD_LOGGING);
        //读取SharedPreferences里的count数据，若存在返回其值，否则返回0
        SharedPreferences.Editor editor = preferences.edit();
        String str = preferences.getString("storePath", "");
        Log.e("全部存储路径", str);
        if ("".equals(str)) {
            tv.setText("自动拍照相机已启动");
        } else {
            filelist = str.split(";");
            tv.setText(filelist.length + "张照片已经被拍摄");
        }
        Log.e("AutoPicList", "resume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.e("AutoPicList", "pause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.e("AutoPicList", "stop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e("AutoPicList", "destroy");
    }

    private int prograssNum = 0;

    public void sendfiles(View view) {
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        dialog.setTitle("Uploading......");
        dialog.setMessage("Please wait for a moment");
        dialog.setIcon(android.R.drawable.ic_dialog_alert);
        dialog.setMax(10 * filelist.length);
        dialog.setProgress(prograssNum);
        dialog.show();

        String BASE_URL = "http://182.92.116.126:8080/ps/environment/image";
        for (int i = 0; i < filelist.length; i++) {
            try {
                HttpUtil.sentHttpData(BASE_URL, filelist[i], null, new HttpCallbackListener() {
                    @Override
                    public void onError(Exception e) {
                        dialog.dismiss();
                        Message msg = new Message();
                        msg.what = 107;
                        uploadingAuto.sendMessage(msg);
                        e.printStackTrace();
                    }

                    @Override
                    public void onFinish(String response) {
                        prograssNum = prograssNum + 10;
                        dialog.setProgress(prograssNum);
                        if ("500".equals(response)) {
                            dialog.dismiss();
                            Message msg = new Message();
                            msg.what = 105;
                            uploadingAuto.sendMessage(msg);
                        } else {
                            if (prograssNum == (filelist.length * 10)) {
                                dialog.dismiss();
                                Message msg = new Message();
                                msg.what = 106;
                                uploadingAuto.sendMessage(msg);
                            }
                        }

                    }
                });
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    public Handler uploadingAuto = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 105:
                    AlertDialog.Builder builderFailed = new AlertDialog.Builder(AutoPicListPage.this);
                    builderFailed.setPositiveButton("好的", new DialogInterface.OnClickListener() {//添加确定按钮
                        @Override
                        public void onClick(DialogInterface dialog, int which) {//确定按钮的响应事件
                            dialog.dismiss();
                        }
                    });
                    AlertDialog dialogFailed = builderFailed.create();
                    dialogFailed.setTitle("Error：");//设置对话框标题
                    dialogFailed.setMessage("该图片非本APP拍摄，不符合要求，请重新选择站点照片");//设置显示的内容
                    Window winFailed = dialogFailed.getWindow();
                    winFailed.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                    dialogFailed.show();
                    break;
                case 106:
                    AlertDialog.Builder builder = new AlertDialog.Builder(AutoPicListPage.this);
                    builder.setPositiveButton("好的", new DialogInterface.OnClickListener() {//添加确定按钮
                        @Override
                        public void onClick(DialogInterface dialog, int which) {//确定按钮的响应事件
                            dialog.dismiss();
                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.setTitle("Congratulations：");//设置对话框标题
                    dialog.setMessage("照片已成功上传，快去拍新的照片吧!");//设置显示的内容
                    Window win = dialog.getWindow();
                    win.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                    dialog.show();
                    break;
                case 107:
                    AlertDialog.Builder builderError = new AlertDialog.Builder(AutoPicListPage.this);
                    builderError.setPositiveButton("好的", new DialogInterface.OnClickListener() {//添加确定按钮
                        @Override
                        public void onClick(DialogInterface dialog, int which) {//确定按钮的响应事件
                            dialog.dismiss();
                        }
                    });
                    AlertDialog dialogError = builderError.create();
                    dialogError.setTitle("Sorry：");//设置对话框标题
                    dialogError.setMessage("与服务器的连接失败，请检查网络后重新上传");//设置显示的内容
                    Window winError = dialogError.getWindow();
                    winError.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                    dialogError.show();
                    break;
            }
        }
    };
}
