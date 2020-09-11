package com.java.yueyang.history;

import android.content.Intent;
import android.os.Bundle;

import com.java.yueyang.data.Manager;
import com.java.yueyang.data.NewsItem;
import com.java.yueyang.news.NewsDetailActivity;
import java.util.List;

import io.reactivex.functions.Consumer;


public class HistoryPresenter implements HistoryContract.Presenter {

    private HistoryContract.View mView;
    private int mPageNo = 1;
    private boolean mLoading = false;

    public HistoryPresenter(HistoryContract.View view) {
        this.mView = view;
        view.setPresenter(this);
    }

    @Override
    public void subscribe() {
        getHistory();
    }

    @Override
    public void unsubscribe() {
        // nothing
    }

    @Override
    public void openNewsDetailUI(NewsItem news, Bundle options) {
        Intent intent = new Intent(mView.context(), NewsDetailActivity.class);
        intent.putExtra("NEWS_TITLE", news.title);
        String info = (news.author.isEmpty() ? news.source : news.author) + "　" + news.date;
        intent.putExtra("NEWS_INFO", info);
        intent.putExtra("NEWS_CONTENT", news.content);
        intent.putExtra("NEWS_type", news.type);
        mView.start(intent, options);
    }

    @Override
    public void getHistory() {
        Manager.I.history()
                .subscribe(new Consumer<List<NewsItem>>() {
                    @Override
                    public void accept(List<NewsItem> list) throws Exception {
                        mView.setHistory(list);
                    }
                });
    }


}
