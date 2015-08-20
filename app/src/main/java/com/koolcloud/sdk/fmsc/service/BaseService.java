package com.koolcloud.sdk.fmsc.service;

import android.app.IntentService;
import android.util.Log;

import com.koolcloud.sdk.fmsc.App;
import com.koolcloud.sdk.fmsc.AppComponent;
import com.koolcloud.sdk.fmsc.R;
import com.koolcloud.sdk.fmsc.domain.database.BankDB;
import com.koolcloud.sdk.fmsc.domain.database.util.DataBaseUtils;

import java.io.IOException;

/**
 * Created by admin on 2015/7/17.
 */
public abstract class BaseService extends IntentService {
    private final static String TAG = "BaseService";

    public BaseService(String name) {
        super(TAG);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        setupComponent((AppComponent) App.getApplication(this).component());

        try {
            DataBaseUtils.createFromRawDataBase(this, BankDB.DB_PATH + BankDB.DATABASE_NAME, R.raw.bank_table);
        } catch (IOException e) {
            e.printStackTrace();
        }

        //test bank
        /*BankDB bankDB = BankDB.getInstance(this);
        Log.i("BaseService", "01040000 bankName:" + bankDB.getBankNameByIssuerId("01040000"));*/
    }

    protected abstract void setupComponent(AppComponent appComponent);
}
