
package com.koolcloud.sdk.fmsc.service.apmp;


import android.content.Context;

import com.koolcloud.sdk.fmsc.interactors.FapmpInteractor;

import org.json.JSONObject;

public class FapmpPresenterImpl implements FapmpPresenter, OnApmpCallBackListener {

    private IFapmpServiceView iFapmpServiceView;
    private FapmpInteractor fapmpInteractor;

    public FapmpPresenterImpl(IFapmpServiceView iFapmpServiceView, FapmpInteractor fapmpInteractor) {
        this.iFapmpServiceView = iFapmpServiceView;
        this.fapmpInteractor = fapmpInteractor;
    }

    @Override
    public void loginApmp(Context ctx, String merchId, String username, String password) {
        fapmpInteractor.loginApmp(ctx, merchId, username, password, this);
    }

    @Override
    public void onLoginCallBack(JSONObject loginResult) {
        iFapmpServiceView.onLoginCallBack(loginResult);
    }
}
