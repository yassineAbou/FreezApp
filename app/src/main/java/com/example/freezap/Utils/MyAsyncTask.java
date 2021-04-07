package com.example.freezap.Utils;

import android.os.AsyncTask;
import android.util.Log;

import java.lang.ref.WeakReference;

/**
 * Created by Yassine Abou on 3/19/2021.
 */
public class MyAsyncTask extends AsyncTask<Void, Void, Long> {

    private static final String TAG = "MyAsyncTask";

    //Implement listeners methods (Callback)
    public interface Listeners {
        void onPreExecute();
        void doInBackground();
        void onPostExecute(Long success);
    }

    //Declare callback
    private final WeakReference<Listeners> callback;

    //create constructor
    public MyAsyncTask(Listeners callback) {
        this.callback = new WeakReference<>(callback);
    }


    @Override
    protected void onPreExecute() {
        super.onPreExecute(); //Call the related callback method
        this.callback.get().onPreExecute();
        Log.e(TAG, "AsyncTask is started.");
    }

    @Override
    protected Long doInBackground(Void... voids) {
        this.callback.get().doInBackground(); //Call the related callback method
        Log.e(TAG, "AsyncTask doing some big work...");
        return Utils.executeLongActionDuring7seconds(); //Execute our task
    }

    @Override
    protected void onPostExecute(Long success) {
        super.onPostExecute(success);
        this.callback.get().onPostExecute(success); //Call the related callback method
        Log.e(TAG, "AsyncTask is finished");
    }
}
