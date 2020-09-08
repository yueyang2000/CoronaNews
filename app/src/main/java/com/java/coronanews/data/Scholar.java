package com.java.coronanews.data;

import org.json.JSONObject;

public class Scholar {
    public String plain_json;
    public String name;
    public String name_zh;
    public boolean is_passedaway = false;
    public String avatar;
    public static Scholar parseJSON(JSONObject json_scholar){
        Scholar ans = new Scholar();
        ans.plain_json = json_scholar.toString();
        ans.name = json_scholar.optString("name");
        ans.is_passedaway = json_scholar.optBoolean("is_passedaway");
        ans.avatar = json_scholar.optString("avatar");
        return ans;
    }
}
