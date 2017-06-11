package com.example.elaine.participatorysensingproject_android.mappage;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.Parameters;
import android.hardware.SensorListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.elaine.participatorysensingproject_android.AutoCamera.AutoCameraActivity;
import com.example.elaine.participatorysensingproject_android.Http.HttpCallbackListener;
import com.example.elaine.participatorysensingproject_android.R;
import com.example.elaine.participatorysensingproject_android.R.drawable;
import com.example.elaine.participatorysensingproject_android.R.id;
import com.example.elaine.participatorysensingproject_android.usercenterpage.UserInformation;

import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@SuppressWarnings("deprecation")
public class cameraDIY extends Activity implements SensorListener {
    private WindowManager wm;
    public static int w;
    public static int h;
    private LinearLayout surfaceView;
    private CameraSurface cameraSurface;
    private int degrees = 0;

    private SurfaceHolder holder;
    public Camera camera = null;
    private boolean isPreview;
    private Parameters parameters;
    Bundle bundle = null;
    private TextView take;
    private TextView back;
    private TextView usephoto;
    private TextView mOrientation;
    private TextView mLRangle;
    private TextView mHBangle;

    final String tag = "InvokeSensor";
    public static int rotCounter = 0;
    public double temp;
    public double w1;
    public static double w2;
    public double wr;
    SensorManager sm = null;
    public static double ox, oy, oz;
    public static double ax, ay, az;
    public static double l_r_angle0;
    public static double l_r_angle;
    public static double h_b_angle0;
    public static double h_b_angle;
    public static int focusX, focusY;
    private LinearLayout overlayoutview;
    private Hint hint;
    private FocusArea fa;
    private Thread ar;
    public static int mark = 0;

    public static String storePath;
    public String uploadFilePath;
    public File jpgFile;
    public static SubPMStationItem aStation;
    public static String station, username;
    public static String singlePicFile;
    public double orientation_x, orientation_y, orientation_z, altitude, latitude, longitude;
    private String realLongitude, realLatitude;

    private ProgressDialog myDialog = null;
    //相机拍照界面的缩放功能
    private SeekBar mZoomSeekBar;
    private int mZoom = 0;
    private Handler mHandler;
    private int rotation;

    //缩略图
    private ImageView miniPic, fullSreenPic;
    private Bitmap modelPic, newModelPic;
    private String imageInSD;

    //NewStation
    private String newStationId = "";
    private String newLongitude, newLatitude, newAltitude = "";
    private String newOrientationX, newOrientationY, newOrientationZ = "";

    //store PM2.5 picture
    private static boolean storeSucceed = false;

    //曝光
    private SeekBar mExposureBar;
    private int mExposure = 0;

    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        ActivityController.addActivity(this);
        storeSucceed = false;

        //清空自动相机拍照次数-count值
        SharedPreferences preferences = this.getSharedPreferences("count", Context.MODE_ENABLE_WRITE_AHEAD_LOGGING);
        //读取SharedPreferences里的count数据，若存在返回其值，否则返回0
        //int count = preferences.getInt("count",0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("count", 0);
        editor.commit();

        //清空自动相机拍摄照片路径集
        SharedPreferences preferencesS = this.getSharedPreferences("storePath", Context.MODE_ENABLE_WRITE_AHEAD_LOGGING);
        //读取SharedPreferences里的count数据，若存在返回其值，否则返回0
        SharedPreferences.Editor editorS = preferencesS.edit();
        editorS.putString("storePath", "");
        editorS.commit();

        //清理
        SharedPreferences preferencesSS = getSharedPreferences("station", Context.MODE_ENABLE_WRITE_AHEAD_LOGGING);
        //读取SharedPreferences里的count数据，若存在返回其值，否则返回0
        SharedPreferences.Editor editorSS = preferencesSS.edit();
        editorSS.putString("station", "");
        editorSS.commit();

        SharedPreferences preferencesSSS = getSharedPreferences("singlePicFile", Context.MODE_ENABLE_WRITE_AHEAD_LOGGING);
        //读取SharedPreferences里的count数据，若存在返回其值，否则返回0
        SharedPreferences.Editor editorSSS = preferencesSSS.edit();
        editorSSS.putString("singlePicFile", "");
        editorSSS.commit();
        SharedPreferences preferencesSSSS = getSharedPreferences("realLongitude", Context.MODE_ENABLE_WRITE_AHEAD_LOGGING);
        //读取SharedPreferences里的count数据，若存在返回其值，否则返回0
        SharedPreferences.Editor editorSSSS = preferencesSSSS.edit();
        editorSSSS.putString("realLongitude", "");
        editorSSSS.commit();

        SharedPreferences preferencesSSSSS = getSharedPreferences("realLatitude", Context.MODE_ENABLE_WRITE_AHEAD_LOGGING);
        //读取SharedPreferences里的count数据，若存在返回其值，否则返回0
        SharedPreferences.Editor editorSSSSS = preferencesSSSSS.edit();
        editorSSSSS.putString("realLatitude", "");
        editorSSSSS.commit();


        //获取传参
        Intent intent = getIntent();

/*        byte[] bis = intent.getByteArrayExtra("bitmap");
        modelPic = BitmapFactory.decodeByteArray(bis, 0, bis.length);*/


        if (intent.getSerializableExtra("subStationItem") != null) {
            aStation = (SubPMStationItem) intent.getSerializableExtra("subStationItem");
            System.out.println("Address:" + aStation.getStationAddress());

            Bundle myBundle = this.getIntent().getExtras();
            station = myBundle.getString("station");
            singlePicFile = myBundle.getString("singlePicFile");
            username = myBundle.getString("username");
            orientation_x = Double.parseDouble(myBundle.getString("orientation_x"));
            orientation_y = Double.parseDouble(myBundle.getString("orientation_y"));
            orientation_z = Double.parseDouble(myBundle.getString("orientation_z"));
            altitude = Double.parseDouble(myBundle.getString("altitude"));
            latitude = Double.parseDouble(myBundle.getString("latitude"));
            longitude = Double.parseDouble(myBundle.getString("longitude"));
            realLatitude = myBundle.getString("realLatitude");
            realLongitude = myBundle.getString("realLongitude");
        } else {
            Bundle myBundle = intent.getExtras();
            newStationId = myBundle.getString("stationID");
            newLongitude = myBundle.getString("longitude");
            newLatitude = myBundle.getString("latitude");
            newAltitude = myBundle.getString("altitude");
            username = myBundle.getString("username");
            Log.e("newPOI", newStationId + ":" + longitude + ":" + latitude + ":" + altitude + ":" + username);
        }

        //设置窗口
        Window window = getWindow();
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        // window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
        wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        w = display.getWidth();
        h = display.getHeight();
        setContentView(R.layout.camera);

        surfaceView = (LinearLayout) findViewById(R.id.surfaceView);
        cameraSurface = new CameraSurface(this);
        cameraSurface.setFocusable(true);
        cameraSurface.setFocusableInTouchMode(true);
        surfaceView.addView(cameraSurface);
        surfaceView.setFocusable(true);
        take = (TextView) findViewById(R.id.takePhoto);
        back = (TextView) findViewById(R.id.back);
        back.setTag("backToPhotoWall");
        usephoto = (TextView) findViewById(R.id.usephoto);
        if (mark == 2) {
            usephoto.setText("用做头像");
        }
        /*mOrientation = (TextView) findViewById(R.id.orientation);
        mLRangle = (TextView) findViewById(R.id.LRangle);
        mHBangle = (TextView) findViewById(R.id.HBangle);*/
        overlayoutview = (LinearLayout) findViewById(R.id.overlayoutview);
        overlayoutview.removeAllViews();
        mZoomSeekBar = (SeekBar) findViewById(R.id.zoomSeekBar);
        mExposureBar = (SeekBar) findViewById(id.exposureSeekBar);
        miniPic = (ImageView) findViewById(R.id.minipic);
        fullSreenPic = (ImageView) findViewById(R.id.fullscreenpic);
        fullSreenPic.setAlpha(0.5f);
        fullSreenPic.setScaleType(ImageView.ScaleType.FIT_START);
        miniPic.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                miniPic.setVisibility(View.GONE);
                fullSreenPic.setVisibility(View.VISIBLE);
            }
        });
        fullSreenPic.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                fullSreenPic.setVisibility(View.GONE);
                miniPic.setVisibility(View.VISIBLE);
            }
        });

        //设置监听
        mHandler = new Handler();
        final SeekBar.OnSeekBarChangeListener onSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
                // TODO Auto-generated method stub
                cameraSurface.setZoom(progress);
                mHandler.removeCallbacksAndMessages(mZoomSeekBar);
                //ZOOM模式下 在结束两秒后隐藏seekbar 设置token为mZoomSeekBar用以在连续点击时移除前一个定时任务
                mHandler.postAtTime(new Runnable() {
                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        mZoomSeekBar.setVisibility(View.GONE);
                    }
                }, mZoomSeekBar, SystemClock.uptimeMillis() + 2000);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub

            }
        };
        //获取当前照相机支持的最大缩放级别，值小于0表示不支持缩放。当支持缩放时，加入拖动条。
        int maxZoom = cameraSurface.getMaxZoom();
        if (maxZoom > 0) {
            mZoomSeekBar.setMax(maxZoom);
            mZoomSeekBar.setOnSeekBarChangeListener(onSeekBarChangeListener);
        }


        //设置曝光监听
        final SeekBar.OnSeekBarChangeListener onSeekBarChangeListenerTwo = new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
                // TODO Auto-generated method stub
                cameraSurface.setExposure(progress);
                mHandler.removeCallbacksAndMessages(mExposureBar);
                //在结束两秒后隐藏seekbar 设置token为mExposureBar用以在连续点击时移除前一个定时任务
                mHandler.postAtTime(new Runnable() {
                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        mExposureBar.setVisibility(View.GONE);
                    }
                }, mExposureBar, SystemClock.uptimeMillis() + 2000);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub

            }
        };
        //获取当前照相机支持的最大缩放级别，值小于0表示不支持缩放。当支持缩放时，加入拖动条。
        int maxExposure = cameraSurface.getMaxExposure();
        if (maxExposure > 0) {
            mExposureBar.setMax(maxExposure);
            mExposureBar.setOnSeekBarChangeListener(onSeekBarChangeListenerTwo);
        }


        //添加手动缩放照片，聚焦功能
        cameraSurface.setOnTouchListener(new View.OnTouchListener() {
            /**
             * 记录是拖拉照片模式还是放大缩小照片模式
             */
            private static final int MODE_INIT = 0;
            /**
             * 放大缩小照片模式
             */
            private static final int MODE_ZOOM = 1;
            private int mode = MODE_INIT;// 初始状态
            /**
             * 用于记录拖拉图片移动的坐标位置
             */
            private float startDis;
            private float startPos;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                /** 通过与运算保留最后八位 MotionEvent.ACTION_MASK = 255 */
                switch (event.getAction() & MotionEvent.ACTION_MASK) {
                    // 手指压下屏幕
                    case MotionEvent.ACTION_DOWN:
                        mode = MODE_INIT;
                        mHandler.removeCallbacksAndMessages(mExposureBar);
                        mExposureBar.setVisibility(View.VISIBLE);
                        startPos = event.getY(0);
                        break;
                    case MotionEvent.ACTION_POINTER_DOWN:
                        //如果mZoomSeekBar为null 表示该设备不支持缩放 直接跳过设置mode Move指令也无法执行
                        if (mZoomSeekBar == null) return true;
                        mHandler.removeCallbacksAndMessages(mZoomSeekBar);
                        mZoomSeekBar.setVisibility(View.VISIBLE);
                        mode = MODE_ZOOM;
                        /** 计算两个手指间的距离 */
                        startDis = distance(event);
                        break;
                    case MotionEvent.ACTION_MOVE:
                        if (mode == MODE_ZOOM) {
                            //只有同时触屏两个点的时候才执行
                            mZoomSeekBar.setVisibility(View.VISIBLE);
                            if (event.getPointerCount() < 2) return true;
                            float endDis = distance(event);// 结束距离
                            //每变化10f zoom变1
                            int scale = (int) ((endDis - startDis) / 10f);
                            if (scale >= 1 || scale <= -1) {
                                int zoom = cameraSurface.getZoom() + scale;
                                //zoom不能超出范围
                                if (zoom > cameraSurface.getMaxZoom())
                                    zoom = cameraSurface.getMaxZoom();
                                if (zoom < 0) zoom = 0;
                                cameraSurface.setZoom(zoom);
                                float pro = (float) zoom * 100 / (float) cameraSurface.getMaxZoom();
                                mZoomSeekBar.setProgress((int) pro);
                                //将最后一次的距离设为当前距离
                                startDis = endDis;
                            }
                        } else {
                            mode = MODE_PRIVATE;
                            float endPos = event.getY();
                            int scaleTwo = (int) ((startPos - endPos) / 10f);
                            if (scaleTwo >= 1 || scaleTwo <= -1) {
                                int exp = cameraSurface.getExposure() + scaleTwo;
                                //zoom不能超出范围
                                if (exp > cameraSurface.getMaxExposure())
                                    exp = cameraSurface.getMaxExposure();
                                if (exp < cameraSurface.getMinExposure())
                                    exp = cameraSurface.getMinExposure();
                                cameraSurface.setExposure(exp);
                                float proTwo = (float) (exp + 12) * 100 / (float) (cameraSurface.getMaxExposure() * 2);
                                mExposureBar.setProgress((int) proTwo);
                                //将最后一次的距离设为当前距离
                                startPos = endPos;
                            }
                        }
                        break;
                    // 手指离开屏幕
                    case MotionEvent.ACTION_UP:
                        if (mode != MODE_ZOOM || mode != MODE_PRIVATE) {
                            //设置聚焦
                            focusOnTouch((int) event.getX(), (int) event.getY());
                            focusX = (int) event.getX();
                            focusY = (int) event.getY();
                            fa = new FocusArea(cameraDIY.this);
                            overlayoutview.addView(fa);
                            mHandler.postAtTime(new Runnable() {

                                @Override
                                public void run() {
                                    // TODO Auto-generated method stub
                                    overlayoutview.removeView(fa);
                                    fa = null;
                                }
                            }, fa, SystemClock.uptimeMillis() + 2000);
                        } else if (mode == MODE_PRIVATE) {
                            mHandler.postAtTime(new Runnable() {
                                @Override
                                public void run() {
                                    // TODO Auto-generated method stub
                                    mExposureBar.setVisibility(View.GONE);
                                }
                            }, mExposureBar, SystemClock.uptimeMillis() + 2000);
                        } else if (mode == MODE_ZOOM) {
                            //ZOOM模式下 在结束两秒后隐藏seekbar 设置token为mZoomSeekBar用以在连续点击时移除前一个定时任务
                            mHandler.postAtTime(new Runnable() {

                                @Override
                                public void run() {
                                    // TODO Auto-generated method stub
                                    mZoomSeekBar.setVisibility(View.GONE);
                                }
                            }, mZoomSeekBar, SystemClock.uptimeMillis() + 2000);
                        }
                        break;
                }
                return true;
            }
        });

        if (mark == 1) {
            hint = new Hint(this);
            overlayoutview.addView(hint);
            ar = new Thread(new HintThread());
            ar.start();

            //miniPic.setAdjustViewBounds(true);
            //miniPic.setMaxHeight(192);
            //miniPic.setMaxWidth(192);

            /*new Thread(new Runnable() {
                @Override
                public void run() {
                    String imageSdUri = Environment.getExternalStorageDirectory() + "/singlePic/" + singlePicFile;
                    File mFile = new File(imageSdUri);
                    //若该文件存在
                    if (mFile.exists()) {
                        BitmapFactory.Options options = new BitmapFactory.Options();
                        options.inJustDecodeBounds = true;
                        modelPic = BitmapFactory.decodeFile(imageSdUri, options);
                        int w = options.outWidth;
                        int h = options.outHeight;
                        options.inSampleSize = w / getWindowManager().getDefaultDisplay().getWidth() / 4;
                        options.inJustDecodeBounds = false;
                        modelPic = BitmapFactory.decodeFile(imageSdUri, options);
                        Message bitmapMsg = new Message();
                        bitmapMsg.what = 777;
                        miniBitmapHandler.sendMessage(bitmapMsg);
                    }
                }
            }).start();*/

            String imageSdUri = Environment.getExternalStorageDirectory() + "/singlePic/" + singlePicFile;
            //ImageLoader imageLoader = ImageLoader.getInstance();
            //Bitmap bitmapa = imageLoader.getBitmapFromMemoryCache(imageSdUri);
            Bitmap bitmapb = ImageLoader.decodeSampledBitmapFromResource(imageSdUri, 200);
            miniPic.setImageBitmap(bitmapb);
            fullSreenPic.setImageBitmap(bitmapb);

        }
        if (mark == 0) {
            miniPic.setImageBitmap(null);
            fullSreenPic.setImageBitmap(null);
        }
    }


    /*private Handler miniBitmapHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 777) {
                if (modelPic != null) {
                    if (modelPic.getHeight() < modelPic.getWidth()) {
                        newModelPic = DegreeBitmap.adjustPhotoRotation(modelPic, 90);
                    }
                    //miniPic.setImageBitmap(ThumbnailUtils.extractThumbnail(newModelPic, 108, 192, ThumbnailUtils.OPTIONS_RECYCLE_INPUT));
                    //fullSreenPic.setImageBitmap(ThumbnailUtils.extractThumbnail(newModelPic, 108, 192, ThumbnailUtils.OPTIONS_RECYCLE_INPUT));
                }
            }
        }
    };*/

    private float distance(MotionEvent event) {
        float dx = event.getX(1) - event.getX(0);
        float dy = event.getY(1) - event.getY(0);
        /** 使用勾股定理返回两点之间的距离 */
        return (float) Math.sqrt(dx * dx + dy * dy);
    }


    private void focusOnTouch(int x, int y) {
        Rect rect = new Rect(x - 100, y - 100, x + 100, y + 100);
        int left = rect.left * 2000 / cameraSurface.getWidth() - 1000;
        int top = rect.top * 2000 / cameraSurface.getHeight() - 1000;
        int right = rect.right * 2000 / cameraSurface.getWidth() - 1000;
        int bottom = rect.bottom * 2000 / cameraSurface.getHeight() - 1000;
        // 如果超出了(-1000,1000)到(1000, 1000)的范围，则会导致相机崩溃
        left = left < -1000 ? -1000 : left;
        top = top < -1000 ? -1000 : top;
        right = right > 1000 ? 1000 : right;
        bottom = bottom > 1000 ? 1000 : bottom;
        focusOnRect(new Rect(left, top, right, bottom));
    }

    protected void focusOnRect(Rect rect) {
        if (camera != null) {
            Parameters parameters = camera.getParameters(); // 先获取当前相机的参数配置对象
            parameters.setFocusMode(Parameters.FOCUS_MODE_AUTO); // 设置聚焦模式
            if (parameters.getMaxNumFocusAreas() > 0) {
                List<Camera.Area> focusAreas = new ArrayList<Camera.Area>();
                focusAreas.add(new Camera.Area(rect, 1000));
                parameters.setFocusAreas(focusAreas);
            }
            camera.cancelAutoFocus(); // 先要取消掉进程中所有的聚焦功能
            camera.setParameters(parameters); // 一定要记得把相应参数设置给相机
            camera.autoFocus(new AutoFocusCallback() {
                @Override
                public void onAutoFocus(boolean success, Camera camera) {
                    Log.d("AutoFocus", "onAutoFocus : " + success);
                }
            });
        }
    }


    @Override
    public void onSensorChanged(int sensor, float[] values) {
        if (sensor == SensorManager.SENSOR_ORIENTATION) {
            ox = values[0];
            oy = values[1];
            oz = values[2];
            orientation(ox);
        }
        if (sensor == SensorManager.SENSOR_ACCELEROMETER) {
            ax = values[0];
            ay = values[1];
            az = values[2];
            orientation(ox);
        }
    }

    public void onAccuracyChanged(int sensor, int accuracy) {
        Log.d(tag, "onAccuracyChanged: " + sensor + ", accuracy: " + accuracy);
    }

    public void orientation(double a) {
        if ((a - temp) < -180) {
            rotCounter++;
        } else if ((a - temp) > 180) {
            rotCounter--;
        }
        temp = a;
        w1 = a + rotCounter * 360;
        w2 = 0.05 * w1 + 0.95 * w2;
        wr = w2 + l_r_angle(ax, ay);
        while (wr < 0) {
            wr = wr + 360;
        }
        while (wr > 360) {
            wr = wr - 360;
        }
        //mOrientation.setText("Orientation: " + String.format("%.2f", wr));
    }

    public double l_r_angle(double a, double b) {
        if (b > 0) {
            l_r_angle0 = 180 + Math.atan(a / b) * 180 / Math.PI;
        } else {
            l_r_angle0 = Math.atan(a / b) * 180 / Math.PI;
        }
        if (Double.isNaN(l_r_angle)) {
            l_r_angle = l_r_angle0;
        } else {
            l_r_angle = 0.05 * l_r_angle0 + 0.95 * l_r_angle;
        }
        h_b_angle(l_r_angle);
        //mLRangle.setText("LRangle: " + String.format("%.2f", l_r_angle));
        return l_r_angle;
    }

    public void h_b_angle(double l_r_angle) {
        if (l_r_angle >= -45 && l_r_angle < 45 || l_r_angle > 135 && l_r_angle <= 225) {
            if (l_r_angle > -45 && l_r_angle < 45) {
                h_b_angle0 = Math.atan(az / ay) * 180 / Math.PI;
            } else {
                h_b_angle0 = -1 * Math.atan(az / ay) * 180 / Math.PI;
            }
        } else {
            if (l_r_angle >= 45 && l_r_angle <= 135) {
                h_b_angle0 = Math.atan(az / ax) * 180 / Math.PI;
            } else {
                h_b_angle0 = -1 * Math.atan(az / ax) * 180 / Math.PI;
            }
        }
        if (Double.isNaN(h_b_angle)) {
            h_b_angle = h_b_angle0;
        } else {
            h_b_angle = 0.05 * h_b_angle0 + 0.95 * h_b_angle;
        }

        //mHBangle.setText("HBangle: " + String.format("%.2f", h_b_angle));
    }

    @Override
    public void onResume() {
        super.onResume();
        // register this class as a listener for the orientation and accelerometer sensors
        sm = (SensorManager) getSystemService(SENSOR_SERVICE);//必须先获得系统服务再注册监听！不然会crash掉！
        sm.registerListener(this, SensorManager.SENSOR_ORIENTATION | SensorManager.SENSOR_ACCELEROMETER, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        sm.unregisterListener(this);
        ActivityController.removeActivity(this);
    }

    public void onBackPressed() {
        SubPMStationItem item = aStation;
        AskPhoto ask = new AskPhoto(getBaseContext(), item.getStationID(), item.getStationAddress());
        Toast.makeText(cameraDIY.this, "正在退回照片墙", Toast.LENGTH_LONG).show();
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Intent updatePhotoWall = new Intent(cameraDIY.this, PhotoWall.class);
        updatePhotoWall.putExtra("subStationItem", item);
        updatePhotoWall.putExtra("realLatitude", realLatitude);
        updatePhotoWall.putExtra("realLongitude", realLongitude);
        startActivity(updatePhotoWall);
        overridePendingTransition(R.anim.in_from_left, 0);
        cameraDIY.this.finish();

        //super.onBackPressed();
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////
    class CameraSurface extends SurfaceView implements SurfaceHolder.Callback, Camera.PictureCallback, OnClickListener {

        public CameraSurface(Context context) {
            super(context);
            // TODO Auto-generated constructor stub
            try {
                holder = this.getHolder();
                holder.addCallback(this);
                holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
                holder.setFormat(PixelFormat.TRANSLUCENT);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        @Override
        public void surfaceCreated(SurfaceHolder holder) {

            try {
                if (camera != null) {
                    try {
                        camera.stopPreview();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    try {
                        camera.release();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    camera = null;
                }
                camera = Camera.open();

                parameters = camera.getParameters();
                parameters.setPictureFormat(PixelFormat.JPEG);
                //其他参数设置
                parameters.setFocusMode(Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
                parameters.setFlashMode(Parameters.FLASH_MODE_OFF);
                setCameraDisplayOrientation(cameraDIY.this, 0, camera);
                camera.setPreviewDisplay(holder);

                int bestPreWidth = 0;
                int bestPreHeight = 0;
                int bestPicWidth = 0;
                int bestPicHeight = 0;
                List<Camera.Size> previewSizes = parameters.getSupportedPreviewSizes();
                if (previewSizes.size() > 1) {
                    Iterator<Camera.Size> cei = previewSizes.iterator();
                    double diff = 10000;
                    while (cei.hasNext()) {
                        Camera.Size aSize = cei.next();
                        Log.v("SNAPSHOT", "Checking " + aSize.width + " x " + aSize.height);
                        if (aSize.width > bestPreWidth && aSize.height > bestPreHeight && Math.pow(((aSize.width / aSize.height) - (w / h)), 2) <= diff) {
                            diff = Math.pow(((aSize.width / aSize.height) - (w / h)), 2);
                            bestPreWidth = aSize.width;
                            bestPreHeight = aSize.height;
                        }
                    }
                }
                parameters.setPreviewSize(bestPreWidth, bestPreHeight);
                List<Camera.Size> pictureSizes = parameters.getSupportedPictureSizes();
                if (pictureSizes.size() > 1) {
                    Iterator<Camera.Size> cei2 = pictureSizes.iterator();
                    while (cei2.hasNext()) {
                        Camera.Size aSize2 = cei2.next();
                        Log.v("SNAPSHOT", "Checking " + aSize2.width + " x " + aSize2.height);
                        if (aSize2.width > bestPicWidth && aSize2.width <= 2000
                                && aSize2.height > bestPicHeight && aSize2.height <= 1200) {

                            bestPicWidth = aSize2.width;
                            bestPicHeight = aSize2.height;
                        }
                    }
                }
                parameters.setPictureSize(bestPicWidth, bestPicHeight);

                camera.setParameters(parameters);
                camera.startPreview();
                isPreview = true;

                take.setOnClickListener(this);
                back.setOnClickListener(this);
                usephoto.setOnClickListener(this);


            } catch (IOException e) {
                Log.e(null, e.toString());
            }

        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            // TODO Auto-generated method stub
            camera.autoFocus(new AutoFocusCallback() {
                @Override
                public void onAutoFocus(boolean success, Camera camera) {
                    if (success) {
                        camera.stopPreview();
                        parameters = camera.getParameters();
                        parameters.setFlashMode(Parameters.FLASH_MODE_OFF);
                        parameters.setPictureFormat(PixelFormat.JPEG);
                        parameters.setFocusMode(Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
                        setCameraDisplayOrientation(cameraDIY.this, 0, camera);
                        camera.setParameters(parameters);
                        camera.startPreview();
/*            			if(Math.abs(h_b_angle-HB)<1 && Math.abs(l_r_angle-LR)<1){
                            camera.takePicture(null, null, null, CameraSurface.this);
                		    ar.interrupt();
                		}
*/
                        camera.cancelAutoFocus();
                    }
                }
            });
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {

            if (camera != null) {
                if (isPreview) {
                    camera.stopPreview();
                    camera.release();
                    camera = null;
                }
            }

        }

        public void setCameraDisplayOrientation(Activity activity, int cameraId, Camera camera) {
            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(cameraId, info);
            rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
            int degrees = 0;
            switch (rotation) {
                case Surface.ROTATION_0:
                    degrees = 0;
                    break;
                case Surface.ROTATION_90:
                    degrees = 90;
                    break;
                case Surface.ROTATION_180:
                    degrees = 180;
                    break;
                case Surface.ROTATION_270:
                    degrees = 270;
                    break;
            }
            int result;
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                result = (info.orientation + degrees) % 360;
                result = (360 - result) % 360;   // compensate the mirror
            } else {
                // back-facing
                result = (info.orientation - degrees + 360) % 360;
            }
            camera.setDisplayOrientation(result);
            camera.getParameters().setRotation(result);
        }

        public void onPictureTaken(byte[] data, Camera camera) {
            // TODO Auto-generated method stub
            camera.stopPreview();
            try {
                if (mark == 2) {
                    String iconName = username + ".jpg";
                    File iconFolder = new File(Environment.getExternalStorageDirectory() + "/PSUserIcon/");
                    if (!iconFolder.exists()) {
                        iconFolder.mkdir();
                    }
                    File iconFile = new File(iconFolder, iconName);
                    FileOutputStream outputStream = new FileOutputStream(iconFile);
                    outputStream.write(data);
                    outputStream.flush();
                    outputStream.close();
                    SharedPreferences sp = getSharedPreferences("userIcon", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putString("picPath", iconFile.getAbsolutePath()).commit();
                    //照片旋转（横向拍摄的照片，横向展示出来。纵向拍摄的照片，纵向展示出来。）
                    FileInputStream fis = new FileInputStream(iconFile.getAbsolutePath());
                    Bitmap bmpDefaultPic = BitmapFactory.decodeStream(fis);
                    int picRotation = 0;
                    if (l_r_angle >= -45 && l_r_angle < 45 || l_r_angle > 135 && l_r_angle <= 225) {
                        if (l_r_angle > -45 && l_r_angle < 45) {
                            picRotation = 90;
                        } else {
                            picRotation = 270;
                        }
                    } else {
                        if (l_r_angle >= 45 && l_r_angle <= 135) {
                            picRotation = 0;
                        } else {
                            picRotation = 180;
                        }
                    }
                    Matrix matrix = new Matrix();
                    matrix.reset();
                    matrix.postRotate(picRotation);
                    Bitmap bMapRotate = Bitmap.createBitmap(bmpDefaultPic, 0, 0, bmpDefaultPic.getWidth(),
                            bmpDefaultPic.getHeight(), matrix, true);
                    bmpDefaultPic = bMapRotate;

                    //照片裁剪
                    int w = bmpDefaultPic.getWidth(); // 得到图片的宽，高
                    int h = bmpDefaultPic.getHeight();
                    int wh = w > h ? h : w;// 裁切后所取的正方形区域边长
                    int retX = w > h ? (w - h) / 2 : 0;// 基于原图，取正方形左上角x坐标
                    int retY = w > h ? 0 : (h - w) / 2;
                    Bitmap bmp = Bitmap.createBitmap(bmpDefaultPic, retX, retY, wh, wh, null,
                            false);
                    UserInformation.UserIcon = bmp;
                    return;
                } else {
                    newOrientationX = wr + "";
                    newOrientationY = l_r_angle + "";
                    newOrientationZ = h_b_angle + "";
                    long currentTime = System.currentTimeMillis();
                    String filename = username + "_" + currentTime + ".jpg";
                    File fileFolder = new File(Environment.getExternalStorageDirectory() + "/PM2.5/");
                    if (!fileFolder.exists()) {
                        Log.e("cameraDIY", "file doesn't exist");
                        fileFolder.mkdir();
                    }
                    jpgFile = new File(fileFolder, filename);
                    FileOutputStream outputStream = new FileOutputStream(jpgFile);
                    outputStream.write(data);
                    outputStream.flush();
                    outputStream.close();
                    uploadFilePath = storePath = jpgFile.getAbsolutePath();
                    //uploadFilePath = jpgFile.getAbsolutePath();

                    FileInputStream fis = new FileInputStream(storePath);
                    Bitmap bitmap = BitmapFactory.decodeStream(fis);
                    int picRotation = 0;
                    double x = l_r_angle;
                    if (l_r_angle >= -45 && l_r_angle < 45 || l_r_angle > 135 && l_r_angle <= 225) {
                        if (l_r_angle > -45 && l_r_angle < 45) {
                            picRotation = 90;
                        } else {
                            picRotation = 270;
                        }
                    } else {
                        if (l_r_angle >= 45 && l_r_angle <= 135) {
                            picRotation = 0;
                        } else {
                            picRotation = 180;
                        }
                    }
                    Matrix matrix = new Matrix();
                    matrix.reset();
                    matrix.postRotate(picRotation);
                    Bitmap bMapRotate = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
                            bitmap.getHeight(), matrix, true);
                    bitmap = bMapRotate;

                    Bitmap newbitmap;
                    if (bitmap.getWidth() > bitmap.getHeight()) {
                        if (bitmap.getWidth() * 1080 / 1920 > bitmap.getHeight()) {
                            newbitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getHeight() * 1920 / 1080, bitmap.getHeight());
                        } else {
                            newbitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getWidth() * 1080 / 1920);
                        }

                    } else {
                        if (bitmap.getHeight() * 1080 / 1920 > bitmap.getWidth()) {
                            newbitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getWidth() * 1920 / 1080);
                        } else {
                            newbitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getHeight() * 1080 / 1920, bitmap.getHeight());
                        }

                    }

/*
                    File f = new File(storePath);
                    if (f.exists()) {
                        f.delete();
                    }
                    if (!f.exists()) {
                        f = new File(storePath);
                    }
*/


                    BufferedOutputStream bos = new BufferedOutputStream(
                            new FileOutputStream(jpgFile));
                    newbitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
                    bos.flush();
                    bos.close();


                    if (mark == 1) {
                        Log.i("originalEXIF", "OriginalEXIF : " + PhotoWall.readEXIF(Environment.getExternalStorageDirectory() + "/singlePic/" + singlePicFile));
                        String msg = PhotoWall.modifyEXIF(Environment.getExternalStorageDirectory() + "/singlePic/" + singlePicFile, aStation.getStationID() + "", realLatitude, realLongitude, username);
                        if ("".equals(msg)) {
                            msg = "{"
                                    + "\"Station\":\"" + station + "\","
                                    + "\"Altitude\":\"" + newAltitude + "\","
                                    + "\"username\":\"" + username + "\","
                                    + "\"Orientation_Z\":\"" + newOrientationZ + "\","
                                    + "\"Orientation_Y\":\"" + newOrientationY + "\","
                                    + "\"Orientation_X\":\"" + newOrientationX + "\","
                                    + "\"Longitude\":\"" + newLongitude + "\","
                                    + "\"RealLongitude\":\"" + newLongitude + "\","
                                    + "\"Latitude\":\"" + newLatitude + "\","
                                    + "\"RealLatitude\":\"" + newLatitude + "\""
                                    + "}";
                            JSONObject jsStr = new JSONObject(msg);
                            Log.e("newExifJson", jsStr.toString());
                        }
                        boolean result = PhotoWall.addEXIF(msg, storePath);
                        if (result == true) {
                            Log.e("addExif", "add success");
                        } else {
                            Log.e("addExif", "add failed");
                        }
                        Log.i("AfterModifyExif", "AfterModifyExif : " + PhotoWall.readEXIF(storePath));
                    } else if (mark == 0) {
                        String msgNew = "{"
                                + "\"Station\":\"" + newStationId + "\","
                                + "\"Altitude\":\"" + newAltitude + "\","
                                + "\"username\":\"" + username + "\","
                                + "\"Orientation_Z\":\"" + newOrientationZ + "\","
                                + "\"Orientation_Y\":\"" + newOrientationY + "\","
                                + "\"Orientation_X\":\"" + newOrientationX + "\","
                                + "\"Longitude\":\"" + newLongitude + "\","
                                + "\"RealLongitude\":\"" + newLongitude + "\","
                                + "\"Latitude\":\"" + newLatitude + "\","
                                + "\"RealLatitude\":\"" + newLatitude + "\""
                                + "}";
                        JSONObject jsStrNew = new JSONObject(msgNew);
                        Log.e("newExifJson", jsStrNew.toString());
                        boolean result = PhotoWall.addEXIF(msgNew, storePath);
                        if (result == true) {
                            Log.e("PhotoWall New Station", "add success");
                        } else {
                            Log.e("PhotoWall New Station", "add failed");
                        }
                        Log.i("newStation add exif msg", PhotoWall.readEXIF(storePath));
                    }
                    sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(jpgFile)));
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                Message msg = new Message();
                msg.what = 1;
                storehandler.sendMessage(msg);
            }
        }

        private Handler storehandler = new Handler() {

            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 1:
                        Toast.makeText(cameraDIY.this, "照片已保存", Toast.LENGTH_LONG).show();
                        storeSucceed = true;
                        break;
                    default:
                        break;
                }
            }
        };

        public void onClick(View v) {
            if (camera != null) {
                int id = v.getId();
                if (id == R.id.takePhoto) {
                    camera.takePicture(null, null, null, this);
                    //camera.stopPreview();
                    //一定不能关闭预览，不然照相机不会调用onPictureTaken的函数去对捕捉的照片进行处理。
                    if (mark == 1) {
                        ar.interrupt();
                        overlayoutview.removeView(hint);
                    }
                    back.setText("重拍");
                    back.setTag("reshoot");
                    usephoto.setText("使用照片");
                    usephoto.setVisibility(VISIBLE);
                    take.setVisibility(GONE);

                } else if (id == R.id.usephoto) {
                    if ("自动拍摄".equals(usephoto.getText())) {

                        SharedPreferences preferences = getSharedPreferences("station", Context.MODE_ENABLE_WRITE_AHEAD_LOGGING);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString("station", station);
                        editor.commit();
                        SharedPreferences preferencesS = getSharedPreferences("singlePicFile", Context.MODE_ENABLE_WRITE_AHEAD_LOGGING);
                        SharedPreferences.Editor editorS = preferencesS.edit();
                        editorS.putString("singlePicFile", singlePicFile);
                        editorS.commit();
                        SharedPreferences preferencesSS = getSharedPreferences("realLongitude", Context.MODE_ENABLE_WRITE_AHEAD_LOGGING);
                        SharedPreferences.Editor editorSS = preferencesSS.edit();
                        editorSS.putString("realLongitude", MapPage.currentLongitude + "");
                        editorSS.commit();
                        SharedPreferences preferencesSSS = getSharedPreferences("realLatitude", Context.MODE_ENABLE_WRITE_AHEAD_LOGGING);
                        SharedPreferences.Editor editorSSS = preferencesSSS.edit();
                        editorSSS.putString("realLatitude", MapPage.currentLatitude + "");
                        editorSSS.commit();

                        Intent autoCamera = new Intent(cameraDIY.this, AutoCameraActivity.class);
                        startActivity(autoCamera);
                        cameraDIY.this.finish();
                    } else if (mark == 2) {
                        cameraDIY.this.finish();
                    } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(cameraDIY.this);
                        builder.setPositiveButton("现在上传", new DialogInterface.OnClickListener() {//添加确定按钮
                            @Override
                            public void onClick(DialogInterface dialog, int which) {//确定按钮的响应事件
                                dialog.dismiss();
                                String BASE_URL = "http://182.92.116.126:8080/ps/environment/image";
                                if (myDialog == null) {
                                    myDialog = ProgressDialog.show(cameraDIY.this, "请耐心等候", "正在上传这张美美的照片...");
                                }
                                while (!storeSucceed) {
                                    //wait until the picture has been stored successfully;
                                }
                                try {
                                    HttpUtil.sentHttpData(BASE_URL, uploadFilePath, null, new HttpCallbackListener() {

                                        @Override
                                        public void onError(Exception e) {
                                            myDialog.dismiss();
                                            Message message = new Message();
                                            message.what = 101;
                                            loadingPhotoWall.sendMessage(message);
                                            e.printStackTrace();
                                        }

                                        @Override
                                        public void onFinish(String response) {
                                            if ("500".equals(response)) {
                                                myDialog.dismiss();
                                                Message message = new Message();
                                                message.what = 102;
                                                loadingPhotoWall.sendMessage(message);
                                            } else {
                                                //等待2s让服务器对新上传的数据进行计算
                                                try {
                                                    Thread.sleep(2000);
                                                } catch (InterruptedException e) {
                                                    e.printStackTrace();
                                                }
                                                myDialog.dismiss();
                                                if (mark == 1) {
                                                    Message message = new Message();
                                                    message.what = 103;
                                                    loadingPhotoWall.sendMessage(message);
                                                } else if (mark == 0) {
                                                    Message message = new Message();
                                                    message.what = 104;
                                                    loadingPhotoWall.sendMessage(message);
                                                }
                                            }
                                        }
                                    });
                                } catch (Exception e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                }
                            }
                        });
                        builder.setNegativeButton("保存本地", new DialogInterface.OnClickListener() {//添加确定按钮
                            @Override
                            public void onClick(DialogInterface dialog, int which) {//确定按钮的响应事件
                                AskPhoto ask = new AskPhoto(getBaseContext(), aStation.getStationID(), aStation.getStationAddress());
                                Toast.makeText(cameraDIY.this, "正在保存照片", Toast.LENGTH_LONG).show();
                                while (!storeSucceed) {
                                    //wait until the picture has been stored successfully;
                                }
                                try {
                                    Thread.sleep(3000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                dialog.dismiss();
                                Intent updatePhotoWall = new Intent(cameraDIY.this, PhotoWall.class);
                                Bundle extra = new Bundle();
                                extra.putSerializable("subStationItem", aStation);
                                extra.putString("realLatitude", realLatitude);
                                extra.putString("realLongitude", realLongitude);
                                updatePhotoWall.putExtras(extra);
                                startActivity(updatePhotoWall);
                                overridePendingTransition(R.anim.in_from_left, 0);
                                cameraDIY.this.finish();
                            }
                        });
                        AlertDialog dialog = builder.create();
                        dialog.setTitle("親愛的用戶: ");//设置对话框标题
                        dialog.setMessage("您是否要立即上传这张美美的照片呢?");//设置显示的内容
                        Window win = dialog.getWindow();
                        win.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                        dialog.show();
                    }
                } else if (id == R.id.back) {
                    if (v.getTag().equals("backToPhotoWall")) {
                        if (mark == 1) {
                            SubPMStationItem item = aStation;
                            AskPhoto ask = new AskPhoto(getBaseContext(), item.getStationID(), item.getStationAddress());
                            Toast.makeText(cameraDIY.this, "正在退回照片墙", Toast.LENGTH_LONG).show();
                            back.setClickable(false);
                            try {
                                Thread.sleep(3000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            Intent updatePhotoWall = new Intent(cameraDIY.this, PhotoWall.class);
                            Bundle extra = new Bundle();
                            extra.putSerializable("subStationItem", aStation);
                            extra.putString("realLatitude", realLatitude);
                            extra.putString("realLongitude", realLongitude);
                            updatePhotoWall.putExtras(extra);
                            startActivity(updatePhotoWall);
                            overridePendingTransition(R.anim.in_from_left, 0);
                            cameraDIY.this.finish();
                        } else if (mark == 0) {
                            Intent backMapPage = new Intent(cameraDIY.this, MapPage.class);
                            overridePendingTransition(R.anim.in_from_left, 0);
                            startActivity(backMapPage);
                            cameraDIY.this.finish();
                        } else if (mark == 2) {
                            cameraDIY.this.finish();
                        }
                    } else if (v.getTag().equals("reshoot")) {
                        usephoto.setText("自动拍摄");
                        if (mark == 1) {
                            overlayoutview.removeView(hint);
                            hint = new Hint(cameraDIY.this);
                            overlayoutview.addView(hint);
                            ar = new Thread(new HintThread());
                            ar.start();
                        }
                        take.setVisibility(VISIBLE);
                        back.setText("取消");
                        back.setTag("backToPhotoWall");
                        camera.startPreview();
                    }
                }
            }
        }

        Handler loadingPhotoWall = new Handler() {
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 101:
                        AlertDialog.Builder builderError = new AlertDialog.Builder(cameraDIY.this);
                        builderError.setPositiveButton("知道了", new DialogInterface.OnClickListener() {//添加确定按钮
                            @Override
                            public void onClick(DialogInterface dialog, int which) {//确定按钮的响应事件
                                dialog.dismiss();
                            }
                        });
                        AlertDialog dialogError = builderError.create();
                        dialogError.setTitle("对不起：");//设置对话框标题
                        dialogError.setMessage("上传失败 : 客户端捕捉到异常");//设置显示的内容
                        Window winError = dialogError.getWindow();
                        winError.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                        dialogError.show();
                        break;
                    case 102:
                        AlertDialog.Builder builderFailed = new AlertDialog.Builder(cameraDIY.this);
                        builderFailed.setPositiveButton("知道了", new DialogInterface.OnClickListener() {//添加确定按钮
                            @Override
                            public void onClick(DialogInterface dialog, int which) {//确定按钮的响应事件
                                dialog.dismiss();
                            }
                        });
                        AlertDialog dialogFailed = builderFailed.create();
                        dialogFailed.setTitle("对不起：");//设置对话框标题
                        dialogFailed.setMessage("上传失败 : 服务器拒绝");//设置显示的内容
                        Window winFailed = dialogFailed.getWindow();
                        winFailed.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                        dialogFailed.show();
                        break;
                    case 103:
                        SubPMStationItem item = aStation;
                        AskPhoto ask = new AskPhoto(getBaseContext(), item.getStationID(), item.getStationAddress());
                        AlertDialog.Builder builder = new AlertDialog.Builder(cameraDIY.this);
                        builder.setPositiveButton("知道了", new DialogInterface.OnClickListener() {//添加确定按钮
                            @Override
                            public void onClick(DialogInterface dialog, int which) {//确定按钮的响应事件
                                Toast.makeText(cameraDIY.this, "正在退回照片墙......", Toast.LENGTH_LONG).show();
                                dialog.dismiss();
                                try {
                                    Thread.sleep(2000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                Intent updatePhotoWall = new Intent(cameraDIY.this, PhotoWall.class);
                                Bundle extra = new Bundle();
                                extra.putSerializable("subStationItem", aStation);
                                extra.putString("realLatitude", realLatitude);
                                extra.putString("realLongitude", realLongitude);
                                updatePhotoWall.putExtras(extra);
                                startActivity(updatePhotoWall);
                                overridePendingTransition(R.anim.in_from_left, 0);
                                cameraDIY.this.finish();
                            }
                        });
                        AlertDialog dialog = builder.create();
                        dialog.setTitle("谢谢您的参与：");//设置对话框标题
                        dialog.setMessage("新照片上传成功！");//设置显示的内容
                        Window win = dialog.getWindow();
                        win.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                        dialog.show();
                        break;

                    case 104:
                        //AskPhoto askNewStation = new AskPhoto(getBaseContext(), aStation.getStationID(), aStation.getStationAddress());
                        AlertDialog.Builder builderNewStation = new AlertDialog.Builder(cameraDIY.this);
                        builderNewStation.setPositiveButton("知道了", new DialogInterface.OnClickListener() {//添加确定按钮
                            @Override
                            public void onClick(DialogInterface dialog, int which) {//确定按钮的响应事件
                                dialog.dismiss();
                                Intent backMapPage = new Intent(cameraDIY.this, MapPage.class);
                                overridePendingTransition(R.anim.in_from_left, 0);
                                startActivity(backMapPage);
                                cameraDIY.this.finish();
                            }
                        });
                        AlertDialog dialogNewStation = builderNewStation.create();
                        dialogNewStation.setTitle("恭喜您：");//设置对话框标题
                        dialogNewStation.setMessage("新站点已经成功建立！");//设置显示的内容
                        Window winNewStation = dialogNewStation.getWindow();
                        winNewStation.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                        dialogNewStation.show();
                        break;
                }
            }
        };

        //拍照界面缩放功能
        public int getMaxZoom() {
            if (camera == null) return -1;
            Parameters parameters = camera.getParameters();
            if (!parameters.isZoomSupported()) return -1;
            Log.e("MaxZoom", parameters.getMaxZoom() + "");
            return parameters.getMaxZoom() > 40 ? 40 : parameters.getMaxZoom();
        }

        //拍照界面曝光功能
        public int getMaxExposure() {
            if (camera == null) return -1;
            Parameters parameters = camera.getParameters();
            if (parameters.getMinExposureCompensation() == parameters.getMaxExposureCompensation())
                return -1;
            Log.e("MaxExposure", parameters.getMaxExposureCompensation() + "");
            return parameters.getMaxExposureCompensation() > 12 ? 12 : parameters.getMaxExposureCompensation();
        }

        public int getMinExposure() {
            if (camera == null) return -1;
            Parameters parameters = camera.getParameters();
            if (parameters.getMinExposureCompensation() == parameters.getMaxExposureCompensation())
                return -1;
            Log.e("MinExposure", parameters.getMinExposureCompensation() + "");
            return parameters.getMinExposureCompensation() < -12 ? -12 : parameters.getMinExposureCompensation();
        }

        /**
         * 设置相机缩放级别
         *
         * @param zoom
         */
        public void setZoom(int zoom) {
            if (camera == null) return;
            Parameters parameters = camera.getParameters();
            if (!parameters.isZoomSupported()) return;
            parameters.setZoom(zoom);
            camera.setParameters(parameters);
            mZoom = zoom;
        }

        public int getZoom() {
            return mZoom;
        }

        /**
         * 设置相机曝光度
         *
         * @param exposure
         */
        public void setExposure(int exposure) {
            if (camera == null) return;
            Parameters parameters = camera.getParameters();
            if (parameters.getMinExposureCompensation() == parameters.getMaxExposureCompensation())
                return;
            parameters.setExposureCompensation(exposure);
            camera.setParameters(parameters);
            mExposure = exposure;
        }

        public int getExposure() {
            return mExposure;
        }

    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////////
    @SuppressLint("DrawAllocation")
    class FocusArea extends View {
        private Paint paint;
        private Context context;
        private Bitmap myBitmap;

        public FocusArea(Context context) {
            super(context);
            // TODO Auto-generated constructor stub
            this.context = context;
            paint = new Paint();
            this.initBitmap();
        }

        private void initBitmap() {
            myBitmap = BitmapFactory.decodeResource(getResources(), drawable.doudou);
        }

        @Override
        public void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            paint.setColor(Color.BLUE);
            paint.setStyle(Paint.Style.FILL);
            canvas.drawCircle(focusX, focusY, 20, paint);
            Rect dst = new Rect(focusX - 55, focusY - 5, focusX - 50, focusY + 5);
            canvas.drawBitmap(myBitmap, null, dst, paint);
        }

    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////
    @SuppressLint("DrawAllocation")
    class Hint extends View {
        private Paint paint;
        private Context context;
        private int r;
        private Bitmap myBitmap;

        public Hint(Context context) {
            super(context);
            // TODO Auto-generated constructor stub
            this.context = context;
            paint = new Paint();
            this.initBitmap();
        }

        private void initBitmap() {
            myBitmap = BitmapFactory.decodeResource(getResources(), drawable.doudou);
        }

        @Override
        public void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            paint.setColor(Color.RED);
            paint.setStyle(Paint.Style.FILL);
            canvas.drawCircle(getWidth() / 2, getHeight() / 2, 10, paint);
            Rect dst = new Rect((int) (getWidth() / 2 - (l_r_angle - orientation_y) * 10 - 30), (int) (getHeight() / 2 - (h_b_angle - orientation_z) * 10 - 30), (int) (getWidth() / 2 - (l_r_angle - orientation_y) * 10 + 30), (int) (getHeight() / 2 - (h_b_angle - orientation_z) * 10 + 30));
            canvas.drawBitmap(myBitmap, null, dst, paint);

            paint.setColor(Color.GREEN);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(5);
            RectF oval1 = new RectF(50, 150, 300, 400);
            canvas.drawArc(oval1, 180, 180, false, paint);
            paint.setTextSize(35);
            canvas.drawText("右转", 10, 350, paint);
            canvas.drawText("左转", 255, 350, paint);
            canvas.drawLine(175, 275, (float) (175 - Math.cos(((wr - orientation_x) + 360) / 4 / 180 * Math.PI) * 125), (float) (275 - Math.sin(((wr - orientation_x) + 360) / 4 / 180 * Math.PI) * 125), paint);
            paint.setColor(Color.RED);
            paint.setStyle(Paint.Style.FILL);
            canvas.drawCircle(175, 150, 5, paint);
            //canvas.drawBitmap(myBitmap, (float) (getWidth() / 2 - (l_r_angle - LR) - myBitmap.getWidth() / 2), (float) (getHeight() / 2 - (h_b_angle - HB) - myBitmap.getHeight() / 2), paint);
        }

    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////
    class HintThread implements Runnable {
        @Override
        public void run() {
            // TODO Auto-generated method stub
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    Thread.sleep(100);
                } catch (Exception e) {
                    Thread.currentThread().interrupt();
                }
                if (fa != null) {
                    fa.postInvalidate();
                }
                hint.postInvalidate();
            }
        }
    }
}
