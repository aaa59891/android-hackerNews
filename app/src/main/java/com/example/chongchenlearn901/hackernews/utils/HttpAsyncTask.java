package com.example.chongchenlearn901.hackernews.utils;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by chongchen on 2018-01-11.
 *
 * Is there a problem in this way, maybe create too many thread?
 */

public class HttpAsyncTask extends AsyncTask<String, Void, String> {
    private static final String TAG = "HttpAsyncTask";

    public interface DealWithResult{
        void action(String s);
    }

    private DealWithResult dealWithResult;

    public HttpAsyncTask(DealWithResult dealWithResult){
        this.dealWithResult = dealWithResult;
    }

    @Override
    protected void onPostExecute(String s) {
        if(this.dealWithResult == null){
            return;
        }

        this.dealWithResult.action(s);
    }

    @Override
    protected String doInBackground(String... strings) {
        if(strings == null || strings.length == 0){
            return null;
        }
        final String changeLine = "\n";
        HttpURLConnection connection = null;
        StringBuilder sb = new StringBuilder();
        try {
            URL url = new URL(strings[0]);
            connection = (HttpURLConnection) url.openConnection();
            connection.connect();
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            for(String data = reader.readLine(); data != null; data = reader.readLine()){
                sb.append(data + changeLine);
            }
        } catch (Exception e) {
            Log.e(TAG, "doInBackground: ", e);
        }finally {
            if(connection != null){
                connection.disconnect();
            }
        }
        return sb.toString();
    }
}
