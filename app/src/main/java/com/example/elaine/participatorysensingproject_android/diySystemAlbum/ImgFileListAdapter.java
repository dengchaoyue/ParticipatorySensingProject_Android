package com.example.elaine.participatorysensingproject_android.diySystemAlbum;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.media.ThumbnailUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.elaine.participatorysensingproject_android.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ImgFileListAdapter extends BaseAdapter {

    Context context;
    String filecount = "filecount";
    String filename = "filename";
    String imgpath = "imgpath";
    List<HashMap<String, String>> listdata;
    Util util;
    Bitmap[] bitmaps;
    private int index = -1;
    List<View> holderlist;

    public ImgFileListAdapter(Context context, List<HashMap<String, String>> listdata) {
        this.context = context;
        this.listdata = listdata;
        bitmaps = new Bitmap[listdata.size()];
        util = new Util(context);
        holderlist = new ArrayList<View>();
    }

    @Override
    public int getCount() {
        return listdata.size();
    }

    @Override
    public Object getItem(int arg0) {
        return listdata.get(arg0);
    }

    @Override
    public long getItemId(int arg0) {
        return arg0;
    }

    @Override
    public View getView(final int arg0, View arg1, ViewGroup arg2) {
        Holder holder;
        if (arg0 != index && arg0 > index) {
            holder = new Holder();
            arg1 = LayoutInflater.from(context).inflate(R.layout.imgfileadapter, null);
            holder.photo_imgview = (ImageView) arg1.findViewById(R.id.filephoto_imgview);
            holder.filecount_textview = (TextView) arg1.findViewById(R.id.filecount_textview);
            holder.filename_textView = (TextView) arg1.findViewById(R.id.filename_textview);
            holder.arrow_front = (TextView) arg1.findViewById(R.id.front_icon);
            arg1.setTag(holder);
            holderlist.add(arg1);
        } else {
            holder = (Holder) holderlist.get(arg0).getTag();
            arg1 = holderlist.get(arg0);
        }

        holder.filename_textView.setText(listdata.get(arg0).get(filename));
        holder.filecount_textview.setText(listdata.get(arg0).get(filecount));
        Typeface iconfont = Typeface.createFromAsset(context.getAssets(), "iconfont.ttf");
        holder.arrow_front.setTypeface(iconfont);
        holder.arrow_front.setText(R.string.icon_front);

        if (bitmaps[arg0] == null) {
            util.imgExcute(holder.photo_imgview, new ImgCallBack() {
                @Override
                public void resultImgCall(ImageView imageView, Bitmap bitmap) {
                    bitmaps[arg0] = bitmap;
                    int w = bitmap.getWidth(); // 得到图片的宽，高
                    int h = bitmap.getHeight();
                    int wh = w > h ? h : w;// 裁切后所取的正方形区域边长
                    int retX = w > h ? (w - h) / 2 : 0;// 基于原图，取正方形左上角x坐标
                    int retY = w > h ? 0 : (h - w) / 2;
                    Bitmap bmp = Bitmap.createBitmap(bitmap, retX, retY, wh, wh, null,
                            false);
                    imageView.setImageBitmap(ThumbnailUtils.extractThumbnail(bmp, 80, 80));
                }
            }, listdata.get(arg0).get(imgpath));
        } else {
            int w = bitmaps[arg0].getWidth(); // 得到图片的宽，高
            int h = bitmaps[arg0].getHeight();
            int wh = w > h ? h : w;// 裁切后所取的正方形区域边长
            int retX = w > h ? (w - h) / 2 : 0;// 基于原图，取正方形左上角x坐标
            int retY = w > h ? 0 : (h - w) / 2;
            Bitmap bmp = Bitmap.createBitmap(bitmaps[arg0], retX, retY, wh, wh, null,
                    false);
            holder.photo_imgview.setImageBitmap(ThumbnailUtils.extractThumbnail(bmp, 80, 80));
        }

        return arg1;
    }

    class Holder {
        public ImageView photo_imgview;
        public TextView filecount_textview;
        public TextView filename_textView;
        public TextView arrow_front;
    }


}
