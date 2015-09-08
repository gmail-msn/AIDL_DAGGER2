package com.koolcloud.sdk.fmsc.interactors.processors;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.koolcloud.sdk.fmsc.R;
import com.koolcloud.sdk.fmsc.domain.devices.PinPadManager;
import com.koolcloud.sdk.fmsc.service.device.OnReceiveTrackListener;
import com.koolcloud.sdk.fmsc.util.StringUtils;
import com.koolyun.smartpos.sdk.message.parameter.EMVICData;
import com.koolyun.smartpos.sdk.message.parameter.EMVICManager;
import com.koolyun.smartpos.sdk.message.parameter.UtilFor8583;
import com.koolyun.smartpos.sdk.util.ConstantUtils;
import com.koolyun.smartpos.sdk.util.Utility;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by admin on 2015/7/25.
 */
public class ICCardDataHandler extends Handler {
    private final String TAG = "ICCardDataHandler";
    private Context context;
    private static OnReceiveTrackListener mListener;
    private EMVICManager emvManager;

    public ICCardDataHandler(Context ctx, OnReceiveTrackListener listener, EMVICManager emvManager, Looper waitDataLooper) {
        super(waitDataLooper);
        this.context = ctx;
        this.mListener = listener;
        this.emvManager = emvManager;
    }

    @Override
    public void handleMessage(Message msg) {
        try {
            switch (msg.what) {
                case EMVICManager.STATUS_VALUE_0: {
                    break;
                }
                case EMVICManager.STATUS_VALUE_1: {
                    JSONObject transData = new JSONObject();
                    try {
                        transData.put("message", StringUtils.getResourceString(context, R.string.ic_status_insert_warning_3));
                        transData.put("process_code", EMVICManager.STATUS_VALUE_1);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    mListener.onReceiveICDataError(transData);
                    break;
                }
                case EMVICManager.STATUS_VALUE_2:
                    break;
                case EMVICManager.STATUS_VALUE_3:

                    break;
                case EMVICManager.STATUS_VALUE_4: {
                    JSONObject transData = new JSONObject();
                    try {
                        transData.put("message", StringUtils.getResourceString(context, R.string.ic_status_insert_warning_4));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    mListener.onReceiveICDataError(transData);
                    break;
                }
                case EMVICManager.TRADE_STATUS_0:
                    break;
                case EMVICManager.TRADE_STATUS_1:

                    break;
                case EMVICManager.TRADE_STATUS_2:
                    break;
                case EMVICManager.TRADE_STATUS_3: {
                    JSONObject emvCardData = new JSONObject();
                    try {
                        EMVICData mEMVICData = EMVICData.getEMVICInstance();
                        emvManager.getPAN();
                        String pan = mEMVICData.getICPan();
                        emvCardData.put("card_number", pan);
                        emvCardData.put("process_code", EMVICManager.TRADE_STATUS_3);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    mListener.onReceiveICCardData(emvCardData);
                    break;
                }
                case EMVICManager.TRADE_STATUS_4:
                    break;
                case EMVICManager.TRADE_STATUS_5:
                    break;
                case EMVICManager.TRADE_STATUS_6:
                    break;
                case EMVICManager.TRADE_STATUS_7:
                    break;
                case EMVICManager.TRADE_STATUS_8: {

                    JSONObject transData = new JSONObject();
                    try {
                        transData.put("message", StringUtils.getResourceString(context, R.string.ic_status_insert_warning_7));
                        transData.put("process_code", EMVICManager.TRADE_STATUS_8);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    mListener.onReceiveICDataError(transData);
                    break;
                }
                case EMVICManager.TRADE_STATUS_9:
                    break;
                case EMVICManager.TRADE_STATUS_10: {
                    JSONObject transData = new JSONObject();
                    try {
                        transData.put("message", StringUtils.getResourceString(context, R.string.ic_status_insert_warning_14));
                        transData.put("process_code", EMVICManager.TRADE_STATUS_10);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    mListener.onReceiveICDataError(transData);
                    break;
                }
                case EMVICManager.TRADE_STATUS_BAN: {
                    JSONObject transData = new JSONObject();
                    try {
                        transData.put("isCancelled", true);
                        transData.put("message", StringUtils.getResourceString(context, R.string.ic_status_insert_warning_9));
                        transData.put("process_code", EMVICManager.TRADE_STATUS_BAN);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    mListener.onReceiveICDataError(transData);
                    break;
                }
                case EMVICManager.TRADE_STATUS_ABORT: {
                    JSONObject transData = new JSONObject();
                    try {
                        transData.put("isCancelled", true);
                        transData.put("message", StringUtils.getResourceString(context, R.string.ic_status_insert_warning_10));
                        transData.put("process_code", EMVICManager.TRADE_STATUS_ABORT);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    mListener.onReceiveICDataError(transData);
                    break;
                }
                case EMVICManager.TRADE_STATUS_APPROVED:
                    break;
                case EMVICManager.TRADE_STATUS_DISABLESERVICE: {
                    JSONObject transData = new JSONObject();
                    try {
                        transData.put("isCancelled", true);
                        transData.put("message", StringUtils.getResourceString(context, R.string.ic_status_insert_warning_12));
                        transData.put("process_code", EMVICManager.TRADE_STATUS_DISABLESERVICE);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    mListener.onReceiveICDataError(transData);
                    break;
                }
                case EMVICManager.TRADE_STATUS_ONLINE: {
                    JSONObject transData = new JSONObject();
                    try {
                        transData.put("message", StringUtils.getResourceString(context, R.string.ic_status_insert_warning_13));
                        transData.put("process_code", EMVICManager.TRADE_STATUS_ONLINE);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    mListener.onReceiveICDataError(transData);
                    break;
                }
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
        } catch (Exception e) {
            Log.w(TAG, e);
        }
    }
}
