package com.koolcloud.sdk.fmsc.ui.login;


import com.koolcloud.sdk.fmsc.ActivityScope;
import com.koolcloud.sdk.fmsc.AppComponent;

import dagger.Component;

/**
 * Created by Miroslaw Stanek on 17.03.15.
 */
@ActivityScope
@Component(
        dependencies = AppComponent.class,
        modules = LoginModule.class
)
public interface LoginComponent {
    void inject(LoginActivity activity);

    LoginPresenter getLoginPresenter();
}