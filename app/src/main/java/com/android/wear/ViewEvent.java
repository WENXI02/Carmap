package com.android.wear;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by wenxi on 2017/2/14.
 */

public class ViewEvent extends Service {

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e("TAG","ViewEventService");
        AndroidWearManager.getAndroidWearManager().close(getApplicationContext());
        return super.onStartCommand(intent, flags, startId);
    }
}
