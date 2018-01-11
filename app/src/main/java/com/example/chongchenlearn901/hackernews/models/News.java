package com.example.chongchenlearn901.hackernews.models;

import org.json.JSONException;
import org.json.JSONObject;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by chongchen on 2018-01-11.
 */

public class News extends RealmObject {
    private static final String TAG = "News";
    @PrimaryKey
    private int id;

    private String title;
    private String url;

    public News(){}

    public News(int id, String title, String url) {
        this.id = id;
        this.title = title;
        this.url = url;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public static News parseFromJsonString(String body) throws JSONException {
        News news = new News();
        JSONObject jsonObject = new JSONObject(body);
        news.id = jsonObject.getInt("id");
        news.title = jsonObject.getString("title");
        news.url = jsonObject.getString("url");
        return news;
    }
}
