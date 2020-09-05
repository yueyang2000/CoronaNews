package com.java.coronanews.data;

import android.app.Activity;
import android.util.Log;

import com.xyzlf.share.library.bean.ShareEntity;
import com.xyzlf.share.library.interfaces.ShareConstant;
import com.xyzlf.share.library.util.ShareUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by chenyu on 2017/9/7.
 * 新闻API相关操作
 */

class API {
    private API() {
    }



    static String hostURL = "https://covid-dashborad.aminer.cn/api/events/list";
    static String[] url_type = {"all", "event", "points", "news", "paper"};

    static NewsItem GetNewsFromJson(JSONObject json_news) throws JSONException {
        JSONArray list;
        NewsItem news = new NewsItem();
        news.plain_json = json_news.toString();
        list = json_news.getJSONArray("data");

        JSONObject jdata = list.getJSONObject(0);
        news._id = jdata.optString("_id");
        news.author = "";
        list = jdata.getJSONArray("authors");
        for(int i = 0; i<list.length(); i++)
        {
            JSONObject author = list.getJSONObject(i);
            news.author = news.author + "/" +author.optString("name");
        }
        news.content = jdata.optString("content");
        news.date = jdata.optString("data");
        news.influence = jdata.optString("influence");
        news.source = jdata.optString("source");
        news.time = jdata.optString("time");
        news.title = jdata.optString("title");
        news.type = jdata.optString("type");
        news.lang = jdata.optString("lang");
        JSONObject statics = json_news.optJSONObject("pagination");
        news.page = statics.optInt("page");
        news.size = statics.optInt("size");
        news.total = statics.optInt("total");
        return news;
    }

    /**
     * @param url 网页地址
     * @return 网页内容
     */
    static String GetBodyFromURL(String url) throws IOException {
        URL cs = new URL(url);
        URLConnection urlConn = cs.openConnection();
        urlConn.setConnectTimeout(10 * 1000);
        BufferedReader in = new BufferedReader(new InputStreamReader(urlConn.getInputStream()));
        String inputLine, body = "";
        while ((inputLine = in.readLine()) != null)
            body = body + inputLine;
        in.close();
        return body;
    }

    /**
     * 测试是否可以访问
     * @param url 地址
     * @return 网络不可用是返回null
     */
    static Boolean TestConnection(String url) {
        try {
            URL cs = new URL(url);
            URLConnection conn = cs.openConnection();
            conn.connect();

            int code = 0;
            if (cs.getProtocol().toLowerCase().equals("http")) {
                code = ((HttpURLConnection)conn).getResponseCode();
            } else if (cs.getProtocol().toLowerCase().equals("https")) {
                code = ((HttpsURLConnection)conn).getResponseCode();
            } else {
                return null;
            }

            return code == 200 && !conn.getURL().toString().endsWith("error.html");
        } catch(Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static List<NewsItem> GetNews(final int pageNo, final int pageSize, final String type) throws IOException, JSONException {
        String URL_String = new String(String.format("https://covid-dashboard.aminer.cn/api/events/list?type=%s&page=%d&size=%d", type, pageNo, pageSize));
        String body = GetBodyFromURL(URL_String);
        if(body.equals("")) {
            Log.d("warning","No body found.");
            return new ArrayList<NewsItem>();
        }
        List<NewsItem> result = new ArrayList<NewsItem>();
        JSONObject allData = new JSONObject(body);
        JSONArray list = allData.getJSONArray("list");
        for (int t = 0; t < list.length(); t++) {
            JSONObject json_news = list.getJSONObject(t);
            result.add(GetNewsFromJson(json_news));
        }
        return result;
    }

    /**
     * 分享
     * @param activity 调用者
     * @param title 标题
     * @param text 文本内容
     * @param url 分享链接
     * @param imgUrl 图片链接
     */
    public static void ShareNews(Activity activity, String title, String text, String url, String imgUrl)
    {
        ShareEntity testBean = new ShareEntity(title, text);
        testBean.setUrl(url);
        testBean.setImgUrl(imgUrl);
        ShareUtil.showShareDialog(activity, testBean, ShareConstant.REQUEST_CODE);
    }
}
