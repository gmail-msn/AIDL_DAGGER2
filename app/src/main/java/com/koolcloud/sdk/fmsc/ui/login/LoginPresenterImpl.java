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

package com.koolcloud.sdk.fmsc.ui.login;


import android.content.Context;
import android.util.Log;

import com.koolcloud.sdk.fmsc.interactors.subinteractors.LoginInteractor;
import com.koolcloud.sdk.fmsc.util.Logger;

public class LoginPresenterImpl implements LoginPresenter, OnLoginFinishedListener {

    private LoginView loginView;
    private LoginInteractor loginInteractor;

    public LoginPresenterImpl(LoginView loginView, LoginInteractor loginInteractor) {
        Logger.i("constructor LoginPresenterImpl()");
        this.loginView = loginView;
        this.loginInteractor = loginInteractor;
    }

    @Override
    public void validateCredentials(Context ctx, String merchId, String username, String password) {
        Logger.i("validateCredentials()");
        loginView.showProgress();
        loginInteractor.login(ctx, merchId, username, password, this);
    }

    @Override
    public void onUsernameError() {
        Logger.i("onUsernameError()");
        loginView.setUsernameError();
        loginView.hideProgress();
    }

    @Override
    public void onPasswordError() {
        Logger.i("onPasswordError()");
        loginView.setPasswordError();
        loginView.hideProgress();
    }

    @Override
    public void onMerchIdError() {
        Logger.i("onMerchIdError()");
        loginView.setMerchIdError();
        loginView.hideProgress();
    }

    @Override
    public void onSuccess() {
        Logger.i("onSuccess()");
        loginView.hideProgress();
        loginView.navigateToHome();
    }

    @Override
    public void onError(String errorMsg) {
        Logger.i("onError()");
        loginView.showErrorMessage(errorMsg);
        loginView.hideProgress();
    }

}
