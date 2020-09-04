package com.java.coronanews.news;

import android.os.Bundle;

import com.java.coronanews.data.NewsItem;
// import com.java.coronanews.news.newsdetail.NewsDetailActivity;


/**
 * Created by chenyu on 2017/9/7.
 */

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
//        Intent intent = new Intent(mView.context(), NewsDetailActivity.class);
//        intent.putExtra(NewsDetailActivity.NEWS_ID, news.news_ID);
//        intent.putExtra(NewsDetailActivity.NEWS_TITLE, news.news_Title);
//        intent.putExtra(NewsDetailActivity.NEWS_PICTURE_URL, news.picture_url);
//        intent.putExtra(NewsDetailActivity.NEWS_IS_FAVORITED, news.is_favorite);
//        mView.start(intent, options);
    }

    @Override
    public void fetchNewsRead(final int pos, NewsItem news) {
//        Manager.I.fetchDetailNews(news.news_ID)
//                .subscribe(new Consumer<DetailNews>() {
//                    @Override
//                    public void accept(DetailNews detailNews) throws Exception {
//                        mView.resetItemRead(pos, detailNews.has_read);
//                    }
//                });
    }

    private void fetchNews() {
//        final long start = System.currentTimeMillis();
//        mLoading = true;
//        mLastFetchStart = start;
//        Single<List<NewsItem>> single = null;
//        if (mKeyword.trim().length() > 0) {
//            single = Manager.I.searchNews(mKeyword, mPageNo, PAGE_SIZE, mCategory);
//        } else if (mCategory > 0) {
//            single = Manager.I.fetchSimpleNews(mPageNo, PAGE_SIZE, mCategory);
//        } else {
//            single = Manager.I.recommend();
//        }
//
//        single.subscribe(new Consumer<List<NewsItem>>() {
//            @Override
//            public void accept(List<NewsItem> simpleNewses) throws Exception {
//                System.out.println(System.currentTimeMillis() - start + " | " + mCategory + " | " + simpleNewses.size() + " | " + mPageNo);
//                if (start != mLastFetchStart) return;
//
//                mLoading = false;
//                if (mKeyword.trim().length() != 0 || mCategory > 0) {
//                    mView.onSuccess(simpleNewses.size() == 0);
//                    if (simpleNewses.size() == 1 && simpleNewses.get(0) == NewsItem.NULL) {
//                        simpleNewses.clear();
//                        requireMoreNews();
//                    }
//
//                    // TODO onError
//                    if (mPageNo == 1) mView.setNewsList(simpleNewses);
//                    else mView.appendNewsList(simpleNewses);
//
//                    if (mPageNo == 1 && simpleNewses.size() > 0 && simpleNewses.size() < 10)
//                        requireMoreNews();
//                } else {
//                    if (mPageNo > 1 || simpleNewses.size() == 0) {
//                        mView.onSuccess(true);
//                        mView.appendNewsList(new ArrayList<>());
//                    } else {
//                        mView.onSuccess(false);
//                        mView.setNewsList(simpleNewses);
//                    }
//                }
//            }
//        });
    }
}
