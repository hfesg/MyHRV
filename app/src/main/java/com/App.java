package com;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.Config;

/**
 * Created by xushuzhan on 2017/3/26.
 */

public class App extends Application {
    private static final String TAG = "com.App";
    private static Context context;

    public static Context getContext() {
        return context;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
        Config.init(this);
        Log.d(TAG, "onCreate: app");
    }

    @Override
    public void onTerminate() {
        context = null;
        super.onTerminate();
    }
}
