package com.koolcloud.sdk.fmsc.interactors;

import android.content.Context;

import com.koolcloud.sdk.fmsc.service.transaction.OnReceiveTransactionListener;

/**
 * Created by admin on 2015/5/14.
 */
public interface TransactionInteractor {
    public void signInPosp(Context ctx, String paymentId, String keyIndex, OnReceiveTransactionListener receiveTransactionListener);
}
