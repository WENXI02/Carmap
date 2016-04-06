package com.example.wenxi.carmap;

import android.app.Application;
import android.os.Build;
import android.util.Log;

import com.baidu.mapapi.SDKInitializer;
import com.example.wenxi.carmap.Fingerprint.FingerprintModule;


import dagger.ObjectGraph;

/**
 * Created by wenxi on 16/3/21.
 */
public class CarmapApplication extends Application {


    private static final String TAG = CarmapApplication.class.getSimpleName();

    private ObjectGraph mObjectGraph;
    @Override
    public void onCreate() {
        super.onCreate();
        // 在使用 SDK 各组间之前初始化 context 信息，传入 ApplicationContext
        SDKInitializer.initialize(this);

        if(Build.VERSION.SDK_INT>=23)
        {
            initObjectGraph(new FingerprintModule(this));
        }
    }
    /**
     * Initialize the Dagger module. Passing null or mock modules can be used for testing.
     *
     * @param module for Dagger
     */
    public void initObjectGraph(Object module) {
        mObjectGraph = module != null ? ObjectGraph.create(module) : null;
    }

    public void inject(Object object) {
        if (mObjectGraph == null) {
            // This usually happens during tests.
            Log.i(TAG, "Object graph is not initialized.");
            return;
        }
        mObjectGraph.inject(object);
    }

}
