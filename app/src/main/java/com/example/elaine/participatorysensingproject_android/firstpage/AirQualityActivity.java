package com.example.elaine.participatorysensingproject_android.firstpage;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.Poi;
import com.example.elaine.participatorysensingproject_android.Http.DownloadFirstPagePic;
import com.example.elaine.participatorysensingproject_android.Http.HttpCallbackListener;
import com.example.elaine.participatorysensingproject_android.Http.HttpUtil;
import com.example.elaine.participatorysensingproject_android.Location.LocationService;
import com.example.elaine.participatorysensingproject_android.R;
import com.example.elaine.participatorysensingproject_android.Weather.Weather;
import com.example.elaine.participatorysensingproject_android.Weather.WeatherAPI;
import com.example.elaine.participatorysensingproject_android.Weather.WeatherUtil;
import com.example.elaine.participatorysensingproject_android.mappage.ActivityController;
import com.example.elaine.participatorysensingproject_android.permissionrequest.PermissionsChecker;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.google.gson.Gson;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;


public class AirQualityActivity extends FragmentActivity implements SwipeRefreshLayout.OnRefreshListener {

    private final static String SCREEN_SHOT_PATH = Environment.getExternalStorageDirectory() + "/ScreenShot/";

    private static final int SHOW_RESPONSE = 0;
    private static final int SHOW_PICTURES = 1;
    private static final int SHOW_WEATHER = 2;

    private SwipeRefreshLayout mRefreshLayout;
    private LinearLayout mLinearLayout1;
    private LinearLayout mLinearLayout2;
    private LinearLayout.LayoutParams param;
    private DefinedScrollView scrollView;
    private LayoutInflater inflater;
    private int pageCount = 0;


    private ViewPager viewPager;
    private LineChart mLineChart;
    private LineChart mLineChart2;
    private TextView station_tv;
    private TextView actualValue_tv;
    private TextView time_tv;
    private TextView calculatedValue_tv;
    private ImageView firstPageImg;
    private ImageView alphaLayer;
    private TextView airQuality_tv;
    private TextView temperature_tv;
    private ImageView weatherImg1;
    private TextView humidity_tv;

    private TextView aqiSecond_tv;
    private TextView no2_tv;
    private TextView so2_tv;
    private TextView pm10_tv;
    private TextView co_tv;
    private TextView o3_tv;
    private TextView pm25_tv;

    private ArrayList<TextView> date_tv;
    private ArrayList<ImageView> weaImg;
    private ArrayList<TextView> windKind_tv;
    private ArrayList<TextView> windLevel_tv;
    private ArrayList<TextView> humi_tv;

    private LocationService locationService;
    private TextView location_tv;

    public static double latitude;
    public static double longitude;
    private ArrayList<String> mPicNameArray;
    public static double count;


    public ArrayList<String> getmPicNameArray() {
        return mPicNameArray;
    }

    private PermissionsChecker mPermissionsChecker; // 权限检测器
    // 所需的全部权限
    private static final int REQUEST_CODE = 0; // 请求码
    public static final String[] PERMISSIONS = new String[]{
            Manifest.permission.CAMERA,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.WRITE_CONTACTS,
            Manifest.permission.READ_CONTACTS
    };

    private void setupView() {

        mRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.refresh_layout);
        scrollView = (DefinedScrollView) findViewById(R.id.definedview);
        pageCount = 2;

        LinearLayout tab_home = (LinearLayout) findViewById(R.id.tab_home);
        LinearLayout tab_map = (LinearLayout) findViewById(R.id.tab_map);
        LinearLayout tab_task = (LinearLayout) findViewById(R.id.tab_task);
        LinearLayout tab_user = (LinearLayout) findViewById(R.id.tab_user);
        tab_home.setSelected(true);
        tab_map.setSelected(false);
        tab_task.setSelected(false);
        tab_user.setSelected(false);

        mLinearLayout2 = new LinearLayout(this);
        for (int i = 0; i < pageCount; i++) {
            param = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);
            inflater = this.getLayoutInflater();
            if (i == 0) {
                TextView camera = (TextView) findViewById(R.id.Button1);
                final TextView share = (TextView) findViewById(R.id.Button2);
                TextView title = (TextView) findViewById(R.id.centerText);
                title.setText("空气质量");
                Typeface iconfont = Typeface.createFromAsset(getAssets(), "iconfont.ttf");
                camera.setTypeface(iconfont);
                camera.setText(R.string.icon_camera);
                share.setTypeface(iconfont);
                share.setText(R.string.icon_share);

                final IWXAPI api = WXAPIFactory.createWXAPI(this, "wxb2ce4912e1f471f7", true);
                api.registerApp("wxb2ce4912e1f471f7");
                share.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        share.setPressed(false);
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
                        String path = MediaStore.Images.Media.insertImage(AirQualityActivity.this.getContentResolver(), temBitmap, "Title", null);
                        Uri uri = Uri.parse(path);

                        Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_SEND);
                        intent.putExtra(Intent.EXTRA_STREAM, uri);
                        intent.setType("image/*");
                        startActivity(intent);

//                        WXImageObject imgObj = new WXImageObject(temBitmap);
//
//                        WXMediaMessage msg = new WXMediaMessage();
//                        msg.mediaObject = imgObj;
//
//                        Bitmap thumbBmp = Bitmap.createScaledBitmap(temBitmap, 90, 120, true);
//                        temBitmap.recycle();
//                        msg.thumbData = bmpToByteArray(thumbBmp, true);
//
//                        SendMessageToWX.Req req = new SendMessageToWX.Req();
//                        req.transaction = buildTransaction("img");
//                        req.message = msg;
//                        req.scene = SendMessageToWX.Req.WXSceneSession;
//                        api.sendReq(req);
                    }
                });

                final View page_first = inflater.inflate(R.layout.activity_main_first, null);

                location_tv = (TextView) page_first.findViewById(R.id.location);
                station_tv = (TextView) page_first.findViewById(R.id.station);
                actualValue_tv = (TextView) page_first.findViewById(R.id.actual_value_1);
                time_tv = (TextView) page_first.findViewById(R.id.latest_time);
                calculatedValue_tv = (TextView) page_first.findViewById(R.id.calculated_value);
                airQuality_tv = (TextView) page_first.findViewById(R.id.airQuality);
                firstPageImg = (ImageView) page_first.findViewById(R.id.firstPageImg);
                alphaLayer = (ImageView) page_first.findViewById(R.id.alphaLayer);
                temperature_tv = (TextView) page_first.findViewById(R.id.temperature_1);
                weatherImg1 = (ImageView) page_first.findViewById(R.id.weather_1);
                humidity_tv = (TextView) page_first.findViewById(R.id.humidity_1);

                mLineChart = (LineChart) page_first.findViewById(R.id.chart);
                mLineChart.setDescription("");
                mLineChart.setTouchEnabled(false);
                MyMarkerView mv = new MyMarkerView(this, R.layout.custom_marker_view);
                mLineChart.setMarkerView(mv);
                Legend legend = mLineChart.getLegend();
                legend.setEnabled(false);

                XAxis xAxis = mLineChart.getXAxis();
                xAxis.setSpaceBetweenLabels(1);

                //设置X轴的文字在底部
                xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                xAxis.setDrawGridLines(false);
                xAxis.setDrawAxisLine(false);
                mLineChart.getAxisLeft().setEnabled(false);
                mLineChart.getAxisRight().setEnabled(false);

                viewPager = (ViewPager) page_first.findViewById(R.id.viewPager);
                viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                    @Override
                    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                    }

                    @Override
                    public void onPageSelected(int position) {
                        mLineChart.highlightValue(position, 0);
                    }

                    @Override
                    public void onPageScrollStateChanged(int state) {

                    }
                });

                viewPager.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        viewPager.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        int imgHeight = viewPager.getHeight();
                        int imgWidth = imgHeight * 3 / 4;
                        ViewGroup.LayoutParams lp = viewPager.getLayoutParams();
                        lp.width = imgWidth;
                        viewPager.requestLayout();
                    }
                });

                mLinearLayout1 = new LinearLayout(this);
                mLinearLayout1.addView(page_first, param);
                scrollView.addView(mLinearLayout1);
            } else {
                View page_second = inflater.inflate(R.layout.activity_main_two, null);

                aqiSecond_tv = (TextView) page_second.findViewById(R.id.actual_value_2);
                no2_tv = (TextView) page_second.findViewById(R.id.no2);
                so2_tv = (TextView) page_second.findViewById(R.id.so2);
                pm10_tv = (TextView) page_second.findViewById(R.id.pm10);
                co_tv = (TextView) page_second.findViewById(R.id.co);
                o3_tv = (TextView) page_second.findViewById(R.id.o3);
                pm25_tv = (TextView) page_second.findViewById(R.id.pm25);

                date_tv = new ArrayList<>();
                date_tv.add((TextView) page_second.findViewById(R.id.date1));
                date_tv.add((TextView) page_second.findViewById(R.id.date2));
                date_tv.add((TextView) page_second.findViewById(R.id.date3));
                date_tv.add((TextView) page_second.findViewById(R.id.date4));
                date_tv.add((TextView) page_second.findViewById(R.id.date5));
                date_tv.add((TextView) page_second.findViewById(R.id.date6));
                date_tv.add((TextView) page_second.findViewById(R.id.date7));

                weaImg = new ArrayList<>();
                weaImg.add((ImageView) page_second.findViewById(R.id.wea_img1));
                weaImg.add((ImageView) page_second.findViewById(R.id.wea_img2));
                weaImg.add((ImageView) page_second.findViewById(R.id.wea_img3));
                weaImg.add((ImageView) page_second.findViewById(R.id.wea_img4));
                weaImg.add((ImageView) page_second.findViewById(R.id.wea_img5));
                weaImg.add((ImageView) page_second.findViewById(R.id.wea_img6));
                weaImg.add((ImageView) page_second.findViewById(R.id.wea_img7));

                windKind_tv = new ArrayList<>();
                windKind_tv.add((TextView) page_second.findViewById(R.id.wind11));
                windKind_tv.add((TextView) page_second.findViewById(R.id.wind12));
                windKind_tv.add((TextView) page_second.findViewById(R.id.wind13));
                windKind_tv.add((TextView) page_second.findViewById(R.id.wind14));
                windKind_tv.add((TextView) page_second.findViewById(R.id.wind15));
                windKind_tv.add((TextView) page_second.findViewById(R.id.wind16));
                windKind_tv.add((TextView) page_second.findViewById(R.id.wind17));

                windLevel_tv = new ArrayList<>();
                windLevel_tv.add((TextView) page_second.findViewById(R.id.wind21));
                windLevel_tv.add((TextView) page_second.findViewById(R.id.wind22));
                windLevel_tv.add((TextView) page_second.findViewById(R.id.wind23));
                windLevel_tv.add((TextView) page_second.findViewById(R.id.wind24));
                windLevel_tv.add((TextView) page_second.findViewById(R.id.wind25));
                windLevel_tv.add((TextView) page_second.findViewById(R.id.wind26));
                windLevel_tv.add((TextView) page_second.findViewById(R.id.wind27));

                humi_tv = new ArrayList<>();
                humi_tv.add((TextView) page_second.findViewById(R.id.humi1));
                humi_tv.add((TextView) page_second.findViewById(R.id.humi2));
                humi_tv.add((TextView) page_second.findViewById(R.id.humi3));
                humi_tv.add((TextView) page_second.findViewById(R.id.humi4));
                humi_tv.add((TextView) page_second.findViewById(R.id.humi5));
                humi_tv.add((TextView) page_second.findViewById(R.id.humi6));
                humi_tv.add((TextView) page_second.findViewById(R.id.humi7));


                mLineChart2 = (LineChart) page_second.findViewById(R.id.chart2);
                mLineChart2.setDescription("");
                mLineChart2.setTouchEnabled(false);
                Legend legend = mLineChart2.getLegend();
                legend.setEnabled(false);

                XAxis xAxis = mLineChart2.getXAxis();
                xAxis.setSpaceBetweenLabels(1);

                xAxis.setEnabled(false);
                mLineChart2.getAxisLeft().setEnabled(false);
                mLineChart2.getAxisRight().setEnabled(false);

                mLinearLayout1 = new LinearLayout(this);
                mLinearLayout1.addView(page_second, param);
                scrollView.addView(mLinearLayout1);
            }
        }
        scrollView.setPageListener(new DefinedScrollView.PageListener() {
            @Override
            public void page(int page) {
                setCurPage(page);
                if (page == 1) {
                    mRefreshLayout.setEnabled(false);
                } else {
                    mRefreshLayout.setEnabled(true);
                }
            }
        });
        mRefreshLayout.setOnRefreshListener(this);
        viewPager.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_MOVE:
                        mRefreshLayout.setEnabled(false);
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        mRefreshLayout.setEnabled(true);
                        break;
                }
                return false;
            }
        });
    }

    private void setCurPage(int page) {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityController.addActivity(this);
        setContentView(R.layout.activity_main);

        /*//请求权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mPermissionsChecker = new PermissionsChecker(this);
            if (mPermissionsChecker.lacksPermissions(PERMISSIONS)) {
                startPermissionsActivity();
            }
        }*/


        locationService = ((SensingApplication) getApplication()).locationService;
        //获取locationservice实例，建议应用中只初始化1个location实例，然后使用，可以参考其他示例的activity，都是通过此种方式获取locationservice实例的
        locationService.registerListener(mListener);
        //注册监听
        locationService.setLocationOption(locationService.getDefaultLocationClientOption());
        setupView();
        locationService.registerListener(mListener);
        locationService.start();// 定位SDK
        Log.i("ying", "location start");
        // start之后会默认发起一次定位请求，开发者无须判断isstart并主动调用request
    }

    /*private void startPermissionsActivity() {
        PermissionsActivity.startActivityForResult(this, REQUEST_CODE, PERMISSIONS);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 拒绝时, 关闭页面, 缺少主要权限, 无法运行
        if (requestCode == REQUEST_CODE && resultCode == PermissionsActivity.PERMISSIONS_DENIED) {
            finish();
        }
    }*/

    @Override
    protected void onStart() {
        super.onStart();
        locationService.start();

    }

    @Override
    protected void onStop() {
//        locationService.unregisterListener(mListener); //注销掉监听
        locationService.stop(); //停止定位服务
        super.onStop();
    }


    private void setChartDataForFirstPage(ArrayList<Integer> actualValueArray, ArrayList<String> timeArray) {
        ArrayList<String> xValues = new ArrayList<>();
        String pattern = "yyyy-MM-dd";
        SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
        for (String time : timeArray) {
            try {
                Date date = dateFormat.parse(time);
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date);
                int month = calendar.get(Calendar.MONTH) + 1;
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                xValues.add(month + "." + day);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        ArrayList<Entry> yValue1 = new ArrayList<>();
        ArrayList<Integer> colors = new ArrayList<Integer>();
        int xIndex = 0;
        for (int value : actualValueArray) {
            Entry entry = new Entry(value, xIndex++);
            yValue1.add(entry);
            if (value <= 50)
                colors.add(ContextCompat.getColor(this, R.color.aqi_Excellent));
            else if (value >= 51 && value <= 100)
                colors.add(ContextCompat.getColor(this, R.color.aqi_Good));
            else if (value >= 101 && value <= 150)
                colors.add(ContextCompat.getColor(this, R.color.aqi_LightlyPolluted));
            else if (value >= 151 && value <= 200)
                colors.add(ContextCompat.getColor(this, R.color.aqi_ModeratelyPolluted));
            else if (value >= 201 && value <= 300)
                colors.add(ContextCompat.getColor(this, R.color.aqi_HeavilyPolluted));
            else
                colors.add(ContextCompat.getColor(this, R.color.aqi_SeverelyPolluted));
        }

        LineDataSet dataSet1 = new LineDataSet(yValue1, "AQI");
        dataSet1.setCircleColors(colors);
        dataSet1.setColor(Color.BLACK);
        dataSet1.setHighlightEnabled(false);
        dataSet1.setDrawCircleHole(true);
        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(dataSet1);
        LineData lineData = new LineData(xValues, dataSets);
        lineData.setValueFormatter(new IntegerValueFormatter());

        //将数据插入
        mLineChart.setData(lineData);
        mLineChart.invalidate();
    }

    private void setPicturesForFirstPage(final ArrayList<String> localPicNameArray) {
        String localImgDirPath = DownloadFirstPagePic.getAlbumPath();
        FragmentManager fm = getSupportFragmentManager();
        viewPager.setAdapter(new FragmentStatePagerAdapter(fm) {
            @Override
            public Fragment getItem(int position) {
                return PictureSlideFragment.newInstance(position);
            }

            @Override
            public int getCount() {
                return localPicNameArray.size();
            }
        });

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 2;
        Bitmap bm = BitmapFactory.decodeFile(localImgDirPath + localPicNameArray.get(localPicNameArray.size() - 1));
        firstPageImg.setImageBitmap(bm);
    }

    private void setChartDataForSecondPage(Weather weather) {

        Weather.AqiBean.CityBean cityBean = weather.getAqi().getCity();
        aqiSecond_tv.setText(cityBean.getAqi());
        no2_tv.setText("NO₂: " + cityBean.getNo2());
        so2_tv.setText("SO₂: " + cityBean.getSo2());
        pm10_tv.setText("PM10: " + cityBean.getPm10());
        co_tv.setText("CO: " + cityBean.getCo());
        o3_tv.setText("O₃: " + cityBean.getO3());
        pm25_tv.setText("PM2.5: " + cityBean.getPm25());

        ArrayList<String> xValues = new ArrayList<>();
        ArrayList<Integer> temper_high = new ArrayList<>();
        ArrayList<Integer> temper_low = new ArrayList<>();

        String pattern = "yyyy-MM-dd";
        SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);

        final List<Weather.DailyForecastBean> daily_forecast = weather.getDaily_forecast();
        final int dateNum = daily_forecast.size();
        for (Weather.DailyForecastBean forcast : daily_forecast) {
            try {
                Date date = dateFormat.parse(forcast.getDate());
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date);
                int month = calendar.get(Calendar.MONTH) + 1;
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                xValues.add(month + "." + day);
            } catch (ParseException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            temper_high.add(Integer.valueOf(forcast.getTmp().getMax()));
            temper_low.add(Integer.valueOf(forcast.getTmp().getMin()));
        }

        ArrayList<Entry> yValue1 = new ArrayList<>();
        ArrayList<Entry> yValue2 = new ArrayList<>();
        int xIndex = 0;
        for (int value : temper_high) {
            Entry entry = new Entry(value, xIndex++);
            yValue1.add(entry);
        }
        xIndex = 0;
        for (int value : temper_low) {
            Entry entry = new Entry(value, xIndex++);
            yValue2.add(entry);
        }

        LineDataSet dataSet1 = new LineDataSet(yValue1, "high");
        dataSet1.setHighlightEnabled(false);
        dataSet1.setDrawCircleHole(true);

        LineDataSet dataSet2 = new LineDataSet(yValue2, "low");
        dataSet2.setHighlightEnabled(false);
        dataSet2.setDrawCircleHole(true);

        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(dataSet1);
        dataSets.add(dataSet2);
        LineData lineData = new LineData(xValues, dataSets);
        lineData.setValueTextSize(12f);
        lineData.setValueTextColor(ContextCompat.getColor(this, R.color.coldGray));
        lineData.setValueFormatter(new MyValueFormatter());

        //将数据插入
        mLineChart2.setData(lineData);
        mLineChart2.invalidate();

        mLineChart2.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mLineChart2.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                float offsetLeft = mLineChart2.getWidth() / (dateNum * 2);
                ViewPortHandler handler = mLineChart2.getViewPortHandler();
                float offsetBottom = handler.offsetBottom();
                float offsetTop = handler.offsetTop();
                mLineChart2.setViewPortOffsets(offsetLeft, offsetTop, offsetLeft, offsetBottom);
            }
        });

        //没有做天气预报天数自适应处理，暂定天数等于7

        for (int i = 0; i < dateNum; i++) {
            date_tv.get(i).setText(xValues.get(i));
            //设置天气图标
            String imgId = "w" + daily_forecast.get(i).getCond().getCode_d();
            int imgResource = getResources().getIdentifier(imgId, "drawable", getPackageName());
            Drawable image = ContextCompat.getDrawable(this, imgResource);
            weaImg.get(i).setImageDrawable(image);
            //设置风向、等级、湿度
            windLevel_tv.get(i).setText(daily_forecast.get(i).getWind().getSc());
            humi_tv.get(i).setText(daily_forecast.get(i).getHum() + "%");
            String windKind = daily_forecast.get(i).getWind().getDir();
            if (windKind.equals("无持续风向")) {
                windKind_tv.get(i).setText("未知");
            } else {
                windKind_tv.get(i).setText(windKind);
            }
        }
    }

    private Handler firtPageHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SHOW_RESPONSE:
                    String jsonData = (String) msg.obj;
                    try {
                        JSONObject response = new JSONObject(jsonData);
                        JSONObject message = response.getJSONObject("message");
                        String existPOI = message.getString("existPOI");
                        if (existPOI.equals("yes")) {
                            JSONObject existStation = message.getJSONObject("existStation");
                            String station = existStation.getString("nearPmStation");
                            String actualValue = String.valueOf(existStation.getInt("actual_fpm"));
                            String time = existStation.getString("latestTime");
                            int intCalValue = existStation.getInt("fpm");
                            String calculatedValue = String.valueOf(intCalValue);
                            JSONArray latestPicArray = message.getJSONArray("latestPics");
                            ArrayList<String> picNameArray = new ArrayList<>();
                            ArrayList<Integer> actualValueArray = new ArrayList<>();
                            ArrayList<String> timeArray = new ArrayList<>();
                            for (int i = 0; i < latestPicArray.length(); i++) {
                                JSONObject jsonObject = latestPicArray.getJSONObject(i);
                                if (jsonObject.length() > 0) {
                                    picNameArray.add(jsonObject.getString("pic"));
                                    actualValueArray.add(jsonObject.getInt("actual_fpm"));
                                    timeArray.add(jsonObject.getString("latestTime"));
                                } else {
                                    //异常处理
                                }
                            }
                            Collections.reverse(picNameArray);
                            Collections.reverse(actualValueArray);
                            Collections.reverse(timeArray);
                            float alpha = 0;
                            if ((intCalValue > 0) && (intCalValue < 475)) {
                                alpha = intCalValue / 500;
                            } else if ((intCalValue >= 475) && (intCalValue <= 500)) {
                                alpha = 0.75f;
                            }
                            alphaLayer.setAlpha(alpha);
                            station_tv.setText(station);
                            actualValue_tv.setText(actualValue);
                            time_tv.setText(time);
                            calculatedValue_tv.setText(calculatedValue);
                            if (intCalValue <= 50)
                                airQuality_tv.setText("优");
                            else if (intCalValue >= 51 && intCalValue <= 100)
                                airQuality_tv.setText("良");
                            else if (intCalValue >= 101 && intCalValue <= 150)
                                airQuality_tv.setText("轻度污染");
                            else if (intCalValue >= 151 && intCalValue <= 200)
                                airQuality_tv.setText("中度污染");
                            else if (intCalValue >= 201 && intCalValue <= 300)
                                airQuality_tv.setText("重度污染");
                            else
                                airQuality_tv.setText("严重污染");
                            setChartDataForFirstPage(actualValueArray, timeArray);

                            mPicNameArray = picNameArray;
                            final int picNum = picNameArray.size();
                            RequestQueue mQueue = Volley.newRequestQueue(AirQualityActivity.this);
                            ImageLoader imageLoader = new ImageLoader(mQueue, new BitmapCache());
                            NetworkImageView networkImageView = (NetworkImageView) findViewById(R.id.firstPageImg);
                            networkImageView.setDefaultImageResId(R.drawable.img1);
                            networkImageView.setErrorImageResId(R.drawable.img1);
                            networkImageView.setImageUrl(DownloadFirstPagePic.ROOT_URL + Uri.encode(picNameArray.get(picNum - 1), "utf-8"),
                                    imageLoader);
                            FragmentManager fm = getSupportFragmentManager();
                            /*viewPager.setAdapter(new FragmentStatePagerAdapter(fm) {
                                @Override
                                public Fragment getItem(int position) {
                                    return PictureSlideFragment.newInstance(position);
                                }

                                @Override
                                public int getCount() {
                                    return picNum;
                                }
                            });*/

                        } else {
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                case SHOW_PICTURES:
                    ArrayList<String> localPicNameArray = (ArrayList<String>) msg.obj;
                    mPicNameArray = localPicNameArray;
                    setPicturesForFirstPage(localPicNameArray);
                    break;
                case SHOW_WEATHER:
                    String jsonWeather = (String) msg.obj;
                    jsonWeather = jsonWeather.replaceAll(" ", "");
                    jsonWeather = jsonWeather.replaceFirst("3.0", "");
                    Gson gson = new Gson();
                    WeatherAPI weatherAPI = gson.fromJson(jsonWeather, WeatherAPI.class);
                    Weather weather = weatherAPI.getHeWeatherdataservice().get(0);
                    temperature_tv.setText(weather.getNow().getTmp() + "\u2103");
                    String imgId = "w" + weather.getNow().getCond().getCode();
                    int imgResource = getResources().getIdentifier(imgId, "drawable", getPackageName());
                    Drawable image = ContextCompat.getDrawable(AirQualityActivity.this, imgResource);
                    weatherImg1.setImageDrawable(image);

                    humidity_tv.setText(weather.getNow().getHum() + "%");
                    setChartDataForSecondPage(weather);
                    break;
                default:
                    break;
            }
        }
    };

    private BDLocationListener mListener = new BDLocationListener() {

        @Override
        public void onReceiveLocation(BDLocation location) {
            // TODO Auto-generated method stub
            if (null != location && location.getLocType() != BDLocation.TypeServerError) {
                String cityName = location.getCity();
                location_tv.setText(cityName);
                if (cityName != null) {
                    cityName = WeatherUtil.replaceCity(cityName);
                    Log.i("ying", cityName);
                }
                latitude = location.getLatitude();
                longitude = location.getLongitude();
                StringBuilder reqBasicInfo = new StringBuilder();
                reqBasicInfo.append(HttpUtil.FIRST_PAGE);
                reqBasicInfo.append("longitude=");
                reqBasicInfo.append(longitude);
                reqBasicInfo.append("&latitude=");
                reqBasicInfo.append(latitude);
                reqBasicInfo.append("&baidu=1");
                HttpUtil.sentHttpRequest(reqBasicInfo.toString(), new HttpCallbackListener() {
                    @Override
                    public void onFinish(String response) {
                        Log.i("ying", "999999999999999999");
                        Log.i("ying", response);
                        Message message = new Message();
                        message.what = SHOW_RESPONSE;
                        message.obj = response;
                        firtPageHandler.sendMessage(message);
                    }

                    @Override
                    public void onError(Exception e) {

                    }
                });

                StringBuilder reqWeather = new StringBuilder();
                reqWeather.append(HttpUtil.WEATHER_API);
                reqWeather.append("city=");
                try {
                    reqWeather.append(URLEncoder.encode(cityName, "utf-8"));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                reqWeather.append("&key=");
                reqWeather.append("798cf89748404030b059057d14f79703");
                Log.i("ying", reqWeather.toString());
                HttpUtil.sentHttpRequest(reqWeather.toString(), new HttpCallbackListener() {
                    @Override
                    public void onFinish(String response) {
                        Log.i("ying", "88888888888888888");
                        Log.i("ying", response);
                        Message message = new Message();
                        message.what = SHOW_WEATHER;
                        message.obj = response;
                        firtPageHandler.sendMessage(message);
                    }

                    @Override
                    public void onError(Exception e) {

                    }
                });


                StringBuffer sb = new StringBuffer(256);
                sb.append("time : ");
                /**
                 * 时间也可以使用systemClock.elapsedRealtime()方法 获取的是自从开机以来，每次回调的时间；
                 * location.getTime() 是指服务端出本次结果的时间，如果位置不发生变化，则时间不变
                 */
                sb.append(location.getTime());
                sb.append("\nerror code : ");
                sb.append(location.getLocType());
                sb.append("\nlatitude : ");
                sb.append(location.getLatitude());
                sb.append("\nlontitude : ");
                sb.append(location.getLongitude());
                sb.append("\nradius : ");
                sb.append(location.getRadius());
                sb.append("\nCountryCode : ");
                sb.append(location.getCountryCode());
                sb.append("\nCountry : ");
                sb.append(location.getCountry());
                sb.append("\ncitycode : ");
                sb.append(location.getCityCode());
                sb.append("\ncity : ");
                sb.append(location.getCity());
                sb.append("\nDistrict : ");
                sb.append(location.getDistrict());
                sb.append("\nStreet : ");
                sb.append(location.getStreet());
                sb.append("\naddr : ");
                sb.append(location.getAddrStr());
                sb.append("\nDescribe: ");
                sb.append(location.getLocationDescribe());
                sb.append("\nDirection(not all devices have value): ");
                sb.append(location.getDirection());
                sb.append("\nPoi: ");
                if (location.getPoiList() != null && !location.getPoiList().isEmpty()) {
                    for (int i = 0; i < location.getPoiList().size(); i++) {
                        Poi poi = (Poi) location.getPoiList().get(i);
                        sb.append(poi.getName() + ";");
                    }
                }
                if (location.getLocType() == BDLocation.TypeGpsLocation) {// GPS定位结果
                    sb.append("\nspeed : ");
                    sb.append(location.getSpeed());// 单位：km/h
                    sb.append("\nsatellite : ");
                    sb.append(location.getSatelliteNumber());
                    sb.append("\nheight : ");
                    sb.append(location.getAltitude());// 单位：米
                    sb.append("\ndescribe : ");
                    sb.append("gps定位成功");
                } else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {// 网络定位结果
                    // 运营商信息
                    sb.append("\noperationers : ");
                    sb.append(location.getOperators());
                    sb.append("\ndescribe : ");
                    sb.append("网络定位成功");
                } else if (location.getLocType() == BDLocation.TypeOffLineLocation) {// 离线定位结果
                    sb.append("\ndescribe : ");
                    sb.append("离线定位成功，离线定位结果也是有效的");
                } else if (location.getLocType() == BDLocation.TypeServerError) {
                    sb.append("\ndescribe : ");
                    sb.append("服务端网络定位失败，可以反馈IMEI号和大体定位时间到loc-bugs@baidu.com，会有人追查原因");
                } else if (location.getLocType() == BDLocation.TypeNetWorkException) {
                    sb.append("\ndescribe : ");
                    sb.append("网络不同导致定位失败，请检查网络是否通畅");
                } else if (location.getLocType() == BDLocation.TypeCriteriaException) {
                    sb.append("\ndescribe : ");
                    sb.append("无法获取有效定位依据导致定位失败，一般是由于手机的原因，处于飞行模式下一般会造成这种结果，可以试着重启手机");
                }

                Log.i("ying", String.valueOf(sb));
//                locationService.stop();
//                Log.i("ying","location stop");
            }
        }

    };


    @Override
    public void onRefresh() {

        /*
        locationService.registerListener(mListener);
        locationService.start();// 定位SDK
        Log.i("ying","location start");
        */


        // start之后会默认发起一次定位请求，开发者无须判断isstart并主动调用request
        mRefreshLayout.setRefreshing(true);
        locationService.start();// 定位SDK
        mRefreshLayout.setRefreshing(false);
    }


    private String buildTransaction(final String type) {
        return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
    }

    public static byte[] bmpToByteArray(final Bitmap bmp, final boolean needRecycle) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, output);
        if (needRecycle) {
            bmp.recycle();
        }

        byte[] result = output.toByteArray();
        try {
            output.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }
}
