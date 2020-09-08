package com.java.coronanews.main;

import com.java.coronanews.BaseView;
import com.java.coronanews.BasePresenter;
//import com.java.coronanews.data.Config;

/**
 * Created by equation on 9/7/17.
 */

public interface MainContract {

    interface View extends BaseView<Presenter> {

        /**
         * 切换到新闻页面
         */
        void switchToNews();

        void switchToChart();
        /**
         * 切换到关于页面
         */
        void switchToSetting();

        /**
         * 切换到history页面
         */
        void switchToHistory ();

        void switchToInfo();

        void switchToScholar();
    }

    interface Presenter extends BasePresenter {

        /**
         * 切换页面
         *
         * @param id 页面 ID
         */
        void switchNavigation(int id);

        /**
         * 获得当前页面
         * @return 页面 ID
         */
        int getCurrentNavigation();
    }
}
