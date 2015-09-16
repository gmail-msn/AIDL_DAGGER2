package com.koolcloud.sdk.fmsc.service.transaction;

import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import android.util.Log;

import com.koolcloud.sdk.fmsc.AppComponent;
import com.koolcloud.sdk.fmsc.service.BaseService;
import com.koolcloud.sdk.fmsc.service.ITransactionCallBack;
import com.koolcloud.sdk.fmsc.service.ITransactionInterface;

import org.json.JSONObject;

import javax.inject.Inject;

/**
 * Created by admin on 2015/7/17.
 */
public class TransactionService extends BaseService implements ITransactionService {
    private final String TAG = "TransactionService";

    @Inject
    TransactionPresenter mTransactionPresenter;

    private Context context;
    ITransactionCallBack mTransactionCallBack;

    public TransactionService(){
        super("TransactionService");
    }
    public TransactionService(String name) {
        super(name);
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
        return transactionInterface;
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
    protected void setupComponent(AppComponent appComponent) {
        DaggerTransactionComponent.builder()
                .appComponent(appComponent)
                .transactionModule(new TransactionModule(this))
                .build()
                .inject(this);
    }

    ITransactionInterface.Stub transactionInterface = new ITransactionInterface.Stub() {

        @Override
        public void signIn(String paymentId, ITransactionCallBack transactionCallBack) throws RemoteException {
            mTransactionCallBack = transactionCallBack;
            mTransactionPresenter.signInPosp(context, paymentId);
        }

        @Override
        public void clearReversalData(ITransactionCallBack transactionCallBack) throws RemoteException {
            mTransactionCallBack = transactionCallBack;
            mTransactionPresenter.clearReversalData(context);
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
    public void onFinishTransaction(JSONObject jsonObject) {

    }

    @Override
    public void onReceiveSignInResult(JSONObject jsonObject) {
        try {
            mTransactionCallBack.signInCallBack(jsonObject.toString());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onReceiveClearReversalData(boolean result) {
        try {
            mTransactionCallBack.onClearReversalDataCallBack(result);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
