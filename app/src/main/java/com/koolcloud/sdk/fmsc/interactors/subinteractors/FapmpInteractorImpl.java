/*
 *
 *  *
 *  *  * Copyright (C) 2014 Antonio Leiva Gordillo.
 *  *  *
 *  *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  *  * you may not use this file except in compliance with the License.
 *  *  * You may obtain a copy of the License at
 *  *  *
 *  *  *      http://www.apache.org/licenses/LICENSE-2.0
 *  *  *
 *  *  * Unless required by applicable law or agreed to in writing, software
 *  *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  *  * See the License for the specific language governing permissions and
 *  *  * limitations under the License.
 *  *
 *
 */

package com.koolcloud.sdk.fmsc.interactors.subinteractors;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.koolcloud.sdk.fmsc.domain.database.PaymentParamsDB;
import com.koolcloud.sdk.fmsc.domain.entity.PaymentInfo;
import com.koolcloud.sdk.fmsc.service.apmp.OnApmpCallBackListener;
import com.koolcloud.sdk.fmsc.util.ApmpUtil;
import com.koolcloud.sdk.fmsc.util.JsonUtil;
import com.koolcloud.sdk.fmsc.util.PreferenceUtil;
import com.koolyun.smartpos.sdk.service.ApmpService;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;


public class FapmpInteractorImpl implements FapmpInteractor {
    private static final int SUCCESS_HANDLER = 0;
    private static final int FAILURE_HANDLER = 1;
    Context ctx;
    OnApmpCallBackListener mListener;

    @Override
    public void loginApmp(final Context ctx, final String merchId, final String username, final String password, final OnApmpCallBackListener listener) {
        // Mock login. I'm creating a handler to delay the answer a couple of seconds
        Log.i("FapmpInteractorImpl", "login");
        this.ctx = ctx;
        this.mListener = listener;

        ApmpService apmpService = ApmpUtil.getApmpInstance(ctx);
        JSONObject loginResult = apmpService.login(username, password, merchId, android.os.Build.SERIAL);
        String terminalId = loginResult.optString("iposId");
        if (!TextUtils.isEmpty(terminalId)) {
            PreferenceUtil.saveMerchID(ctx, merchId);
            PreferenceUtil.saveTerminalID(ctx, terminalId);
        }

        mListener.onLoginCallBack(loginResult);
    }

    @Override
    public void logoutApmp(Context ctx, OnApmpCallBackListener listener) {
        ApmpService apmpService = ApmpUtil.getApmpInstance(ctx);
        JSONObject logoutObj = apmpService.logout();
        Log.i("FapmpInteractorImpl", "logout:" + logoutObj.toString());
        listener.onLogoutCallBack(logoutObj);
    }

    @Override
    public void downloadPaymentParams(Context ctx, String merchId, OnApmpCallBackListener listener) {
        this.ctx = ctx;
        this.mListener = listener;
        ApmpService apmpService = ApmpUtil.getApmpInstance(ctx);

        //TODO:download payment params and then save to local
        JSONObject paramsObj = apmpService.getPaymentList(merchId);
        PaymentParamsDB paymentParamsDB = PaymentParamsDB.getInstance(ctx);
        JSONArray jsonArray = paramsObj.optJSONArray("prdtList");

        //clear cached payment table
        paymentParamsDB.clearPaymentParamsTableData();
        if (jsonArray != null) {
            List<PaymentInfo> acquireJsonList = JsonUtil.parseJsonArray2AcquireInstitute(jsonArray);
            if (acquireJsonList != null && acquireJsonList.size() > 0) {
                paymentParamsDB.insertPayment(acquireJsonList);
            }
        }

        mListener.onDownloadPaymentParamsCallBack(paramsObj);

    }

}
