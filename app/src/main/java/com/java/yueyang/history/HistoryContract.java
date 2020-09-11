package com.java.yueyang.history;

import android.os.Bundle;

import com.java.yueyang.BasePresenter;
import com.java.yueyang.BaseView;
import com.java.yueyang.data.NewsItem;

import java.util.List;


public interface HistoryContract {

    interface View extends BaseView<Presenter> {
        void setHistory(List<NewsItem> list);
    }

    interface Presenter extends BasePresenter {
        void getHistory();
        void openNewsDetailUI(NewsItem news, Bundle options);
    }
}
