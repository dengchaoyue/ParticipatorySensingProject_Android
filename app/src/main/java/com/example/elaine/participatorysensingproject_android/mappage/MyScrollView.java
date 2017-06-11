package com.example.elaine.participatorysensingproject_android.mappage;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.example.elaine.participatorysensingproject_android.R;
import com.example.elaine.participatorysensingproject_android.R.drawable;
import com.example.elaine.participatorysensingproject_android.R.id;
import com.example.elaine.participatorysensingproject_android.R.string;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;

/**
 * @author dengchaoyue
 */
public class MyScrollView extends ScrollView implements OnTouchListener {

    public static final int PAGE_SIZE = 9;

    private int page;

    private int columnWidth;

    private int firstColumnHeight;

    private int secondColumnHeight;

    private int thirdColumnHeight;

    private boolean loadOnce;

    private ImageLoader imageLoader;

    private LinearLayout firstColumn;

    private LinearLayout secondColumn;

    private LinearLayout thirdColumn;

    private static Set<LoadImageTask> taskCollection;

    private static View scrollLayout;

    private static int scrollViewHeight;

    private static int lastScrollY = -1;

    private List<ImageView> imageViewList = new ArrayList<ImageView>();


    public static Handler handler = new Handler() {

        public void handleMessage(Message msg) {
            MyScrollView myScrollView = (MyScrollView) msg.obj;
            int scrollY = myScrollView.getScrollY();
            // 如果当前的滚动位置和上次相同，表示已停止滚动
            if (scrollY == lastScrollY) {

                if (scrollViewHeight + scrollY >= scrollLayout.getHeight() && taskCollection.isEmpty()) {
                    myScrollView.loadMoreImages();
                }
                myScrollView.checkVisibility();
            } else {
                lastScrollY = scrollY;
                Message message = new Message();
                message.obj = myScrollView;
                // 5毫秒后再次对滚动位置进行判断
                handler.sendMessageDelayed(message, 5);
            }
            myScrollView.checkVisibility();
        }

        ;

    };

    public MyScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        imageLoader = ImageLoader.getInstance();
        taskCollection = new HashSet<LoadImageTask>();
        setOnTouchListener(this);
        Message message = new Message();
        message.obj = this;
        handler.sendMessage(message);///改了！
        this.postInvalidate();//刷新界面
    }


    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (changed && !loadOnce) {
            scrollViewHeight = getHeight();
            scrollLayout = getChildAt(0);
            firstColumn = (LinearLayout) findViewById(id.first_column);
            secondColumn = (LinearLayout) findViewById(id.second_column);
            thirdColumn = (LinearLayout) findViewById(id.third_column);
            columnWidth = firstColumn.getWidth();
            loadOnce = true;
            loadMoreImages();
        }
    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            Message message = new Message();
            message.obj = this;
            handler.sendMessageDelayed(message, 5);
        }
        return false;
    }


    public void loadMoreImages() {
        int startIndex = page * PAGE_SIZE;
        int endIndex = page * PAGE_SIZE + PAGE_SIZE;// PageSize
        if (startIndex < Images.imageUrls.size()) {
            Toast.makeText(getContext(), "正在加载...", Toast.LENGTH_SHORT).show();
            if (endIndex > Images.imageUrls.size()) {
                endIndex = Images.imageUrls.size();
            }
            // 下载照片
            for (int i = startIndex; i < endIndex; i++) {
                LoadImageTask task = new LoadImageTask();
                taskCollection.add(task);
                task.execute(Images.imageUrls.get(i));
            }
            page++;
        } else {
            Toast.makeText(getContext(), "已没有更多图片", Toast.LENGTH_SHORT).show();
        }
    }


    public void checkVisibility() {
        for (int i = 0; i < imageViewList.size(); i++) {
            ImageView imageView = imageViewList.get(i);
            int borderTop = (Integer) imageView.getTag(string.border_top);
            int borderBottom = (Integer) imageView.getTag(string.border_bottom);
            if (borderBottom > getScrollY()// 在屏幕内
                    && borderTop < getScrollY() + scrollViewHeight) {
                String imageUrl = (String) imageView.getTag(R.string.image_url);
                Bitmap bitmap = imageLoader.getBitmapFromMemoryCache(imageUrl);
                if (bitmap != null) {
                    imageView.setImageBitmap(bitmap);
                    imageView.setOnClickListener(new OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            // TODO Auto-generated method stub
                            Intent singlePic = new Intent(getContext(), singlePicPage.class);
                            String imageUrl = (String) v.getTag(R.string.image_url);
                            int index = Images.imageUrls.indexOf(imageUrl);
                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            Bitmap bitmap = imageLoader.getBitmapFromMemoryCache(imageUrl);
                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                            byte[] bitmapByte = baos.toByteArray();
                            singlePic.putExtra("bitmap", bitmapByte);
                            singlePic.putExtra("subStationIndex", index);
                            getContext().startActivity(singlePic);
                        }

                    });
                    LoadImageTask task = new LoadImageTask(imageView);
                    task.execute(imageUrl);
                }
            } else {
                imageView.setImageResource(drawable.loading);// 在屏幕外
            }
        }
    }


    class LoadImageTask extends AsyncTask<String, Void, Bitmap> {
        /**
         * 图片的URL地址
         */
        private String mImageUrl;


        /**
         * 可重复使用的ImageView
         */
        private ImageView mImageView;

        public LoadImageTask() {
        }

        /**
         * 将可重复使用的ImageView传入
         *
         * @param imageView
         */
        public LoadImageTask(ImageView imageView) {
            mImageView = imageView;
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            mImageUrl = params[0];
            Bitmap imageBitmap = imageLoader.getBitmapFromMemoryCache(mImageUrl);
            if (imageBitmap == null) {
                imageBitmap = loadImage(mImageUrl);
            }

            return imageBitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (bitmap != null) {
                //double ratio = bitmap.getWidth() / (columnWidth * 1.0);
                int columnHeight = getHeight() / 3;
                addImage(bitmap, columnWidth, columnHeight);
            }
            taskCollection.remove(this);
        }


        private Bitmap loadImage(String imageUrl) {
            File imageFile = new File(getImagePath(imageUrl));
            if (!imageFile.exists()) {
                downloadImage(imageUrl);
            }
            Bitmap bitmap = ImageLoader.decodeSampledBitmapFromResource(imageFile.getPath(), columnWidth);
            imageLoader.addBitmapToMemoryCache(imageUrl, bitmap);
            return bitmap;
        }


        private void addImage(Bitmap bitmap, int imageWidth, int imageHeight) {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(imageWidth, imageHeight);
            if (mImageView != null) {
                mImageView.setImageBitmap(bitmap);
            } else {
                ImageView imageView = new ImageView(getContext());
                imageView.setLayoutParams(params);
                imageView.setImageBitmap(bitmap);
                imageView.setScaleType(ScaleType.FIT_XY);
                imageView.setPadding(5, 5, 5, 5);
                imageView.setTag(string.image_url, mImageUrl);
                findColumnToAdd(imageView, imageHeight).addView(imageView);
                imageViewList.add(imageView);
            }
        }


        private LinearLayout findColumnToAdd(ImageView imageView, int imageHeight) {
            if (firstColumnHeight <= secondColumnHeight) {
                if (firstColumnHeight <= thirdColumnHeight) {
                    imageView.setTag(string.border_top, firstColumnHeight);
                    firstColumnHeight += imageHeight;
                    imageView.setTag(string.border_bottom, firstColumnHeight);
                    return firstColumn;
                }
                imageView.setTag(string.border_top, thirdColumnHeight);
                thirdColumnHeight += imageHeight;
                imageView.setTag(string.border_bottom, thirdColumnHeight);
                return thirdColumn;
            } else {
                if (secondColumnHeight <= thirdColumnHeight) {
                    imageView.setTag(string.border_top, secondColumnHeight);
                    secondColumnHeight += imageHeight;
                    imageView.setTag(string.border_bottom, secondColumnHeight);
                    return secondColumn;
                }
                imageView.setTag(string.border_top, thirdColumnHeight);
                thirdColumnHeight += imageHeight;
                imageView.setTag(string.border_bottom, thirdColumnHeight);
                return thirdColumn;
            }
        }

        private void downloadImage(String imageUrl) {
            HttpURLConnection con = null;
            FileOutputStream fos = null;
            BufferedOutputStream bos = null;
            BufferedInputStream bis = null;
            File imageFile = null;
            try {
                URL url = new URL(imageUrl);
                con = (HttpURLConnection) url.openConnection();
                con.setConnectTimeout(5 * 1000);
                con.setReadTimeout(15 * 1000);
                con.setDoInput(true);
                con.setDoOutput(true);
                con.setRequestProperty("Charset", "UTF-8");
                bis = new BufferedInputStream(con.getInputStream());
                imageFile = new File(getImagePath(imageUrl));
                fos = new FileOutputStream(imageFile);
                bos = new BufferedOutputStream(fos);
                byte[] b = new byte[1024];
                int length;
                while ((length = bis.read(b)) != -1) {
                    bos.write(b, 0, length);
                    bos.flush();
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (bis != null) {
                        bis.close();
                    }
                    if (bos != null) {
                        bos.close();
                    }
                    if (con != null) {
                        con.disconnect();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (imageFile != null) {
                Bitmap bitmap = ImageLoader.decodeSampledBitmapFromResource(imageFile.getPath(), columnWidth);
                imageLoader.addBitmapToMemoryCache(imageUrl, bitmap);
            }
            getContext().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(imageFile)));
        }

        private String getImagePath(String imageUrl) {
            int lastSlashIndex = imageUrl.lastIndexOf("/");
            String imageName = imageUrl.substring(lastSlashIndex + 1);
            String imageDir = Environment.getExternalStorageDirectory().getPath() + "/PhotoWallFalls/";
            File file = new File(imageDir);
            if (!file.exists()) {
                file.mkdirs();
            }
            String imagePath = imageDir + imageName;
            return imagePath;
        }
    }

}