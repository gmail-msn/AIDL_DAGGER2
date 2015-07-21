package com.koolcloud.sdk.fmsc.interactors;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.koolcloud.sdk.fmsc.domain.Constant;
import com.koolcloud.sdk.fmsc.service.transaction.OnReceiveTransactionListener;
import com.koolcloud.sdk.fmsc.util.ApmpUtil;
import com.koolcloud.sdk.fmsc.util.PreferenceUtil;
import com.koolyun.smartpos.sdk.service.ApmpService;
import com.koolyun.smartpos.sdk.service.POSPTransactionService;
import com.koolyun.smartpos.sdk.util.MessageUtil;
import com.koolyun.smartpos.sdk.util.UtilForDataStorage;

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
    private Context context;

    @Override
    public void signInPosp(Context ctx, String paymentId, String keyIndex, OnReceiveTransactionListener receiveTransactionListener) {
        JSONObject merchObj = MessageUtil.getMerchDataFromPreference(ctx);
        String merchId = merchObj.optString("merchId");
        String tId = merchObj.optString("tID");

        Log.e(TAG, "merchId:" + merchId);
        Log.e(TAG, "termId:" + tId);

        POSPTransactionService pospTransactionService = ApmpUtil.getTransactionService(ctx, merchId, tId, keyIndex);
        JSONObject signInObj = pospTransactionService.signIn(paymentId);
        receiveTransactionListener.onReceiveSignInResult(signInObj);
    }


    class ExecuteTransactionThread extends Thread {
        JSONObject transObj;
        public ExecuteTransactionThread(JSONObject transObj) {
            this.transObj = transObj;
        }

        @Override
        public void run() {
            ApmpService apmpService = ApmpService.getInstance(null);
            apmpService.setClientType(ApmpService.POS_TYPE_SMART);
            apmpService.setEnvironmentMode(ApmpService.ENV_PRODUCT);
            apmpService.setAndroidBuildSerial(android.os.Build.SERIAL);
            String merchId = PreferenceUtil.getMerchID(context);
            String termId = PreferenceUtil.getTerminalID(context);

            POSPTransactionService service = POSPTransactionService.getInstance(context, apmpService, merchId, termId, transObj.optString("brhKeyIndex"));
            JSONObject transResultObj = null;

            String transType = transObj.optString("transType");
            if (!TextUtils.isEmpty(transType) && transType.equals(Constant.APMP_TRAN_BALANCE)) {
                transResultObj = service.startGetBalance(transObj);
            } else if (!TextUtils.isEmpty(transType) && transType.equals(Constant.APMP_TRAN_SUPER_TRANSFER)) {
                transResultObj = service.startSuperTransfer(transObj);
            } else {
                transResultObj = service.startConsumption(transObj);
            }

//            Message msg = mTransactionHandler.obtainMessage();
//            msg.obj = transResultObj;
//            msg.what = FINISH_TRANSACTION_HANDLER;
//            mTransactionHandler.sendMessage(msg);

        }
    }
}
