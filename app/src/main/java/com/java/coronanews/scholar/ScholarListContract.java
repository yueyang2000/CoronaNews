package com.java.coronanews.scholar;

import android.os.Bundle;

import com.java.coronanews.BasePresenter;
import com.java.coronanews.BaseView;
import com.java.coronanews.data.Scholar;

import java.util.List;

/**
 * Created by chenyu on 2017/9/7.
 */

public interface ScholarListContract {

    interface View extends BaseView<Presenter> {

        /**
         * 清空当前UI，并填充新闻，用做初始化
         * 可能会调用多次
         * @param list 新闻列表
         */
        void setScholar(List<Scholar> list);

        /**
         * 获取新闻成功
         * @param loadCompleted 是否加载完成
         */
        void onSuccess(boolean loadCompleted);

        /**
         * 获取新闻失败
         */
        void onError();
    }

    interface Presenter extends BasePresenter {

        /**
         *  是否正在加载
         */
        boolean isLoading();


        void openDetailUI(Scholar scholar, Bundle options);

        void getScholar();

    }
}
