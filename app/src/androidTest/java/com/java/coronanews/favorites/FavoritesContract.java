package com.java.coronanews.favorites;

import android.os.Bundle;

import com.java.coronanews.BasePresenter;
import com.java.coronanews.BaseView;
import com.java.coronanews.data.SimpleNews;

import java.util.List;

/**
 * Created by equation on 9/12/17.
 */

public interface FavoritesContract {

    interface View extends BaseView<Presenter> {

        /**
         * 设置收藏列表
         * @param list 新闻列表
         */
        void setFavorites(List<SimpleNews> list);
    }

    interface Presenter extends BasePresenter {

        /**
         * 获取收藏列表
         */
        void getFavorites();


        /**
         * 打开新闻详情
         * @param news 被打开的新闻
         * @param options 过渡选项
         */
        void openNewsDetailUI(SimpleNews news, Bundle options);
    }
}
