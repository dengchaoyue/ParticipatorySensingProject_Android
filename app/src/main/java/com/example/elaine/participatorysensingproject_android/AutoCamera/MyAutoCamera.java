package com.example.elaine.participatorysensingproject_android.AutoCamera;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.hardware.Camera;
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
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.elaine.participatorysensingproject_android.R;
import com.example.elaine.participatorysensingproject_android.mappage.ActivityController;
import com.example.elaine.participatorysensingproject_android.mappage.PhotoWall;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Elaine on 2017/4/16.
 */
public class MyAutoCamera extends Activity implements SensorListener {
    private WindowManager wm;
    public static int w;
    public static int h;
    private LinearLayout surfaceView;
    private MyCameraSurface cameraSurface;

    private SurfaceHolder holder;
    public static Camera camera;
    private boolean isPreview;
    private Camera.Parameters parameters;
    private TextView take;
    private TextView back;
    private TextView usephoto;
    private LinearLayout overlayoutview;

    public static String autoStorePath;
    public ArrayList<String> uploadFilePath;
    public File jpgFile;
    private String station, username, realLongitude, realLatitude;
    private String singlePicFile;

    //相机拍照界面的缩放功能
    private SeekBar mZoomSeekBar;
    private int mZoom = 0;
    private Handler mHandler;
    private int rotation;

    //缩略图
    private ImageView miniPic, fullSreenPic;

    //相机姿态
    public double l_r_angle;
    public double l_r_angle0;
    public double ax, ay, az;

    //存储照片的List
    public static List<Bitmap> autoPic = new ArrayList<Bitmap>();
    public static List<String> autoPicPath = new ArrayList<String>();

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityController.addActivity(this);

        //设置窗口
        Window window = getWindow();
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        w = display.getWidth();
        h = display.getHeight();
        setContentView(R.layout.camera);

        surfaceView = (LinearLayout) findViewById(R.id.surfaceView);
        cameraSurface = new MyCameraSurface(this);
        cameraSurface.setFocusable(true);
        cameraSurface.setFocusableInTouchMode(true);
        surfaceView.addView(cameraSurface);
        surfaceView.setFocusable(true);
        take = (TextView) findViewById(R.id.takePhoto);
        back = (TextView) findViewById(R.id.back);
        back.setTag("backToPhotoWall");
        usephoto = (TextView) findViewById(R.id.usephoto);

        overlayoutview = (LinearLayout) findViewById(R.id.overlayoutview);
        overlayoutview.removeAllViews();
        mZoomSeekBar = (SeekBar) findViewById(R.id.zoomSeekBar);
        miniPic = (ImageView) findViewById(R.id.minipic);
        fullSreenPic = (ImageView) findViewById(R.id.fullscreenpic);
        fullSreenPic.setAlpha(0.5f);
        fullSreenPic.setScaleType(ImageView.ScaleType.FIT_START);
        fullSreenPic.setVisibility(View.GONE);
        miniPic.setVisibility(View.GONE);

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

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                /** 通过与运算保留最后八位 MotionEvent.ACTION_MASK = 255 */
                switch (event.getAction() & MotionEvent.ACTION_MASK) {
                    // 手指压下屏幕
                    case MotionEvent.ACTION_DOWN:
                        mode = MODE_INIT;
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
                                mZoomSeekBar.setProgress(zoom);
                                //将最后一次的距离设为当前距离
                                startDis = endDis;
                            }
                        }
                        break;
                    // 手指离开屏幕
                    case MotionEvent.ACTION_UP:
                        if (mode != MODE_ZOOM) {
                            //设置聚焦
                            focusOnTouch((int) event.getX(), (int) event.getY());
                        } else {
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
    }

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
            Camera.Parameters parameters = camera.getParameters(); // 先获取当前相机的参数配置对象
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO); // 设置聚焦模式
            if (parameters.getMaxNumFocusAreas() > 0) {
                List<Camera.Area> focusAreas = new ArrayList<Camera.Area>();
                focusAreas.add(new Camera.Area(rect, 1000));
                parameters.setFocusAreas(focusAreas);
            }
            camera.cancelAutoFocus(); // 先要取消掉进程中所有的聚焦功能
            camera.setParameters(parameters); // 一定要记得把相应参数设置给相机
            camera.autoFocus(new Camera.AutoFocusCallback() {
                @Override
                public void onAutoFocus(boolean success, Camera camera) {
                    Log.d("AutoFocus", "onAutoFocus : " + success);
                }
            });
        }
    }

    public double l_r_angle(double a, double b) {
        if (b > 0) {
            l_r_angle0 = 180 + Math.atan(a / b) * 180 / Math.PI;
        } else {
            l_r_angle0 = Math.atan(a / b) * 180 / Math.PI;
        }
        l_r_angle = 0.05 * l_r_angle0 + 0.95 * l_r_angle;
        //mLRangle.setText("LRangle: " + String.format("%.2f", l_r_angle));
        return l_r_angle;
    }

    @Override
    public void onSensorChanged(int sensor, float[] values) {
        if (sensor == SensorManager.SENSOR_ACCELEROMETER) {
            ax = values[0];
            ay = values[1];
            az = values[2];
        }
    }

    @Override
    public void onAccuracyChanged(int sensor, int accuracy) {
        Log.d("AutoCameraSensor", "onAccuracyChanged: " + sensor + ", accuracy: " + accuracy);
    }

    @Override
    public void onResume() {
        super.onResume();
        //获取传参
        SharedPreferences preferences = getSharedPreferences("station", Context.MODE_ENABLE_WRITE_AHEAD_LOGGING);
        station = preferences.getString("station", "");

        SharedPreferences preferencesS = getSharedPreferences("singlePicFile", Context.MODE_ENABLE_WRITE_AHEAD_LOGGING);
        singlePicFile = preferencesS.getString("singlePicFile", "");

        SharedPreferences preferencesSS = getSharedPreferences("realLatitude", Context.MODE_ENABLE_WRITE_AHEAD_LOGGING);
        realLatitude = preferencesSS.getString("realLatitude", "");

        SharedPreferences preferencesSSS = getSharedPreferences("realLongitude", Context.MODE_ENABLE_WRITE_AHEAD_LOGGING);
        realLongitude = preferencesSSS.getString("realLongitude", "");

        SharedPreferences preferencesSSSS = getSharedPreferences("user", Context.MODE_ENABLE_WRITE_AHEAD_LOGGING);
        username = preferencesSSSS.getString("username", "");
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        //cameraSurface.takePhoto();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ActivityController.removeActivity(this);
    }

    public void onBackPressed() {
        MyAutoCamera.this.finish();
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////
    public class MyCameraSurface extends SurfaceView implements SurfaceHolder.Callback, Camera.PictureCallback, View.OnClickListener, Serializable {

        public MyCameraSurface(Context context) {
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
                parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                setCameraDisplayOrientation(MyAutoCamera.this, 0, camera);
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
                        if (aSize2.width > bestPicWidth && aSize2.width <= 1920
                                && aSize2.height > bestPicHeight && aSize2.height <= 1080) {

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

                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                camera.takePicture(null, null, null, MyCameraSurface.this);

            } catch (IOException e) {
                Log.e(null, e.toString());
            }

        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            // TODO Auto-generated method stub
            camera.autoFocus(new Camera.AutoFocusCallback() {
                @Override
                public void onAutoFocus(boolean success, Camera camera) {
                    if (success) {
                        camera.stopPreview();
                        parameters = camera.getParameters();
                        parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                        parameters.setPictureFormat(PixelFormat.JPEG);
                        parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
                        setCameraDisplayOrientation(MyAutoCamera.this, 0, camera);
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
                long currentTime = System.currentTimeMillis();
                String filename = username + "_" + currentTime + ".jpg";
                File fileFolder = new File(Environment.getExternalStorageDirectory() + "/PM2.5Auto/");
                if (!fileFolder.exists()) {
                    Log.e("MyAutoCamera", "file doesn't exist");
                    fileFolder.mkdir();
                }
                jpgFile = new File(fileFolder, filename);
                FileOutputStream outputStream = new FileOutputStream(jpgFile);
                outputStream.write(data);
                outputStream.flush();
                outputStream.close();
                autoStorePath = jpgFile.getAbsolutePath();

                FileInputStream fis = new FileInputStream(autoStorePath);
                Bitmap bitmap = BitmapFactory.decodeStream(fis);
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

                BufferedOutputStream bos = new BufferedOutputStream(
                        new FileOutputStream(jpgFile));
                newbitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
                bos.flush();
                bos.close();

                autoPicPath.add(jpgFile.getAbsolutePath());
                autoPic.add(newbitmap);


                Log.i("originalEXIF", "OriginalEXIF : " + PhotoWall.readEXIF(Environment.getExternalStorageDirectory() + "/singlePic/" + singlePicFile));
                String msg = PhotoWall.modifyEXIF(Environment.getExternalStorageDirectory() + "/singlePic/" + singlePicFile, station + "", realLatitude, realLongitude, username);
                boolean result = PhotoWall.addEXIF(msg, autoStorePath);
                if (result == true) {
                    Log.e("addExif", "add success");
                } else {
                    Log.e("addExif", "add failed");
                }
                Log.i("AfterModifyExif", "AfterModifyExif : " + PhotoWall.readEXIF(autoStorePath));

                sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(jpgFile)));
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
                        SharedPreferences preferences = MyAutoCamera.this.getSharedPreferences("storePath", Context.MODE_ENABLE_WRITE_AHEAD_LOGGING);
                        //读取SharedPreferences里的count数据，若存在返回其值，否则返回0
                        SharedPreferences.Editor editor = preferences.edit();
                        String str = preferences.getString("storePath", "");
                        if (!"".equals(str)) {
                            str = str + ";" + autoStorePath;
                        } else {
                            str = autoStorePath;
                        }
                        editor.putString("storePath", str);
                        editor.commit();

                        Toast.makeText(MyAutoCamera.this, "照片保存成功", Toast.LENGTH_LONG).show();
                        camera.release();
                        Log.e("自动拍照界面", autoStorePath + "");

                        Intent autoPicListPageTwo = new Intent(MyAutoCamera.this, AutoPicListPageTwo.class);
                        startActivity(autoPicListPageTwo);
                        MyAutoCamera.this.finish();
                        break;
                    default:
                        break;
                }
            }
        };

        //拍照界面缩放功能
        public int getMaxZoom() {
            if (camera == null) return -1;
            Camera.Parameters parameters = camera.getParameters();
            if (!parameters.isZoomSupported()) return -1;
            return parameters.getMaxZoom() > 40 ? 40 : parameters.getMaxZoom();
        }

        /**
         * 设置相机缩放级别
         *
         * @param zoom
         */
        public void setZoom(int zoom) {
            if (camera == null) return;
            Camera.Parameters parameters = camera.getParameters();
            if (!parameters.isZoomSupported()) return;
            parameters.setZoom(zoom);
            camera.setParameters(parameters);
            mZoom = zoom;
        }

        public int getZoom() {
            return mZoom;
        }

        @Override
        public void onClick(View v) {
            //nothing
        }

        public void takePhoto() {
            camera.takePicture(null, null, null, MyCameraSurface.this);
        }
    }

}
