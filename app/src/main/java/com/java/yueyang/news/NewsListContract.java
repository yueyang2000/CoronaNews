package com.java.yueyang.news;

import android.os.Bundle;

import com.java.yueyang.BasePresenter;
import com.java.yueyang.BaseView;
import com.java.yueyang.data.NewsItem;

import java.util.List;

public interface NewsListContract {

    interface View extends BaseView<Presenter> {

        void setNewsList(List<NewsItem> list);

        void appendNewsList(List<NewsItem> list);


        void resetItemRead(int pos, boolean has_read);

        void onSuccess(boolean loadCompleted);

        void onError();
    }

    interface Presenter extends BasePresenter {

        boolean isLoading();

        void requireMoreNews();

        void refreshNews();

        void openNewsDetailUI(NewsItem news, Bundle options);


        void fetchNewsRead(int pos);

        void setKeyword(String keyword);
    }
}
