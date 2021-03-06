package com.koolcloud.sdk.fmsc.interactors.subinteractors;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.koolcloud.sdk.fmsc.domain.Constant;
import com.koolcloud.sdk.fmsc.service.device.OnReceiveTrackListener;
import com.koolcloud.sdk.fmsc.service.transaction.OnReceiveTransactionListener;
import com.koolcloud.sdk.fmsc.util.ApmpUtil;
import com.koolcloud.sdk.fmsc.util.JsonUtil;
import com.koolcloud.sdk.fmsc.util.Logger;
import com.koolcloud.sdk.fmsc.util.PreferenceUtil;
import com.koolyun.smartpos.sdk.service.ApmpService;
import com.koolyun.smartpos.sdk.service.POSPTransactionService;
import com.koolyun.smartpos.sdk.util.MessageUtil;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by admin on 2015/5/14.
 */
public class TransactionInteractorImpl implements TransactionInteractor{
    private final String TAG = "TransactionInteractorImpl";

    private final int FINISH_TRANSACTION_HANDLER = 1;
    private static final int RECEIVE_TRACK_DATA = 0;
    private static final int RECEIVE_TRACK_DATA_ERROR = 1;
    private OnReceiveTransactionListener receiveTransactionListener;
    private OnReceiveTrackListener mReceiveTrackListener;
    private Context context;

    @Override
    public void signInPosp(Context ctx, String paymentId, String keyIndex, OnReceiveTransactionListener receiveTransactionListener) {
        try {
            String merchId = PreferenceUtil.getMerchID(ctx);
            String tId = PreferenceUtil.getTerminalID(ctx);

            Logger.i("merchId:" + merchId);
            Logger.i("termId:" + tId);

            JSONObject signInParams = new JSONObject();
            signInParams.put("paymentId", paymentId);
            POSPTransactionService pospTransactionService = ApmpUtil.getTransactionService(ctx, merchId, tId, keyIndex);
            JSONObject signInObj = pospTransactionService.signIn(signInParams);
            handleResponseJsonObj(signInObj);
            receiveTransactionListener.onReceiveSignInResult(signInObj);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onStartTransaction(Context ctx, JSONObject jsonObject, OnReceiveTrackListener receiveTrackListener) {
        mReceiveTrackListener = receiveTrackListener;
        this.context = ctx;

        JSONObject transObj = JsonUtil.makeTransactionParams(ctx, jsonObject);
        new ExecuteTransactionThread(transObj).start();
    }

    @Override
    public void clearReversalData(Context ctx, OnReceiveTransactionListener receiveTransactionListener) {
        this.context = ctx;
        String merchId = PreferenceUtil.getMerchID(context);
        String termId = PreferenceUtil.getTerminalID(context);

        POSPTransactionService service = ApmpUtil.getTransactionService(context, merchId, termId, "");
        boolean result = service.clearReversalData();
        receiveTransactionListener.onReceiveClearReversalData(result);
    }

    class ExecuteTransactionThread extends Thread {
        JSONObject transObj;
        public ExecuteTransactionThread(JSONObject transObj) {
            this.transObj = transObj;
        }

        @Override
        public void run() {
            String merchId = PreferenceUtil.getMerchID(context);
            String termId = PreferenceUtil.getTerminalID(context);

            POSPTransactionService service = ApmpUtil.getTransactionService(context, merchId, termId, transObj.optString("brhKeyIndex"));

            JSONObject transResultObj = null;

            String transType = transObj.optString("transType");
            Logger.i("transaction interactor transType:" + transType);
            if (!TextUtils.isEmpty(transType) && transType.equals(Constant.APMP_TRAN_BALANCE)) {
                transResultObj = service.startGetBalance(transObj);
            } else if (!TextUtils.isEmpty(transType) && transType.equals(Constant.APMP_TRAN_SUPER_TRANSFER)) {
                transResultObj = service.startSuperTransfer(transObj);
            } else if (!TextUtils.isEmpty(transType) && transType.equals(Constant.APMP_TRAN_CONSUME)) {
                transResultObj = service.startConsumption(transObj);
            }

            handleResponseJsonObj(transResultObj);
            mReceiveTrackListener.onFinishTransaction(transResultObj);
        }
    }

    private void handleResponseJsonObj(JSONObject resObj) {
        if (null != resObj) {
            resObj.remove("respMsg");
            resObj.remove("pk_id");
            resObj.remove("apOrderId");
            resObj.remove("action");
        }
    }
}
