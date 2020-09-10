package com.java.yueyang.info;

import android.os.Bundle;

import com.java.yueyang.BasePresenter;
import com.java.yueyang.BaseView;
import com.java.yueyang.data.COVIDInfo;

import java.util.List;

/**
 * Created by equation on 9/12/17.
 */

public interface InfoContract {

    interface View extends BaseView<Presenter> {

        /**
         * 设置收藏列表
         * @param list 新闻列表
         */
        void setInfo(List<COVIDInfo> list);
        void setFooterVisible(boolean flag);

    }

    interface Presenter extends BasePresenter {

        /**
         * 获取收藏列表
         */
        void getInfo();


        /**
         * 打开新闻详情
         * @param news 被打开的新闻
         * @param options 过渡选项
         */
        void openNewsDetailUI(COVIDInfo news, Bundle options);
        void setKeyword(String keyword);
    }
}
