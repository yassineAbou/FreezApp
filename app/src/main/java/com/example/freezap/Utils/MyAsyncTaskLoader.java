package com.example.freezap.Utils;

import android.content.Context;

import androidx.loader.content.AsyncTaskLoader;

/**
 * Created by Yassine Abou on 3/19/2021.
 */
public class MyAsyncTaskLoader extends AsyncTaskLoader<Long> {

    public MyAsyncTaskLoader(Context context) {
        super(context);
    }

    @Override
    public Long loadInBackground() {
        return Utils.executeLongActionDuring7seconds();
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        forceLoad();
    }

}
