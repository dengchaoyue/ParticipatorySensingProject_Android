package com.example.elaine.participatorysensingproject_android.AutoCamera;

import android.app.KeyguardManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.PowerManager;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by Elaine on 2017/3/13.
 */
public class MyReceiver extends BroadcastReceiver {
    private double periodTimesNo = 0;
    private double periodDuringNo = 0;
    private int count = 0;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    @Override
    public void onReceive(Context context, Intent intent) {

        preferences = context.getSharedPreferences("count",Context.MODE_ENABLE_WRITE_AHEAD_LOGGING);
        //读取SharedPreferences里的count数据，若存在返回其值，否则返回0
        count = preferences.getInt("count",0);
        editor = preferences.edit();
        editor.putInt("count",++count);
        editor.commit();

        periodTimesNo = intent.getDoubleExtra("PeriodTimesNumber", 0.0);
        periodDuringNo = intent.getDoubleExtra("PeriodDuringNumber", 0.0);
        if (count <= periodTimesNo) {

            Log.e("Broadcast", "count:" + count + ";periodTimeNo:" + periodTimesNo);
            wakeUpAndUnlock(context);
            Intent AutoPic = new Intent(context, MyAutoCamera.class);
            //很重要！！FLAG！！！
            AutoPic.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(AutoPic);

        } else if (count == periodTimesNo + 1) {
            Log.e("Broadcast", "!count:" + count + ";periodTimeNo:" + periodTimesNo);
        } else {
            Toast.makeText(context, "broadcast has finished", Toast.LENGTH_SHORT).show();
        }

    }

    public static void wakeUpAndUnlock(Context context) {
        KeyguardManager km = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
        KeyguardManager.KeyguardLock kl = km.newKeyguardLock("unLock");
        //解锁
        kl.disableKeyguard();
        //获取电源管理器对象
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        //获取PowerManager.WakeLock对象,后面的参数|表示同时传入两个值,最后的是LogCat里用的Tag
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.SCREEN_DIM_WAKE_LOCK, "bright");
        //点亮屏幕
        wl.acquire();
        //释放
        wl.release();
    }

}
