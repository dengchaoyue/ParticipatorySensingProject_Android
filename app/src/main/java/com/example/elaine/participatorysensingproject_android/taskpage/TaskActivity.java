/**
 * class name:
 * class description:
 * author: dengchaoyue
 * version: 1.0
 */
package com.example.elaine.participatorysensingproject_android.taskpage;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;

import com.example.elaine.participatorysensingproject_android.R;
import com.example.elaine.participatorysensingproject_android.mappage.ActivityController;
import com.example.elaine.participatorysensingproject_android.usercenterpage.UserNotSignedActivity;

import java.util.ArrayList;
import java.util.List;

public class TaskActivity extends FragmentActivity {
    private ViewPager mViewPager;
    private MyTaskFragmentPageAdapter mAdapter;
    public static List<MyTaskFragment> mMyTaskFragmentList = new ArrayList<MyTaskFragment>();
    public static String username = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityController.addActivity(this);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.task_main_activity);
        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        Button button1 = (Button) findViewById(R.id.Button1);
        Button button2 = (Button) findViewById(R.id.Button2);
        Button button3 = (Button) findViewById(R.id.Button3);
        Button button4 = (Button) findViewById(R.id.Button4);
        init();


        final Button buttonA = (Button) findViewById(R.id.ButtonA);
        final Button buttonB = (Button) findViewById(R.id.ButtonB);
        buttonA.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                buttonA.setBackgroundResource(R.drawable.button_shape_one);
                buttonA.setText("Tasks Center");
                buttonA.setTextColor(Color.WHITE);
                buttonB.setBackgroundResource(R.drawable.button_shape_two);
                buttonB.setText("My Center");
                buttonB.setTextColor(Color.rgb(58, 95, 205));

            }
        });
        buttonB.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                buttonB.setBackgroundResource(R.drawable.button_shape_one);
                buttonB.setText("My Center");
                buttonB.setTextColor(Color.WHITE);
                buttonA.setBackgroundResource(R.drawable.button_shape_two);
                buttonA.setText("Tasks Center");
                buttonA.setTextColor(Color.rgb(58, 95, 205));

            }
        });
        LinearLayout tab_home = (LinearLayout) findViewById(R.id.tab_home);
        LinearLayout tab_map = (LinearLayout) findViewById(R.id.tab_map);
        LinearLayout tab_task = (LinearLayout) findViewById(R.id.tab_task);
        LinearLayout tab_user = (LinearLayout) findViewById(R.id.tab_user);
        tab_home.setSelected(false);
        tab_map.setSelected(false);
        tab_task.setSelected(true);
        tab_user.setSelected(false);
    }

    /**
     *
     */
    private void init() {
        // TODO Auto-generated method stub
        // Supply num input as an argument.
        SharedPreferences preferences = getSharedPreferences("user", MODE_PRIVATE);
        username = preferences.getString("username", "");
        if ("".equals(username)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(TaskActivity.this);
            builder.setNegativeButton("前去登录", new DialogInterface.OnClickListener() {//添加确定按钮
                @Override
                public void onClick(DialogInterface dialogNote, int which) {//确定按钮的响应事件
                    dialogNote.dismiss();
                    Intent register = new Intent(TaskActivity.this, UserNotSignedActivity.class);
                    startActivity(register);
                    TaskActivity.this.finish();
                }
            });
            AlertDialog dialogNote = builder.create();
            dialogNote.setTitle("Note: ");//设置对话框标题
            dialogNote.setMessage("您还没有登录，请先登录后再进行拍照");//设置显示的内容
            Window win = dialogNote.getWindow();
            win.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            dialogNote.show();
        } else {
            //requestNewTasks();
            //requestMyTasks();
            MyTaskFragment t1 = new MyTaskFragment();
            Bundle args1 = new Bundle();
            args1.putInt("num", 0);
            t1.setArguments(args1);
            MyTaskFragment t2 = new MyTaskFragment();
            Bundle args2 = new Bundle();
            args2.putInt("num", 1);
            t2.setArguments(args1);
            MyTaskFragment t3 = new MyTaskFragment();
            Bundle args3 = new Bundle();
            args3.putInt("num", 2);
            t3.setArguments(args3);
            MyTaskFragment t4 = new MyTaskFragment();
            Bundle args4 = new Bundle();
            args4.putInt("num", 3);
            t4.setArguments(args4);
            mMyTaskFragmentList.add(t1);
            mMyTaskFragmentList.add(t2);
            mMyTaskFragmentList.add(t3);
            mMyTaskFragmentList.add(t4);

            mAdapter = new MyTaskFragmentPageAdapter(this.getSupportFragmentManager());
            mViewPager.setAdapter(mAdapter);
            mViewPager.setCurrentItem(0);
        }
    }
}