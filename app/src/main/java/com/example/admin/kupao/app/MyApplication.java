package com.example.admin.kupao.app;

import android.app.Application;
import android.content.Context;

/**
 * Created by admin on 2018/2/2.
 */

public class MyApplication extends Application {

    private static Context context;

    @Override public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
    }

    public static Context getContext(){
        return context;
    }
}
