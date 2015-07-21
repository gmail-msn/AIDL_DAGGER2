package com.koolcloud.sdk.fmsc.service.apmp;


import com.koolcloud.sdk.fmsc.ActivityScope;
import com.koolcloud.sdk.fmsc.AppComponent;

import dagger.Component;

/**
 * Created by Miroslaw Stanek on 17.03.15.
 */
@ActivityScope
@Component(
        dependencies = AppComponent.class,
        modules = FapmpModule.class
)
public interface FapmpComponent {
    void inject(FapmpService fapmpService);

    FapmpPresenter getFapmpPresenter();
}
