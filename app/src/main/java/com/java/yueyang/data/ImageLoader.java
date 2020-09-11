package com.java.yueyang.data;

import android.content.Context;
import android.widget.ImageView;

import com.java.yueyang.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;


public class ImageLoader {

    public static void init(Context context) {
        mImageLoader.init(ImageLoaderConfiguration.createDefault(context));
        mDisplayImageOptions = new DisplayImageOptions.Builder()
                .showImageOnLoading(null)
                .showImageForEmptyUri(R.drawable.bg)
                .showImageOnFail(R.drawable.bg)
                .resetViewBeforeLoading(true)
                .cacheOnDisk(true)
                .build();
    }

    public static void displayImage(String imageUrl, ImageView imageView) {
        mImageLoader.displayImage(imageUrl, imageView, mDisplayImageOptions);
    }

    private static com.nostra13.universalimageloader.core.ImageLoader
            mImageLoader = com.nostra13.universalimageloader.core.ImageLoader.getInstance();
    private static DisplayImageOptions mDisplayImageOptions;
}
