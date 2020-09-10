package com.java.yueyang.data;

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
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

class API {
    private API() {
    }



    static String hostURL = "https://covid-dashborad.aminer.cn/api/events/list";
    static String[] url_type = {"all", "event", "points", "news", "paper"};

    static NewsItem GetNewsFromJson(JSONObject json_news) throws JSONException {
        return NewsItem.parseJSON(json_news);
    }


    public static List<COVIDInfo> GetCOVIDInfo(final String entity) throws IOException, JSONException{
        String URL_String = new String(String.format("https://innovaapi.aminer.cn/covid/api/v1/pneumonia/entityquery?entity=%s", entity));
        String body = GetBodyFromURL(URL_String,false);
        if(body.equals("")) {
            Log.d("warning","No body found.");
            return new ArrayList<COVIDInfo>();
        }
        List<COVIDInfo> result = new ArrayList<>();
        JSONObject allData = new JSONObject(body);
        if(!allData.optString("msg").equals("success"))
            return new ArrayList<COVIDInfo>();
        JSONArray list = allData.getJSONArray("data");
        for (int t = 0; t < list.length(); t++) {
            JSONObject json_info = list.getJSONObject(t);
            result.add(COVIDInfo.parseJSON(json_info));
        }
        return result;
    }

    public static List<Scholar> GetScholar() throws IOException, JSONException{
        String URL_String = new String("https://innovaapi.aminer.cn/predictor/api/v1/valhalla/highlight/get_ncov_expers_list?v=2");
        String body = GetBodyFromURL(URL_String, false);
        JSONObject obj = new JSONObject(body);
        JSONArray arr = obj.getJSONArray("data");
        ArrayList<Scholar> result = new ArrayList<>();
        for(int i = 0; i < arr.length(); i++){
            JSONObject json_scolar = arr.getJSONObject(i);
            result.add(Scholar.parseJSON(json_scolar));
        }
        return result;
    }

//    public static List<COVIDInfo> GetRelatedInfo(List<String> name)  throws IOException, JSONException{
//        List<COVIDInfo> result = new ArrayList<>();
//        for(int i = 0; i<name.size(); i++)
//        {
//            String entity = name.get(i);
//            String URL_String = new String(String.format("https://innovaapi.aminer.cn/covid/api/v1/pneumonia/entityquery?entity=%s", entity));
//            String body = GetBodyFromURL(URL_String,false);
//            JSONObject allData = new JSONObject(body);
//            JSONArray list = allData.getJSONArray("data");
//            JSONObject json_news = list.getJSONObject(0);
//            result.add(COVIDInfo.parseJSON(json_news));
//        }
//        return result;
//    }

    /**
     * @param url 网页地址
     * @return 网页内容
     */
    static String GetBodyFromURL(String url, boolean getdata) throws IOException {
        URL cs = new URL(url);
        BufferedReader in = null;
        if(!getdata){
            URLConnection urlConn = cs.openConnection();
            urlConn.setConnectTimeout(10 * 1000);
            in = new BufferedReader(new InputStreamReader(urlConn.getInputStream()));
        }
        else{
            HttpURLConnection urlConn = (HttpURLConnection)cs.openConnection();
            //urlConn.setConnectTimeout(10 * 1000);
            urlConn.setRequestMethod("GET");
            in = new BufferedReader(new InputStreamReader(urlConn.getInputStream()));
        }
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

    public static List<NewsItem> GetNews(final int pageNo, final int pageSize, final int categiory) throws IOException, JSONException {
        List<String> available = Config.availableList();
        String URL_String = new String(String.format("https://covid-dashboard.aminer.cn/api/events/list?type=%s&page=%d&size=%d", available.get(categiory), pageNo, pageSize));
        String body = GetBodyFromURL(URL_String,false);
        if(body.equals("")) {
            Log.d("warning","No body found.");
            return new ArrayList<NewsItem>();
        }
        List<NewsItem> result = new ArrayList<NewsItem>();
        JSONObject allData = new JSONObject(body);
        JSONArray list = allData.getJSONArray("data");
        for (int t = 0; t < list.length(); t++) {
            JSONObject json_news = list.getJSONObject(t);
            result.add(GetNewsFromJson(json_news));
        }
        return result;
    }
    /**
     * 获得疫情数据内容
     */
    public static JSONObject GetEpidemicData() throws IOException, JSONException{
        String URL_String = "https://covid-dashboard.aminer.cn/api/dist/epidemic.json";
        String body = GetBodyFromURL(URL_String, true);

        if(body.equals("")) {
            Log.d("warning","No body found.");
            return new JSONObject();
        }
        return new JSONObject(body); //直接返回json文件，存储在config里
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
