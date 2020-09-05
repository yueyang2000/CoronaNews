package com.java.coronanews.data;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class NewsItem {
    public static NewsItem NULL = new NewsItem();
    public String plain_json; // 原始json字符串

    public String _id = "";
    public String type = "";
    public String title = "title";
    // public String category;
    public String date = ""; // date and time
    public String time = "2020"; // y/m/d
    public String lang = "";
    public String influence = "";
    public String content = "";
    public String author = "YY";
    public String source = "";

    public int page = 0;
    public int size = 0;

    public boolean has_read = false;
    public NewsItem(){}

    public static NewsItem parseJSON(JSONObject json_news) throws JSONException{
        JSONArray list;
        NewsItem news = new NewsItem();
        news.plain_json = json_news.toString();

        JSONObject jdata = json_news;
        news._id = jdata.optString("_id");
        news.author = "";
        try{
            list = jdata.getJSONArray("authors");
            for(int i = 0; i<list.length(); i++)
            {
                JSONObject author = list.getJSONObject(i);
                news.author = news.author + "/" +author.optString("name");
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }

        news.content = jdata.optString("content");
        news.date = jdata.optString("data");
        news.influence = jdata.optString("influence");
        news.source = jdata.optString("source");
        news.time = jdata.optString("time");
        news.title = jdata.optString("title");
        news.type = jdata.optString("type");
        news.lang = jdata.optString("lang");
        return news;
    }
}