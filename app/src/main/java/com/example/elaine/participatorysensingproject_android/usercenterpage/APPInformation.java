package com.example.elaine.participatorysensingproject_android.usercenterpage;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.example.elaine.participatorysensingproject_android.R;
import com.example.elaine.participatorysensingproject_android.mappage.ActivityController;

/**
 * Created by Elaine on 2016/11/3.
 */
public class APPInformation extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityController.addActivity(this);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.usercenter_aboutus);
        TextView pageTitle = (TextView) findViewById(R.id.centerText);
        pageTitle.setText("关于我们");

        Typeface iconfont = Typeface.createFromAsset(getAssets(), "iconfont.ttf");
        TextView button1 = (TextView) findViewById(R.id.Button1);
        button1.setTypeface(iconfont);
        button1.setText(R.string.icon_back);
        button1.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                APPInformation.this.finish();
            }
        });
        TextView button2 = (TextView) findViewById(R.id.Button2);
        button2.setText("");
        TextView aboutusTv = (TextView) findViewById(R.id.app_information);
        aboutusTv.setText(getTextContent());
    }

    private String getTextContent() {
        String abcontent = "让每一个人，都成为我们家园的绿色志愿者！受制于空气质量检测设备高昂的价格，" +
                "目前，以北京市为例，全市仅有30多个空气质量监测站点，而这远远满足不了全市各地点空气质量" +
                "实时监测的需求。为了解决这一现状，北京邮电大学网络技术研究院BNRC实验室出品sensorandroidclient" +
                "软件。该软件以参与式感知为基础、AR拍摄为辅助、PM2.5计算为核心对空气质量进行监测和预报。" +
                "只要有的地方，就会成为一个观测站点，通过用户拍照上传，进行照片分析处理，从而实现实时、" +
                "实地监测和预报空气质量。\n联系方式：chaoyue0917@gmail.com";
        return abcontent;
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        APPInformation.this.finish();
    }
}
