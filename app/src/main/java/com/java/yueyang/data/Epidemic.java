package com.java.yueyang.data;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.TreeMap;
import java.util.TreeSet;

public class Epidemic {
    public static JSONObject data;
    public static TreeMap<String, TreeSet<String>> place_map = new TreeMap<>();
    public static TreeSet<String> countries = new TreeSet<>();
    public static String[] types = {"CONFIRMED","SUSPECTED","CURED","DEAD","SEVERE","RISK","inc24"};
    public static boolean loaded =  false;
    public static ArrayList<String> getData(String country, String provience, int type) {
        ArrayList<String> answer = new ArrayList<>();
        String key = (provience=="all")? country: country+"|"+provience;
        try{
            JSONObject j = data.getJSONObject(key);
            String begin = j.getString("begin");
            JSONArray arr = j.getJSONArray("data");
            for(int i=0;i<arr.length();i++){
                JSONArray arr1 = arr.getJSONArray(i);
                answer.add(arr1.getString(type));
            }
        }
        catch(JSONException e){
            return new ArrayList<String>();
        }
        return answer;
    }
    public static String getDate(String country, String provience, int type){
        String key = (provience=="all")? country: country+"|"+provience;
        try{
            JSONObject j = data.getJSONObject(key);
            return j.getString("begin");
        }
        catch(JSONException e){
            return "";
        }
    }

}
