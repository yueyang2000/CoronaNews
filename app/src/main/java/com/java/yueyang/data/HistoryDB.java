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


class HistoryDB {
    private SQLiteDatabase db;
    private String db_path;
    private Random rand = new Random();

    private static final String TABLE_NAME_READ = "news_read";

    private static final String KEY_ID = "news_id"; // string
    private static final String KEY_DETAIL = "detail_json"; // text

    HistoryDB(Context context) {
        db_path = context.getFilesDir().getPath() + "/data.db";
        db = SQLiteDatabase.openOrCreateDatabase(db_path, null);
        createTable();
    }






    void createTable() {
        final String read_table = String.format("CREATE TABLE IF NOT EXISTS `%s`(%s string primary key, %s text)",
                TABLE_NAME_READ, KEY_ID, KEY_DETAIL);
        db.execSQL(read_table);
    }

    void dropTable() {
        db.execSQL(String.format("DROP TABLE IF EXISTS `%s`", TABLE_NAME_READ));
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
