package com.uet.android.mouspad;

import android.app.Application;
import android.content.Context;

import com.uet.android.mouspad.Ebook.EbookDatabase;

public class App extends Application {
    private EbookDatabase mEbookDatabase;
    private String dbname;
    private static App instance;
    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        mEbookDatabase = new EbookDatabase(this);
        DiscreteScrollViewOptions.init(this);
    }
    public static EbookDatabase getDB(Context context) {

        return ((App)context.getApplicationContext()).mEbookDatabase;
    }

    public static App getInstance() {
        return instance;
    }

    @Override
    public void onTerminate() {
        if (mEbookDatabase!=null) mEbookDatabase.close();
        super.onTerminate();
    }
}
