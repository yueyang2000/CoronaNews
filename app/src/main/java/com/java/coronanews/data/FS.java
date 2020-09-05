package com.java.coronanews.data;

import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;


class FS {
    private SQLiteDatabase db, sdb;
    private String db_path, sdb_path;
    private Thread sdb_thread, word_pv_thread, picture_list_thread;
    private Map<String, Integer> word_pv;
    private Random rand = new Random();

    private static final String TABLE_NAME_SIMPLE = "news_simple";
    private static final String TABLE_NAME_DETAIL = "news_detail";
    private static final String TABLE_NAME_READ = "news_read";
    private static final String TABLE_NAME_WORD = "words";

    private static final String KEY_ID = "news_id"; // string
    private static final String KEY_SIMPLE = "simple_json"; // text
    private static final String KEY_CATEGORY = "category"; // integer
    private static final String KEY_DETAIL = "detail_json"; // text
    private static final String KEY_PICTURE = "picture_url"; // text
    private static final String KEY_WORD = "word"; // text
    private static final String KEY_VALUE = "value"; // boolean

    FS(Context context) throws IOException {
        db_path = context.getFilesDir().getPath() + "/data.db";
        db = SQLiteDatabase.openOrCreateDatabase(db_path, null);

        createSDBThread(context);
        createWordPVThread(context);

        // dropTables(); // FIXME
        createTables();
    }

    void waitForInit() throws InterruptedException {
        if (sdb_thread != null && sdb_thread.isAlive()) sdb_thread.join();
        if (word_pv_thread != null && word_pv_thread.isAlive()) word_pv_thread.join();
    }

    Map<String, Integer> getWordPV() throws InterruptedException {
        if (word_pv_thread != null && word_pv_thread.isAlive()) word_pv_thread.join();
        return word_pv;
    }

    private void createSDBThread(final Context context) {
        sdb_thread = new Thread(new Runnable() {
            @Override
            public void run() {
                sdb_path = context.getFilesDir().getPath() + "/sdata.db";

                InputStream input = null;
                OutputStream output = null;
                try {
                    input = context.getAssets().open("sample.db");
                    output = new FileOutputStream(sdb_path);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                byte[] buffer = new byte[1024];
                int length;
                try {
                    while ((length = input.read(buffer))>0) {
                        output.write(buffer,0,length);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                try {
                    input.close();
                    output.flush();
                    output.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                sdb = SQLiteDatabase.openOrCreateDatabase(sdb_path, null);
            }
        });
        sdb_thread.start();
    }

    private void createWordPVThread(final Context context) {
        word_pv_thread = new Thread(new Runnable() {
            @Override
            public void run() {
                word_pv = new TreeMap<>();
                long start = System.currentTimeMillis();
                try {
                    BufferedReader in = new BufferedReader(new InputStreamReader(context.getAssets().open("word_pv")));
                    String line = in.readLine();
                    while(line != null) {
                        line = line.trim();
                        String[] items = line.split(" ");
                        if (items.length == 2) {
                            word_pv.put(items[0], Integer.parseInt(items[1]));
                        } else {
                            Log.e("ERROR ON word_pv", line);
                        }
                        line = in.readLine();
                    }
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                System.out.println("createWordPVThread | " + (System.currentTimeMillis() - start) + " | " + word_pv.size());
            }
        });
        word_pv_thread.start();
    }



    void createTables() {
        final String category_table = String.format("CREATE TABLE IF NOT EXISTS `%s`(%s string, %s integer, %s text, PRIMARY KEY(%s, %s))",
                TABLE_NAME_SIMPLE, KEY_ID, KEY_CATEGORY, KEY_SIMPLE, KEY_ID, KEY_CATEGORY);
        db.execSQL(category_table);

        final String detail_table = String.format("CREATE TABLE IF NOT EXISTS `%s`(%s string primary key, %s text)",
                TABLE_NAME_DETAIL, KEY_ID, KEY_DETAIL);
        db.execSQL(detail_table);

        final String read_table = String.format("CREATE TABLE IF NOT EXISTS `%s`(%s string primary key, %s text)",
                TABLE_NAME_READ, KEY_ID, KEY_DETAIL);
        db.execSQL(read_table);

        final String word_table = String.format("CREATE TABLE IF NOT EXISTS `%s`(%s text primary key, %s boolean)",
                TABLE_NAME_WORD, KEY_WORD, KEY_VALUE);
        db.execSQL(word_table);
    }

    void dropTables() {
        db.execSQL(String.format("DROP TABLE IF EXISTS `%s`", TABLE_NAME_SIMPLE));
        db.execSQL(String.format("DROP TABLE IF EXISTS `%s`", TABLE_NAME_DETAIL));
        db.execSQL(String.format("DROP TABLE IF EXISTS `%s`", TABLE_NAME_READ));
        db.execSQL(String.format("DROP TABLE IF EXISTS `%s`", TABLE_NAME_WORD));
    }

    void insertSimple(NewsItem simpleNews, int category) {
        String cmd = String.format("INSERT OR REPLACE INTO `%s`(%s, %s, %s) VALUES(%s, %s, %s)",
                TABLE_NAME_SIMPLE,
                KEY_ID, KEY_SIMPLE, KEY_CATEGORY,
                DatabaseUtils.sqlEscapeString(simpleNews._id),
                DatabaseUtils.sqlEscapeString(simpleNews.plain_json),
                String.valueOf(category));
        db.execSQL(cmd);
    }

    List<NewsItem> fetchSimple(int pageNo, int pageSize, int category) throws JSONException {
        String cmd = String.format("SELECT * FROM `%s` WHERE %s=%s ORDER BY %s ASC LIMIT %s OFFSET %s",
                TABLE_NAME_SIMPLE, KEY_CATEGORY, String.valueOf(category), KEY_ID, String.valueOf(pageSize), String.valueOf(pageSize*pageNo-pageSize));
        Cursor cursor = db.rawQuery(cmd, null);
        List<NewsItem> list = new ArrayList<NewsItem>();
        while(cursor.moveToNext()) {
            list.add(API.GetNewsFromJson(new JSONObject(cursor.getString(cursor.getColumnIndex(KEY_SIMPLE))), true));
        }
        cursor.close();
        return list;
    }

    void insertDetail(NewsItem detailNews) {
        String cmd = String.format("INSERT OR REPLACE INTO `%s`(%s, %s) VALUES(%s, %s)",
                TABLE_NAME_DETAIL, KEY_ID, KEY_DETAIL,
                DatabaseUtils.sqlEscapeString(detailNews._id),
                DatabaseUtils.sqlEscapeString(detailNews.plain_json));
        db.execSQL(cmd);
    }

    NewsItem fetchDetail(String news_ID) throws JSONException {
        String cmd = String.format("SELECT * FROM `%s` WHERE %s=%s",
                TABLE_NAME_DETAIL, KEY_ID, DatabaseUtils.sqlEscapeString(news_ID));
        Cursor cursor = db.rawQuery(cmd, null);
        NewsItem detailNews = null;
        if (cursor.moveToFirst()) {
            detailNews = API.GetNewsFromJson(new JSONObject(cursor.getString(cursor.getColumnIndex(KEY_DETAIL))), true);
        }
        cursor.close();
        return detailNews;
    }

    void insertRead(String news_ID, NewsItem news) {
        String cmd = String.format("INSERT OR REPLACE INTO `%s`(%s,%s) VALUES(%s,%s)",
                TABLE_NAME_READ, KEY_ID, KEY_DETAIL,
                DatabaseUtils.sqlEscapeString(news_ID),
                DatabaseUtils.sqlEscapeString(news.plain_json));
        db.execSQL(cmd);
    }

    boolean hasRead(String news_ID) {
        String cmd = String.format("SELECT * FROM `%s` WHERE %s=%s",
                TABLE_NAME_READ, KEY_ID, DatabaseUtils.sqlEscapeString(news_ID));
        Cursor cursor = db.rawQuery(cmd, null);
        boolean read = cursor.moveToFirst();
        cursor.close();
        return read;
    }

    List<NewsItem> fetchRead() throws JSONException {
        String cmd = String.format("SELECT * FROM `%s`",
                TABLE_NAME_READ);
        Cursor cursor = db.rawQuery(cmd, null);
        List<NewsItem> results = new ArrayList<NewsItem>();
        while(cursor.moveToNext()) {
            results.add(API.GetDetailNewsFromJson(new JSONObject(cursor.getString(cursor.getColumnIndex(KEY_DETAIL))), true));
        }
        cursor.close();
        return results;
    }

    void insertFavorite(String news_ID, NewsItem news) {
        String cmd = String.format("INSERT OR REPLACE INTO `%s`(%s, %s) VALUES(%s, %s)",
                TABLE_NAME_FAVORITE, KEY_ID, KEY_DETAIL,
                DatabaseUtils.sqlEscapeString(news_ID),
                DatabaseUtils.sqlEscapeString(news.plain_json));
        db.execSQL(cmd);
    }

    void removeFavorite(String news_ID) {
        String cmd = String.format("DELETE FROM `%s` WHERE %s=%s",
                TABLE_NAME_FAVORITE, KEY_ID,
                DatabaseUtils.sqlEscapeString(news_ID));
        db.execSQL(cmd);
    }

    boolean isFavorite(String news_ID) {
        String cmd = String.format("SELECT * FROM `%s` WHERE %s=%s",
                TABLE_NAME_FAVORITE, KEY_ID, DatabaseUtils.sqlEscapeString(news_ID));
        Cursor cursor = db.rawQuery(cmd, null);
        boolean favorite = cursor.moveToFirst();
        cursor.close();
        return favorite;
    }

    List<NewsItem> fetchFavorite() throws JSONException {
        String cmd = String.format("SELECT * FROM `%s`", TABLE_NAME_FAVORITE);
        Cursor cursor = db.rawQuery(cmd, null);
        List<NewsItem> list = new ArrayList<NewsItem>();
        while(cursor.moveToNext()) {
            list.add(API.GetDetailNewsFromJson(new JSONObject(cursor.getString(cursor.getColumnIndex(KEY_DETAIL))), true));
        }
        cursor.close();
        Collections.reverse(list);
        return list;
    }



    List<NewsItem> fetchAllFromSampleNotRead() throws JSONException, InterruptedException {
        if (sdb_thread.isAlive()) sdb_thread.join();

        String cmd = String.format("SELECT * FROM `%s`", TABLE_NAME_SIMPLE);
        Cursor cursor = sdb.rawQuery(cmd, null);
        Map<String, String> m = new HashMap<String, String>();
        while(cursor.moveToNext()) {
            m.put(cursor.getString(cursor.getColumnIndex(KEY_ID)), cursor.getString(cursor.getColumnIndex(KEY_SIMPLE)));
        }
        cursor.close();

        cmd = String.format("SELECT %s FROM `%s`", KEY_ID, TABLE_NAME_READ);
        cursor = db.rawQuery(cmd, null);
        while(cursor.moveToNext()) {
            if (m.containsKey(cursor.getString(cursor.getColumnIndex(KEY_ID))))
                m.remove(cursor.getString(cursor.getColumnIndex(KEY_ID)));
        }
        cursor.close();

        List<NewsItem> results = new ArrayList<NewsItem>();
        for(String news_ID: m.keySet()) {
            NewsItem news = new NewsItem();
            news._id = news_ID;
            news.loadKeywords(m.get(news_ID));
            results.add(news);
        }
        return results;
    }

    NewsItem fetchDetailFromSample(String news_ID) throws InterruptedException, JSONException {
        if (sdb_thread.isAlive()) sdb_thread.join();

        String cmd = String.format("SELECT * FROM `%s` WHERE %S=%s",
                TABLE_NAME_DETAIL, KEY_ID, DatabaseUtils.sqlEscapeString(news_ID));
        Cursor cursor = sdb.rawQuery(cmd, null);
        NewsItem news = null;
        if (cursor.moveToFirst()) {
            news = API.GetDetailNewsFromJson(new JSONObject(cursor.getString(cursor.getColumnIndex(KEY_DETAIL))), true);
        }
        cursor.close();
        return news;
    }

}
