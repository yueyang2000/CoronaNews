package com.java.coronanews.data;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


class Relationship{
    public Relationship(){}
    public static Relationship newInstance(){
        return new Relationship();
    }
    public String relation;
    public URL url;
    public String label;
    public boolean foward;
}


public class COVIDInfo {
    public COVIDInfo(){
        property = new Property();
        relationships = new ArrayList<>();
    }
    public String hot;
    public String label;
    public URL url;
    public URL image;

    public String enwiiki;
    public String baidu;
    public String zhwiki;

    public class Property{
        public Property(){}
        public String defination;
        public String feature;
        public String include;
        public String condition;
        public String transmit;
        public String application;
    }
    Property property;

    ArrayList<Relationship> relationships;
    public static COVIDInfo parseJSON(JSONObject json_info) throws IOException, JSONException {
        COVIDInfo result = new COVIDInfo();
        result.hot = json_info.optString("hot");
        result.label = json_info.optString("label");
        result.url = new URL(json_info.optString("url"));
        result.image = new URL(json_info.optString("img"));
        result.enwiiki = json_info.optString("enwiki");
        result.baidu = json_info.optString("baidu");
        result.zhwiki = json_info.optString("zhwiki");
        JSONObject _covid = json_info.getJSONObject("COVID");
        JSONObject _property = _covid.getJSONObject("properties");
        result.property.defination = _property.optString("定义");
        result.property.application = _property.optString("应用");
        result.property.feature = _property.optString("特征");
        result.property.include = _property.optString("包括");
        result.property.condition = _property.optString("生存条件");
        result.property.transmit = _property.optString("传播方式");
        JSONArray _relationships = _covid.getJSONArray("relationships");
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


