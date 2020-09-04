package com.java.coronanews.main;

import com.java.coronanews.R;
//import com.java.coronanews.data.Config;
//import com.java.coronanews.data.Manager;


public class MainPresenter implements MainContract.Presenter {

    private MainContract.View mMainView;
    private boolean mRestartByMode;
    private int mCurrentNavigation = R.id.nav_news;

    public MainPresenter(MainContract.View view, boolean restartByMode) {
        this.mMainView = view;
        this.mRestartByMode = restartByMode;
        view.setPresenter(this);

        if (mRestartByMode) {
            mCurrentNavigation = R.id.nav_settings;
        } else {
            mCurrentNavigation = R.id.nav_news;
        }
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
            case R.id.nav_favorites:
                mMainView.switchToFavorites();
                break;
            case R.id.nav_settings:
                mMainView.switchToSettings();
                break;
            case R.id.nav_about:
                mMainView.switchToAbout();
                break;
            default:
                break;
        }
    }
}
