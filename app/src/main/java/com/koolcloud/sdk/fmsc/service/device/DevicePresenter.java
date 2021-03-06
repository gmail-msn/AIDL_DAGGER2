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

package com.koolcloud.sdk.fmsc.service.device;

import android.content.Context;

import com.koolcloud.sdk.fmsc.interactors.subinteractors.TransactionInteractor;

import org.json.JSONObject;

public interface DevicePresenter {

    public void onStartSwiper(Context context);

    public void onStopSwipter();

    public void onStartReadICData(Context ctx, String keyIndex, String transAmount);

    public void onStopReadICData();

    public void onStartPinPad(Context ctx, JSONObject jsonObject);

    public void onStartTransaction(Context context, JSONObject transJsonObj, TransactionInteractor mTransactionInteractor);

}
