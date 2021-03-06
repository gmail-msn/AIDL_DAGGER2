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


import com.koolcloud.sdk.fmsc.interactors.subinteractors.DevicesInteractor;

import dagger.Module;
import dagger.Provides;

@Module
public class DeviceModule {

    private IDeviceServiceView view;

    public DeviceModule(IDeviceServiceView view) {
        this.view = view;
    }

    @Provides
    public IDeviceServiceView provideView() {
        return view;
    }

    @Provides
    public DevicePresenter providePresenter(IDeviceServiceView deviceServiceView, DevicesInteractor devicesInteractor) {
        return new DevicePresenterImpl(deviceServiceView, devicesInteractor);
    }
}
