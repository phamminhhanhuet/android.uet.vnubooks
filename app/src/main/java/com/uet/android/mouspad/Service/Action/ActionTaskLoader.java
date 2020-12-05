package com.uet.android.mouspad.Service.Action;

import android.content.Context;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.uet.android.mouspad.Model.InformationAction;
import com.uet.android.mouspad.Service.ServiceTaskLoader;

public class ActionTaskLoader extends ServiceTaskLoader<InformationAction> {


    private NotificationAction notificationAction;
    private Context context ;
    public ActionTaskLoader(@NonNull Context context, NotificationAction notificationAction) {
        super(context);
        this.context = context;
        this.notificationAction = notificationAction;
    }
    public ActionTaskLoader(@NonNull Context context, InformationAction informationAction) {
        super(context);
        this.mData = informationAction;
        this.context = context;
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Nullable
    @Override
    public InformationAction loadInBackground() {
        //do smt here
        return null;
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        //check if there's cached data, and if so, let your AsyncTaskLoader just deliver that. Otherwise, start loading.
    }

    @Override
    protected void onStopLoading() {
        super.onStopLoading();
    }

    @Override
    protected boolean onCancelLoad() {
        return super.onCancelLoad();
    }

    @Override
    public void deliverResult(@Nullable InformationAction data) {
        //so that you save your fetched data in your cache first, before you call the superclass's implementation of deliverResult().
        super.deliverResult(data);
    }


    @Override
    public void reset() {
        super.reset();
        //ensure the loader is stop
        onStopLoading();
        mData = null;
    }
}
