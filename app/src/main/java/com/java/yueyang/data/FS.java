package com.java.yueyang.data;

import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;


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
        createTables();
    }

    void waitForInit() throws InterruptedException {
    }





    void createTables() {
//        final String category_table = String.format("CREATE TABLE IF NOT EXISTS `%s`(%s string, %s integer, %s text, PRIMARY KEY(%s, %s))",
//                TABLE_NAME_SIMPLE, KEY_ID, KEY_CATEGORY, KEY_SIMPLE, KEY_ID, KEY_CATEGORY);
//        db.execSQL(category_table);
//
//        final String detail_table = String.format("CREATE TABLE IF NOT EXISTS `%s`(%s string primary key, %s text)",
//                TABLE_NAME_DETAIL, KEY_ID, KEY_DETAIL);
//        db.execSQL(detail_table);

        final String read_table = String.format("CREATE TABLE IF NOT EXISTS `%s`(%s string primary key, %s text)",
                TABLE_NAME_READ, KEY_ID, KEY_DETAIL);
        db.execSQL(read_table);

//        final String word_table = String.format("CREATE TABLE IF NOT EXISTS `%s`(%s text primary key, %s boolean)",
//                TABLE_NAME_WORD, KEY_WORD, KEY_VALUE);
//        db.execSQL(word_table);
    }

    void dropTables() {
        //db.execSQL(String.format("DROP TABLE IF EXISTS `%s`", TABLE_NAME_SIMPLE));
        //db.execSQL(String.format("DROP TABLE IF EXISTS `%s`", TABLE_NAME_DETAIL));
        db.execSQL(String.format("DROP TABLE IF EXISTS `%s`", TABLE_NAME_READ));
        //db.execSQL(String.format("DROP TABLE IF EXISTS `%s`", TABLE_NAME_WORD));
    }


    void insertRead( NewsItem news) {
        String news_ID = news._id;
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
            results.add(API.GetNewsFromJson(new JSONObject(cursor.getString(cursor.getColumnIndex(KEY_DETAIL)))));
        }
        cursor.close();
        return results;
    }
}
