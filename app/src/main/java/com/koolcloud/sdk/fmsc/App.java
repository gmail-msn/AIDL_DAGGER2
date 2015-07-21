
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

package com.koolcloud.sdk.fmsc;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.koolcloud.sdk.fmsc.domain.AnalyticsManager;

import javax.inject.Inject;


public class App extends Application {

    private AppComponent component;
    private static Context ctx;

    @Inject
    AnalyticsManager analyticsManager;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.w("App", "onCreate()");
        setupGraph();
        analyticsManager.registerAppEnter();
    }

    private void setupGraph() {
        component = DaggerAppComponent.builder()
                .appModule(new AppModule(this))
                .build();
        component.inject(this);
        Log.w("App", "setupGraph()");
    }

    public AppComponent component() {
        Log.w("App", "component()");
        return component;
    }

    public static App getApplication(Context context) {
        Log.w("App", "getApplication()");
        ctx = context.getApplicationContext();
        return (App) context.getApplicationContext();
    }

    public static Context getContext() {
        return ctx;
    }
}