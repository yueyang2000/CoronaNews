package com.java.yueyang.scholar;

import android.os.Bundle;

import com.java.yueyang.BasePresenter;
import com.java.yueyang.BaseView;
import com.java.yueyang.data.Scholar;

import java.util.List;


public interface ScholarListContract {

    interface View extends BaseView<Presenter> {

        void setScholar(List<Scholar> list);

        void onSuccess(boolean loadCompleted);

        void onError();
    }

    interface Presenter extends BasePresenter {

        boolean isLoading();

        void openDetailUI(Scholar scholar, Bundle options);

        void getScholar();

    }
}
