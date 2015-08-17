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
import com.koolyun.smartpos.sdk.message.parameter.UtilFor8583;
import com.koolyun.smartpos.sdk.util.ConstantUtils;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

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
                    mDevicesCallBack.onGetCardDataCallBack(jsonObject.toString());
                } else {
                    jsonObject.put("message", StringUtils.getResourceString(context, R.string.msg_insert_ic_card_go_on_transaction));
                    mDevicesCallBack.onSwipeCardErrorCallBack(jsonObject.toString());
                }
            } else {
                mDevicesCallBack.swipeCardCallBack(true);

                cachedSwipeCardData(trackData);
                jsonObject.put("card_number", trackData.get("pan"));
                jsonObject.put("show_dialog", false);
                //TODO: send message with card pan data to client
                mDevicesCallBack.onGetCardDataCallBack(jsonObject.toString());
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void startPinpadAfterSwipeCard(boolean isEMVCard) {
        try {
            if (!isEMVCard) {
                if (null != startPinPadObj) {
                    mDevicePresenter.onStartPinPad(context, startPinPadObj);
                } else {
                    //FIXME: clean the cache data and reswipe the card
                    cleanCacheParams();
                    mDevicePresenter.onStartSwiper(context);
                    mDevicesCallBack.onSwipeCardErrorCallBack(StringUtils.getResourceString(context, R.string.msg_redo_swipe_card));
                }
            } else {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("message", StringUtils.getResourceString(context, R.string.msg_insert_ic_card_go_on_transaction));

                if (!emvCardInserted) {
                    mDevicesCallBack.onSwipeCardErrorCallBack(jsonObject.toString());
                }
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

            //TODO: close MSR
            mDevicePresenter.onStopSwipter();

            //TODO: go transaction work flow.
            mDevicePresenter.onStartTransaction(context, transJsonObj, mTransactionInteractor);

        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onReceiveICCardData(JSONObject icCardData) {
        try {
            Log.w(TAG, "icCard data:" + icCardData.toString());
            mDevicesCallBack.onGetCardDataCallBack(icCardData.toString());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onReceiveICDataError(JSONObject transICData) {
        try {
            int processCode = transICData.optInt("process_code");
            switch (processCode) {
                case EMVICManager.TRADE_STATUS_10:
                    emvCardInserted = true;
                    break;
                case EMVICManager.STATUS_VALUE_1:
                    emvCardInserted = false;
                    break;
            }
            Log.w(TAG, "IC_ERROR:" + transICData.toString());
            mDevicesCallBack.onSwipeCardErrorCallBack(transICData.toString());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onReceivePinpadData(Bundle pinpadData) {
        try {
            boolean isCancelled = pinpadData.getBoolean("isCancelled");
            JSONObject jsonObject = new JSONObject();
            if (isCancelled) {
                jsonObject.put("message", StringUtils.getResourceString(this, R.string.msg_transaction_cancelled));
                mDevicesCallBack.onSwipeCardErrorCallBack(jsonObject.toString());
            } else {//TODO: go transaction work flow.
                String pinblock = pinpadData.getString("pwd");
                transJsonObj.put("pinblock", pinblock);

                UtilFor8583.getInstance().trans.setEntryMode(ConstantUtils.ENTRY_SWIPER_MODE);
                //TODO:start transaction
                mDevicePresenter.onStartTransaction(context, transJsonObj, mTransactionInteractor);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onFinishTransaction(JSONObject jsonObject) {
        Log.w(TAG, "trans result:" + jsonObject.toString());
        try {
            cleanCacheParams();
            mTransactionCallBack.onTransactionCallBack(jsonObject.toString());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCheckTransactionParamError(JSONObject jsonObject) {
        mDevicePresenter.onStopSwipter();
        mDevicePresenter.onStopReadICData();
        try {
            mDevicesCallBack.onSwipeCardErrorCallBack(jsonObject.toString());
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
            mIdCard = cardId.trim();
            mToAccount = toAccount.trim();

            try {
                PaymentParamsDB paymentDB = PaymentParamsDB.getInstance(context);
                PaymentInfo paymentInfo = paymentDB.getPaymentByPaymentId(paymentId);

                JSONObject jsonObject = new JSONObject();
                if (null == paymentInfo) {
                        jsonObject.put("message", StringUtils.getResourceString(context, R.string.msg_payment_no_exist));
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
                e.printStackTrace();
            }
        }

        @Override
        public void onStopSwipeCard() throws RemoteException {
            mDevicePresenter.onStopSwipter();
            mDevicePresenter.onStopReadICData();
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
        } catch (JSONException e) {
            e.printStackTrace();
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

        transJsonObj = null;
        startPinPadObj = null;
        emvCardInserted = false;
    }
}
