package com.uet.android.mouspad.Service;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

public abstract class ServiceAction {
    private int mServiceRequestCode;
    private Context mContext;

    // use this class to pass action you want to process (like menu_search_epub_list,update,
    //get a result from an action....
    //every fragment will connect to this class to poll data after each action

    public ServiceAction(Context mContext, int mServiceRequestCode) {
        this.mContext = mContext;
        this.mServiceRequestCode = mServiceRequestCode;
    }

    public void getAction(){
        Toast.makeText(mContext,"Service Action create", Toast.LENGTH_LONG).show();
        Log.d("Service Action", " create");
    }

    public void processAction(){
        Toast.makeText(mContext,"Service Action start", Toast.LENGTH_LONG).show();
        Log.d("Service Action", " start");
    }

    public void processData(){
        Toast.makeText(mContext,"Service Action process data", Toast.LENGTH_LONG).show();
        Log.d("Service Action", " process data");
        //process data and then the fragment contain it will process data
    }
}
