package com.java.yueyang;

public interface BasePresenter {

    /**
     * 启动事务
     */
    void subscribe();

    /**
     * 取消事务
     */
    void unsubscribe();
}
