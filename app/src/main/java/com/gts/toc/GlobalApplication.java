package com.gts.toc;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.res.Resources;

/**
 * Created by warsono on 11/12/16.
 */

public class GlobalApplication extends Application {
    private static GlobalApplication sInstance;
    private static Activity mActivity;
    private static Context mContext;
    private static Resources mResources;

    @Override
    public void onCreate() {
        super.onCreate();

        sInstance   = this;
        mContext    = getApplicationContext();
        mContext    = getApplicationContext();
        mResources  = getResources();
    }

    public static GlobalApplication getInstance() {
        return sInstance;
    }

    public static Activity getActivity(){
        return mActivity;
    }

    public static Context getContext(){
        return mContext;
    }

    public static Resources getAppResource(){
        return mResources;
    }
}
