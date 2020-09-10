package com.java.yueyang.cluster;

import android.os.Bundle;

import com.java.yueyang.BasePresenter;
import com.java.yueyang.BaseView;

import java.util.List;

public interface ClusterContract {

    interface View extends BaseView<Presenter> {


        void setCluster(List<String> list);
        void setCnt(List<Integer> list);
    }

    interface Presenter extends BasePresenter {

        void getCluster();


        void openClusterDetailUI(int position, Bundle options);
    }
}
