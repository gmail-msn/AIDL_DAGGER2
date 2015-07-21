package com.koolcloud.sdk.fmsc.interactors;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.koolcloud.sdk.fmsc.domain.Constant;
import com.koolcloud.sdk.fmsc.domain.devices.CardSwiper;
import com.koolcloud.sdk.fmsc.domain.devices.PinPadManager;
import com.koolcloud.sdk.fmsc.service.device.OnReceiveTrackListener;
import com.koolcloud.sdk.fmsc.util.PreferenceUtil;
import com.koolyun.smartpos.sdk.message.parameter.EMVICData;
import com.koolyun.smartpos.sdk.message.parameter.EMVICManager;
import com.koolyun.smartpos.sdk.message.parameter.UtilFor8583;
import com.koolyun.smartpos.sdk.service.ApmpService;
import com.koolyun.smartpos.sdk.service.POSPTransactionService;
import com.koolyun.smartpos.sdk.util.ConstantUtils;
import com.koolyun.smartpos.sdk.util.Utility;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

/**
 * Created by admin on 2015/5/14.
 */
public class DevicesInteractorImpl implements DevicesInteractor, CardSwiper.CardSwiperListener {
    private final int FINISH_TRANSACTION_HANDLER = 1;
    private CardSwiper ex_cardSwiper;
    private EMVICManager emvManager = null;
    private OnReceiveTrackListener mListener;
    private static final int RECEIVE_TRACK_DATA = 0;
    private static final int RECEIVE_TRACK_DATA_ERROR = 1;
    private Context context;

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

    Handler mICDataHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case EMVICManager.STATUS_VALUE_0: {
                    break;
                }
                case EMVICManager.STATUS_VALUE_1: {
                    break;
                }
                case EMVICManager.STATUS_VALUE_2:
                    break;
                case EMVICManager.STATUS_VALUE_3:
                    break;
                case EMVICManager.STATUS_VALUE_4: {
                    break;
                }
                case EMVICManager.TRADE_STATUS_0:
                    break;
                case EMVICManager.TRADE_STATUS_1:
                    onStopSwiper();
                    break;
                case EMVICManager.TRADE_STATUS_2:
                    break;
                case EMVICManager.TRADE_STATUS_3:
                    break;
                case EMVICManager.TRADE_STATUS_4:
                    break;
                case EMVICManager.TRADE_STATUS_5:
                    break;
                case EMVICManager.TRADE_STATUS_6:
                    break;
                case EMVICManager.TRADE_STATUS_7:
                    break;
                case EMVICManager.TRADE_STATUS_8:
                    break;
                case EMVICManager.TRADE_STATUS_9:
                    break;
                case EMVICManager.TRADE_STATUS_10:
                    break;
                case EMVICManager.TRADE_STATUS_BAN: {
                    JSONObject transData = new JSONObject();
                    try {
                        transData.put("isCancelled", true);
                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    onStopReadICData();
                    break;
                }
                case EMVICManager.TRADE_STATUS_ABORT: {
                    JSONObject transData = new JSONObject();
                    try {
                        transData.put("isCancelled", true);
                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    onStopReadICData();
                    break;
                }
                case EMVICManager.TRADE_STATUS_APPROVED:
                    break;
                case EMVICManager.TRADE_STATUS_DISABLESERVICE: {
                    JSONObject transData = new JSONObject();
                    try {
                        transData.put("isCancelled", true);
                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    onStopReadICData();
                    break;
                }
                case EMVICManager.TRADE_STATUS_ONLINE:
                    break;
                case EMVICManager.TRADE_STATUS_AFGETICDATA: {
                    JSONObject transData = new JSONObject();
                    EMVICData mEMVICData = EMVICData.getEMVICInstance();
                    String pwd = Utility.hexString(mEMVICData.getPinBlock());
                    try {
                        transData.put("pan", mEMVICData.getICPan());
                        transData.put("track2", mEMVICData.getTrack2());
                        transData.put("validTime", mEMVICData.getDataOfExpired());
                        transData.put("pwd", pwd);
                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    UtilFor8583.getInstance().trans.setEntryMode(ConstantUtils.ENTRY_IC_MODE);
                    mListener.onReceiveICData(transData);
                    break;
                }
                case EMVICManager.TRADE_STATUS_READICCARDID: {
                    EMVICData mEMVICData = EMVICData.getEMVICInstance();
                    emvManager.getPAN();
                    String pan = mEMVICData.getICPan();
                    JSONObject sendMsg = new JSONObject();
                    try {
                        sendMsg.put("pan", pan);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                }
                case PinPadManager.MODE_INPUT_AUTH_CODE:
                    //needPwd = true;
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
        if (emvManager == null) {
            emvManager = EMVICManager.getEMVICManagerInstance();
            emvManager.setTransAmount(transAmountIC);
            emvManager.onCreate(context, keyIndex, mICDataHandler);
        }
        this.mListener = listener;
        emvManager.onStart();
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

            Message msg = mTransactionHandler.obtainMessage();
            msg.obj = transResultObj;
            msg.what = FINISH_TRANSACTION_HANDLER;
            mTransactionHandler.sendMessage(msg);

        }
    }
}
