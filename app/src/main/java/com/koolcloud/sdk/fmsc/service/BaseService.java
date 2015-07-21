package com.koolcloud.sdk.fmsc.service;

import android.app.IntentService;

import com.koolcloud.sdk.fmsc.App;
import com.koolcloud.sdk.fmsc.AppComponent;

/**
 * Created by admin on 2015/7/17.
 */
public abstract class BaseService extends IntentService {
    private final static String TAG = "BaseService";

    public BaseService(String name) {
        super(TAG);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        setupComponent((AppComponent) App.getApplication(this).component());
    }

    protected abstract void setupComponent(AppComponent appComponent);
}
