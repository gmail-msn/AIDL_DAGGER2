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

package com.koolcloud.sdk.fmsc.ui.common;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.koolcloud.sdk.fmsc.App;
import com.koolcloud.sdk.fmsc.AppComponent;
import com.koolcloud.sdk.fmsc.util.Logger;


public abstract class BaseActivity extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Logger.i("onCreate");
        setupComponent((AppComponent) App.getApplication(this).component());
    }

    protected abstract void setupComponent(AppComponent appComponent);
}
