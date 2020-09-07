package com.java.coronanews.news;

import android.content.Intent;
import android.os.Bundle;
import java.util.*;

import com.java.coronanews.data.Manager;
import com.java.coronanews.data.NewsItem;
import io.reactivex.Single;
import io.reactivex.functions.Consumer;
// import com.java.coronanews.news.newsdetail.NewsDetailActivity;


public class NewsListPresenter implements NewsListContract.Presenter {

    private final int PAGE_SIZE = 20;

    private NewsListContract.View mView;
    private int mCategory;
    private String mKeyword;
    private int mPageNo = 1;
    private boolean mLoading = false;
    private long mLastFetchStart;

    public NewsListPresenter(NewsListContract.View view, int category, String keyword) {
        this.mView = view;
        this.mCategory = category;
        this.mKeyword = keyword;

        view.setPresenter(this);
    }

    @Override
    public boolean isLoading() {
        return mLoading;
    }

    @Override
    public void setKeyword(String keyword) {
        mKeyword = keyword;
        refreshNews();
    }

    @Override
    public void subscribe() {
        refreshNews();
    }

    @Override
    public void unsubscribe() {
        // nothing
    }

    @Override
    public void requireMoreNews() {
        mPageNo++;
        fetchNews();
    }

    @Override
    public void refreshNews() {
        mPageNo = 1;
        fetchNews();
    }

    @Override
    public void openNewsDetailUI(NewsItem news, Bundle options) {
        Manager.I.touchRead(news);
        Intent intent = new Intent(mView.context(), NewsDetailActivity.class);
        intent.putExtra("NEWS_TITLE", news.title);
        String info = (news.author.isEmpty() ? news.source : news.author) + "　" + news.date;
        intent.putExtra("NEWS_INFO", info);
        intent.putExtra("NEWS_CONTENT", news.content);
        intent.putExtra("NEWS_type", news.type);
        mView.start(intent, options);
    }

    @Override
    public void fetchNewsRead(final int pos) {
        mView.resetItemRead(pos, true);
    }

    private void fetchNews() {


        final long start = System.currentTimeMillis();
        mLoading = true;
        mLastFetchStart = start;
        Single<List<NewsItem>> single = null;

        if(mKeyword.trim().length() != 0){
            single = Manager.I.fetchNews(mPageNo, 20, mCategory, mKeyword);
            single.subscribe(new Consumer<List<NewsItem>>() {
                @Override
                public void accept(List<NewsItem> newsItems) throws Exception {
                    mView.onSuccess(true);
                    mLoading = false;
                    if(mPageNo == 1){
                        mView.setNewsList(newsItems);
                    }
                    else{
                        mView.appendNewsList(newsItems);
                    }
                    if(mPageNo == 1 && newsItems.size()<6){
                        requireMoreNews();
                    }
                }
            });
        }
        else{
            single = Manager.I.fetchNews(mPageNo, PAGE_SIZE, mCategory, "");
            single.subscribe(new Consumer<List<NewsItem>>() {
                @Override
                public void accept(List<NewsItem> simpleNewses) throws Exception {
                    if (start != mLastFetchStart) return;
                    mLoading = false;
                    if (mPageNo > 1) {
                        mView.onSuccess(true);
                        mView.appendNewsList(simpleNewses);
                    } else {
                        mView.onSuccess(false);
                        mView.setNewsList(simpleNewses);
                    }
                }
            });
        }
    }
}
