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

package com.koolcloud.sdk.fmsc.interactors;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.koolcloud.sdk.fmsc.domain.database.PaymentParamsDB;
import com.koolcloud.sdk.fmsc.domain.entity.PaymentInfo;
import com.koolcloud.sdk.fmsc.ui.login.OnLoginFinishedListener;
import com.koolcloud.sdk.fmsc.util.JsonUtil;
import com.koolcloud.sdk.fmsc.util.PreferenceUtil;
import com.koolyun.smartpos.sdk.service.ApmpService;
import com.koolyun.smartpos.sdk.service.POSPTransactionService;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;


public class LoginInteractorImpl implements LoginInteractor {
    private static final int SUCCESS_HANDLER = 0;
    private static final int FAILURE_HANDLER = 1;
    String merchId;
    String username;
    String password;
    Context ctx;
    OnLoginFinishedListener listener;
    @Override
    public void login(final Context ctx, final String merchId, final String username, final String password, final OnLoginFinishedListener listener) {
        // Mock login. I'm creating a handler to delay the answer a couple of seconds
        Log.i("LoginInteractorImpl", "login");
        this.merchId = merchId;
        this.username = username;
        this.password = password;
        this.ctx = ctx;
        this.listener = listener;
        boolean error = false;
        if (TextUtils.isEmpty(merchId)) {
            listener.onMerchIdError();
            error = true;
        }
        if (TextUtils.isEmpty(username)){
            listener.onUsernameError();
            error = true;
        }
        if (TextUtils.isEmpty(password)){
            listener.onPasswordError();
            error = true;
        }
        if (!error){
            new ServiceThread().start();
        }
    }

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SUCCESS_HANDLER:
                    listener.onSuccess();
                    break;
                case FAILURE_HANDLER:
                    JSONObject loginResult = (JSONObject) msg.obj;
                    listener.onError(loginResult.optString("errorMsg"));
                    break;
            }
        }
    };

    class ServiceThread extends Thread {

        @Override
        public void run() {
            /*SmartPosServices service = SmartPosServices.getInstance(App.getContext());
                    JSONObject loginResult = service.login(username, password, merchId);*/
            ApmpService apmpService = ApmpService.getInstance(ctx);
            apmpService.setClientType(ApmpService.POS_TYPE_SMART);
            apmpService.setEnvironmentMode(ApmpService.ENV_PRODUCT);
            apmpService.setAndroidBuildSerial(android.os.Build.SERIAL);
            JSONObject loginResult = apmpService.login(username, password, merchId, android.os.Build.SERIAL);
            Log.d("LoginInteractorImpl", loginResult.toString());

//                    JSONObject jsonData = JsonUtil.getResponseData(loginResult);
            if (null != loginResult) {
                int responseCode = loginResult.optInt("responseCode");
                if (responseCode == 0) {
                    //TODO:save merchant ID and terminal ID
                    PreferenceUtil.saveTerminaID(ctx, loginResult.optString("iposId"));
                    PreferenceUtil.saveMerchID(ctx, merchId);

                    //TODO:download payment params and then save to local
                    JSONObject paramsObj = apmpService.getPaymentList(merchId);
                    Log.w("PaymentParams", "params:" + paramsObj.toString());

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

                    POSPTransactionService pospTransactionService = POSPTransactionService.getInstance(ctx, apmpService, merchId, loginResult.optString("iposId"), "00");
                    JSONObject signInObj = pospTransactionService.signIn("0000000025");

                    mHandler.sendEmptyMessage(SUCCESS_HANDLER);

                } else {
                    Message msg = mHandler.obtainMessage();
                    msg.obj = loginResult;
                    msg.what = FAILURE_HANDLER;
                    mHandler.sendMessage(msg);

                }
            }
        }
    }
}
