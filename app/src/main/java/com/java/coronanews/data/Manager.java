package com.java.coronanews.data;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import org.json.JSONObject;
import org.reactivestreams.Publisher;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.Callable;

import io.reactivex.Flowable;
import io.reactivex.FlowableTransformer;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by chenyu on 2017/9/8.
 * 所有数据相关操作的管理员，单例
 * 已设置subscribeOn(Schedulers.io())和observeOn(AndroidSchedulers.mainThread())
 * 网络优先
 */

public class Manager {
    public static Manager I = null;

    /**
     * 创建单例
     * @param context 上下文
     */
    public static synchronized void CreateI(Context context) {
        try {
            I = new Manager(context);
        } catch (IOException e) {
            e.printStackTrace();
            throw new AssertionError();
        }
    }

    private FS fs;
    private Config config;
    private FlowableTransformer<NewsItem, NewsItem> liftAllSimple;
    private FlowableTransformer<NewsItem, NewsItem> liftAllDetail;

    private Manager(final Context context) throws IOException {
        this.fs = new FS(context);
        this.config = new Config(context);
    }

    public Single<Boolean> waitForInit() {

        return Single.fromCallable(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                fs.waitForInit();
                return true;
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    public Config getConfig() {
        return config;
    }

    public Single<List<COVIDInfo>> fetchInfo(String keyword){
        return Flowable.fromCallable(new Callable<List<COVIDInfo>>() {
            @Override
            public List<COVIDInfo> call() throws Exception{
                try{
                    return API.GetCOVIDInfo(keyword);
                }
                catch(Exception e){
                    return new ArrayList<COVIDInfo>();
                }
            }
        }).flatMap(new Function<List<COVIDInfo>, Publisher<COVIDInfo>>() {
            @Override
            public Publisher<COVIDInfo> apply(@NonNull List<COVIDInfo> infos) throws Exception {
                return Flowable.fromIterable(infos);
            }
        }).toList().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }


    public Single<List<NewsItem>> fetchNews(final int pageNo, final int pageSize, final int category, String keyword) {
        return Flowable.fromCallable(new Callable<List<NewsItem>>() {
            @Override
            public List<NewsItem> call() throws Exception {
                try {
                    return API.GetNews(pageNo, pageSize, category);
                } catch(Exception e) {
                    return new ArrayList<NewsItem>();
                }
            }
        }).flatMap(new Function<List<NewsItem>, Publisher<NewsItem>>() {
            @Override
            public Publisher<NewsItem> apply(@NonNull List<NewsItem> simpleNewses) throws Exception {
                return Flowable.fromIterable(simpleNewses);
            }
        }).map(new Function<NewsItem, NewsItem>() {
            @Override
            public NewsItem apply(@NonNull NewsItem news) throws Exception {
                news.has_read = fs.hasRead(news._id);
                return news;
            }
        }).filter(new Predicate<NewsItem>() {
            @Override
            public boolean test(NewsItem newsItem) throws Exception {
                if(keyword.equals("")){
                    return true;
                }
                else{
                    return newsItem.title.contains(keyword);
                }
            }
        }).toList().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }



    public Single<JSONObject> fetchData() {
        return Single.fromCallable(new Callable<JSONObject>() {
            @Override
            public JSONObject call() throws Exception {
                try {
                    return API.GetEpidemicData();
                } catch(Exception e) {
                    return new JSONObject();
                }
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }


    public void touchRead(final NewsItem news) {
        Single.fromCallable(new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                if (news != null)  fs.insertRead(news);
                return new Object();
            }
        }).subscribeOn(Schedulers.io()).subscribe();
    }



    /**
     *
     * @return 收藏列表
     */

    public Single<List<NewsItem>> history() {
        return Flowable.fromCallable(new Callable<List<NewsItem>>() {
            @Override
            public List<NewsItem> call() throws Exception {
                return fs.fetchRead();
            }
        }).flatMap(new Function<List<NewsItem>, Publisher<NewsItem>>() { // 展开
            @Override
            public Publisher<NewsItem> apply(@NonNull List<NewsItem> news) throws Exception {
                return Flowable.fromIterable(news);
            }
        }).toList().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }


    public Single<List<COVIDInfo>> fetchRelatedInfo(List<String>name)
    {
        return Flowable.fromCallable(new Callable<List<COVIDInfo>>() {
            @Override
            public List<COVIDInfo> call() throws Exception{
                try{
                    return API.GetRelatedInfo(name);
                }
                catch(Exception e){
                    return new ArrayList<COVIDInfo>();
                }
            }
        }).flatMap(new Function<List<COVIDInfo>, Publisher<COVIDInfo>>() {
            @Override
            public Publisher<COVIDInfo> apply(@NonNull List<COVIDInfo> infos) throws Exception {
                return Flowable.fromIterable(infos);
            }
        }).toList().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }


    /**
     * 清空数据库缓存
     * @return 是否成功
     */
    public Single<Boolean> clean() {
        return Single.fromCallable(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                fs.dropTables();
                fs.createTables();
                return true;
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 分享
     * @param activity 调用者
     * @param title 标题
     * @param text 文本内容
     * @param url 分享链接
     * @param imgUrl 图片链接
     */

    public static void shareNews(Activity activity, String title, String text, String url, String imgUrl) {
        API.ShareNews(activity, title, text, url, imgUrl);
    }

    // ========================================================
    private class FetchRead<T extends NewsItem> implements Function<T, T> {
        @Override
        public T apply(@NonNull T t) throws Exception {
            if (t == NewsItem.NULL) return t;

            t.has_read = fs.hasRead(t._id);
            return t;
        }
    }
}
