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

import android.os.Bundle;

import org.json.JSONObject;

import java.util.Hashtable;

public interface IDeviceServiceView {

    public void receiveTrackData(Hashtable<String, String> trackData);

    public void receiveTrackDataError(int resCode, int trackIndex);

    public void onReceiveICData(JSONObject transICData);

    public void onReceiveICCardData(JSONObject icCardData);

    public void onReceiveICDataError(JSONObject transICData);

    public void onReceivePinpadData(Bundle pinpadData);

    public void onFinishTransaction(JSONObject jsonObject);

    public void onCheckTransactionParamError(JSONObject jsonObject);
}
