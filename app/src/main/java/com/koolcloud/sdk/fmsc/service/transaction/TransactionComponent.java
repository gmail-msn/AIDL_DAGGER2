package com.koolcloud.sdk.fmsc.service.transaction;


import com.koolcloud.sdk.fmsc.ActivityScope;
import com.koolcloud.sdk.fmsc.AppComponent;

import dagger.Component;

/**
 * Created by Miroslaw Stanek on 17.03.15.
 */
@ActivityScope
@Component(
        dependencies = AppComponent.class,
        modules = TransactionModule.class
)
public interface TransactionComponent {
    void inject(TransactionService transactionService);

    TransactionPresenter getTransactionPresenter();
}
