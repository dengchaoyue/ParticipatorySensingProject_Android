package com.example.elaine.participatorysensingproject_android.diySystemAlbum;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.elaine.participatorysensingproject_android.Http.HttpCallbackListener;
import com.example.elaine.participatorysensingproject_android.R;
import com.example.elaine.participatorysensingproject_android.mappage.ActivityController;
import com.example.elaine.participatorysensingproject_android.mappage.AskPhoto;
import com.example.elaine.participatorysensingproject_android.mappage.HttpUtil;
import com.example.elaine.participatorysensingproject_android.mappage.PhotoWall;
import com.example.elaine.participatorysensingproject_android.mappage.SubPMStationItem;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;

public class ImgsActivity extends Activity {
    static SubPMStationItem aStation;
    Bundle bundle;
    FileTraversal fileTraversal;
    GridView imgGridView;
    ImgsAdapter imgsAdapter;
    LinearLayout select_layout;
    Util util;
    RelativeLayout relativeLayout2;
    HashMap<Integer, ImageView> hashImage;
    Button choise_button;
    ArrayList<String> filelist;
    String realLongitude, realLatitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityController.addActivity(this);
        setContentView(R.layout.photogrally);

        imgGridView = (GridView) findViewById(R.id.gridView1);
        bundle = getIntent().getExtras();
        aStation = (SubPMStationItem) getIntent().getSerializableExtra("subStationItem");
        realLatitude = getIntent().getStringExtra("realLatitude");
        realLongitude = getIntent().getStringExtra("realLongitude");
        fileTraversal = bundle.getParcelable("data");
        imgsAdapter = new ImgsAdapter(this, fileTraversal.filecontent, onItemClickClass);
        imgGridView.setAdapter(imgsAdapter);
        select_layout = (LinearLayout) findViewById(R.id.selected_image_layout);

        TextView pageTitle = (TextView) findViewById(R.id.centerText);
        pageTitle.setText(fileTraversal.filename);
        TextView button1 = (TextView) findViewById(R.id.Button1);
        Typeface iconfont = Typeface.createFromAsset(getAssets(), "iconfont.ttf");
        button1.setTypeface(iconfont);
        button1.setText(R.string.icon_back);
        button1.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent intent = new Intent(ImgsActivity.this, ImgFileListActivity.class);
                intent.putExtra("subStationItem", aStation);
                intent.putExtra("realLongitude", realLongitude);
                intent.putExtra("realLatitude", realLatitude);
                startActivity(intent);
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                ImgsActivity.this.finish();
            }
        });
        TextView button2 = (TextView) findViewById(R.id.Button2);
        button2.setTypeface(iconfont);
        button2.setText(R.string.icon_upload);
        button2.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                sendfiles(v);
            }

        });

        relativeLayout2 = (RelativeLayout) findViewById(R.id.relativeLayout2);
        choise_button = (Button) findViewById(R.id.button3);
        hashImage = new HashMap<Integer, ImageView>();
        filelist = new ArrayList<String>();
//		imgGridView.setOnItemClickListener(this);
        util = new Util(this);
    }

    class BottomImgIcon implements OnItemClickListener {

        int index;

        public BottomImgIcon(int index) {
            this.index = index;
        }

        @Override
        public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                long arg3) {

        }
    }

    @SuppressLint("NewApi")
    public ImageView iconImage(String filepath, int index, CheckBox checkBox) throws FileNotFoundException {
        LayoutParams params = new LayoutParams(relativeLayout2.getMeasuredHeight() - 10, relativeLayout2.getMeasuredHeight() - 10);
        ImageView imageView = new ImageView(this);
        imageView.setLayoutParams(params);
        imageView.setBackgroundResource(R.drawable.imgbg);
        float alpha = 1;
        imageView.setAlpha(alpha);
        util.imgExcute(imageView, imgCallBack, filepath);
        imageView.setOnClickListener(new ImgOnclick(filepath, checkBox));
        return imageView;
    }

    ImgCallBack imgCallBack = new ImgCallBack() {
        @Override
        public void resultImgCall(ImageView imageView, Bitmap bitmap) {
            imageView.setImageBitmap(bitmap);
        }
    };

    class ImgOnclick implements OnClickListener {
        String filepath;
        CheckBox checkBox;

        public ImgOnclick(String filepath, CheckBox checkBox) {
            this.filepath = filepath;
            this.checkBox = checkBox;
        }

        @Override
        public void onClick(View arg0) {
            checkBox.setChecked(false);
            select_layout.removeView(arg0);
            choise_button.setText("已选择(" + select_layout.getChildCount() + ")张");
            filelist.remove(filepath);
        }
    }

    ImgsAdapter.OnItemClickClass onItemClickClass = new ImgsAdapter.OnItemClickClass() {
        @Override
        public void OnItemClick(View v, int Position, CheckBox checkBox) {
            String filepath = fileTraversal.filecontent.get(Position);
            if (checkBox.isChecked()) {
                checkBox.setChecked(false);
                select_layout.removeView(hashImage.get(Position));
                filelist.remove(filepath);
                choise_button.setText("已有" + select_layout.getChildCount() + "张图片被选中");
            } else {
                try {
                    checkBox.setChecked(true);
                    Log.i("img", "img choise position->" + Position);
                    ImageView imageView = iconImage(filepath, Position, checkBox);
                    if (imageView != null) {
                        hashImage.put(Position, imageView);
                        filelist.add(filepath);
                        select_layout.addView(imageView);
                        choise_button.setText("已有" + select_layout.getChildCount() + "张图片被选中");
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    };
    private int prograssNum = 0;

    public void sendfiles(View view) {
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        dialog.setTitle("Uploading......");
        dialog.setMessage("Please wait for a moment");
        dialog.setIcon(android.R.drawable.ic_dialog_alert);
        dialog.setMax(10 * filelist.size());
        dialog.setProgress(prograssNum);
        dialog.show();

        String BASE_URL = "http://182.92.116.126:8080/ps/environment/image";
        for (int i = 0; i < filelist.size(); i++) {
            File file = new File(filelist.get(i));
            /*File newfile = new File(Environment.getExternalStorageDirectory() + "/PM2.5/" + "dengchaoyue_" + System.currentTimeMillis() + ".jpg");
            file.renameTo(newfile);
            sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)));*/
            try {
                HttpUtil.sentHttpData(BASE_URL, file.getAbsolutePath(), null, new HttpCallbackListener() {
                    @Override
                    public void onError(Exception e) {
                        dialog.dismiss();
                        Message msg = new Message();
                        msg.what = 107;
                        uploading.sendMessage(msg);
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
                            uploading.sendMessage(msg);
                        } else {
                            if (prograssNum == (filelist.size() * 10)) {
                                dialog.dismiss();
                                Message msg = new Message();
                                msg.what = 106;
                                uploading.sendMessage(msg);
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

    public Handler uploading = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 105:
                    AlertDialog.Builder builderFailed = new AlertDialog.Builder(ImgsActivity.this);
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
                    AlertDialog.Builder builder = new AlertDialog.Builder(ImgsActivity.this);
                    builder.setPositiveButton("好的", new DialogInterface.OnClickListener() {//添加确定按钮
                        @Override
                        public void onClick(DialogInterface dialog, int which) {//确定按钮的响应事件
                            dialog.dismiss();
                            SubPMStationItem item = aStation;
                            AskPhoto ask = new AskPhoto(getBaseContext(), item.getStationID(), item.getStationAddress());
                            Toast.makeText(ImgsActivity.this, "正在退回照片墙", Toast.LENGTH_LONG).show();
                            try {
                                Thread.sleep(3000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            Intent updatePhotoWall = new Intent(ImgsActivity.this, PhotoWall.class);
                            updatePhotoWall.putExtra("subStationItem", aStation);
                            updatePhotoWall.putExtra("realLongitude", realLongitude);
                            updatePhotoWall.putExtra("realLatitude", realLatitude);
                            startActivity(updatePhotoWall);
                            overridePendingTransition(R.anim.in_from_left, 0);
                            ImgsActivity.this.finish();
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
                    AlertDialog.Builder builderError = new AlertDialog.Builder(ImgsActivity.this);
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
