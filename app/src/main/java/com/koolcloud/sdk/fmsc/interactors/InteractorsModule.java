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

import android.util.Log;

import com.koolcloud.sdk.fmsc.interactors.subinteractors.DevicesInteractor;
import com.koolcloud.sdk.fmsc.interactors.subinteractors.DevicesInteractorImpl;
import com.koolcloud.sdk.fmsc.interactors.subinteractors.FapmpInteractor;
import com.koolcloud.sdk.fmsc.interactors.subinteractors.FapmpInteractorImpl;
import com.koolcloud.sdk.fmsc.interactors.subinteractors.LoginInteractor;
import com.koolcloud.sdk.fmsc.interactors.subinteractors.LoginInteractorImpl;
import com.koolcloud.sdk.fmsc.interactors.subinteractors.TransactionInteractor;
import com.koolcloud.sdk.fmsc.interactors.subinteractors.TransactionInteractorImpl;
import com.koolcloud.sdk.fmsc.util.Logger;

import dagger.Module;
import dagger.Provides;

@Module
public class InteractorsModule {

    @Provides public LoginInteractor provideLoginInteractor() {
        Logger.i("provideLoginInteractor()");
        return new LoginInteractorImpl();
    }

    @Provides public FapmpInteractor provideFapmpInteractor() {
        return new FapmpInteractorImpl();
    }

    @Provides public TransactionInteractor provideTransactionInteractor() {
        return new TransactionInteractorImpl();
    }

    @Provides public DevicesInteractor provideDevicesInteractor() {
        return new DevicesInteractorImpl();
    }
}
