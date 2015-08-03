package com.koolcloud.sdk.fmsc.service.device;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import android.util.Log;

import com.koolcloud.sdk.fmsc.AppComponent;
import com.koolcloud.sdk.fmsc.R;
import com.koolcloud.sdk.fmsc.domain.database.PaymentParamsDB;
import com.koolcloud.sdk.fmsc.domain.entity.PaymentInfo;
import com.koolcloud.sdk.fmsc.interactors.subinteractors.TransactionInteractor;
import com.koolcloud.sdk.fmsc.service.BaseService;
import com.koolcloud.sdk.fmsc.service.IDevicesCallBack;
import com.koolcloud.sdk.fmsc.service.IDevicesInterface;
import com.koolcloud.sdk.fmsc.service.ITransactionCallBack;
import com.koolcloud.sdk.fmsc.util.ApmpUtil;
import com.koolcloud.sdk.fmsc.util.StringUtils;
import com.koolyun.smartpos.sdk.message.parameter.UtilFor8583;
import com.koolyun.smartpos.sdk.util.ConstantUtils;

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

    private JSONObject transJsonObj = null;

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
            String serviceCode = trackData.get("servicesCode");
            if (serviceCode.startsWith("2") || serviceCode.startsWith("6")) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("message", StringUtils.getResourceString(context, R.string.msg_insert_ic_card_go_on_transaction));
                mDevicesCallBack.onSwipeCardErrorCallBack(jsonObject.toString());
            } else {
                mDevicesCallBack.swipeCardCallBack(true);

                //TODO: start pinpad to get PINBLOCK
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("pan", trackData.get("pan"));
                jsonObject.put("actionPurpose", ApmpUtil.getActionByTransType(mTransType));
                jsonObject.put("brhKeyIndex", mBrhKeyIndex);
                jsonObject.put("transAmount", mTransAmount);

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

                mDevicePresenter.onStartPinPad(context, jsonObject);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void receiveTrackDataError(int resCode, int trackIndex) {
        try {
            mDevicesCallBack.onSwipeCardErrorCallBack(StringUtils.getResourceString(context, R.string.msg_swipe_card_error_retry));
        } catch (RemoteException e) {
            e.printStackTrace();
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

            //TODO: go transaction work flow.
            mDevicePresenter.onStartTransaction(context, transJsonObj, mTransactionInteractor);
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onReceiveICDataError(JSONObject transICData) {
        try {
            mDevicesCallBack.onSwipeCardErrorCallBack(transICData.toString());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onReceivePinpadData(Bundle pinpadData) {
        boolean isCancelled = pinpadData.getBoolean("isCancelled");
        JSONObject jsonObject = new JSONObject();
        if (isCancelled) {
            try {
                jsonObject.put("message", StringUtils.getResourceString(this, R.string.msg_transaction_cancelled));
                mDevicesCallBack.onSwipeCardErrorCallBack(jsonObject.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        } else {//TODO: go transaction work flow.
            String pinblock = pinpadData.getString("pwd");
            try {
                transJsonObj.put("pinblock", pinblock);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            UtilFor8583.getInstance().trans.setEntryMode(ConstantUtils.ENTRY_SWIPER_MODE);
            //TODO:start transaction
            mDevicePresenter.onStartTransaction(context, transJsonObj, mTransactionInteractor);
        }

    }

    @Override
    public void onFinishTransaction(JSONObject jsonObject) {
        Log.w(TAG, "trans result:" + jsonObject.toString());
        try {
            clearParams();
            mTransactionCallBack.onTransactionCallBack(jsonObject.toString());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    IDevicesInterface.Stub devicesInterface = new IDevicesInterface.Stub() {

        @Override
        public void onStartSwipeCard(String paymentId, String transAmount, String transType,
                                     String cardId, String toAccount,
                                     IDevicesCallBack devicesCallBack, ITransactionCallBack transactionCallBack) throws RemoteException {
            transJsonObj = new JSONObject();
            mDevicesCallBack = devicesCallBack;
            mTransactionCallBack = transactionCallBack;
            mTransType = transType;
            mTransAmount = transAmount;
            mPaymentId = paymentId;
            mIdCard = cardId;
            mToAccount = toAccount;

            PaymentParamsDB paymentDB = PaymentParamsDB.getInstance(context);
            PaymentInfo paymentInfo = paymentDB.getPaymentByPaymentId(paymentId);

            if (null == paymentInfo) {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("message", StringUtils.getResourceString(context, R.string.msg_payment_no_exist));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                mDevicesCallBack.onSwipeCardErrorCallBack(jsonObject.toString());
                return;
            } else {

                mBrhKeyIndex = paymentInfo.getBrhKeyIndex();
                mOpenBrh = paymentInfo.getOpenBrh();

                onStopSwipeCard();
                mDevicePresenter.onStartSwiper(context);
                mDevicePresenter.onStartReadICData(context, mBrhKeyIndex, transAmount);
            }
        }

        @Override
        public void onStopSwipeCard() throws RemoteException {
            mDevicePresenter.onStopSwipter();
            mDevicePresenter.onStopReadICData();
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

    private void clearParams() {
        mTransType = null;
        mTransAmount = null;
        mPaymentId = null;
        mBrhKeyIndex = null;
        mOpenBrh = null;
        mIdCard = null;
        mToAccount = null;

        transJsonObj = null;
    }
}
