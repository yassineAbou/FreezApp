package com.example.freezap.Controllers;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;

import com.example.freezap.Utils.MyAlarmReceiver;
import com.example.freezap.Utils.MyAsyncTask;
import com.example.freezap.Utils.MyAsyncTaskLoader;
import com.example.freezap.Utils.MyHandlerThread;
import com.example.freezap.Utils.MyJobService;
import com.example.freezap.Utils.Utils;
import com.example.freezap.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity implements MyAsyncTask.Listeners,
        LoaderManager.LoaderCallbacks<Long> {
    //Implements the MyAsyncTask callback methods
    //Implementation of loader callbacks

    private ActivityMainBinding mainActivity;

    //Declaring a HandlerThread
    private MyHandlerThread mHandlerThread;

    //Create static task id that will identify our loader
    private static int TASK_ID = 100;

    private static final String TAG = "MainActivity";

    //Creating an intent to execute our broadcast
    private PendingIntent pendingIntent;

    private static int JOBSCHEDULER_ID = 200;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       mainActivity = ActivityMainBinding.inflate(getLayoutInflater());
        View view = mainActivity.getRoot();
        setContentView(view);

        //Configure Handler Thread
        this.configureHandlerThread();

        //Try to resume possible loading AsyncTask
        this.resumeAsyncTaskLoaderIfPossible();

        //Configuring The AlarmManager
        this.configureAlarmManager();

        

    }

    @Override
    protected void onDestroy() {
        //QUIT HANDLER THREAD (Free precious resources)
      mHandlerThread.quit();
        super.onDestroy();
    }

    // ------------
    // ACTIONS
    // ------------


    public void onClickButton(View v) {
        int buttonTag = Integer.valueOf(v.getTag().toString());
        switch (buttonTag) {
            case 10: // CASE USER CLICKED ON BUTTON "EXECUTE ACTION IN MAIN THREAD"
                Utils.executeLongActionDuring7seconds();
                break;
            case 20:
                this.startHandlerThread();
                break;
            case 30: // CASE USER CLICKED ON BUTTON "START ALARM"
               this.startAlarm();
            break;
            case 40: // CASE USER CLICKED ON BUTTON "STOP ALARM"
               this.stopAlarm();
                break;
            case 50: // CASE USER CLICKED ON BUTTON "EXECUTE JOB SCHEDULER"
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    this. startJobScheduler();
                }
                break;
            case 60:
               this.startAsyncTask();
                break;
            case 70:
                this.startAsyncTaskLoader();
                break;
        }
    }


    // -----------------
    // CONFIGURATION
    // -----------------

    private void configureHandlerThread() {
        mHandlerThread = new MyHandlerThread("MyAwesomeHandlerThread", mainActivity.activityMainProgressBar);
    }

    // 2 - Configuring the AlarmManager
    private void configureAlarmManager(){
        Intent alarmIntent = new Intent(MainActivity.this, MyAlarmReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(MainActivity.this,
                0, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    // -------------------------------------------
    // BACKGROUND TASK (HandlerThread & AsyncTask)
    // -------------------------------------------

    //EXECUTE HANDLER THREAD
    private void startHandlerThread() {
        mHandlerThread.startHandler();
    }

    //We create and start our AsyncTask
    private void startAsyncTask() {
        new MyAsyncTask(this).execute();
    }


    //Override methods of callback
    @Override
    public void onPreExecute() {
        //We update our UI before task (starting ProgressBar)
        this.updateUIBeforeTask();
    }

    @Override
    public void doInBackground() {

    }

    @Override
    public void onPostExecute(Long taskEnd) {
        //We update our UI before task (stopping ProgressBar)
        this.updateUIAfterTask(taskEnd);
    }

    //Start a new AsyncTaskLoader
    private void startAsyncTaskLoader(){
        getSupportLoaderManager().restartLoader(TASK_ID, null, this);
    }

    //Resume previous AsyncTaskLoader if still running
    private void resumeAsyncTaskLoaderIfPossible(){
        if (getSupportLoaderManager().getLoader(TASK_ID) != null && getSupportLoaderManager().getLoader(TASK_ID).isStarted()) {
            getSupportLoaderManager().initLoader(TASK_ID, null, this);
            this.updateUIBeforeTask();
        }
    }

    //Implements callback methods

    @NonNull
    @Override
    public Loader<Long> onCreateLoader(int id, @Nullable Bundle args) {
        Log.e("TAG", "On Create !");
        this.updateUIBeforeTask();
        return new MyAsyncTaskLoader(this); // 5 - Return a new AsyncTaskLoader
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Long> loader, Long data) {
        Log.e("TAG", "On Finished !");
        loader.stopLoading(); // 6 - Force loader to stop
        updateUIAfterTask(data);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Long> loader) {

    }


    // -----------------
    // UPDATE UI
    // -----------------

    private void updateUIBeforeTask() {
        mainActivity.activityMainProgressBar.setVisibility(View.VISIBLE);
    }

    private void updateUIAfterTask(Long taskEnd) {
        mainActivity.activityMainProgressBar.setVisibility(View.GONE);
        Toast.makeText(this, "Task is finally finished at : "
                +taskEnd+" !", Toast.LENGTH_LONG).show();
    }

    // ---------------------------------------------
    // SCHEDULE TASK (AlarmManager & JobScheduler)
    // ---------------------------------------------

    //Start Alarm
    private void startAlarm() {
        AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        manager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,0,
               AlarmManager.INTERVAL_FIFTEEN_MINUTES, pendingIntent);
        Toast.makeText(this, "Alarm set !", Toast.LENGTH_SHORT).show();
    }

    //Stop Alarm
    private void stopAlarm() {
        AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        manager.cancel(pendingIntent);
        Toast.makeText(this, "Alarm Canceled !", Toast.LENGTH_SHORT).show();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void startJobScheduler() {
        ComponentName cn = new ComponentName(getBaseContext(), MyJobService.class);
        JobInfo info;

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.N) {
            info = new JobInfo.Builder(JOBSCHEDULER_ID, cn)
                    .setPeriodic(10000)
                    .build();
        }

        else {
            info = new JobInfo.Builder(JOBSCHEDULER_ID, cn)
                    .setMinimumLatency(10000)
                    .build();
        }

        JobScheduler scheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
        scheduler.schedule(info);
    }


}