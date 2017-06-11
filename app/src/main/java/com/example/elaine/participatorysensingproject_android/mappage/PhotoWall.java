/**
 * class name:
 * class description:
 * author: dengchaoyue
 * version: 1.0
 */
package com.example.elaine.participatorysensingproject_android.mappage;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.json.JSONException;
import org.json.JSONObject;
import com.example.elaine.participatorysensingproject_android.R;
import com.example.elaine.participatorysensingproject_android.Http.DownloadSinglePic;
import com.example.elaine.participatorysensingproject_android.Http.HttpCallbackListener;
import com.example.elaine.participatorysensingproject_android.R.id;
import com.example.elaine.participatorysensingproject_android.diySystemAlbum.ImgFileListActivity;
import com.example.elaine.participatorysensingproject_android.usercenterpage.UserNotSignedActivity;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author dengchaoyue
 */
public class PhotoWall extends Activity {
    static SubPMStationItem aStation;
    String orientation_x, orientation_y, orientation_z, latitude, altitude, longitude, station, realLatitude, realLongitude;
    String username;
    ProgressDialog myDialog;
    static String picName;
    private static final int FAILED = 404;
    private static final int ERROR = 505;
    private static final int ABLEDIALOG = 202;
    private static final int DISABLEDIALOG = 303;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityController.addActivity(this);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.photo_wall);

        Intent intent = getIntent();
        aStation = (SubPMStationItem) intent.getSerializableExtra("subStationItem");
        realLatitude = intent.getStringExtra("realLatitude");
        realLongitude = intent.getStringExtra("realLongitude");
        System.out.println("Address:" + aStation.getStationAddress());

        TextView pageTitle = (TextView) findViewById(R.id.centerText);
        pageTitle.setText("照片墙");
        TextView button1 = (TextView) findViewById(R.id.Button1);
        Typeface iconfont = Typeface.createFromAsset(getAssets(), "iconfont.ttf");
        button1.setTypeface(iconfont);
        button1.setText(R.string.icon_back);
        button1.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Images.imageUrls.clear();
                PhotoWall.this.finish();
            }
        });
        TextView button2 = (TextView) findViewById(R.id.Button2);
        button2.setTypeface(iconfont);
        button2.setText(R.string.icon_camera);
        button2.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                createCustomDialog();
            }

        });
    }

    public void createCustomDialog() {
        final Dialog dialog = new Dialog(PhotoWall.this, R.style.dialog);//加载样式
        dialog.setContentView(R.layout.dialog_layout);
        dialog.show();
        TextView openCamera = (TextView) dialog.findViewById(id.openCamera);
        TextView openAlbum = (TextView) dialog.findViewById(id.openAlbum);
        openAlbum.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                cameraDIY.mark = 1;
                Intent intent = new Intent(PhotoWall.this, ImgFileListActivity.class);
                //intent.setClass(PhotoWall.this,ImgFileListActivity.class);
                intent.putExtra("subStationItem", aStation);
                intent.putExtra("realLongitude", realLongitude);
                intent.putExtra("realLatitude", realLatitude);
                startActivity(intent);
                dialog.dismiss();
                PhotoWall.this.finish();
            }
        });
        openCamera.setOnClickListener(new OnClickListener() {
                                          @Override
                                          public void onClick(View v) {
                                              //判断当前用户是否登录，如果未登录，跳转到登录界面。
                                              SharedPreferences preferences = getSharedPreferences("user", MODE_PRIVATE);
                                              username = preferences.getString("username", "");
                                              if ("".equals(username)) {
                                                  AlertDialog.Builder builder = new AlertDialog.Builder(PhotoWall.this);
                                                  builder.setNegativeButton("前去登录", new DialogInterface.OnClickListener() {//添加确定按钮
                                                      @Override
                                                      public void onClick(DialogInterface dialogNote, int which) {//确定按钮的响应事件
                                                          dialogNote.dismiss();
                                                          Intent register = new Intent(PhotoWall.this, UserNotSignedActivity.class);

                                                          startActivity(register);
                                                          PhotoWall.this.finish();
                                                      }
                                                  });
                                                  AlertDialog dialogNote = builder.create();
                                                  dialogNote.setTitle("Note: ");//设置对话框标题
                                                  dialogNote.setMessage("您还没有登录，请先登录后再进行拍照");//设置显示的内容
                                                  Window win = dialogNote.getWindow();
                                                  win.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                                                  dialogNote.show();
                                              } else {
                                                  cameraDIY.mark = 1;
                                                  String imageUrl = Images.imageUrls.get(0);
                                                  int lastSlashIndex = imageUrl.lastIndexOf("/");
                                                  dialog.dismiss();

                                                  myDialog = ProgressDialog.show(PhotoWall.this, "手机姿态定位", "正在获取当前站点信息...", true);



                                                  String url = "http://182.92.116.126:8080/" + imageUrl.substring(lastSlashIndex + 10);
                                                  System.out.println(url);
                                                  picName = url.substring(url.lastIndexOf("/") + 1);
                                                  DownloadSinglePic.download(PhotoWall.this,picName, new HttpCallbackListener() {
                                                      @Override
                                                      public void onError(Exception e) {
                                                          Message msg = new Message();
                                                          msg.what = ERROR;
                                                          aDialog.sendMessage(msg);
                                                      }

                                                      @Override
                                                      public void onFinish(String response) {
                                                          if ("Failed".equals(response)) {
                                                              Message msg = new Message();
                                                              msg.what = FAILED;
                                                              aDialog.sendMessage(msg);
                                                          } else {
                                                              System.out.println(response);
                                                              sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(new File(response))));
                                                              /*File f = new File(response);
                                                              bmpDefaultPic = decodeBitmap(f.getAbsolutePath());*/

                                                              String msgString = PhotoWall.readEXIF(response);
                                                              JSONObject message = null;
                                                              if (msgString != null) {
                                                                  try {
                                                                      message = new JSONObject(msgString);
                                                                  } catch (JSONException e1) {
                                                                      // TODO Auto-generated catch block
                                                                      e1.printStackTrace();
                                                                  }
                                                                  //JSONObject msg = readEXIF(Environment.getExternalStorageDirectory().getPath() + "/singlePic/" + singlePicFile);
                                                                  try {
                                                                      orientation_x = message.getString("Orientation_X");
                                                                      orientation_y = message.getString("Orientation_Y");
                                                                      orientation_z = message.getString("Orientation_Z");
                                                                      latitude = message.getString("Latitude");
                                                                      altitude = message.getString("Altitude");
                                                                      longitude = message.getString("Longitude");
                                                                      station = aStation.getStationID() + "";
                                                                  } catch (JSONException e) {
                                                                      // TODO Auto-generated catch block
                                                                      e.printStackTrace();
                                                                  }
                                                                  Message msg = new Message();
                                                                  msg.what = ABLEDIALOG;
                                                                  aDialog.sendMessage(msg);
                                                              } else {
                                                                  orientation_x = "0";
                                                                  orientation_y = "0";
                                                                  orientation_z = "0";
                                                                  latitude = "0";
                                                                  altitude = "0";
                                                                  longitude = "0";
                                                                  station = aStation.getStationID() + "";
                                                                  Message msg = new Message();
                                                                  msg.what = DISABLEDIALOG;
                                                                  aDialog.sendMessage(msg);
                                                              }
                                                          }
                                                      }
                                                  });
                                              }
                                          }
                                      }

        );

    }


    private Handler aDialog = new Handler() {

        public void handleMessage(Message msg) {
            switch (msg.what) {
                case ERROR:
                    myDialog.dismiss();
                    Toast.makeText(getApplicationContext(), "下载原始图片失败---error", Toast.LENGTH_LONG).show();
                    break;
                case FAILED:
                    myDialog.dismiss();
                    Toast.makeText(getApplicationContext(), "下载原始图片失败---failed", Toast.LENGTH_LONG).show();
                    break;
                case DISABLEDIALOG:
                    myDialog.dismiss();
                    Toast.makeText(getApplicationContext(), "下载原始图片失败---未获取照片姿态值", Toast.LENGTH_LONG).show();

                    Intent takePhotoSS = new Intent(PhotoWall.this, cameraDIY.class);
                    if (aStation.getStationAddress() != null) {
                        takePhotoSS.putExtra("subStationItem", aStation);
                    }
                    Bundle myBundleSS = new Bundle();
                    myBundleSS.putString("singlePicFile", picName);
                    myBundleSS.putString("station", station);
                    myBundleSS.putString("username", username);
                    myBundleSS.putString("latitude", "-1");
                    myBundleSS.putString("realLatitude", realLatitude);
                    myBundleSS.putString("altitude", "-1");
                    myBundleSS.putString("longitude", "-1");
                    myBundleSS.putString("realLongitude", realLongitude);
                    myBundleSS.putString("orientation_x", "-1");
                    myBundleSS.putString("orientation_y", "-1");
                    myBundleSS.putString("orientation_z", "-1");
                    takePhotoSS.putExtras(myBundleSS);
                    startActivity(takePhotoSS);
                    PhotoWall.this.finish();
                    break;

                case ABLEDIALOG:
                    myDialog.dismiss();
                    Toast.makeText(getApplicationContext(), "下载原始图片成功", Toast.LENGTH_LONG).show();
                    Intent takePhoto = new Intent(PhotoWall.this, cameraDIY.class);

                    /*ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bmpDefaultPic.compress(Bitmap.CompressFormat.PNG, 100, baos);
                    byte[] bitmapByte = baos.toByteArray();
                    takePhoto.putExtra("bitmap", bitmapByte);*/

                    if (aStation.getStationAddress() != null) {
                        takePhoto.putExtra("subStationItem", aStation);
                    }
                    Bundle myBundle = new Bundle();
                    myBundle.putString("singlePicFile", picName);
                    myBundle.putString("station", station);
                    myBundle.putString("username", username);
                    myBundle.putString("latitude", latitude);
                    myBundle.putString("realLatitude", realLatitude);
                    myBundle.putString("altitude", altitude);
                    myBundle.putString("longitude", longitude);
                    myBundle.putString("realLongitude", realLongitude);
                    myBundle.putString("orientation_x", orientation_x);
                    myBundle.putString("orientation_y", orientation_y);
                    myBundle.putString("orientation_z", orientation_z);
                    takePhoto.putExtras(myBundle);
                    startActivity(takePhoto);
                    PhotoWall.this.finish();
                    break;
            }
        }
    };


    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
    }


    public static boolean addEXIF(String msg, String path) {
        // TODO Auto-generated method stub
        try {
            //获取当前时间
            SimpleDateFormat formatter = new SimpleDateFormat ("yyy:MM:dd HH:mm:ss");
            Date curDate = new Date(System.currentTimeMillis());//获取当前时间
            String str = formatter.format(curDate);

            ExifInterface exif = new ExifInterface(path);
            //System.out.println(exif.getAttribute(ExifInterface.TAG_DATETIME));
            exif.setAttribute("UserComment", msg);
            exif.setAttribute("DateTime", str);
            exif.saveAttributes();
            System.out.println(exif.getAttribute(ExifInterface.TAG_DATETIME));
            System.out.println(exif.getAttribute("UserComment"));
            return true;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
        }
    }

    /**
     * @param path,stationId
     * @return
     */
    public static String modifyEXIF(String path, String stationId, String realLatitude, String realLongitude,String username) {
        // TODO Auto-generated method stub
        File singlePic = new File(path);
        System.out.println("cy:" + path);
        String a = null;
        if (!singlePic.exists()) {
            Log.e("ModifyExif", "modifyEXIF failed, model pic does not exist exif msg;");
        }
        try {
            ExifInterface exif = new ExifInterface(path);
            String DateTime = exif.getAttribute("DateTime");
            String DateTime2 = exif.getAttribute(ExifInterface.TAG_DATETIME);
            System.out.println(DateTime + ":" + DateTime2);
            if (exif.getAttribute("UserComment") != null) {
                a = exif.getAttribute("UserComment");
                a = a.replace("?","");
                Log.e("WaitModifyExif", a);
                try {
                    JSONObject obj = new JSONObject(a);
                    obj.put("username", username);
                    obj.put("Station", stationId);
                    obj.put("RealLongitude", realLongitude);
                    obj.put("RealLatitude", realLatitude);
                    exif.setAttribute("UserComment", obj.toString());
                    exif.saveAttributes();
                    //obj.put(ExifInterface.TAG_DATETIME, DateTime);
                    String str2 = exif.getAttribute(ExifInterface.TAG_DATETIME);
                    System.out.println(str2);
                    return exif.getAttribute("UserComment");
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    return null;
                }
            }
            return a;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
        //return a;
    }

    public static String readEXIF(String path) {
        File singlePic = new File(path);
        System.out.println("cy:" + path);
        String a = null;
        if (!singlePic.exists()) {
            Log.e("readEXIF", "readEXIF failed, file does not exist;");
        }
        try {
            ExifInterface exif = new ExifInterface(path);
            String DateTime = exif.getAttribute("DateTime");
            String DateTime2 = exif.getAttribute(ExifInterface.TAG_DATETIME);
            System.out.println(DateTime + ":" + DateTime2);
            if (exif.getAttribute("UserComment") != null) {
                a = exif.getAttribute("UserComment");
                Log.e("ReadExif", a);
                //a = PhotoWall.modifyEXIF(a);
            }
            if (a != null) {
                a = a.replace("?", "");
                Log.e("ReadExif", a);
            }

            return a;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
        //return a;
    }

    protected void onDestroy() {
        super.onDestroy();
        Images.imageUrls.clear();
        Images.address.clear();
        Images.fpm.clear();
        Images.actual_fpm.clear();
        Images.nearStation.clear();
        Images.time.clear();
        ActivityController.removeActivity(this);
    }

    public void onBackPressed() {
        super.onBackPressed();
        Images.imageUrls.clear();
        Images.address.clear();
        Images.fpm.clear();
        Images.actual_fpm.clear();
        Images.nearStation.clear();
        Images.time.clear();
    }
}
