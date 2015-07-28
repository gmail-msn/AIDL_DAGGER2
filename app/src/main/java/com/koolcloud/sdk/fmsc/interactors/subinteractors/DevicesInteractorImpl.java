package com.koolcloud.sdk.fmsc.interactors.subinteractors;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.koolcloud.sdk.fmsc.domain.Constant;
import com.koolcloud.sdk.fmsc.domain.devices.CardSwiper;
import com.koolcloud.sdk.fmsc.domain.devices.PinPadManager;
import com.koolcloud.sdk.fmsc.interactors.processors.ICCardDataHandler;
import com.koolcloud.sdk.fmsc.service.device.OnReceiveTrackListener;
import com.koolcloud.sdk.fmsc.util.PreferenceUtil;
import com.koolyun.smartpos.sdk.message.parameter.EMVICManager;
import com.koolyun.smartpos.sdk.service.ApmpService;
import com.koolyun.smartpos.sdk.service.POSPTransactionService;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

/**
 * Created by admin on 2015/5/14.
 */
public class DevicesInteractorImpl implements DevicesInteractor, CardSwiper.CardSwiperListener {
    private final String TAG = "DevicesInteractorImpl";
    private final int FINISH_TRANSACTION_HANDLER = 1;
    private CardSwiper ex_cardSwiper;
    private EMVICManager emvManager = null;
    private OnReceiveTrackListener mListener;
    private static final int RECEIVE_TRACK_DATA = 0;
    private static final int RECEIVE_TRACK_DATA_ERROR = 1;
    private Context context;
    private ICCardDataHandler mICDataHandler;
    private Looper waitDataLooper;

    @Override
    public void onStartSwiper(Context context, OnReceiveTrackListener listener) {
        this.mListener = listener;
        if (ex_cardSwiper == null) {
            ex_cardSwiper = new CardSwiper();
            ex_cardSwiper.onCreate(context, this);
        }
        ex_cardSwiper.onStart();
    }

    Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case RECEIVE_TRACK_DATA:
                    Hashtable<String, String> trackData = (Hashtable) msg.obj;
                    mListener.onReceiveTrackData(trackData);
                    break;
                case RECEIVE_TRACK_DATA_ERROR:
                    Map<String, Integer> errorParams = (HashMap) msg.obj;
                    mListener.onReceiveTrackDataError(errorParams.get("resCode"), errorParams.get("trackIndex"));
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    public void onStopSwiper() {
        if (ex_cardSwiper != null) {
            ex_cardSwiper.onPause();
            ex_cardSwiper = null;
        }
    }

    @Override
    public void onStartReadICData(Context context, String keyIndex, String transAmountIC, OnReceiveTrackListener listener) {

//        Looper.prepare();
        if (waitDataLooper == null) {
            HandlerThread waitDataThread = new HandlerThread("waitICData");
            waitDataThread.start();
            waitDataLooper = waitDataThread.getLooper();
        }

        if (emvManager == null) {
            emvManager = EMVICManager.getEMVICManagerInstance();
            emvManager.setTransAmount(transAmountIC);
            mICDataHandler = new ICCardDataHandler(context, listener, emvManager, waitDataLooper);
            emvManager.onCreate(context, keyIndex, mICDataHandler);
        }
        this.mListener = listener;
        emvManager.onStart();
//        Looper.loop();
    }

    @Override
    public void onStopReadICData() {
        if (emvManager != null) {
            emvManager.onPause();
            emvManager = null;
        }

    }

    @Override
    public void onStartPinPad(JSONObject jsonObject, OnReceiveTrackListener listener) {
        this.mListener = listener;
        PinPadManager.getInstance(jsonObject, mTransactionHandler).start();
    }

    Handler mTransactionHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case PinPadManager.MODE_PAY_NEXT:
                    Bundle pinpadData = (Bundle) msg.getData();
                    mListener.onReceivePinpadData(pinpadData);
                    break;
                case FINISH_TRANSACTION_HANDLER:
                    JSONObject transObj = (JSONObject) msg.obj;
                    mListener.onFinishTransaction(transObj);
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    public void onRecvTrackData(final Hashtable<String, String> trackData) {
        Message msg = mHandler.obtainMessage();
        msg.what = RECEIVE_TRACK_DATA;
        msg.obj = trackData;
        mHandler.sendMessage(msg);
    }

    @Override
    public void onRecvTrackDataError(int resCode, int trackIndex) {
        Message msg = mHandler.obtainMessage();
        msg.what = RECEIVE_TRACK_DATA_ERROR;
        Map<String, Integer> errorParams = new HashMap<String, Integer>();
        errorParams.put("resCode", resCode);
        errorParams.put("trackIndex", trackIndex);
        msg.obj = errorParams;
        mHandler.sendMessage(msg);
    }
}
