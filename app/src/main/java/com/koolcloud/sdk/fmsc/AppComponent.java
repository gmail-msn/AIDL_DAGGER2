package com.koolcloud.sdk.fmsc;


import com.koolcloud.sdk.fmsc.domain.AnalyticsManager;
import com.koolcloud.sdk.fmsc.domain.DomainModule;
import com.koolcloud.sdk.fmsc.interactors.FapmpInteractor;
import com.koolcloud.sdk.fmsc.interactors.TransactionInteractor;
import com.koolcloud.sdk.fmsc.interactors.InteractorsModule;
import com.koolcloud.sdk.fmsc.interactors.LoginInteractor;
import com.koolcloud.sdk.fmsc.interactors.DevicesInteractor;

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
