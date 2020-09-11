package com.java.yueyang.info;

import android.content.Intent;
import android.os.Bundle;

import com.java.yueyang.data.COVIDInfo;
import com.java.yueyang.data.Manager;

import java.util.List;

import io.reactivex.functions.Consumer;


public class InfoPresenter implements InfoContract.Presenter {

    private InfoContract.View mView;
    private String mKeyword = "";

    public InfoPresenter(InfoContract.View view) {
        this.mView = view;
        view.setPresenter(this);
    }
    public void setKeyword(String keyword){
        mKeyword = keyword;
        getInfo();
    }

    @Override
    public void subscribe() {
        getInfo();
    }

    @Override
    public void unsubscribe() {
        // nothing
    }

    @Override
    public void openNewsDetailUI(COVIDInfo info, Bundle options) {
        Intent intent = new Intent(mView.context(), InfoDetailActivity.class);
        intent.putExtra("INFO", info.label);
        mView.start(intent, options);
    }

    @Override
    public void getInfo() {
        Manager.I.fetchInfo(mKeyword)
                .subscribe(new Consumer<List<COVIDInfo>>() {
                    @Override
                    public void accept(List<COVIDInfo> list) throws Exception {
                        mView.setInfo(list);
                        if(!mKeyword.equals(""))
                            mView.setFooterVisible(false);
                    }
                });
    }


}
