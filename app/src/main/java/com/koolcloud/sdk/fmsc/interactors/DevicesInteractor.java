package com.koolcloud.sdk.fmsc.interactors;

import android.app.Activity;
import android.content.Context;

import com.koolcloud.sdk.fmsc.service.device.OnReceiveTrackListener;

import org.json.JSONObject;


/**
 * Created by admin on 2015/5/14.
 */
public interface DevicesInteractor {

    public void onStartSwiper(Context context, OnReceiveTrackListener listener);
    public void onStopSwiper();

    public void onStartReadICData(Context context, String keyIndex, String transAmountIC, OnReceiveTrackListener listener);
    public void onStopReadICData();
    public void onStartPinPad(JSONObject jsonObject, OnReceiveTrackListener listener);
}
