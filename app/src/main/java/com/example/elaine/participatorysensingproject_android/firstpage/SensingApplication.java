package com.example.elaine.participatorysensingproject_android.firstpage;

import android.app.Application;
import android.app.Service;
import android.os.Vibrator;

import com.baidu.mapapi.SDKInitializer;
import com.example.elaine.participatorysensingproject_android.Location.LocationService;
import com.facebook.stetho.Stetho;

/**
 * Created by ying on 2016/5/27.
 */
public class SensingApplication extends Application {
    public LocationService locationService;
    public Vibrator mVibrator;
    private static SensingApplication instance;

    public static SensingApplication getInstance(){
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        /***
         * 初始化定位sdk，建议在Application中创建
         */
        locationService = new LocationService(getApplicationContext());
        mVibrator = (Vibrator) getApplicationContext().getSystemService(Service.VIBRATOR_SERVICE);
        SDKInitializer.initialize(getApplicationContext());


//        IWXAPI api = WXAPIFactory.createWXAPI(this, "wxb2ce4912e1f471f7", false);
//        api.registerApp("wxb2ce4912e1f471f7");
        Stetho.initialize(
                Stetho.newInitializerBuilder(this)
                        .enableDumpapp(
                                Stetho.defaultDumperPluginsProvider(this))
                        .enableWebKitInspector(
                                Stetho.defaultInspectorModulesProvider(this))
                        .build());
    }
}
