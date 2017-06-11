package com.example.elaine.participatorysensingproject_android.AutoCamera;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import com.example.elaine.participatorysensingproject_android.R;

import java.util.ArrayList;

/**
 * Created by Elaine on 2016/10/10.
 */

public class HorizontalScrollViewAdapterBelow {

    private Context mContext;
    private LayoutInflater mInflater;
    private ArrayList<String> mDatas;
    private static int ONESCREENNUM = 2;
    public HorizontalScrollViewAdapterBelow(Context context, ArrayList<String> mDatas) {
        this.mContext = context;
        mInflater = LayoutInflater.from(context);
        this.mDatas = mDatas;
    }

    public int getCount() {
        return mDatas.size();
    }

    public Object getItem(int position) {
        return mDatas.get(position);
    }

    public long getItemId(int position) {
        return position;
    }


    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolderBelow viewHolderBelow = null;
        if (convertView == null) {
            viewHolderBelow = new ViewHolderBelow();
            convertView = mInflater.inflate(
                    R.layout.activity_index_gallery_item_below, parent, false);
            convertView.setMinimumWidth(
                            ((WindowManager)mContext
                            .getSystemService(Context.WINDOW_SERVICE))
                            .getDefaultDisplay().getWidth()/ONESCREENNUM);

            viewHolderBelow.mText = (TextView) convertView
                    .findViewById(R.id.gallery_item_below);

            convertView.setTag(viewHolderBelow);
        } else {
            viewHolderBelow = (ViewHolderBelow) convertView.getTag();
        }
        viewHolderBelow.mText.setText(mDatas.get(position));

        return convertView;
    }

    private class ViewHolderBelow {
        TextView mText;
    }

}
