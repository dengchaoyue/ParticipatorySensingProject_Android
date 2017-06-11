/**
 * class name:
 * class description:
 * author: dengchaoyue
 * version: 1.0
 */
package com.example.elaine.participatorysensingproject_android.mappage;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.elaine.participatorysensingproject_android.R;

import java.io.ByteArrayOutputStream;

public class singlePicPage extends Activity {

    int fpm, actual_fpm;
    String time, near_station, address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityController.addActivity(this);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.single_pic_page);
        TextView pageTitle = (TextView) findViewById(R.id.centerText);
        TextView button1 = (TextView) findViewById(R.id.Button1);
        Typeface iconfont = Typeface.createFromAsset(getAssets(), "iconfont.ttf");
        button1.setTypeface(iconfont);
        button1.setText(R.string.icon_back);
        button1.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                singlePicPage.this.finish();
            }
        });
        final TextView button2 = (TextView) findViewById(R.id.Button2);
        button2.setTypeface(iconfont);
        button2.setText(R.string.icon_share);
        button2.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                button2.setPressed(false);
                View view = getWindow().getDecorView();
                // 设置是否可以进行绘图缓存
                view.setDrawingCacheEnabled(true);
                // 如果绘图缓存无法，强制构建绘图缓存
                view.buildDrawingCache();
                // 返回这个缓存视图
                Bitmap bitmap = view.getDrawingCache();
                // 获取状态栏高度
                Rect frame = new Rect();
                // 测量屏幕宽和高
                view.getWindowVisibleDisplayFrame(frame);
                int stautsHeight = frame.top;

                int width = getWindowManager().getDefaultDisplay().getWidth();
                int height = getWindowManager().getDefaultDisplay().getHeight();
                // 根据坐标点和需要的宽和高创建bitmap
                Bitmap temBitmap = Bitmap.createBitmap(bitmap, 0, stautsHeight, width, height - stautsHeight);

                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                temBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
                String path = MediaStore.Images.Media.insertImage(singlePicPage.this.getContentResolver(), temBitmap, "Title", null);
                Uri uri = Uri.parse(path);

                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_STREAM, uri);
                intent.setType("image/*");
                startActivity(intent);
            }
        });
        ImageView imageView = (ImageView) findViewById(R.id.pic);
        TextView FPMView = (TextView) findViewById(R.id.fpm);
        TextView ActualFPMView = (TextView) findViewById(R.id.actfpm);
        TextView timeView = (TextView) findViewById(R.id.time);
        Intent intent = getIntent();
        if (intent != null) {
            byte[] bis = intent.getByteArrayExtra("bitmap");
            int index = intent.getIntExtra("subStationIndex", -1);
            Bitmap bitmap = BitmapFactory.decodeByteArray(bis, 0, bis.length);
            imageView.setImageBitmap(bitmap);
            fpm = Images.fpm.get(index);
            actual_fpm = Images.actual_fpm.get(index);
            time = Images.time.get(index);
            near_station = Images.nearStation.get(index);
            address = Images.address.get(index);
            FPMView.setText("预估值：" + fpm + "");
            ActualFPMView.setText("实际值：" + actual_fpm + "");
            timeView.setText(time);
            pageTitle.setText(address + "站点照片");
            pageTitle.setTextSize(15);

        }

    }

    protected void onDestroy() {
        super.onDestroy();
        ActivityController.removeActivity(this);
    }

    public void onBackPressed() {
        super.onBackPressed();
    }
}