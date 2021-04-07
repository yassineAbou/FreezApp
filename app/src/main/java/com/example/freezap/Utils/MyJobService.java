package com.example.freezap.Utils;

import android.annotation.SuppressLint;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.widget.Toast;

/**
 * Created by Yassine Abou on 3/20/2021.
 */
@SuppressLint("NewApi")
public class MyJobService extends JobService {
    @Override
    public boolean onStartJob(JobParameters params) {
        Toast.makeText(this, "We love you so much guys...", Toast.LENGTH_SHORT).show();
        jobFinished(params, false);
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        Toast.makeText(this, "stopped", Toast.LENGTH_SHORT).show();
        return false;
    }
}
