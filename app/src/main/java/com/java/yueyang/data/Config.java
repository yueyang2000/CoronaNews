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

    public static class Category {
        public String title;
        public int idx;
        Category(String title, int idx) { this.title = title; this.idx = idx; }
    }

    private String path;

    private Scheduler save_thread = Schedulers.newThread();
    private List<Integer> available_categories;
    Config(Context context) {
        path = context.getFilesDir().getPath() + "/config.json";
        loadConfig();
    }

    private void loadConfig() {
        JSONObject obj = new JSONObject();
        try {
            Scanner in = new Scanner(new FileInputStream(path));
            String content = "";
            while(in.hasNextLine()) content = content + in.nextLine();
            in.close();
            obj = new JSONObject(content);
        } catch(Exception e) {
            e.printStackTrace();
        }
        available_categories = new ArrayList<>();
        try {
            JSONArray array = obj.getJSONArray("available_categories");
            for(int i = 0; i < array.length(); i ++)
                available_categories.add(array.getInt(i));
        } catch(Exception e) {
            for(int i = 1; i < category_cnt; i ++)
                available_categories.add(i);
        }
    }
}
