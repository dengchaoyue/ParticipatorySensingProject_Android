package com.example.elaine.participatorysensingproject_android.usercenterpage;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.media.ThumbnailUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.elaine.participatorysensingproject_android.R;

import java.util.List;

/**
 * Created by Elaine on 2016/11/2.
 */
public class UserCenterAdapter extends BaseAdapter {

    private List<UserCenterItem> listData;
    int resourceId;
    Context myContext;
    LayoutInflater inflater;

    public UserCenterAdapter(Context context, int textViewResourceId, List<UserCenterItem> listdata) {
        listData = listdata;
        resourceId = textViewResourceId;
        myContext = context;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return listData.size();
    }

    @Override
    public Object getItem(int position) {
        return listData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = inflater.inflate(resourceId, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.loginLL = (LinearLayout) convertView.findViewById(R.id.login_linearlayout);
            viewHolder.otherLL = (LinearLayout) convertView.findViewById(R.id.otheritem_linearlayout);
            viewHolder.loginTV = (TextView) convertView.findViewById(R.id.user_center_login_tv);
            viewHolder.otherTv = (TextView) convertView.findViewById(R.id.user_center_item_tv);
            viewHolder.arrowOne = (TextView) convertView.findViewById(R.id.uc_front_icon);
            viewHolder.arrowTwo = (TextView) convertView.findViewById(R.id.uc_front_icon_sec);
            viewHolder.userIcon = (ImageView) convertView.findViewById(R.id.user_center_icon);
            Typeface iconfont = Typeface.createFromAsset(myContext.getAssets(), "iconfont.ttf");
            viewHolder.arrowOne.setTypeface(iconfont);
            viewHolder.arrowOne.setText(R.string.icon_front);
            viewHolder.arrowTwo.setTypeface(iconfont);
            viewHolder.arrowTwo.setText(R.string.icon_front);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        if (listData.get(position).getContent().equals("登录")) {
            viewHolder.otherLL.setVisibility(View.GONE);
            viewHolder.loginLL.setVisibility(View.VISIBLE);
            viewHolder.loginTV.setText(listData.get(position).getContent());
        } else if (listData.get(position).getContent().equals("关于我们")) {
            viewHolder.loginLL.setVisibility(View.GONE);
            viewHolder.otherLL.setVisibility(View.VISIBLE);
            viewHolder.otherTv.setText(listData.get(position).getContent());
        } else if (listData.get(position).getContent().equals("切换语言")) {
            viewHolder.loginLL.setVisibility(View.GONE);
            viewHolder.otherLL.setVisibility(View.VISIBLE);
            viewHolder.otherTv.setText(listData.get(position).getContent());
        } else {
            viewHolder.otherLL.setVisibility(View.GONE);
            viewHolder.loginLL.setVisibility(View.VISIBLE);
            viewHolder.loginTV.setText(listData.get(position).getContent());
            SharedPreferences sp = myContext.getSharedPreferences("userIcon", 0x0000);
            SharedPreferences.Editor editor = sp.edit();
            String picPath = sp.getString("picPath", "");
            if (!"".equals(picPath)) {
                Bitmap bmpDefaultPic = BitmapFactory.decodeFile(picPath, null);
                int w = bmpDefaultPic.getWidth(); // 得到图片的宽，高
                int h = bmpDefaultPic.getHeight();
                int wh = w > h ? h : w;// 裁切后所取的正方形区域边长
                int retX = w > h ? (w - h) / 2 : 0;// 基于原图，取正方形左上角x坐标
                int retY = w > h ? 0 : (h - w) / 2;
                Bitmap bmp = Bitmap.createBitmap(bmpDefaultPic, retX, retY, wh, wh, null,
                        false);
                UserInformation.UserIcon = bmp;
                viewHolder.userIcon.setImageBitmap(ThumbnailUtils.extractThumbnail(bmp, 80, 80));
            } else {
                viewHolder.userIcon.setImageResource(R.drawable.user_icon);
            }
        }
        return convertView;
    }

    class ViewHolder {
        LinearLayout loginLL, otherLL;
        TextView loginTV, otherTv, arrowOne, arrowTwo;
        ImageView userIcon;
    }
}
