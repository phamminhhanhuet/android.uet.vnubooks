package com.uet.android.mouspad.Service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.uet.android.mouspad.Service.Action.NotificationAction;

import java.util.Timer;
import java.util.TimerTask;

public class ImplementService extends Service {
    public  ImplementService(){

    }
    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private NotificationAction notificationAction;
    private Context context;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //super.onStartCommand(intent, flags, startId);
        Log.d("Service", " In Service Action");
        startTimer();
        return  START_STICKY;
    }

    @Override
    public void onDestroy() {
        stoptimertask();
        super.onDestroy();
    }

    //we are going to use a handler to be able to run in our TimerTask
    final Handler handler = new Handler();
    Timer timer;
    TimerTask timerTask;
    String TAG = "Timers";
    int Your_X_SECS = 5;

    public void startTimer() {
        //set a new Timer
        timer = new Timer();

        //initialize the TimerTask's job
        initializeTimerTask();

        //schedule the timer, after the first 5000ms the TimerTask will run every 10000ms
        timer.schedule(timerTask, 5000, Your_X_SECS * 1000); //
        //timer.schedule(timerTask, 5000,1000); //
    }

    public void stoptimertask() {
        //stop the timer, if it's not already null
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    public void initializeTimerTask() {

        timerTask = new TimerTask() {
            public void run() {
                //use a handler to run a toast that shows the current timestamp
                handler.post(new Runnable() {
                    public void run() {
                        //TODO CALL NOTIFICATION FUNC
                        //not on 9/10/20: you can custom your own notification here. But I commented them, cause of you will use firebase cloud message
//                        notificationAction = NotificationAction.getInstance(getApplicationContext());
//                        //Toast.makeText(getContext(), "EnterService", Toast.LENGTH_SHORT).show();
//                        if(notificationAction != null){
//                            notificationAction.builder(getApplicationContext());
//                        }
                    }
                });
            }
        };
    }
}
