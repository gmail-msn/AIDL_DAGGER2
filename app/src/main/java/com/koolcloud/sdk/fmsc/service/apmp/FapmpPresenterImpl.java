
package com.koolcloud.sdk.fmsc.service.apmp;


import android.content.Context;

import com.koolcloud.sdk.fmsc.interactors.subinteractors.FapmpInteractor;

import org.json.JSONObject;

public class FapmpPresenterImpl implements FapmpPresenter, OnApmpCallBackListener {

    private IFapmpServiceView iFapmpServiceView;
    private FapmpInteractor fApmpInteractor;

    public FapmpPresenterImpl(IFapmpServiceView iFapmpServiceView, FapmpInteractor apmpInteractor) {
        this.iFapmpServiceView = iFapmpServiceView;
        this.fApmpInteractor = apmpInteractor;
    }

    @Override
    public void loginApmp(Context ctx, String merchId, String username, String password) {
        fApmpInteractor.loginApmp(ctx, merchId, username, password, this);
    }

    @Override
    public void downloadPaymentParams(Context ctx, String merchId) {
        fApmpInteractor.downloadPaymentParams(ctx, merchId, this);
    }

    @Override
    public void onLoginCallBack(JSONObject loginResult) {
        iFapmpServiceView.onLoginCallBack(loginResult);
    }

    @Override
    public void onDownloadPaymentParamsCallBack(JSONObject paymentsObj) {
        iFapmpServiceView.onDownloadPaymentParamsCallBack(paymentsObj);
    }
}
