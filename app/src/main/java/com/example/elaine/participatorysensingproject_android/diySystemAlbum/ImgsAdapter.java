package com.example.elaine.participatorysensingproject_android.diySystemAlbum;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;

import com.example.elaine.participatorysensingproject_android.R;

import java.util.ArrayList;
import java.util.List;

public class ImgsAdapter extends BaseAdapter {

    Context context;
    List<String> data;
    public Bitmap bitmaps[];
    OnItemClickClass onItemClickClass;
    private int index = -1;
    Util util;

    List<View> holderlist;

    public ImgsAdapter(Context context, List<String> data, OnItemClickClass onItemClickClass) {
        this.context = context;
        this.data = data;
        this.onItemClickClass = onItemClickClass;
        bitmaps = new Bitmap[data.size()];
        util = new Util(context);
        holderlist = new ArrayList<View>();
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int arg0) {
        return data.get(arg0);
    }

    @Override
    public long getItemId(int arg0) {
        return arg0;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    @Override
    public View getView(int arg0, View arg1, ViewGroup arg2) {
        Holder holder;
        if (arg0 != index && arg0 > index) {
            index = arg0;
            arg1 = LayoutInflater.from(context).inflate(R.layout.imgsitem, null);
            holder = new Holder();
            holder.imageView = (ImageView) arg1.findViewById(R.id.imageView1);
            holder.checkBox = (CheckBox) arg1.findViewById(R.id.checkBox1);
            arg1.setTag(holder);
            holderlist.add(arg1);
        } else {
            holder = (Holder) holderlist.get(arg0).getTag();
            arg1 = holderlist.get(arg0);
        }
        if (bitmaps[arg0] == null) {
            util.imgExcute(holder.imageView, new ImgClallBackLisner(arg0), data.get(data.size() - 1 - arg0));
        } else {
            int w = bitmaps[arg0].getWidth(); // 得到图片的宽，高
            int h = bitmaps[arg0].getHeight();
            int wh = w > h ? h : w;// 裁切后所取的正方形区域边长
            int retX = w > h ? (w - h) / 2 : 0;// 基于原图，取正方形左上角x坐标
            int retY = w > h ? 0 : (h - w) / 2;
            Bitmap bmp = Bitmap.createBitmap(bitmaps[arg0], retX, retY, wh, wh, null,
                    false);
            holder.imageView.setImageBitmap(ThumbnailUtils.extractThumbnail(bmp, 100, 100));
        }
        arg1.setOnClickListener(new OnPhotoClick(arg0, holder.checkBox));
        return arg1;
    }

    class Holder {
        ImageView imageView;
        CheckBox checkBox;
    }

    public class ImgClallBackLisner implements ImgCallBack {
        int num;

        public ImgClallBackLisner(int num) {
            this.num = num;
        }

        @Override
        public void resultImgCall(ImageView imageView, Bitmap bitmap) {
            bitmaps[num] = bitmap;
            int w = bitmap.getWidth(); // 得到图片的宽，高
            int h = bitmap.getHeight();
            int wh = w > h ? h : w;// 裁切后所取的正方形区域边长
            int retX = w > h ? (w - h) / 2 : 0;// 基于原图，取正方形左上角x坐标
            int retY = w > h ? 0 : (h - w) / 2;
            Bitmap bmp = Bitmap.createBitmap(bitmap, retX, retY, wh, wh, null,
                    false);
            imageView.setImageBitmap(ThumbnailUtils.extractThumbnail(bmp, 100, 100));
        }
    }

    public interface OnItemClickClass {
        public void OnItemClick(View v, int Position, CheckBox checkBox);
    }

    class OnPhotoClick implements OnClickListener {
        int position;
        CheckBox checkBox;

        public OnPhotoClick(int position, CheckBox checkBox) {
            this.position = position;
            this.checkBox = checkBox;
        }

        @Override
        public void onClick(View v) {
            if (data != null && onItemClickClass != null) {
                onItemClickClass.OnItemClick(v, position, checkBox);
            }
        }
    }

}
