
package com.koolcloud.sdk.fmsc.service.transaction;


import android.content.Context;

import com.koolcloud.sdk.fmsc.R;
import com.koolcloud.sdk.fmsc.domain.database.PaymentParamsDB;
import com.koolcloud.sdk.fmsc.domain.entity.PaymentInfo;
import com.koolcloud.sdk.fmsc.interactors.subinteractors.TransactionInteractor;
import com.koolcloud.sdk.fmsc.util.StringUtils;

import org.json.JSONException;
import org.json.JSONObject;

public class TransactionPresenterImpl implements TransactionPresenter, OnReceiveTransactionListener {

    private ITransactionService transactionServiceView;
    private TransactionInteractor transactionInteractor;

    public TransactionPresenterImpl(ITransactionService transactionService, TransactionInteractor transactionInteractor) {
        this.transactionServiceView = transactionService;
        this.transactionInteractor = transactionInteractor;
    }

    @Override
    public void signInPosp(Context ctx, String paymentId) {
        PaymentParamsDB paymentDB = PaymentParamsDB.getInstance(ctx);
        PaymentInfo paymentInfo = paymentDB.getPaymentByPaymentId(paymentId);

        if (null == paymentInfo) {
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("errorMsg", StringUtils.getResourceString(ctx, R.string.msg_payment_no_exist));
                transactionServiceView.onReceiveSignInResult(jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            String keyIndex = paymentInfo.getBrhKeyIndex();
            transactionInteractor.signInPosp(ctx, paymentId, keyIndex, this);
        }
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
