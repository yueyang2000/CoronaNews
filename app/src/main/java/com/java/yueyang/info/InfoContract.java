package com.java.yueyang.info;

import android.os.Bundle;

import com.java.yueyang.BasePresenter;
import com.java.yueyang.BaseView;
import com.java.yueyang.data.COVIDInfo;

import java.util.List;


public interface InfoContract {

    interface View extends BaseView<Presenter> {
        void setInfo(List<COVIDInfo> list);
        void setFooterVisible(boolean flag);

    }

    interface Presenter extends BasePresenter {
        void getInfo();
        void openNewsDetailUI(COVIDInfo news, Bundle options);
        void setKeyword(String keyword);
    }
}
