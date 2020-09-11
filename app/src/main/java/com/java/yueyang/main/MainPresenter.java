package com.java.yueyang.main;

import com.java.yueyang.R;


public class MainPresenter implements MainContract.Presenter {

    private MainContract.View mMainView;
    private int mCurrentNavigation = R.id.nav_news;

    public MainPresenter(MainContract.View view) {
        this.mMainView = view;
        view.setPresenter(this);
        mCurrentNavigation = R.id.nav_news;

    }

    public int getCurrentNavigation() {
        return mCurrentNavigation;
    }

    @Override
    public void subscribe() {
        switchNavigation(mCurrentNavigation);
    }

    @Override
    public void unsubscribe() {
    }


    @Override
    public void switchNavigation(int id) {
        mCurrentNavigation = id;
        switch (id) {
            case R.id.nav_news:
                mMainView.switchToNews();
                break;
            case R.id.nav_history:
                mMainView.switchToHistory();
                break;
            case R.id.nav_data:
                mMainView.switchToChart();
                break;
            case R.id.nav_info:
                mMainView.switchToInfo();
                break;
            case R.id.nav_setting:
                mMainView.switchToSetting();
                break;
            case R.id.nav_scholar:
                mMainView.switchToScholar();
                break;
            case R.id.nav_cluster:
                mMainView.switchToCluster();
                break;
            default:
                break;
        }
    }
}
