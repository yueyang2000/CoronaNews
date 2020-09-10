package com.java.yueyang.data;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;





public class COVIDInfo {
    public COVIDInfo(){
        property = new Property();
        relationships = new ArrayList<>();
    }
    public String plain_json;
    public String hot;
    public String label;
    public URL url;
    public String image;

    public String enwiiki;
    public String baidu;
    public String zhwiki;

    public static class Relationship{
        public Relationship(){}
        public String relation;
        public URL url;
        public String label;
        public boolean foward;
    }
    public class Property{
        public Property(){
            keys = new ArrayList<>();
            values = new ArrayList<>();
        }
        public ArrayList<String> keys;
        public ArrayList<String> values;
    }
    public Property property;
    public ArrayList<Relationship> relationships;

    public String GetDescription(){
        if(!baidu.equals(""))
            return baidu;
        else if(!enwiiki.equals(""))
            return enwiiki;
        else if(!zhwiki.equals(""))
            return zhwiki;
        else
            return "";
    }
    public static COVIDInfo parseJSON(JSONObject json_info) throws IOException, JSONException {
        COVIDInfo result = new COVIDInfo();
        result.plain_json = json_info.toString();
        result.hot = json_info.optString("hot");
        result.label = json_info.optString("label");
        result.url = new URL(json_info.optString("url"));
        String img = json_info.optString("img");
        if(!img.equals("null"))
            result.image = json_info.optString("img");

        json_info = json_info.getJSONObject("abstractInfo");
        result.enwiiki = json_info.optString("enwiki");
        result.baidu = json_info.optString("baidu");
        result.zhwiki = json_info.optString("zhwiki");
        JSONObject _covid = json_info.getJSONObject("COVID");
        JSONObject _property = _covid.getJSONObject("properties");

        for (Iterator<String> it = _property.keys(); it.hasNext(); ) {
            String key = it.next();
            String value = _property.optString(key);
            result.property.values.add(value);
            result.property.keys.add(key);
        }

        JSONArray _relationships = _covid.getJSONArray("relations");
        for(int i = 0; i<_relationships.length(); i++)
        {
            Relationship relationItem = new Relationship();
            JSONObject _relation = _relationships.getJSONObject(i);
            relationItem.foward = _relation.optBoolean("forward");
            relationItem.label = _relation.optString("label");
            relationItem.relation = _relation.optString("relation");
            relationItem.url = new URL(_relation.optString("url"));
            result.relationships.add(relationItem);
        }
        return result;
    }
}


