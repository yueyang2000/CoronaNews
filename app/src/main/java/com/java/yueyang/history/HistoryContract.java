package com.java.yueyang.history;

import android.os.Bundle;

import com.java.yueyang.BasePresenter;
import com.java.yueyang.BaseView;
import com.java.yueyang.data.NewsItem;

import java.util.List;

/**
 * Created by equation on 9/12/17.
 */

public interface HistoryContract {

    interface View extends BaseView<Presenter> {

        /**
         * 设置收藏列表
         * @param list 新闻列表
         */
        void setHistory(List<NewsItem> list);
    }

    interface Presenter extends BasePresenter {

        /**
         * 获取收藏列表
         */
        void getHistory();


        /**
         * 打开新闻详情
         * @param news 被打开的新闻
         * @param options 过渡选项
         */
        void openNewsDetailUI(NewsItem news, Bundle options);
    }
}
