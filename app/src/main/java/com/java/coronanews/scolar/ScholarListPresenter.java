package com.java.coronanews.scolar;

import android.content.Intent;
import android.os.Bundle;

import com.java.coronanews.BasePresenter;
import com.java.coronanews.data.Manager;
import com.java.coronanews.data.NewsItem;
import com.java.coronanews.data.Scholar;

import java.util.List;

import io.reactivex.Single;
import io.reactivex.functions.Consumer;
// import com.java.coronanews.news.newsdetail.NewsDetailActivity;


public class ScholarListPresenter implements ScholarListContract.Presenter {

    private final int PAGE_SIZE = 20;

    private ScholarListFragment mView;
    private int mCategory;
    private String mKeyword;
    private int mPageNo = 1;
    private boolean mLoading = false;
    private long mLastFetchStart;

    public ScholarListPresenter(ScholarListFragment view, int category) {
        this.mView = view;
        this.mCategory = category;

        view.setPresenter(this);
    }

    public boolean isLoading() {
        return mLoading;
    }

    public void subscribe() {
        getScholar();
    }

    public void unsubscribe() {
        // nothing
    }


    public void openDetailUI(Scholar scholar, Bundle options) {
        Intent intent = new Intent(mView.context(), ScholarDetailActivity.class);
        intent.putExtra("SCHOLAR", scholar.plain_json);
        mView.start(intent, options);
    }


    public void getScholar() {
        Manager.I.getScholar(mCategory).subscribe(new Consumer<List<Scholar>>() {
            @Override
            public void accept(List<Scholar> list) throws Exception {
                mView.setScholar(list);
            }
        });
    }
}
