package com.java.yueyang;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;


public interface BaseView<T extends BasePresenter> {

    void setPresenter(T presenter);

    Context context();

    void start(Intent intent, Bundle options);
}
