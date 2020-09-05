package com.java.coronanews.data;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

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
    private AC_AutoMaton ac;
    private Thread ac_thread;
    private FlowableTransformer<NewsItem, NewsItem> liftAllSimple;
    private FlowableTransformer<NewsItem, NewsItem> liftAllDetail;

    private Manager(final Context context) throws IOException {
        this.fs = new FS(context);
        this.config = new Config(context);
        this.ac = new AC_AutoMaton();
        this.liftAllSimple = new FlowableTransformer<NewsItem, NewsItem>() {
            @Override
            public Publisher<NewsItem> apply(@NonNull Flowable<NewsItem> upstream) {
                return upstream
                        .map(new FetchRead<NewsItem>());
            }
        };
        this.liftAllDetail = new FlowableTransformer<NewsItem, NewsItem>() {
            @Override
            public Publisher<NewsItem> apply(@NonNull Flowable<NewsItem> upstream) {
                return upstream
                        .map(new Function<NewsItem, NewsItem>() {
                            @Override
                            public NewsItem apply(@NonNull NewsItem news) throws Exception {
                                if (news == NewsItem.NULL) return news;
                                fs.insertDetail(news);
                                return news;
                            }
                        })
                        .map(new FetchRead<NewsItem>());
            }
        };
    }

    public Single<Boolean> waitForInit() {
        ac_thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    for(String key: fs.getWordPV().keySet()) {
                        ac.add(key, fs.getWordPV().get(key));
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                ac.fix();
            }
        });
        ac_thread.start();

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


    public Single<List<NewsItem>> fetchSimpleNews(final int pageNo, final int pageSize, final String type) {
        return Flowable.fromCallable(new Callable<List<NewsItem>>() {
            @Override
            public List<NewsItem> call() throws Exception {
                try {
                    return API.GetNews(pageNo, pageSize, type);
                } catch(Exception e) {
                    return new ArrayList<NewsItem>();
                }
            }
        }).flatMap(new Function<List<NewsItem>, Publisher<NewsItem>>() {
            @Override
            public Publisher<NewsItem> apply(@NonNull List<NewsItem> simpleNewses) throws Exception {
                if (simpleNewses.size() > 0) return Flowable.fromIterable(simpleNewses);
                return Flowable.fromIterable(fs.fetchSimple(pageNo, pageSize, type));
            }
        }).map(new Function<NewsItem, NewsItem>() {
            @Override
            public NewsItem apply(@NonNull NewsItem news) throws Exception {
                fs.insertSimple(news, category);
                return news;
            }
        }).compose(this.liftAllSimple).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 获取新闻
     * @param news_ID
     * @return 如果成功，则返回新闻对象，否则返回DetailNews.NULL
     */
    public Single<NewsItem> fetchDetailNews(final String news_ID) {
        return Flowable.fromCallable(new Callable<NewsItem>() {
            @Override
            public NewsItem call() throws Exception {
                NewsItem news = fs.fetchDetail(news_ID); // load from disk
                return news != null ? news : NewsItem.NULL;
            }
        }).flatMap(new Function<NewsItem, Publisher<NewsItem>>() {
            @Override
            public Publisher<NewsItem> apply(@NonNull NewsItem detailNews) throws Exception {
                if (detailNews != NewsItem.NULL) return Flowable.just(detailNews);
                try {
                    return Flowable.just(API.GetDetailNews(news_ID)); // load from web
                } catch (Exception e) {
                    return Flowable.just(NewsItem.NULL);
                }
            }
        }).compose(this.liftAllDetail).firstOrError().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 搜索新闻
     * @param keyword
     * @param pageNo
     * @param pageSize
     * @param category
     * @return
     */
    public Single<List<NewsItem>> searchNews(final String keyword, final int pageNo, final int pageSize, final int category) {
        return Flowable.fromCallable(new Callable<List<NewsItem>>() {
            @Override
            public List<NewsItem> call() throws Exception {
                try {
                    return API.SearchNews(keyword, pageNo, pageSize, category);
                } catch(Exception e) {
                    e.printStackTrace();
                    return new ArrayList<NewsItem>();
                }
            }
        }).flatMap(new Function<List<NewsItem>, Publisher<? extends NewsItem>>() {
            @Override
            public Publisher<? extends NewsItem> apply(@NonNull List<NewsItem> simpleNewses) throws Exception {
                return Flowable.fromIterable(simpleNewses);
            }
        }).map(new Function<NewsItem, NewsItem>() {
            @Override
            public NewsItem apply(@NonNull NewsItem simpleNews) throws Exception {
                simpleNews.from_search = true;
                return simpleNews;
            }
        }).compose(this.liftAllSimple).toList().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 添加已读
     * @param news_ID
     */
    public void touchRead(final String news_ID) {
        Single.fromCallable(new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                NewsItem news = fs.fetchDetail(news_ID);
                try {
                    if (news == null) news = API.GetDetailNews(news_ID);
                } catch(Exception e) {

                }
                if (news != null)  fs.insertRead(news_ID, news);
                return new Object();
            }
        }).subscribeOn(Schedulers.io()).subscribe();
    }



    /**
     *
     * @return 收藏列表
     */
    public Single<List<NewsItem>> favorites() {
        return Flowable.fromCallable(new Callable<List<NewsItem>>() {
            @Override
            public List<NewsItem> call() throws Exception {
                return fs.fetchFavorite();
            }
        }).flatMap(new Function<List<NewsItem>, Publisher<NewsItem>>() { // 展开
            @Override
            public Publisher<NewsItem> apply(@NonNull List<NewsItem> news) throws Exception {
                return Flowable.fromIterable(news);
            }
        }).compose(this.liftAllSimple).toList().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
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
