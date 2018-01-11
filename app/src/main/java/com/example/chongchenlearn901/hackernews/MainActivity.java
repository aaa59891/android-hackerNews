package com.example.chongchenlearn901.hackernews;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.chongchenlearn901.hackernews.models.News;
import com.example.chongchenlearn901.hackernews.utils.HttpAsyncTask;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import io.realm.Realm;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    public static final String INTENT_URL = "url";
    private Realm realm;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> titles;
    private ArrayList<News> result;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.realm = Realm.getDefaultInstance();
        result = new ArrayList<>(realm.where(News.class).findAll());
        titles = new ArrayList<>();

        for(News news : result){
            titles.add(news.getTitle());
        }

        ListView listView = findViewById(R.id.listView);
        this.adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, titles);
        listView.setAdapter(this.adapter);
        listView.setOnItemClickListener((parent, view, position, id) -> {
            News news = this.result.get(position);

            Intent intent = new Intent(getApplicationContext(), WebViewActivity.class);
            intent.putExtra(INTENT_URL, news.getUrl());
            startActivity(intent);
        });

        new HttpAsyncTask(dealWithNewsId).execute("https://hacker-news.firebaseio.com/v0/topstories.json?print=pretty");

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }

    private HttpAsyncTask.DealWithResult dealWithNewsId = (body) -> {
        List<Integer> newsIds = getNewsIds(body);
        List<Integer> existIds = getExistIds();
        newsIds.removeAll(existIds);
        Log.d(TAG, "start time: " + new Date());
        for(Integer i : newsIds){
            new HttpAsyncTask(this.dealWithNewsDetail).execute(String.format("https://hacker-news.firebaseio.com/v0/item/%d.json?print=pretty", i));
        }
        Log.d(TAG, "end time: " + new Date());
    };

    private HttpAsyncTask.DealWithResult dealWithNewsDetail = (body) -> {
        try {
            News news = News.parseFromJsonString(body);
            this.realm.beginTransaction();
            this.realm.copyToRealmOrUpdate(news);
            this.realm.commitTransaction();
            this.result.add(0, news);
            this.titles.add(0, news.getTitle());
            this.adapter.notifyDataSetChanged();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    };

    private ArrayList<Integer> getNewsIds(String body){
        ArrayList<Integer> newsIds = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(body);
            for(int i = 0; i < jsonArray.length(); i++){
                newsIds.add(jsonArray.getInt(i));
            }
        } catch (Exception e) {
            Log.e(TAG, "getNewsIds had an error: ", e);
        }
        return newsIds;
    }

    private List<Integer> getExistIds(){
        if(this.realm == null){
            return Collections.emptyList();
        }
        ArrayList<Integer> data = new ArrayList<>();

        for(News news: result){
            data.add(news.getId());
        }
        return data;
    }
}
