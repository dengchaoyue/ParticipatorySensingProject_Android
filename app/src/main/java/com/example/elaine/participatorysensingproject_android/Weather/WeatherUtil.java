package com.example.elaine.participatorysensingproject_android.Weather;

import android.text.TextUtils;

/**
 * Created by ying on 2016/6/16.
 */
public class WeatherUtil {

    /**
     * 安全的 String 返回
     *
     * @param prefix 默认字段
     * @param obj 拼接字段 (需检查)
     */
    public static String safeText(String prefix, String obj) {
        if (TextUtils.isEmpty(obj)) return "";
        return TextUtils.concat(prefix, obj).toString();
    }

    public static String safeText(String msg) {
        return safeText("", msg);
    }

    /**
     * 正则匹配掉错误信息
     */
    public static String replaceCity(String city) {
        city = safeText(city).replaceAll("(?:省|市|自治区|特别行政区|地区|盟)", "");
        return city;
    }
}
