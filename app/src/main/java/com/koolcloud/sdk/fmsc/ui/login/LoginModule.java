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


import android.util.Log;

import com.koolcloud.sdk.fmsc.interactors.LoginInteractor;

import dagger.Module;
import dagger.Provides;

@Module
public class LoginModule {

    private LoginView view;

    public LoginModule(LoginView view) {
        Log.i("LoginModule", "constructor LoginModule()");
        this.view = view;
    }

    @Provides
    public LoginView provideView() {
        Log.i("LoginModule", "provideView()");
        return view;
    }

    @Provides
    public LoginPresenter providePresenter(LoginView loginView, LoginInteractor loginInteractor) {
        Log.i("LoginModule", "providePresenter()");
        return new LoginPresenterImpl(loginView, loginInteractor);
    }
}
