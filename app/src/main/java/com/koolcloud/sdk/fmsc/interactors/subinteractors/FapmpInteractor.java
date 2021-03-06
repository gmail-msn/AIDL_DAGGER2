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

import com.koolcloud.sdk.fmsc.service.apmp.OnApmpCallBackListener;
import com.koolcloud.sdk.fmsc.ui.login.OnLoginFinishedListener;

public interface FapmpInteractor {
    public void loginApmp(Context ctx, String merchId, String username, String password, OnApmpCallBackListener listener);
    public void logoutApmp(Context ctx, OnApmpCallBackListener listener);
    public void downloadPaymentParams(Context ctx, String merchId, OnApmpCallBackListener listener);
}
