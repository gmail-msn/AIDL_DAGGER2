package com.koolcloud.sdk.fmsc.service.device;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;

import com.koolcloud.sdk.fmsc.AppComponent;
import com.koolcloud.sdk.fmsc.R;
import com.koolcloud.sdk.fmsc.domain.Constant;
import com.koolcloud.sdk.fmsc.domain.database.PaymentParamsDB;
import com.koolcloud.sdk.fmsc.domain.entity.PaymentInfo;
import com.koolcloud.sdk.fmsc.interactors.subinteractors.TransactionInteractor;
import com.koolcloud.sdk.fmsc.service.BaseService;
import com.koolcloud.sdk.fmsc.service.IDevicesCallBack;
import com.koolcloud.sdk.fmsc.service.IDevicesInterface;
import com.koolcloud.sdk.fmsc.service.ITransactionCallBack;
import com.koolcloud.sdk.fmsc.util.ApmpUtil;
import com.koolcloud.sdk.fmsc.util.StringUtils;
import com.koolyun.smartpos.sdk.message.parameter.EMVICManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Hashtable;

import javax.inject.Inject;

/**
 * Created by admin on 2015/7/17.
 */
public class DeviceService extends BaseService implements IDeviceServiceView {
    private final String TAG = "DeviceService";

    @Inject
    DevicePresenter mDevicePresenter;

    @Inject
    TransactionInteractor mTransactionInteractor;

    private IDevicesCallBack mDevicesCallBack;
    private ITransactionCallBack mTransactionCallBack;
    private Context context;
    private String mTransType;
    private String mTransAmount;
    private String mPaymentId;
    private String mBrhKeyIndex;
    private String mOpenBrh;
    private String mIdCard;
    private String mToAccount;
    private String mOrderId;

    private JSONObject transJsonObj = null;
    private JSONObject startPinPadObj = new JSONObject();
    private boolean emvCardInserted = false;

    public DeviceService() {
        super("DeviceService");
    }

    public DeviceService(String name) {
        super(name);
    }

    @Override
    protected void setupComponent(AppComponent appComponent) {
        DaggerDeviceComponent.builder()
                .appComponent(appComponent)
                .deviceModule(new DeviceModule(this))
                .build()
                .inject(this);
    }

    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate");
        super.onCreate();
        this.context = this;
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind");
        return devicesInterface;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand");
        return START_STICKY;
    }

    public void onDestroy() {
        Log.d(TAG, "onDestroy");
        super.onDestroy();
    }

    @Override
    protected void onHandleIntent(Intent intent) {

    }

    @Override
    public void receiveTrackData(Hashtable<String, String> trackData) {
        try {
            JSONObject jsonObject = new JSONObject();
            String serviceCode = trackData.get("servicesCode");
            if (null == transJsonObj) {
                transJsonObj = new JSONObject();
            }
            if (null == startPinPadObj) {
                startPinPadObj = new JSONObject();
            }
            if (serviceCode.startsWith("2") || serviceCode.startsWith("6")) {
                if (serviceCode.equals("201") || serviceCode.equals("220")) {
                    //TODO: send message to client and confirm EMV card or not
                    cachedSwipeCardData(trackData);
                    jsonObject.put("card_number", trackData.get("pan"));
                    jsonObject.put("show_dialog", true);
                    jsonObject.put("process_code", Constant.PROCESS_CODE_24);
                    mDevicesCallBack.onGetCardDataCallBack(jsonObject.toString());
                } else {
                    jsonObject.put("message", StringUtils.getResourceString(context, R.string.msg_insert_ic_card_go_on_transaction));
                    jsonObject.put("process_code", Constant.PROCESS_CODE_11);
                    mDevicesCallBack.onSwipeCardErrorCallBack(jsonObject.toString());
                }
            } else {
                mDevicesCallBack.swipeCardCallBack(true);

                cachedSwipeCardData(trackData);
                jsonObject.put("card_number", trackData.get("pan"));
                jsonObject.put("show_dialog", false);
                jsonObject.put("process_code", Constant.PROCESS_CODE_24);
                //TODO: send message with card pan data to client
                mDevicesCallBack.onGetCardDataCallBack(jsonObject.toString());
            }
        } catch (Exception e) {
            Log.w(TAG, e);
        }
    }

    private void startPinpadAfterSwipeCard(boolean isEMVCard) {
        try {
            JSONObject jsonObject = new JSONObject();
            if (!isEMVCard) {
                if (null != startPinPadObj) {
                    mDevicePresenter.onStartPinPad(context, startPinPadObj);
                } else {
                    //FIXME: clean the cache data and reswipe the card
                    cleanCacheParams();
                    mDevicePresenter.onStartSwiper(context);
                    jsonObject.put("message", StringUtils.getResourceString(context, R.string.msg_redo_swipe_card));
                    jsonObject.put("process_code", Constant.PROCESS_CODE_22);
                    mDevicesCallBack.onSwipeCardErrorCallBack(jsonObject.toString());
                }
            } else {

                jsonObject.put("message", StringUtils.getResourceString(context, R.string.msg_insert_ic_card_go_on_transaction));
                jsonObject.put("process_code", Constant.PROCESS_CODE_11);
                if (!emvCardInserted) {
                    mDevicesCallBack.onSwipeCardErrorCallBack(jsonObject.toString());
                }
            }
        } catch (Exception e) {
            Log.w(TAG, e);
        }
    }

    @Override
    public void receiveTrackDataError(int resCode, int trackIndex) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("message", StringUtils.getResourceString(context, R.string.msg_swipe_card_error_retry));
            jsonObject.put("process_code", Constant.PROCESS_CODE_21);
            mDevicesCallBack.onSwipeCardErrorCallBack(jsonObject.toString());
        } catch (Exception e) {
            Log.w(TAG, e);
        }
    }

    @Override
    public void onReceiveICData(JSONObject transICData) {
        try {
            //call back
            mDevicesCallBack.swipeCardCallBack(true);

            transJsonObj.put("pan", transICData.optString("pan"));
            transJsonObj.put("track2", transICData.optString("track2"));
            transJsonObj.put("track3", transICData.optString("track3"));
            transJsonObj.put("validTime", transICData.optString("validTime"));
            transJsonObj.put("pinblock", transICData.optString("pwd"));

            transJsonObj.put("transAmount", mTransAmount);
            transJsonObj.put("transType", mTransType);
            transJsonObj.put("paymentId", mPaymentId);
            transJsonObj.put("brhKeyIndex", mBrhKeyIndex);
            transJsonObj.put("openBrh", mOpenBrh);
            transJsonObj.put("idCard", mIdCard);
            transJsonObj.put("toAccount", mToAccount);
            transJsonObj.put("orderId", mOrderId);
            transJsonObj.put("entryMode", Constant.ENTRY_IC_MODE);
            //TODO: close MSR
            mDevicePresenter.onStopSwipter();

            //TODO: go transaction work flow.
            mDevicePresenter.onStartTransaction(context, transJsonObj, mTransactionInteractor);

        } catch (Exception e) {
            Log.w(TAG, e);
        }
    }

    @Override
    public void onReceiveICCardData(JSONObject icCardData) {
        try {
            Log.w(TAG, "icCard data:" + icCardData.toString());
            mDevicesCallBack.onGetCardDataCallBack(icCardData.toString());
        } catch (Exception e) {
            Log.w(TAG, e);
        }
    }

    @Override
    public void onReceiveICDataError(JSONObject transICData) {
        try {
            String processCode = transICData.optString("process_code");
            if (!TextUtils.isEmpty(processCode)) {
                if (processCode.equals(Constant.PROCESS_CODE_15)) {
                    emvCardInserted = true;
                } else if (processCode.equals(Constant.PROCESS_CODE_13)) {
                    emvCardInserted = false;
                }
            }

            Log.w(TAG, "IC_ERROR:" + transICData.toString());
            mDevicesCallBack.onSwipeCardErrorCallBack(transICData.toString());
        } catch (Exception e) {
            Log.w(TAG, e);
        }
    }

    @Override
    public void onReceivePinpadData(Bundle pinpadData) {
        try {
            boolean isCancelled = pinpadData.getBoolean("isCancelled");
            JSONObject jsonObject = new JSONObject();
            if (isCancelled) {
                jsonObject.put("message", StringUtils.getResourceString(this, R.string.msg_transaction_cancelled));
                jsonObject.put("process_code", Constant.PROCESS_CODE_23);
                mDevicesCallBack.onSwipeCardErrorCallBack(jsonObject.toString());
            } else {//TODO: go transaction work flow.
                String pinblock = pinpadData.getString("pwd");

                if (transJsonObj != null) {
                    transJsonObj.put("pinblock", pinblock);
                    transJsonObj.put("orderId", mOrderId);

                    transJsonObj.put("entryMode", Constant.ENTRY_SWIPER_MODE);
                    //TODO:start transaction
                    mDevicePresenter.onStartTransaction(context, transJsonObj, mTransactionInteractor);
                } else {
                    jsonObject.put("isCancelled", true);
                    jsonObject.put("message", StringUtils.getResourceString(context, R.string.msg_trans_interrupt_warning_17));
                    jsonObject.put("process_code", Constant.PROCESS_CODE_17);
                    mDevicesCallBack.onSwipeCardErrorCallBack(jsonObject.toString());
                }
            }
        } catch (JSONException e) {
            Log.w(TAG, e);
        } catch (Exception e) {
            Log.w(TAG, e);
        }
    }

    @Override
    public void onFinishTransaction(JSONObject jsonObject) {
        Log.w(TAG, "trans result:" + jsonObject.toString());
        try {
            String orderId = jsonObject.optString("orderId");
            if ((jsonObject.isNull("orderId") || TextUtils.isEmpty(orderId)) && !TextUtils.isEmpty(mOrderId)) {
                jsonObject.put("orderId", mOrderId);
            }
            if (TextUtils.isEmpty(mOrderId)) {
                jsonObject.remove("orderId");
            }
            cleanCacheParams();
            mTransactionCallBack.onTransactionCallBack(jsonObject.toString());
        } catch (Exception e) {
            Log.w(TAG, e);
        }
    }

    @Override
    public void onCheckTransactionParamError(JSONObject jsonObject) {
        mDevicePresenter.onStopSwipter();
        mDevicePresenter.onStopReadICData();
        try {
            mDevicesCallBack.onSwipeCardErrorCallBack(jsonObject.toString());
        } catch (Exception e) {
            Log.w(TAG, e);
        }
    }

    IDevicesInterface.Stub devicesInterface = new IDevicesInterface.Stub() {

        @Override
        public void onStartSwipeCard(String paymentId, String transAmount, String transType,
                                     String cardId, String toAccount, String orderId,
                                     IDevicesCallBack devicesCallBack, ITransactionCallBack transactionCallBack) throws RemoteException {
            transJsonObj = new JSONObject();
            mDevicesCallBack = devicesCallBack;
            mTransactionCallBack = transactionCallBack;
            mTransType = transType;
            mTransAmount = transAmount;
            mPaymentId = paymentId;
            mIdCard = cardId.trim();
            mToAccount = toAccount.trim();
            mOrderId = orderId;

            try {
                PaymentParamsDB paymentDB = PaymentParamsDB.getInstance(context);
                PaymentInfo paymentInfo = paymentDB.getPaymentByPaymentId(paymentId);

                JSONObject jsonObject = new JSONObject();
                if (null == paymentInfo) {
                    jsonObject.put("message", StringUtils.getResourceString(context, R.string.msg_payment_no_exist));
                    jsonObject.put("process_code", Constant.PROCESS_CODE_34);
                    mDevicesCallBack.onSwipeCardErrorCallBack(jsonObject.toString());
                    return;
                } else {

                    mBrhKeyIndex = paymentInfo.getBrhKeyIndex();
                    mOpenBrh = paymentInfo.getOpenBrh();

                    onStopSwipeCard();
                    mDevicePresenter.onStartSwiper(context);
                    mDevicePresenter.onStartReadICData(context, mBrhKeyIndex, transAmount);
                }
            } catch (JSONException e) {
                Log.w(TAG, e);
            }
        }

        @Override
        public void onStopSwipeCard() throws RemoteException {
            mDevicePresenter.onStopSwipter();
            mDevicePresenter.onStopReadICData();
        }

        @Override
        public void stopTransaction() throws RemoteException {
            mDevicePresenter.onStopSwipter();
            mDevicePresenter.onStopReadICData();
            cleanCacheParams();
        }

        @Override
        public void startPinPad(boolean isEMVCard) throws RemoteException {
            startPinpadAfterSwipeCard(isEMVCard);
        }

        @Override
        public boolean onTransact(int code, Parcel data, android.os.Parcel reply, int flags) throws RemoteException {
            try {
                return super.onTransact(code, data, reply, flags);
            } catch (RuntimeException e) {
                Log.w(TAG, "Unexpected remote exception", e);
                throw e;
            }
        }
    };

    private void cachedSwipeCardData(Hashtable<String, String> trackData) {
        try {
            startPinPadObj.put("pan", trackData.get("pan"));
            startPinPadObj.put("actionPurpose", ApmpUtil.getActionByTransType(mTransType));
            startPinPadObj.put("brhKeyIndex", mBrhKeyIndex);
            startPinPadObj.put("transAmount", mTransAmount);

            transJsonObj.put("pan", trackData.get("pan"));
            transJsonObj.put("track2", trackData.get("track2"));
            transJsonObj.put("track3", trackData.get("track3"));
            transJsonObj.put("validTime", trackData.get("validTime"));

            transJsonObj.put("transAmount", mTransAmount);
            transJsonObj.put("transType", mTransType);
            transJsonObj.put("paymentId", mPaymentId);
            transJsonObj.put("brhKeyIndex", mBrhKeyIndex);
            transJsonObj.put("openBrh", mOpenBrh);
            transJsonObj.put("idCard", mIdCard);
            transJsonObj.put("toAccount", mToAccount);
            transJsonObj.put("orderId", mOrderId);
        } catch (JSONException e) {
            Log.w(TAG, e);
        }
    }

    private void cleanCacheParams() {
        mTransType = null;
        mTransAmount = null;
        mPaymentId = null;
        mBrhKeyIndex = null;
        mOpenBrh = null;
        mIdCard = null;
        mToAccount = null;
        mOrderId = null;

        transJsonObj = null;
        startPinPadObj = null;
        emvCardInserted = false;
    }
}
