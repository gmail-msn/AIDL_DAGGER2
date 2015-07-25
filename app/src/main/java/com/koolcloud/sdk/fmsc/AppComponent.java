package com.koolcloud.sdk.fmsc;


import com.koolcloud.sdk.fmsc.domain.AnalyticsManager;
import com.koolcloud.sdk.fmsc.domain.DomainModule;
import com.koolcloud.sdk.fmsc.interactors.subinteractors.FapmpInteractor;
import com.koolcloud.sdk.fmsc.interactors.subinteractors.TransactionInteractor;
import com.koolcloud.sdk.fmsc.interactors.InteractorsModule;
import com.koolcloud.sdk.fmsc.interactors.subinteractors.LoginInteractor;
import com.koolcloud.sdk.fmsc.interactors.subinteractors.DevicesInteractor;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(
        modules = {
                AppModule.class,
                DomainModule.class,
                InteractorsModule.class
        }
)
public interface AppComponent {
    void inject(App app);

    AnalyticsManager getAnalyticsManager();
    LoginInteractor getLoginInteractor();
    TransactionInteractor getTransactionInteractor();
    DevicesInteractor getDevicesInteractor();
    FapmpInteractor getFapmpInteractor();
}
