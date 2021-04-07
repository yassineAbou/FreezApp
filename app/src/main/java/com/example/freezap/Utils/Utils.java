package com.example.freezap.Utils;

import android.util.Log;

/**
 * Created by Yassine Abou on 3/15/2021.
 */
public class Utils {
    private static final String TAG = "Utils";

    public static Long executeLongActionDuring7seconds() {
        Log.e(TAG, "Long action is starting...");
        Long endTime = System.currentTimeMillis() + 7000;
        while (System.currentTimeMillis() < endTime) {
            //Loop during 7 secs hehehe...

        }

        Log.e(TAG, "Long action is finished !");

        return endTime;
    }
}
