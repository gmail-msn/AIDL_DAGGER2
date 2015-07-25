package com.koolcloud.sdk.fmsc.interactors.subinteractors;

import android.content.Context;

import com.koolcloud.sdk.fmsc.service.device.OnReceiveTrackListener;
import com.koolcloud.sdk.fmsc.service.transaction.OnReceiveTransactionListener;

import org.json.JSONObject;

/**
 * Created by admin on 2015/5/14.
 */
public interface TransactionInteractor {
    public void signInPosp(Context ctx, String paymentId, String keyIndex, OnReceiveTransactionListener receiveTransactionListener);
    public void onStartTransaction(Context ctx, JSONObject jsonObject, OnReceiveTrackListener receiveTransactionListener);
}
