package com.java.coronanews.main;

import com.java.coronanews.R;
import com.java.coronanews.data.Manager;
import com.java.coronanews.data.Config;
import com.java.coronanews.data.NewsItem;

import org.json.JSONObject;

import java.util.List;

import io.reactivex.Single;
import io.reactivex.functions.Consumer;
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
            case R.id.nav_about:
                mMainView.switchToAbout();
                break;
            default:
                break;
        }
    }
}
