
package com.koolcloud.sdk.fmsc.service.transaction;


import android.content.Context;

import com.koolcloud.sdk.fmsc.interactors.TransactionInteractor;

import org.json.JSONObject;

public class TransactionPresenterImpl implements TransactionPresenter, OnReceiveTransactionListener {

    private ITransactionService transactionServiceView;
    private TransactionInteractor transactionInteractor;

    public TransactionPresenterImpl(ITransactionService transactionService, TransactionInteractor transactionInteractor) {
        this.transactionServiceView = transactionService;
        this.transactionInteractor = transactionInteractor;
    }

    @Override
    public void signInPosp(Context ctx, String paymentId, String keyIndex) {
        transactionInteractor.signInPosp(ctx, paymentId, keyIndex, this);
    }

    @Override
    public void onStartTransaction(Context ctx, JSONObject jsonObject) {
//        transactionInteractor.onStartTransaction(ctx, jsonObject, this);
    }

    @Override
    public void onFinishTransaction(JSONObject jsonObject) {
        transactionServiceView.onFinishTransaction(jsonObject);
    }

    @Override
    public void onReceiveSignInResult(JSONObject jsonObject) {
        transactionServiceView.onReceiveSignInResult(jsonObject);
    }

}
