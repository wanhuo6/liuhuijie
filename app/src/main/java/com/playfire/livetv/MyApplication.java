package com.playfire.livetv;

import android.app.Application;

/**
 * Created on 2016-11-17.
 *
 * @author LiuHuiJie
 */
public class MyApplication extends Application {

    private static MyApplication mMyApplication;

    public static MyApplication getApplication() {
        return mMyApplication;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mMyApplication = this;
    }



}
