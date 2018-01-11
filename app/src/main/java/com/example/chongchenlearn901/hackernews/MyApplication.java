package com.example.chongchenlearn901.hackernews;

import android.app.Application;

import io.realm.Realm;

/**
 * Created by chongchen on 2018-01-11.
 */

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Realm.init(this);
    }
}
