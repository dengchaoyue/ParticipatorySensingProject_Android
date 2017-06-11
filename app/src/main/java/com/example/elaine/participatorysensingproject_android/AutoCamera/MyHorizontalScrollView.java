package com.example.elaine.participatorysensingproject_android.AutoCamera;

/**
 * Created by Elaine on 2016/10/11.
 */

import android.content.Context;
import android.graphics.Color;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.elaine.participatorysensingproject_android.R;

import java.util.HashMap;
import java.util.Map;

public class MyHorizontalScrollView extends HorizontalScrollView implements
        OnClickListener {
    //敏感度，自动调整位置的幅度
    private static int SENSITIVENESS = 200;
    /**
     * 图片滚动时的回调接口
     *
     * @author zhy
     */
    public interface CurrentImageChangeListener {
        void onCurrentImgChanged(int position, View viewIndicator);
    }

    /**
     * 条目点击时的回调
     *
     * @author zhy
     */
    public interface OnItemClickListener {
        void onClick(View view, int pos);
    }
    private CurrentImageChangeListener mListener;

    private OnItemClickListener mOnClickListener;

    private static final String TAG = "MyHorizontalScrollView";

    /**
     * HorizontalListView中的LinearLayout
     */
    private LinearLayout mContainer;

    /**
     * 子元素的宽度
     */
    private int mChildWidth;
    /**
     * 子元素的高度
     */
    private int mChildHeight;
    /**
     * 当前最后一张图片的index
     */
    private int mCurrentIndex;
    /**
     * 当前第一张图片的下标
     */
    private int mFristIndex;
    /**
     * 当前第一个View
     */
    private View mFirstView;
    /**
     * 数据适配器
     */
    private HorizontalScrollViewAdapter mAdapter;
    /**
     * 每屏幕最多显示的个数
     */
    private int mCountOneScreen;
    /**
     * 屏幕的宽度
     */
    private int mScreenWitdh;

    private boolean scroll_flag = false;
    /**
     * 保存View与位置的键值对
     */
    private Map<View, Integer> mViewPos = new HashMap<View, Integer>();

    public MyHorizontalScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // 获得屏幕宽度
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        mScreenWitdh = outMetrics.widthPixels;

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mContainer = (LinearLayout) getChildAt(0);
    }
    /**
     * 加载下一张图片
     */
    protected void loadNextImg() {
        //循环利用移出去的view组件
        View view_reuse = mContainer.getChildAt(0);
        mViewPos.remove(mContainer.getChildAt(0));
        mContainer.removeViewAt(0);
        //获取下一张图片，并且设置onclick事件，且加入容器中
        View view = mAdapter.getView((++mCurrentIndex)%mAdapter.getCount(), view_reuse, mContainer);
        view.setOnClickListener(this);
        //用户不可点击
        view.setClickable(false);
        mContainer.addView(view);
        mViewPos.put(view, mCurrentIndex%mAdapter.getCount());
        //添加偏移量
        scrollTo(mChildWidth/2, 0);
        //当前第一张图片下标
        mFristIndex = (mFristIndex+1)%mAdapter.getCount();
        //如果设置了滚动监听则触发
        if (mListener != null) {
            notifyCurrentImgChanged();
        }
        Log.i("mContainer",mContainer.getChildCount()+"");
        //获取中间view
        this.onClick(mContainer.getChildAt(1));
    }

    /**
     * 加载前一张图片
     */
    protected void loadPreImg() {
        if (mFristIndex >= 0) {
            int oldViewPos = mContainer.getChildCount() - 1;
            View view_reuse = mContainer.getChildAt(oldViewPos);
            mViewPos.remove(mContainer.getChildAt(oldViewPos));
            mContainer.removeViewAt(oldViewPos);
            mFristIndex = --mFristIndex>=0?mFristIndex:(mFristIndex+mAdapter.getCount());
            //将此View放入第一个位置
            View view = mAdapter.getView(mFristIndex, view_reuse, mContainer);
            mViewPos.put(view, mFristIndex);
            mContainer.addView(view, 0);
            //保持 0 | 1  2 3 | 4的偏移量
            scrollTo(mChildWidth/2, 0);
            view.setOnClickListener(this);
            //用户不可点击
            view.setClickable(false);
            //实现循环
            mCurrentIndex = --mCurrentIndex>=0?mCurrentIndex:(mCurrentIndex+mAdapter.getCount());

            //回调
            if (mListener != null) {
                notifyCurrentImgChanged();
            }
            //获取中间view
            this.onClick(mContainer.getChildAt(1));
        }
    }

    /**
     * 滑动时的回调
     */
    public void notifyCurrentImgChanged() {
        for (int i = 0; i < mContainer.getChildCount(); i++) {
            mContainer.getChildAt(i).setBackgroundColor(Color.BLACK);
            TextView v = (TextView) mContainer.getChildAt(i).findViewById(R.id.gallery_item);
            v.setTextColor(Color.GRAY);
        }

        mListener.onCurrentImgChanged(mFristIndex, mContainer.getChildAt(0));

    }

    /**
     * 初始化数据，设置数据适配器
     *
     * @param mAdapter
     */
    public void initDatas(HorizontalScrollViewAdapter mAdapter) {
        this.mAdapter = mAdapter;
        mContainer = (LinearLayout) getChildAt(0);
        // 获得适配器中第一个View
        final View view = mAdapter.getView(0, null, mContainer);
        mContainer.addView(view);

        // 强制计算当前View的宽和高
        if (mChildWidth == 0 && mChildHeight == 0) {
            int w = View.MeasureSpec.makeMeasureSpec(0,
                    View.MeasureSpec.UNSPECIFIED);
            int h = View.MeasureSpec.makeMeasureSpec(0,
                    View.MeasureSpec.UNSPECIFIED);
            view.measure(w, h);
            mChildHeight = view.getMeasuredHeight();
            mChildWidth = view.getMeasuredWidth();
            Log.e(TAG, view.getMeasuredWidth() + "," + view.getMeasuredHeight());
            mChildHeight = view.getMeasuredHeight();
            // 1缓冲CNMLGB
            mCountOneScreen = mScreenWitdh / mChildWidth + 1;

            Log.e(TAG, "mCountOneScreen = " + mCountOneScreen
                    + " ,mChildWidth = " + mChildWidth);


        }
        //初始化第一屏幕的元素
        initFirstScreenChildren(mCountOneScreen);
    }

    /**
     * 加载第一屏的View
     *
     * @param mCountOneScreen
     */
    public void initFirstScreenChildren(int mCountOneScreen) {

        mContainer = (LinearLayout) getChildAt(0);
        mContainer.removeAllViews();
        mViewPos.clear();

        for (int i = -1; i < mCountOneScreen-1; i++) {
            //左缓冲加载最后一个数据
            View view ;
            if(i<0){
                view = mAdapter.getView((i+mAdapter.getCount()), null, mContainer);
            }
            //初始化加载0,1,2,3数据
            else{
                view = mAdapter.getView(i,null,mContainer);
            }
            view.setOnClickListener(this);
            //用户不可点击
            view.setClickable(false);
            view.setBackgroundColor(Color.BLACK);
            mContainer.addView(view);
            if(i<0){
                mViewPos.put(view, i+mAdapter.getCount());
                mCurrentIndex = i+mAdapter.getCount();

            }
            else {
                mViewPos.put(view, i);
                mCurrentIndex = i;
            }
        }
            mFristIndex = mAdapter.getCount()-1;
        if (mListener != null) {
            notifyCurrentImgChanged();
        }
        for (int i = 0; i < mContainer.getChildCount(); i++) {
            mContainer.getChildAt(i).setBackgroundColor(Color.BLACK);
            TextView v = (TextView) mContainer.getChildAt(i).findViewById(R.id.gallery_item);
            v.setTextColor(Color.GRAY);
        }
        scroll_flag = true;
        Log.i("currentX", getScrollX() + "");
        this.onClick(mContainer.getChildAt(1));
}

   @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_MOVE:
                if(scroll_flag){
                    Log.i("scroll_flag", "我应该滚到到mChildWidth");
                    FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT,DensityUtil.dip2px(this.getContext(),80));
                    //此处相当于布局文件中的Android:layout_gravity属性
                    lp.gravity = Gravity.LEFT;
                    mContainer.setLayoutParams(lp);
                    scrollTo(mChildWidth / 2, 0);
                    scroll_flag = false;
                }
                else{
                    int scrollX = getScrollX();
                    // 0 1 | 2 3 4| 加载下一张
                    if (scrollX >=mChildWidth/2+mChildWidth/SENSITIVENESS) {
                        //模拟ACTION_CANCEL动作,取消滚动
                        long uptime = SystemClock.uptimeMillis();
                        this.onTouchEvent(MotionEvent.obtain(uptime, uptime, MotionEvent.ACTION_CANCEL, getX(), getY(), 0));
                        //加载下一张图片并将位移置OFFSET
                        loadNextImg();
                    }
                    // | 0 1 2 | 3 4 加载前一张
                    if ((scrollX<=mChildWidth/2-mChildWidth/SENSITIVENESS))
                    {
                        //模拟ACTION_CANCEL动作,取消滚动
                        long uptime = SystemClock.uptimeMillis();
                        this.onTouchEvent(MotionEvent.obtain(uptime, uptime, MotionEvent.ACTION_CANCEL, getX(), getY(), 0));
                        //加载前一张图片并将位移置OFFSET
                        loadPreImg();
                    }
                }

                break;

        }
        return super.onTouchEvent(ev);
    }


    @Override
    public void onClick(View v) {
        if (mOnClickListener!= null) {
            for (int i = 0; i < mContainer.getChildCount(); i++) {
                mContainer.getChildAt(i).setBackgroundColor(Color.parseColor("#ffffff"));
                TextView vi = (TextView) mContainer.getChildAt(i).findViewById(R.id.gallery_item);
                vi.setTextColor(Color.parseColor("#a9b7b7"));
            }
            Log.i("mViewPos", mViewPos.get(v) + "");
            mOnClickListener.onClick(v, mViewPos.get(v));
            TextView view = (TextView) v.findViewById(R.id.gallery_item);
            view.setTextColor(Color.GRAY);
        }
    }

    public void setOnItemClickListener(OnItemClickListener mOnClickListener) {
        this.mOnClickListener = mOnClickListener;
        //初始状态：选中第一个数字
        this.onClick(mContainer.getChildAt(1));
    }
    public void setCurrentImageChangeListener(
            CurrentImageChangeListener mListener) {
        this.mListener = mListener;
    }
    //关闭惯性滑动的距离
    @Override
    public void fling(int velocityX) {
        super.fling(0);
    }
}