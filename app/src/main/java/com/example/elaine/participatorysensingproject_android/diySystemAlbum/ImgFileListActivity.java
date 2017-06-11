package com.example.elaine.participatorysensingproject_android.diySystemAlbum;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.elaine.participatorysensingproject_android.R;
import com.example.elaine.participatorysensingproject_android.mappage.ActivityController;
import com.example.elaine.participatorysensingproject_android.mappage.AskPhoto;
import com.example.elaine.participatorysensingproject_android.mappage.PhotoWall;
import com.example.elaine.participatorysensingproject_android.mappage.SubPMStationItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ImgFileListActivity extends Activity implements OnItemClickListener {
    static SubPMStationItem aStation;
    ListView listView;
    Util util;
    ImgFileListAdapter listAdapter;
    List<FileTraversal> locallist;
    String realLatitude,realLongitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityController.addActivity(this);
        setContentView(R.layout.imgfilelist);
        aStation = (SubPMStationItem) getIntent().getSerializableExtra("subStationItem");
        realLatitude = getIntent().getStringExtra("realLatitude");
        realLongitude = getIntent().getStringExtra("realLongitude");


        TextView pageTitle = (TextView) findViewById(R.id.centerText);
        pageTitle.setText("相册");
        TextView button1 = (TextView) findViewById(R.id.Button1);
        Typeface iconfont = Typeface.createFromAsset(getAssets(), "iconfont.ttf");
        button1.setTypeface(iconfont);
        button1.setText(R.string.icon_back);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                onBackPressed();
            }
        });
        TextView button2 = (TextView) findViewById(R.id.Button2);
        button2.setText("");

        listView = (ListView) findViewById(R.id.albumListView);
        util = new Util(this);
        locallist = util.LocalImgFileList();
        List<HashMap<String, String>> listdata = new ArrayList<HashMap<String, String>>();
        Bitmap bitmap[] = null;
        if (locallist != null) {
            bitmap = new Bitmap[locallist.size()];
            for (int i = 0; i < locallist.size(); i++) {
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("filecount", locallist.get(i).filecontent.size() + "张");
                map.put("imgpath", locallist.get(i).filecontent.get(0) == null ? null : (locallist.get(i).filecontent.get(locallist.get(i).filecontent.size() - 1)));
                map.put("filename", locallist.get(i).filename);
                listdata.add(map);
            }
        }
        listAdapter = new ImgFileListAdapter(this, listdata);
        listView.setAdapter(listAdapter);
        listView.setOnItemClickListener(this);

    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        Intent intent = new Intent(this, ImgsActivity.class);
        Bundle bundle = new Bundle();
        bundle.putParcelable("data", locallist.get(arg2));
        intent.putExtras(bundle);
        intent.putExtra("subStationItem", aStation);
        intent.putExtra("realLongitude", realLongitude);
        intent.putExtra("realLatitude",realLatitude);
        startActivity(intent);
        ImgFileListActivity.this.finish();
    }

    public void onBackPressed() {
        // TODO Auto-generated method stub
        AskPhoto ask = new AskPhoto(getBaseContext(), aStation.getStationID(), aStation.getStationAddress());
        Toast.makeText(ImgFileListActivity.this, "正在退回照片墙", Toast.LENGTH_LONG).show();
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Intent intent = new Intent(ImgFileListActivity.this, PhotoWall.class);
        intent.putExtra("subStationItem", aStation);
        intent.putExtra("realLongitude", realLongitude);
        intent.putExtra("realLatitude",realLatitude);
        startActivity(intent);
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        ImgFileListActivity.this.finish();
    }

}
