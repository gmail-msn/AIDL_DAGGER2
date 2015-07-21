
package com.koolcloud.sdk.fmsc.service.device;


import android.content.Context;
import android.os.Bundle;

import com.koolcloud.sdk.fmsc.interactors.DevicesInteractor;

import org.json.JSONObject;

import java.util.Hashtable;

public class DevicePresenterImpl implements DevicePresenter, OnReceiveTrackListener {

    private IDeviceServiceView deviceServiceView;
    private DevicesInteractor devicesInteractor;

    public DevicePresenterImpl(IDeviceServiceView deviceServiceView, DevicesInteractor devicesInteractor) {
        this.deviceServiceView = deviceServiceView;
        this.devicesInteractor = devicesInteractor;
    }

    @Override
    public void showMessage(String message) {
        deviceServiceView.showMessageInTextView(message);
    }

    @Override
    public void onStartSwiper(Context context) {
        devicesInteractor.onStartSwiper(context, this);
    }

    @Override
    public void onStopSwipter() {
        devicesInteractor.onStopSwiper();
    }

    @Override
    public void onStartReadICData(Context ctx, String keyIndex, String transAmount) {
        devicesInteractor.onStartReadICData(ctx, keyIndex, transAmount, this);
    }

    @Override
    public void onStopReadICData() {
        devicesInteractor.onStopReadICData();
    }

    @Override
    public void onStartPinPad(Context ctx, JSONObject jsonObject) {
        devicesInteractor.onStartPinPad(jsonObject, this);
    }

    @Override
    public void onFinishTransaction(JSONObject jsonObject) {
        deviceServiceView.onFinishTransaction(jsonObject);
    }

    @Override
    public void onReceiveTrackData(Hashtable<String, String> trackData) {
        deviceServiceView.receiveTrackData(trackData);
    }

    @Override
    public void onReceiveTrackDataError(int resCode, int trackIndex) {
        deviceServiceView.receiveTrackDataError(resCode, trackIndex);
    }

    @Override
    public void onReceiveICData(JSONObject transICData) {
        deviceServiceView.onReceiveICData(transICData);
    }

    @Override
    public void onReceivePinpadData(Bundle pinpadData) {
        deviceServiceView.onReceivePinpadData(pinpadData);
    }
}
