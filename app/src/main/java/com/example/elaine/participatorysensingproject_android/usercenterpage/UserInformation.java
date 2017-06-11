package com.example.elaine.participatorysensingproject_android.usercenterpage;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.elaine.participatorysensingproject_android.R;
import com.example.elaine.participatorysensingproject_android.mappage.ActivityController;
import com.example.elaine.participatorysensingproject_android.mappage.cameraDIY;

/**
 * Created by Elaine on 2016/11/3.
 */
public class UserInformation extends Activity {
    private String username;
    SelectPicPopupWindow menuWindow;
    private final int CAMERA = 1010;
    private final int PICTURE = 1111;
    private ImageView userImage;
    public static Bitmap UserIcon = null;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityController.addActivity(this);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.usercenter_userinfo);
        TextView pageTitle = (TextView) findViewById(R.id.centerText);
        pageTitle.setText("用户信息");
        Intent in = getIntent();
        username = in.getStringExtra("username");

        Typeface iconfont = Typeface.createFromAsset(getAssets(), "iconfont.ttf");
        TextView button1 = (TextView) findViewById(R.id.Button1);
        button1.setTypeface(iconfont);
        button1.setText(R.string.icon_back);
        button1.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                UserInformation.this.finish();
            }
        });
        TextView button2 = (TextView) findViewById(R.id.Button2);
        button2.setText("退出");
        button2.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                UserCenterPage.userName = "";
                UserIcon = null;
                SharedPreferences sp = getSharedPreferences("userIcon", MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                editor.clear().commit();
                SharedPreferences spTwo = getSharedPreferences("user", MODE_PRIVATE);
                SharedPreferences.Editor editorTwo = spTwo.edit();
                editorTwo.clear().commit();
                UserInformation.this.finish();
            }
        });

        TextView uc_front_icon = (TextView) findViewById(R.id.uc_front_icon);
        uc_front_icon.setTypeface(iconfont);
        uc_front_icon.setText(R.string.icon_front);
        uc_front_icon.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                //自定义的弹出框类
                menuWindow = new SelectPicPopupWindow(UserInformation.this, itemsOnClick);
                //显示窗口
                //设置layout在PopupWindow中显示的位置
                menuWindow.showAtLocation(findViewById(R.id.userinfo_username), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
            }
        });


        TextView usernameTv = (TextView) findViewById(R.id.userinfo_username);
        usernameTv.setText(username);
        userImage = (ImageView) findViewById(R.id.userinfo_image);
        if (UserIcon != null) {
            userImage.setImageBitmap(UserIcon);
        } else {
            userImage.setImageResource(R.drawable.user_icon);
        }
    }

    private View.OnClickListener itemsOnClick = new View.OnClickListener() {

        public void onClick(View v) {
            menuWindow.dismiss();
            switch (v.getId()) {
                case R.id.btn_take_photo:
                    cameraDIY.mark = 2;
                    Intent intent = new Intent(UserInformation.this, cameraDIY.class);
                    startActivity(intent);
                    break;
                case R.id.btn_pick_photo:
                    Intent picture = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(picture, PICTURE);
                    break;
                default:
                    break;
            }


        }

    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICTURE && resultCode == Activity.RESULT_OK && null != data) {

            Uri selectedImage = data.getData();
            String[] filePathColumns = {MediaStore.Images.Media.DATA};
            Cursor c = this.getContentResolver().query(selectedImage, filePathColumns, null, null, null);
            c.moveToFirst();
            int columnIndex = c.getColumnIndex(filePathColumns[0]);
            String picturePath = c.getString(columnIndex);
            c.close();
            //获取图片并显示
            Bitmap bmpDefaultPic = BitmapFactory.decodeFile(picturePath, null);
            int w = bmpDefaultPic.getWidth(); // 得到图片的宽，高
            int h = bmpDefaultPic.getHeight();
            int wh = w > h ? h : w;// 裁切后所取的正方形区域边长
            int retX = w > h ? (w - h) / 2 : 0;// 基于原图，取正方形左上角x坐标
            int retY = w > h ? 0 : (h - w) / 2;
            Bitmap bmp = Bitmap.createBitmap(bmpDefaultPic, retX, retY, wh, wh, null,
                    false);
            SharedPreferences.Editor editor = getSharedPreferences("userIcon", MODE_PRIVATE).edit();
            editor.putString("picPath", picturePath);
            editor.commit();

            UserIcon = bmp;
            userImage.setImageBitmap(ThumbnailUtils.extractThumbnail(bmp, 80, 80));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (UserIcon != null) {
            userImage.setImageBitmap(ThumbnailUtils.extractThumbnail(UserIcon, 80, 80));
        } else {
            userImage.setImageResource(R.drawable.user_icon);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        UserInformation.this.finish();
    }
}
