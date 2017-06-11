/**
 * class name:
 * class description:
 * author: dengchaoyue
 * version: 1.0
 */
package com.example.elaine.participatorysensingproject_android.mappage;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnMarkerClickListener;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.model.LatLng;
import com.example.elaine.participatorysensingproject_android.Http.HttpCallbackListener;
import com.example.elaine.participatorysensingproject_android.R;
import com.example.elaine.participatorysensingproject_android.R.drawable;
import com.example.elaine.participatorysensingproject_android.usercenterpage.UserNotSignedActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MapPage extends Activity {

    public static int k = 0;
    private MapView mapView;
    private BaiduMap baiduMap;
    private LocationManager locationManager;
    private String provider;
    private boolean isFirstLocation = true;
    public static boolean loadingFinish = true;
    private static LatLng BaiduLocation;
    private Marker newStationMarkerOverlayout;
    private MarkerOptions myMarkerOptions;
    private String newStationID = "";
    private Location location;
    public static double currentLongitude, currentLatitude;
    private SubPMStationList subPMStationList;
    //站点聚合
    //ClusterManager
    private float currentZoom;
    private List<List<SubPMStationItem>> cluster;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityController.addActivity(this);
        SDKInitializer.initialize(getApplicationContext());
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.map_layout);
        cameraDIY.mark = 0;
        LinearLayout tab_home = (LinearLayout) findViewById(R.id.tab_home);
        LinearLayout tab_map = (LinearLayout) findViewById(R.id.tab_map);
        LinearLayout tab_task = (LinearLayout) findViewById(R.id.tab_task);
        LinearLayout tab_user = (LinearLayout) findViewById(R.id.tab_user);
        tab_home.setSelected(false);
        tab_map.setSelected(true);
        tab_task.setSelected(false);
        tab_user.setSelected(false);
        TextView pageTitle = (TextView) findViewById(R.id.centerText);
        pageTitle.setText("地图界面");
        TextView button2 = (TextView) findViewById(R.id.Button2);
        button2.setText("");
        TextView goBack = (TextView) findViewById(R.id.Button1);
        goBack.setText("");
        mapView = (MapView) findViewById(R.id.map_view);
        baiduMap = mapView.getMap();
        baiduMap.setMyLocationEnabled(true);
        //站点聚合
        subPMStationList = new SubPMStationList();

    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        List<String> providerList = locationManager.getProviders(true);
        if (providerList.contains(LocationManager.NETWORK_PROVIDER)) {
            provider = LocationManager.NETWORK_PROVIDER;
        } else if (providerList.contains(LocationManager.GPS_PROVIDER)) {
            provider = LocationManager.GPS_PROVIDER;
        } else {
            Toast.makeText(this, "No location provider to use", Toast.LENGTH_SHORT).show();
            return;
        }
        /*if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }*/
        location = locationManager.getLastKnownLocation(provider);
        if (location != null) {
            navigateTo(location);
        } else {
            Toast.makeText(this, "无法获取您当前所在的位置", Toast.LENGTH_LONG).show();
        }
        locationManager.requestLocationUpdates(provider, 5000, 1, locationListener);
        mapView.onResume();
    }


    private void navigateTo(Location location) {
        //onChangeLocation,更改全局位置变量
        currentLatitude = location.getLatitude();
        currentLongitude = location.getLongitude();

        LatLng ll = new LatLng(location.getLatitude(), location.getLongitude());
        double[] Baidu = CoordinateTranformUtil.wgs84tobd09(ll.longitude, ll.latitude);
        BaiduLocation = new LatLng(Baidu[1], Baidu[0]);
        Log.i("BaiduLocation.latitude", String.valueOf(BaiduLocation.latitude));
        Log.i("BaiduLocation.longitude", String.valueOf(BaiduLocation.longitude));

        double[] Gao = CoordinateTranformUtil.wgs84togcj02(ll.longitude, ll.latitude);
        LatLng GaodeLocation = new LatLng(Gao[1], Gao[0]);

        if (isFirstLocation == true) {

            MapStatusUpdate update = MapStatusUpdateFactory.newLatLng(BaiduLocation);
            baiduMap.animateMapStatus(update);
            update = MapStatusUpdateFactory.zoomTo(18f);
            currentZoom = 18f;
            baiduMap.animateMapStatus(update);
            isFirstLocation = false;
        }


        //MyLocationData.Builder locationBuilder = new MyLocationData.Builder();
        //locationBuilder.latitude(desLatLng.latitude);
        //locationBuilder.longitude(desLatLng.longitude);
        //Toast.makeText(this, location.getLatitude()+"+"+location.getLongitude(), Toast.LENGTH_LONG).show();
        //MyLocationData locationData = locationBuilder.build();
        //baiduMap.setMyLocationData(locationData);


        //11111111111111111111111111111111111111111111111111111111111111111111111111111111111111111
        //String BASE_URL = "http://182.92.116.126:8080/ps/SubPMStations?longitude=116.351387&latitude=39.961449&baidu=0";
        String BASE_URL = URLCollector.GetAllSationList + "?longitude=" + BaiduLocation.longitude + "&latitude=" + BaiduLocation.latitude + "&baidu=1";
        try {
            HttpUtil.sentHttpRequest(BASE_URL, new HttpCallbackListener() {
                @Override
                public void onFinish(String response) {
                    // TODO Auto-generated method stub
                    parseJSONWithJSONObject(response);
                    Thread.currentThread().interrupt();
                }

                @Override
                public void onError(Exception e) {
                    // TODO Auto-generated method stub
                    e.printStackTrace();
                }
            });
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    ProgressDialog myDialog = null;

    private void parseJSONWithJSONObject(String jsonData) {
        try {
            JSONObject json = new JSONObject(jsonData);
            JSONObject msg = json.getJSONObject("message");
            JSONArray stationsArray = null;
            JSONObject currentStation = null;


            if ("yes".equals(msg.getString("existPOI"))) {
                currentStation = msg.getJSONObject("existStation");
            }

            if (msg.getJSONArray("stations") != null) {
                stationsArray = msg.getJSONArray("stations");
            } else {
                Toast.makeText(getApplicationContext(), "No station has been established nearby", Toast.LENGTH_LONG).show();
            }

            if (currentStation != null) {
                stationsArray.put(currentStation);
            } else {
                BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(drawable.nopoi);
                myMarkerOptions = new MarkerOptions().icon(bitmap).position(BaiduLocation);
                Marker marker = (Marker) baiduMap.addOverlay(myMarkerOptions);
                newStationMarkerOverlayout = marker;
                marker.setTitle("My current position !");
            }

            k = stationsArray.length();
            for (int i = 0; i < stationsArray.length(); i++) {
                SubPMStationItem item = new SubPMStationItem();
                JSONObject jsonObject = stationsArray.getJSONObject(i);
                item.setStationID(Integer.parseInt(jsonObject.getString("stationID")));
                item.setStationAddress(jsonObject.getString("stationAddress"));
                item.setLatestPic(jsonObject.getString("latestPic"));
                item.setFPM(Integer.parseInt(jsonObject.getString("latestPM")));
                item.setActual_fpm(Integer.parseInt(jsonObject.getString("actual_fpm")));
                item.setReputation(0);
                item.setNearPmStation(jsonObject.getString("nearPmStation"));
                item.setLatestTime(jsonObject.getString("latestTime"));
                item.setLongitude(Double.parseDouble(jsonObject.getString("longitude")));
                item.setLatitude(Double.parseDouble(jsonObject.getString("latitude")));
                item.setBlongitude(Double.parseDouble(jsonObject.getString("blongitude")));
                item.setBlatitude(Double.parseDouble(jsonObject.getString("blatitude")));
                subPMStationList.add(item);

                LatLng point = new LatLng(item.getBlatitude(), item.getBlongitude());
//				CoordinateConverter converter  = new CoordinateConverter();    
//				converter.from(CoordType.GPS);    
//				converter.coord(point);    
//				LatLng desPoint = converter.convert(); 
                BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(drawable.poi);
                if (item.getBlatitude() == BaiduLocation.latitude && item.getBlongitude() == BaiduLocation.longitude) {
                    newStationMarkerOverlayout.remove();
                }
                if ("yes".equals(msg.getString("existPOI")) && (i == stationsArray.length() - 1)) {
                    BitmapDescriptor currentbitmap = BitmapDescriptorFactory.fromResource(drawable.currentpoi);
                    MarkerOptions markerOptions = new MarkerOptions().icon(currentbitmap).position(point);
                    Marker marker = (Marker) baiduMap.addOverlay(markerOptions);
                    marker.setTitle(item.getStationID() + "");
                    marker.setPosition(point);
                } else {
                    MarkerOptions markerOptions = new MarkerOptions().icon(bitmap).position(point);
                    Marker marker = (Marker) baiduMap.addOverlay(markerOptions);
                    marker.setTitle(item.getStationID() + "");
                    marker.setPosition(point);
                }
            }
            baiduMap.setOnMarkerClickListener(new OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker arg0) {
                    // TODO Auto-generated method stub
                    if ("My current position !".equals(arg0.getTitle())) {
                        cameraDIY.mark = 0;
                        SharedPreferences preferences = getSharedPreferences("user", MODE_PRIVATE);
                        final String username = preferences.getString("username", "");
                        if ("".equals(username)) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(MapPage.this);
                            builder.setNegativeButton("前去登录", new DialogInterface.OnClickListener() {//添加确定按钮
                                @Override
                                public void onClick(DialogInterface dialogNote, int which) {//确定按钮的响应事件
                                    dialogNote.dismiss();
                                    Intent register = new Intent(MapPage.this, UserNotSignedActivity.class);

                                    startActivity(register);
                                    MapPage.this.finish();
                                }
                            });
                            AlertDialog dialogNote = builder.create();
                            dialogNote.setTitle("Note: ");//设置对话框标题
                            dialogNote.setMessage("您还没有登录，请先登录后再进行拍照");//设置显示的内容
                            Window win = dialogNote.getWindow();
                            win.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                            dialogNote.show();
                        } else {
                            String BASE_URL = "http://182.92.116.126:8080/ps/newPMStation?userName=" + username + "&latitude=" + location.getLatitude() + "&longitude=" + location.getLongitude();
                            try {
                                HttpUtil.requestCreateNewStation(BASE_URL, new HttpCallbackListener() {

                                    @Override
                                    public void onError(Exception e) {
                                        Message message = new Message();
                                        message.what = 101;
                                        requestCreateNewStation.sendMessage(message);
                                        e.printStackTrace();
                                    }

                                    @Override
                                    public void onFinish(String response) {
                                        if ("500".equals(response)) {
                                            Message message = new Message();
                                            message.what = 102;
                                            requestCreateNewStation.sendMessage(message);
                                        } else {
                                            newStationID = decode(response);
                                            if ("".equals(newStationID)) {
                                                Message message = new Message();
                                                message.what = 104;
                                                requestCreateNewStation.sendMessage(message);
                                            } else {
                                                Intent takePhoto = new Intent(MapPage.this, cameraDIY.class);
                                                Bundle myBundle = new Bundle();
                                                myBundle.putString("stationID", "");
                                                myBundle.putString("longitude", location.getLongitude() + "");
                                                myBundle.putString("latitude", location.getLatitude() + "");
                                                myBundle.putString("altitude", location.getAltitude() + "");
                                                myBundle.putString("username", username);
                                                takePhoto.putExtras(myBundle);
                                                startActivity(takePhoto);
                                            }
                                        }
                                    }
                                });
                            } catch (Exception e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }

                        }

                    } else {
                        cameraDIY.mark = 1;
                        String stationID = arg0.getTitle();
                        SubPMStationItem item = subPMStationList.getItem(stationID);
                        Toast.makeText(getApplicationContext(), item.getStationID() + "Station被点击", Toast.LENGTH_SHORT).show();
                        AskPhoto ask = new AskPhoto(getBaseContext(), item.getStationID(), item.getStationAddress());
                        while (!ask.flag) {
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        Intent jumpToPhotoWall = new Intent(MapPage.this, PhotoWall.class);
                        if (item.getStationAddress() != null) {
                            jumpToPhotoWall.putExtra("subStationItem", item);
                            jumpToPhotoWall.putExtra("realLongitude", location.getLongitude() + "");
                            jumpToPhotoWall.putExtra("realLongitude", location.getLongitude() + "");
                        }
                        startActivity(jumpToPhotoWall);
                    }
                    return false;
                }
            });

            baiduMap.setOnMapStatusChangeListener(new BaiduMap.OnMapStatusChangeListener() {
                @Override
                public void onMapStatusChangeStart(MapStatus mapStatus) {
                }

                @Override
                public void onMapStatusChange(MapStatus mapStatus) {
                    if (currentZoom != mapStatus.zoom && mapStatus.zoom < 16f && threadflag == false) {
                        currentZoom = mapStatus.zoom;
                        clusterMarkerPoints(subPMStationList, currentZoom);
                        Log.e("zoomChanged", currentZoom + "");
                        Log.e("zoomChanged", "startNewThread");
                    }
                    Log.e("zoomChanged", "finish2");
                }

                @Override
                public void onMapStatusChangeFinish(MapStatus mapStatus) {

                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //站点聚合算法
    private boolean zoomflag;
    private boolean addflag;
    private boolean threadflag;

    private void clusterMarkerPoints(final SubPMStationList subPMStationList, final float currentZoom) {
        cluster = null;
        cluster = new ArrayList<>();
        zoomflag = true;
        threadflag = true;

        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < subPMStationList.size(); i++) {
                    addflag = false;
                    if (currentZoom < 18f) {
                        if (cluster.size() == 0) {
                            List<SubPMStationItem> newCluster = new ArrayList<>();
                            newCluster.add(subPMStationList.get(i));
                            cluster.add(newCluster);
                        } else {
                            int currentClusterSize = cluster.size();
                            for (int j = 0; j < currentClusterSize; j++) {
                                int distance = (int) GetLongDistance(cluster.get(j).get(0).getBlongitude(), cluster.get(j).get(0).getBlatitude(), subPMStationList.get(i).getBlongitude(), subPMStationList.get(i).getBlatitude());
                                if (distance < 2000 && currentZoom <= 15f && currentZoom > 14f) {
                                    addflag = true;
                                    cluster.get(j).add(subPMStationList.get(i));
                                    break;
                                } else if (distance < 5000 && currentZoom <= 14f && currentZoom > 13f) {
                                    addflag = true;
                                    cluster.get(j).add(subPMStationList.get(i));
                                    break;
                                } else if (distance < 10000 && currentZoom <= 13f && currentZoom > 12f) {
                                    addflag = true;
                                    cluster.get(j).add(subPMStationList.get(i));
                                    break;
                                } else if (distance < 20000 && currentZoom <= 12f && currentZoom > 11f) {
                                    addflag = true;
                                    cluster.get(j).add(subPMStationList.get(i));
                                    break;
                                } else if (distance < 25000 && currentZoom <= 11f && currentZoom > 10f) {
                                    addflag = true;
                                    cluster.get(j).add(subPMStationList.get(i));
                                    break;
                                } else if (distance < 50000 && currentZoom <= 10f && currentZoom > 9f) {
                                    addflag = true;
                                    cluster.get(j).add(subPMStationList.get(i));
                                    break;
                                } else if (distance < 100000 && currentZoom <= 9f && currentZoom > 8f) {
                                    addflag = true;
                                    cluster.get(j).add(subPMStationList.get(i));
                                    break;
                                } else if (distance < 200000 && currentZoom <= 8f && currentZoom > 7f) {
                                    addflag = true;
                                    cluster.get(j).add(subPMStationList.get(i));
                                    break;
                                } else if (distance < 500000 && currentZoom <= 7f && currentZoom > 6f) {
                                    addflag = true;
                                    cluster.get(j).add(subPMStationList.get(i));
                                    break;
                                } else if (distance < 2000000 && currentZoom <= 6f && currentZoom > 5f) {
                                    addflag = true;
                                    cluster.get(j).add(subPMStationList.get(i));
                                    break;
                                } else if (distance < 1000000 && currentZoom <= 5f && currentZoom > 4f) {
                                    addflag = true;
                                    cluster.get(j).add(subPMStationList.get(i));
                                    break;
                                } else if (distance < 2000000 && currentZoom <= 4f && currentZoom > 3f) {
                                    addflag = true;
                                    cluster.get(j).add(subPMStationList.get(i));
                                    break;
                                } else if (distance < 5000000 && currentZoom <= 3f) {
                                    addflag = true;
                                    cluster.get(j).add(subPMStationList.get(i));
                                    break;
                                }
                            }
                            if (!addflag) {
                                List<SubPMStationItem> newCluster = new ArrayList<>();
                                newCluster.add(subPMStationList.get(i));
                                cluster.add(newCluster);

                            }
                        }
                    } else {
                        zoomflag = false;
                        Message ms = new Message();
                        ms.what = 0;
                        clusterHandler.sendMessage(ms);
                    }
                }
                if (zoomflag) {
                    Message ms = new Message();
                    ms.what = 1;
                    clusterHandler.sendMessage(ms);
                }
            }
        }).start();
        Log.e("zoomChanged", "finish");
    }

    private Handler clusterHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    baiduMap.clear();

                    for (int i = 0; i < subPMStationList.size(); i++) {
                        LatLng point = new LatLng(subPMStationList.get(i).getBlatitude(), subPMStationList.get(i).getBlongitude());
                        BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(drawable.poi);
                        if (subPMStationList.get(i).getBlatitude() == BaiduLocation.latitude && subPMStationList.get(i).getBlongitude() == BaiduLocation.longitude) {
                            newStationMarkerOverlayout.remove();
                            BitmapDescriptor currentbitmap = BitmapDescriptorFactory.fromResource(drawable.currentpoi);
                            MarkerOptions markerOptions = new MarkerOptions().icon(currentbitmap).position(point);
                            Marker marker = (Marker) baiduMap.addOverlay(markerOptions);
                            marker.setTitle(subPMStationList.get(i).getStationID() + "");
                            marker.setPosition(point);
                        } else {
                            MarkerOptions markerOptions = new MarkerOptions().icon(bitmap).position(point);
                            Marker marker = (Marker) baiduMap.addOverlay(markerOptions);
                            marker.setTitle(subPMStationList.get(i).getStationID() + "");
                            marker.setPosition(point);
                        }
                    }
                    threadflag = false;
                    Log.e("Thread0", "finish");
                    break;
                case 1:
                    baiduMap.clear();
                    BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(drawable.poi);
                    for (int i = 0; i < cluster.size(); i++) {
                        LatLng point = new LatLng(cluster.get(i).get(0).getBlatitude(), cluster.get(i).get(0).getBlongitude());
                        MarkerOptions markerOptions = new MarkerOptions().icon(bitmap).position(point);
                        Marker marker = (Marker) baiduMap.addOverlay(markerOptions);
                        marker.setTitle(subPMStationList.get(i).getStationID() + "");
                        marker.setPosition(point);
                    }
                    Log.e("Thread1", "finish");
                    threadflag = false;
                    break;
                default:
                    break;
            }
        }
    };

    public static double GetLongDistance(double lon1, double lat1, double lon2, double lat2) {
        double ew1, ns1, ew2, ns2;
        double distance;
        // 角度转换为弧度
        ew1 = lon1 * 0.01745329252;
        ns1 = lat1 * 0.01745329252;
        ew2 = lon2 * 0.01745329252;
        ns2 = lat2 * 0.01745329252;
        // 求大圆劣弧与球心所夹的角(弧度)
        distance = Math.sin(ns1) * Math.sin(ns2) + Math.cos(ns1) * Math.cos(ns2) * Math.cos(ew1 - ew2);
        // 调整到[-1..1]范围内，避免溢出
        if (distance > 1.0)
            distance = 1.0;
        else if (distance < -1.0)
            distance = -1.0;
        // 求大圆劣弧长度
        distance = 6370693.5 * Math.acos(distance);
        return distance;
    }


    private JSONObject json = null;
    private String stateCode = "";
    private String poiID = "";

    private String decode(String response) {
        try {
            json = new JSONObject(response);
            stateCode = json.getString("stateCode");
            if ("000".equals(stateCode)) {
                poiID = json.getString("poiID");
                return poiID;
            } else {
                Message message = new Message();
                message.what = 103;
                requestCreateNewStation.sendMessage(message);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Message message = new Message();
            message.what = 104;
            requestCreateNewStation.sendMessage(message);
        }

        return poiID;
    }

    private Handler requestCreateNewStation = new Handler() {

        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 101:
                    Toast.makeText(getApplicationContext(), "请求新站点ID失败", Toast.LENGTH_LONG).show();
                    break;
                case 102:
                    Toast.makeText(getApplicationContext(), "服务器端无响应", Toast.LENGTH_LONG).show();
                    break;
                case 103:
                    Toast.makeText(getApplicationContext(), "当前位置不允许建立新的站点:" + stateCode, Toast.LENGTH_LONG).show();
                    break;
                case 104:
                    Toast.makeText(getApplicationContext(), "客户端数据解析失败", Toast.LENGTH_LONG).show();
                    break;
            }
        }
    };

    /**
     * @param featureInfo
     */
    private void setExtraInfo(Bundle featureInfo) {
        // TODO Auto-generated method stub

    }

    LocationListener locationListener = new LocationListener() {

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onProviderEnabled(String provider) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onProviderDisabled(String provider) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onLocationChanged(Location location) {
            // TODO Auto-generated method stub
            if (location != null) {
                //navigateTo(location);
            }
        }
    };

    @Override
    protected void onStop() {
        baiduMap.clear();
        super.onStop();
    }

    protected void onDestroy() {
        super.onDestroy();
        baiduMap.setMyLocationEnabled(false);
        baiduMap.clear();
        mapView.onDestroy();
        if (locationManager != null) {
            // if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            //return;
            //}
            locationManager.removeUpdates(locationListener);
        }
        ActivityController.removeActivity(this);
    }

    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }


    public void onBackPressed() {
        ActivityController.finishAll();
    }

}

