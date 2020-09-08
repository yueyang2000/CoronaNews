package com.java.coronanews.settings;

import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.java.coronanews.R;
import com.java.coronanews.data.Config;
import com.java.coronanews.data.Manager;
import com.java.coronanews.data.NewsItem;

import java.util.List;

import io.reactivex.Single;
import io.reactivex.functions.Consumer;
/**
 * Created by 岳 on 2017/9/12.
 */

public class SettingsFragment extends Fragment {
    private Switch mNewsSwitch;
    private Switch mPaperSwitch;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setting, container, false);
        view.findViewById(R.id.button_clear).setOnClickListener((View v) -> {
            Single<Boolean> single = Manager.I.clean();
            single.subscribe(new Consumer<Boolean>() {
                @Override
                public void accept(Boolean result) throws Exception {
                    if(result.booleanValue()) {
                        AlertDialog dialog = new AlertDialog.Builder(getContext())
                                .setTitle("您已经清空了历史记录")
                                .setMessage("    现在历史记录空空如也啦~")
                                .setPositiveButton("确定", null).create();
                        dialog.show();
                    }
                }

            });
        });
        mNewsSwitch = (Switch)view.findViewById(R.id.switch_news);
        mPaperSwitch = (Switch)view.findViewById(R.id.switch_paper);
        mPaperSwitch.setChecked(true);
        mNewsSwitch.setChecked(true);
        mNewsSwitch.setOnCheckedChangeListener((new CompoundButton.OnCheckedChangeListener()  {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(!isChecked){
                    Config.CloseNews = true;
                    mPaperSwitch.setClickable(false);

                }else {
                    Config.CloseNews = false;
                    mPaperSwitch.setClickable(true);
                }
            }
        }));

        mPaperSwitch.setOnCheckedChangeListener((new CompoundButton.OnCheckedChangeListener()  {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(!isChecked){
                    Config.ClosePaper = true;
                    mNewsSwitch.setClickable(false);

                }else {
                    Config.ClosePaper = false;
                    mNewsSwitch.setClickable(true);
                }
            }
        }));

        return view;
    }
}
