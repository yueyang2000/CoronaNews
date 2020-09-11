package com.java.yueyang.main;

import com.java.yueyang.BaseView;
import com.java.yueyang.BasePresenter;

public interface MainContract {

    interface View extends BaseView<Presenter> {

        void switchToNews();

        void switchToChart();

        void switchToSetting();

        void switchToHistory ();

        void switchToInfo();

        void switchToScholar();

        void switchToCluster();
    }

    interface Presenter extends BasePresenter {

        void switchNavigation(int id);

        int getCurrentNavigation();
    }
}
