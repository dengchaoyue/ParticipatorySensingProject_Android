/**
 * class name:
 * class description:
 * author: dengchaoyue
 * version: 1.0
 */
package com.example.elaine.participatorysensingproject_android.taskpage;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;

import com.example.elaine.participatorysensingproject_android.Http.HttpCallbackListener;
import com.example.elaine.participatorysensingproject_android.R;
import com.example.elaine.participatorysensingproject_android.mappage.HttpUtil;
import com.example.elaine.participatorysensingproject_android.mappage.MapPage;
import com.example.elaine.participatorysensingproject_android.mappage.URLCollector;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MyTaskFragment extends Fragment {
    private List<TaskItem> taskList = new ArrayList<TaskItem>();
    static int mNum;
    private ImageView noTaskPic;
    public TaskAdapter adapter;
    private ListView listView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        taskList.clear();
        initTasks();
        adapter = new TaskAdapter(getContext(), R.layout.task_item, taskList);
        listView.setAdapter(adapter);
        mNum = getArguments() != null ? getArguments().getInt("num") : -1;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.task_fragment, null);
        listView = (ListView) view.findViewById(R.id.list_view);
        noTaskPic = (ImageView) view.findViewById(R.id.no_task);
        if (taskList.size() == 0) {
            noTaskPic.setVisibility(View.VISIBLE);
            listView.setVisibility(View.GONE);
        } else {
            noTaskPic.setVisibility(View.GONE);
            listView.setVisibility(View.VISIBLE);
        }
        return view;
    }

    private void initTasks() {

        String BASE_URL = URLCollector.NewTasks + "?lat=" + MapPage.currentLatitude + "&lon=" + MapPage.currentLatitude + "&userName=" + TaskActivity.username;
        try {
            HttpUtil.sentHttpRequest(BASE_URL, new HttpCallbackListener() {
                @Override
                public void onFinish(String response) {
                    // TODO Auto-generated method stub
                    parseJSONWithJSONObject(response);
                    Thread.currentThread().interrupt();
                    Message msg = new Message();
                    msg.what = 1;
                    refresh.sendMessage(msg);

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

    private Handler refresh = new Handler() {

        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1 :
                    adapter.notifyDataSetChanged();
                    break;
                default:
                    break;
            }
        }
    };
    private JSONArray tasks = null;
    private void parseJSONWithJSONObject(String jsonData) {
        try {
            Log.i("tasks", jsonData);
            JSONObject json = new JSONObject(jsonData);
            JSONObject msg = json.getJSONObject("message");
            int taskNum = msg.getInt("taskNum");
            if (taskNum == 0) {
                noTaskPic.setVisibility(View.VISIBLE);
            } else {
                tasks = msg.getJSONArray("tasks");
                if (tasks == null) {
                    noTaskPic.setVisibility(View.VISIBLE);
                } else {
                    for (int i = 0; i < taskNum; i++) {
                        TaskItem newTask = new TaskItem(tasks.getJSONObject(i));
                        taskList.add(newTask);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}