package com.java.coronanews.data;

public class NewsItem {
    public static NewsItem NULL = new NewsItem();
    public String plain_json; // 原始json字符串

    public String _id;
    public String type;
    public String title;
    // public String category;
    public String date; // date and time
    public String time; // y/m/d
    public String lang;
    public String influence;
    public String content;
    public String author;
    public String source;

    public int page;
    public int size;
    public int total;

    public boolean has_read;
}