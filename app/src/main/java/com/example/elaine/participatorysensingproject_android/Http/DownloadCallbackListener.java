package com.example.elaine.participatorysensingproject_android.Http;

import java.util.ArrayList;

/**
 * Created by ying on 2016/6/8.
 */
public interface DownloadCallbackListener {

    void onFinish(ArrayList<String> response);
    void onError(Exception e);
}