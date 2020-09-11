package com.java.yueyang.data;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.Callable;

import io.reactivex.Scheduler;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;


public class Config {
    public static String []categorys = {"news", "paper"};
    public static int category_cnt = 2;
    public static boolean CloseNews = false;
    public static boolean ClosePaper = false;

    public static List<String> availableList(){
        ArrayList<String> list = new ArrayList<>();
        if(!CloseNews)
            list.add(categorys[0]);
        if(!ClosePaper)
            list.add(categorys[1]);
        return list;
    }
    Config(Context context) {}
}
