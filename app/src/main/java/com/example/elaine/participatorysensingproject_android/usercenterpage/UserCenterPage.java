package com.example.elaine.participatorysensingproject_android.usercenterpage;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.elaine.participatorysensingproject_android.R;
import com.example.elaine.participatorysensingproject_android.mappage.ActivityController;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Elaine on 2016/11/2.
 */
public class UserCenterPage extends Activity implements AdapterView.OnItemClickListener {
    private ListView userCenterListView;
    public static String userName, userIcon;
    private List<UserCenterItem> listData;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityController.addActivity(this);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.user_center);

        Intent intent = getIntent();
        userName = intent.getStringExtra("username");
        userIcon = intent.getStringExtra("userIcon");


        TextView pageTitle = (TextView) findViewById(R.id.centerText);
        pageTitle.setText("个人中心");
        TextView button1 = (TextView) findViewById(R.id.Button1);
        button1.setText("");
        TextView button2 = (TextView) findViewById(R.id.Button2);
        button2.setText("");

    }

    @Override
    protected void onResume() {
        super.onResume();

        if (listData != null) {
            listData.clear();
        }
        listData = new ArrayList<UserCenterItem>();
        if ("".equals(userName)) {
            UserCenterItem itemOne = new UserCenterItem();
            itemOne.setContent("登录");
            listData.add(itemOne);
        } else {
            UserCenterItem itemOne = new UserCenterItem();
            itemOne.setContent(userName);
            listData.add(itemOne);
        }
        UserCenterItem itemTwo = new UserCenterItem();
        itemTwo.setContent("关于我们");
        listData.add(itemTwo);
        UserCenterItem itemThree = new UserCenterItem();
        itemThree.setContent("切换语言");
        listData.add(itemThree);
        userCenterListView = (ListView) findViewById(R.id.user_center_listview);
        UserCenterAdapter userCenterAdapter = new UserCenterAdapter(getApplicationContext(), R.layout.usercenter_item, listData);
        userCenterListView.setAdapter(userCenterAdapter);
        setListViewHeightBasedOnChildren(userCenterListView);
        userCenterListView.setOnItemClickListener(this);
    }


    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        if ("登录".equals(listData.get(arg2).getContent())) {
            Intent register = new Intent(this, UserNotSignedActivity.class);
            startActivity(register);
        } else if ("关于我们".equals(listData.get(arg2).getContent())) {
            Intent aboutUs = new Intent(this, APPInformation.class);
            startActivity(aboutUs);
        } else if (userName.equals(listData.get(arg2).getContent())) {
            Intent userInfo = new Intent(this, UserInformation.class);
            userInfo.putExtra("username", userName);
            startActivity(userInfo);
        } else {
            Toast.makeText(this, listData.get(arg2).getContent() + "", Toast.LENGTH_LONG).show();
        }
    }

    public void setListViewHeightBasedOnChildren(ListView listView) {
        // 获取ListView对应的Adapter
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }
        int totalHeight = 0;
        for (int i = 0, len = listAdapter.getCount(); i < len; i++) {
            // listAdapter.getCount()返回数据项的数目
            View listItem = listAdapter.getView(i, null, listView);
            // 计算子项View 的宽高
            listItem.measure(0, 0);
            // 统计所有子项的总高度
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        // listView.getDividerHeight()获取子项间分隔符占用的高度
        // params.height最后得到整个ListView完整显示需要的高度
        listView.setLayoutParams(params);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityController.removeActivity(this);
    }
}
