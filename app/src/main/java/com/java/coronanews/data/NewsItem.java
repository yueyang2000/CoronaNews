package com.java.coronanews.data;

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
    public int total = 0;

    public boolean has_read = false;
    public NewsItem(){}
}