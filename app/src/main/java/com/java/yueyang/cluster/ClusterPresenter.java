package com.java.yueyang.cluster;

import android.content.Intent;
import android.os.Bundle;

import com.java.yueyang.data.Manager;
import com.java.yueyang.data.NewsItem;
import com.java.yueyang.news.NewsDetailActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.functions.Consumer;



public class ClusterPresenter implements ClusterContract.Presenter {

    private ClusterContract.View mView;
    private int mPageNo = 1;
    private boolean mLoading = false;
    private ArrayList<JSONObject> events_list;
    private JSONObject cluster;
    public ClusterPresenter(ClusterContract.View view) {
        this.mView = view;
        view.setPresenter(this);
    }

    @Override
    public void subscribe() {
        getCluster();
    }

    @Override
    public void unsubscribe() {
        // nothing
    }

    @Override
    public void openClusterDetailUI(int position, Bundle options) {
        JSONArray arr = cluster.optJSONObject(String.valueOf(position)).optJSONArray("events");
        ArrayList<Integer> idx = transform(arr);
        JSONArray events = new JSONArray();
        String title = cluster.optJSONObject(String.valueOf(position)).optString("keywords");
        String msg = "";
        for(int i=0; i<idx.size();i++){
            String event_title = events_list.get(idx.get(i)).optString("title");
            String event_date = events_list.get(idx.get(i)).optString("date");
            msg = msg + event_title + "\n" + event_date + "\n\n";
        }
        Intent intent = new Intent(mView.context(), ClusterDetailActivity.class);
        intent.putExtra("MSG", msg);
        intent.putExtra("TITLE", title);
        mView.start(intent, options);
    }
    private JSONObject getJsonFromFile(String file_name)throws IOException, JSONException{
        InputStream is = mView.context().getAssets().open(file_name);
        int length = is.available();
        byte[] buffer = new byte[length];
        is.read(buffer);
        String content = new String(buffer);
        return new JSONObject(content);
    }
    private ArrayList<Integer> transform(JSONArray arr){
        try{
            ArrayList<Integer> list = new ArrayList<>();
            for(int i = 0;i<arr.length();i++){
                list.add(arr.optInt(i));
            }
            return list;
        }
        catch(Exception e){
            return new ArrayList<Integer>();
        }
    }


    @Override
    public void getCluster() {
        try{
            cluster = getJsonFromFile("cluster.json");
        }
        catch(IOException | JSONException e)
        {
            e.printStackTrace();
            mView.setCluster(new ArrayList<String>());
            return;
        }

        int cluster_num = 10;
        List<String> list = new ArrayList<String>();
        List<Integer> cnt_list = new ArrayList<>();
        for(int i=0; i<cluster_num;i++){
            String keywords = cluster.optJSONObject(String.valueOf(i)).optString("keywords");
            list.add(keywords);
            cnt_list.add(cluster.optJSONObject(String.valueOf(i)).optJSONArray("events").length());
        }

        try{
            JSONArray events = getJsonFromFile("events.json").getJSONArray("data");
            events_list = new ArrayList<>();
            for(int i = 0;i<events.length();i++){
                events_list.add(events.optJSONObject(i));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        mView.setCluster(list);
        mView.setCnt(cnt_list);
    }


}
