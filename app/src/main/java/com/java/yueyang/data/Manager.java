package com.java.yueyang.data;

import android.app.Activity;
import android.content.Context;

import org.json.JSONObject;
import org.reactivestreams.Publisher;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import io.reactivex.Flowable;
import io.reactivex.FlowableTransformer;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;



public class Manager {
    public static Manager I = null;

    public static synchronized void CreateI(Context context) {
        try {
            I = new Manager(context);
        } catch (IOException e) {
            e.printStackTrace();
            throw new AssertionError();
        }
    }

    private HistoryDB fs;
    private Config config;

    private Manager(final Context context) throws IOException {
        this.fs = new HistoryDB(context);
        this.config = new Config(context);
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


    //获取疫情数据
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

    public Single<List<Scholar>> getScholar(int flag){
        return Flowable.fromCallable(new Callable<List<Scholar>>() {
            @Override
            public List<Scholar> call() throws Exception{
                try{
                    return API.GetScholar();
                }
                catch(Exception e){
                    return new ArrayList<Scholar>();
                }
            }
        }).flatMap(new Function<List<Scholar>, Publisher<Scholar>>() {
            @Override
            public Publisher<Scholar> apply(@NonNull List<Scholar> scholars) throws Exception {
                return Flowable.fromIterable(scholars);
            }
        }).filter(new Predicate<Scholar>() {
            @Override
            public boolean test(Scholar scholar) throws Exception {
                if(flag == 0) {
                    return true;
                }
                else{
                    return scholar.is_passedaway;
                }
            }
        }).toList().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    public Single<Boolean> clean() {
        return Single.fromCallable(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                fs.dropTable();
                fs.createTable();
                return true;
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
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
