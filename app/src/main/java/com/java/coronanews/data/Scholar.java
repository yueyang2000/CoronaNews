package com.java.coronanews.data;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Iterator;

public class Scholar {
    public String plain_json;
    public String name;
    public String name_zh;
    public boolean is_passedaway = false;
    public String avatar;
    public String indices;
    public String affiliation;
    public String bio;
    public String edu;
    public String position;
    public String work;
    public String field;
    public static Scholar parseJSON(JSONObject json_scholar){

        Scholar ans = new Scholar();
        ans.plain_json = json_scholar.toString();
        ans.name = json_scholar.optString("name");
        ans.name_zh = json_scholar.optString("name_zh");
        ans.is_passedaway = json_scholar.optBoolean("is_passedaway");
        ans.avatar = json_scholar.optString("avatar");
        JSONObject ind = json_scholar.optJSONObject("indices");
        if(ind!=null){
            ans.indices = "";
            for (Iterator<String> it = ind.keys(); it.hasNext(); ) {
                String key = it.next();
                double val = ind.optDouble(key);
                ans.indices = ans.indices+key+": "+val+"\n";
            }
        }
        JSONObject profile = json_scholar.optJSONObject("profile");
        ans.affiliation = profile.optString("affiliation");
        ans.bio = profile.optString("bio");
        ans.edu = profile.optString("edu");
        ans.position = profile.optString("position");
        ans.work = profile.optString("work");
        ans.field = "";
        JSONArray tags = json_scholar.optJSONArray("tags");
        JSONArray score = json_scholar.optJSONArray("tags_score");
        if(tags!=null && score != null){
            for(int i = 0; i<tags.length();i++){
                ans.field = ans.field+tags.optString(i)+"  "+score.optInt(i)+"\n";
            }
        }

        return ans;
    }
    public String getName(){
        if(!name_zh.equals("")){
            return name_zh;
        }
        else{
            return name;
        }
    }
}
