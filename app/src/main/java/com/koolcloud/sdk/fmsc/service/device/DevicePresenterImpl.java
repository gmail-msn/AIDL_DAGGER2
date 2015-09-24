
package com.koolcloud.sdk.fmsc.service.device;


import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.koolcloud.sdk.fmsc.R;
import com.koolcloud.sdk.fmsc.domain.Constant;
import com.koolcloud.sdk.fmsc.interactors.subinteractors.DevicesInteractor;
import com.koolcloud.sdk.fmsc.interactors.subinteractors.TransactionInteractor;
import com.koolcloud.sdk.fmsc.util.Logger;
import com.koolcloud.sdk.fmsc.util.StringUtils;
import com.koolyun.smartpos.sdk.util.ConstantUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Hashtable;

import javax.inject.Inject;

public class DevicePresenterImpl implements DevicePresenter, OnReceiveTrackListener {
    private final String TAG = "DevicePresenterImpl";
    private final int ID_CARD_MIN_LENGTH = 6;
    private final int BANK_CARD_LENGTH_MIN = 11;
    private final int BANK_CARD_LENGTH_MAX = 19;
    private IDeviceServiceView deviceServiceView;
    private DevicesInteractor devicesInteractor;

    public DevicePresenterImpl(IDeviceServiceView deviceServiceView, DevicesInteractor devicesInteractor) {
        this.deviceServiceView = deviceServiceView;
        this.devicesInteractor = devicesInteractor;
    }

    @Override
    public void onStartSwiper(Context context) {
        devicesInteractor.onStartSwiper(context, this);
    }

    @Override
    public void onStopSwipter() {
        devicesInteractor.onStopSwiper();
    }

    @Override
    public void onStartReadICData(Context ctx, String keyIndex, String transAmount) {
        devicesInteractor.onStartReadICData(ctx, keyIndex, transAmount, this);
    }

    @Override
    public void onStopReadICData() {
        devicesInteractor.onStopReadICData();
    }

    @Override
    public void onStartPinPad(Context ctx, JSONObject jsonObject) {
        devicesInteractor.onStartPinPad(jsonObject, this);
    }

    @Override
    public void onStartTransaction(Context ctx, JSONObject jsonObject, TransactionInteractor mTransactionInteractor) {
        try {

            Logger.i("presenter onStartTransaction");
            String transType = jsonObject.optString("transType");
            String mIdCard = jsonObject.optString("idCard");
            String mToAccount = jsonObject.optString("toAccount");
            if (TextUtils.isEmpty(transType)) {
                jsonObject.put("message", StringUtils.getResourceString(ctx, R.string.msg_param_transaction_type_error));
                jsonObject.put("process_code", Constant.PROCESS_CODE_31);
                deviceServiceView.onCheckTransactionParamError(jsonObject);
                return;
            }
            if (!TextUtils.isEmpty(transType) && transType.equals(ConstantUtils.APMP_TRAN_SUPER_TRANSFER)) {
                if (TextUtils.isEmpty(mIdCard) || mIdCard.length() < ID_CARD_MIN_LENGTH) {
                    jsonObject.put("message", StringUtils.getResourceString(ctx, R.string.msg_param_id_card_length_error));
                    jsonObject.put("process_code", Constant.PROCESS_CODE_32);
                    deviceServiceView.onCheckTransactionParamError(jsonObject);
                    return;
                }
                if (TextUtils.isEmpty(mToAccount) || mToAccount.length() < BANK_CARD_LENGTH_MIN || mToAccount.length() > BANK_CARD_LENGTH_MAX) {
                    jsonObject.put("message", StringUtils.getResourceString(ctx, R.string.msg_param_bank_card_length_error));
                    jsonObject.put("process_code", Constant.PROCESS_CODE_33);
                    deviceServiceView.onCheckTransactionParamError(jsonObject);
                    return;
                }
            }
            mTransactionInteractor.onStartTransaction(ctx, jsonObject, this);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onFinishTransaction(JSONObject jsonObject) {
        Logger.i("presenter onFinishTransaction");
        deviceServiceView.onFinishTransaction(jsonObject);
    }

    @Override
    public void onReceiveTrackData(Hashtable<String, String> trackData) {
        deviceServiceView.receiveTrackData(trackData);
    }

    @Override
    public void onReceiveTrackDataError(int resCode, int trackIndex) {
        deviceServiceView.receiveTrackDataError(resCode, trackIndex);
    }

    @Override
    public void onReceiveICData(JSONObject transICData) {
        deviceServiceView.onReceiveICData(transICData);
    }

    @Override
    public void onReceiveICCardData(JSONObject icCardData) {
        deviceServiceView.onReceiveICCardData(icCardData);
    }

    @Override
    public void onReceiveICDataError(JSONObject transICData) {
        deviceServiceView.onReceiveICDataError(transICData);
    }

    @Override
    public void onReceivePinpadData(Bundle pinpadData) {
        deviceServiceView.onReceivePinpadData(pinpadData);
    }
}
