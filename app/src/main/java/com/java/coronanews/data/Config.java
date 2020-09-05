package com.java.coronanews.data;

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

/**
 * Created by chenyu on 2017/9/12.
 * 配置相关
 */

public class Config {
    public static String []categorys = {"News", "Paper"};
    public static int category_cnt = 2;

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


    /**
     * 所有分类
     * @return
     */
    public List<Category> allCategories() {
        List<Category> list = new ArrayList<>();
        for(int x = 1; x < category_cnt; x ++) {
            list.add(new Category(categorys[x], x));
        }
        return list;
    }

    /**
     * 已选的分类
     * @param withFirst 是否包含第一个分类
     * @return
     */
    public List<Category> availableCategories(boolean withFirst) {
        List<Category> list = new ArrayList<>();
        for(int x: available_categories) {
            list.add(new Category(categorys[x], x));
        }
        return list;
    }

    /**
     * 未选的分类
     * @return
     */
    public List<Category> unavailableCategories() {
        List<Category> list = new ArrayList<>();
        for(Integer x = 1; x < category_cnt; x ++)
            if (!available_categories.contains(x))
                list.add(new Category(categorys[x], x));
        return list;
    }

    /**
     * 添加分类
     * @param cate 分类
     * @return 是否成功
     */
    public boolean addCategory(int cate) {
        if (!available_categories.contains(cate)) {
            available_categories.add(cate);
            saveConfig();
            return true;
        }
        return false;
    }

    /**
     * 删除分类
     * @param cate 分类
     * @return 是否成功
     */
    public boolean removeCategory(int cate) {
        if (available_categories.contains(cate)) {
            available_categories.remove(cate);
            saveConfig();
            return true;
        }
        return false;
    }

    /**
     * 切换分类的状态，已选变成未选，未选变成已选
     * @param cate
     */
    public void switchAvailable(int cate) {
        if (available_categories.contains(cate))
            available_categories.remove(cate);
        else available_categories.add(cate);
        saveConfig();
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

    void saveConfig() {

        Single.fromCallable(new Callable<Boolean>() {

            @Override
            public Boolean call() throws Exception {
                JSONObject obj = new JSONObject();
                try {
                    JSONArray available_categories_array = new JSONArray();
                    for(int x: available_categories)
                        available_categories_array.put(x);
                    obj.put("available_categories", available_categories_array);
                    OutputStreamWriter w = new OutputStreamWriter(new FileOutputStream(path));
                    w.write(obj.toString());
                    w.close();
                } catch(Exception e) {
                    e.printStackTrace();
                }
                return true;
            }
        }).subscribeOn(save_thread).subscribe();
    }
}
