package com.koolcloud.sdk.fmsc.service.device;


import com.koolcloud.sdk.fmsc.ActivityScope;
import com.koolcloud.sdk.fmsc.AppComponent;

import dagger.Component;

/**
 * Created by Miroslaw Stanek on 17.03.15.
 */
@ActivityScope
@Component(
        dependencies = AppComponent.class,
        modules = DeviceModule.class
)
public interface DeviceComponent {
    void inject(DeviceService deviceService);

    DevicePresenter getDevicePresenter();
}
