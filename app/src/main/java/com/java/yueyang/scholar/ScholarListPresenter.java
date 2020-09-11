package com.java.yueyang.scholar;

import android.content.Intent;
import android.os.Bundle;

import com.java.yueyang.data.Manager;
import com.java.yueyang.data.Scholar;

import java.util.List;

import io.reactivex.functions.Consumer;

public class ScholarListPresenter implements ScholarListContract.Presenter {

    private final int PAGE_SIZE = 20;

    private ScholarListFragment mView;
    private int mCategory;
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
