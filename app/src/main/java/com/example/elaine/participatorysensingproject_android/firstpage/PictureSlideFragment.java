package com.example.elaine.participatorysensingproject_android.firstpage;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;
import com.example.elaine.participatorysensingproject_android.Http.DownloadFirstPagePic;
import com.example.elaine.participatorysensingproject_android.R;

import java.util.ArrayList;

/**
 * Created by lenovo on 2016/5/14 0014.
 */
public class PictureSlideFragment extends Fragment {

    private int mIndex;
    private ArrayList<String> picNameArray;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_picture_slide, container, false);

        RequestQueue mQueue = Volley.newRequestQueue(getActivity());
        ImageLoader imageLoader = new ImageLoader(mQueue, new BitmapCache());
        NetworkImageView networkImageView = (NetworkImageView) v.findViewById(R.id.pictureSlide);
        networkImageView.setDefaultImageResId(R.drawable.no_pic);
        networkImageView.setErrorImageResId(R.drawable.no_pic);
        networkImageView.setImageUrl(DownloadFirstPagePic.ROOT_URL + Uri.encode(picNameArray.get(mIndex), "utf-8"),
                imageLoader);

        return v;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mIndex = getArguments() != null ? getArguments().getInt("index") : 1;
        AirQualityActivity activity = (AirQualityActivity) getActivity();
        picNameArray = activity.getmPicNameArray();
    }

    public static PictureSlideFragment newInstance(int index) {
        PictureSlideFragment f = new PictureSlideFragment();

        Bundle args = new Bundle();
        args.putInt("index", index);
        f.setArguments(args);

        return f;
    }
}
