package com.koolcloud.sdk.fmsc.service.device;

import android.content.Intent;
import android.os.Bundle;

import com.koolcloud.sdk.fmsc.AppComponent;
import com.koolcloud.sdk.fmsc.service.BaseService;

import org.json.JSONObject;

import java.util.Hashtable;

import javax.inject.Inject;

/**
 * Created by admin on 2015/7/17.
 */
public class DeviceService extends BaseService implements IDeviceServiceView {
    @Inject
    DevicePresenter mDevicePresenter;

    public DeviceService() {
        super("DeviceService");
    }

    public DeviceService(String name) {
        super(name);
    }

    @Override
    protected void setupComponent(AppComponent appComponent) {
        DaggerDeviceComponent.builder()
                .appComponent(appComponent)
                .deviceModule(new DeviceModule(this))
                .build()
                .inject(this);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

    }

    @Override
    public void showMessageInTextView(String message) {

    }

    @Override
    public void receiveTrackData(Hashtable<String, String> trackData) {

    }

    @Override
    public void receiveTrackDataError(int resCode, int trackIndex) {

    }

    @Override
    public void onReceiveICData(JSONObject transICData) {

    }

    @Override
    public void onReceivePinpadData(Bundle pinpadData) {

    }

    @Override
    public void onFinishTransaction(JSONObject jsonObject) {

    }
}
