package com.uet.android.mouspad.Service;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.loader.content.AsyncTaskLoader;

public abstract class ServiceTaskLoader<D> extends AsyncTaskLoader<D> {
    //you will use this class to make the serviceAction does his works
    private class ConcreteAction extends  ServiceAction{

        public ConcreteAction(Context mContext, int mServiceRequestCode) {
            super(mContext, mServiceRequestCode);
        }
    }
    private ConcreteAction mConcreteAction ;
    protected D mData;

    public ServiceTaskLoader(@NonNull Context context) {
        super(context);
    }

    @Nullable
    @Override
    public D loadInBackground() {
        mConcreteAction = new ConcreteAction(getContext(),0);
        mConcreteAction.getAction();
        mConcreteAction.processAction();
        Log.d("Service", " A Service Action is call from a Context which contains a ServiceTaskLoader");
        return null;
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        //check if there's cached data, and if so, let your AsyncTaskLoader just deliver that. Otherwise, start loading.
    }

    @Override
    protected void onStopLoading() {
        mConcreteAction.processData();
        super.onStopLoading();
    }

    @Override
    protected boolean onCancelLoad() {
        return super.onCancelLoad();
    }

    @Override
    public void deliverResult(@Nullable D data) {
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
