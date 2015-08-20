package com.koolcloud.sdk.fmsc.service.apmp;

import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import android.util.Log;

import com.koolcloud.sdk.fmsc.AppComponent;
import com.koolcloud.sdk.fmsc.interactors.subinteractors.TransactionInteractor;
import com.koolcloud.sdk.fmsc.service.BaseService;
import com.koolcloud.sdk.fmsc.service.IApmpCallBack;
import com.koolcloud.sdk.fmsc.service.IApmpInterface;

import org.json.JSONObject;

import javax.inject.Inject;

/**
 * Created by admin on 2015/7/17.
 */
public class FapmpService extends BaseService implements IFapmpServiceView {

    private final String TAG = "FapmpService";

    @Inject
    FapmpPresenter mFapmpPresenter;

    IApmpCallBack mIApmpCallBack;
    private Context context;

    public FapmpService() {
        super("FapmpService");
    }

    public FapmpService(String name) {
        super(name);
    }

    @Override
    protected void setupComponent(AppComponent appComponent) {
        DaggerFapmpComponent.builder()
                .appComponent(appComponent)
                .fapmpModule(new FapmpModule(this))
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
        return apmpInterface;
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

    IApmpInterface.Stub apmpInterface = new IApmpInterface.Stub() {

        @Override
        public void loginApmp(String merchId, String userName, String password, IApmpCallBack apmpCallBack) throws RemoteException {
            mIApmpCallBack = apmpCallBack;
            mFapmpPresenter.loginApmp(context, merchId, userName, password);
        }

        @Override
        public void logoutApmp(IApmpCallBack apmpCallBack) throws RemoteException {
            mIApmpCallBack = apmpCallBack;
            mFapmpPresenter.logoutApmp(context);
        }

        @Override
        public void downloadPaymentParams(String merchId, IApmpCallBack apmpCallBack) throws RemoteException {
            mIApmpCallBack = apmpCallBack;
            mFapmpPresenter.downloadPaymentParams(context, merchId);
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

    @Override
    protected void onHandleIntent(Intent intent) {

    }

    @Override
    public void onLoginCallBack(JSONObject loginResult) {
        try {
            mIApmpCallBack.loginApmpCallBack(loginResult.toString());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onLogoutCallBack(JSONObject logoutResult) {
        try {
            mIApmpCallBack.logoutCallBack(logoutResult.toString());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDownloadPaymentParamsCallBack(JSONObject paymentsObj) {
        try {
            mIApmpCallBack.onDownloadPaymentParamsCallBack(paymentsObj.toString());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
