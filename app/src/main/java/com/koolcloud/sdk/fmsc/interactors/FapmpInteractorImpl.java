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
import android.util.Log;

import com.koolcloud.sdk.fmsc.domain.database.PaymentParamsDB;
import com.koolcloud.sdk.fmsc.domain.entity.PaymentInfo;
import com.koolcloud.sdk.fmsc.service.apmp.OnApmpCallBackListener;
import com.koolcloud.sdk.fmsc.util.ApmpUtil;
import com.koolcloud.sdk.fmsc.util.JsonUtil;
import com.koolcloud.sdk.fmsc.util.PreferenceUtil;
import com.koolyun.smartpos.sdk.service.ApmpService;
import com.koolyun.smartpos.sdk.service.POSPTransactionService;

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
        Log.i("LoginInteractorImpl", "login");
        this.ctx = ctx;
        this.mListener = listener;

        ApmpService apmpService = ApmpUtil.getApmpInstance(ctx);
        JSONObject loginResult = apmpService.login(username, password, merchId, android.os.Build.SERIAL);
        mListener.onLoginCallBack(loginResult);
    }

}
