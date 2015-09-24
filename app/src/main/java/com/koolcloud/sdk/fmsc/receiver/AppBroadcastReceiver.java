package com.koolcloud.sdk.fmsc.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.koolcloud.sdk.fmsc.util.Logger;

/**
 * <p>Title: AppBroadcastReceiver.java </p>
 * <p>Description: App install or remove receiver and then refresh the main UI</p>
 * <p>Copyright: Copyright (c) 2013</p>
 * <p>Company: All In Pay</p>
 * @author 		Teddy
 * @date 		2015-08-20
 * @version 	
 */
public class AppBroadcastReceiver extends BroadcastReceiver {
	private final String TAG = "AppBroadcastReceiver";

	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		Logger.i(intent.getDataString());
		if (Intent.ACTION_PACKAGE_ADDED.equals(action)) {
			Logger.i("app installed");
			Logger.i(intent.getDataString());

		} else if (Intent.ACTION_PACKAGE_REMOVED.equals(action)) {
			Logger.i("app removed");
			Logger.i(intent.getDataString());

		} else if (Intent.ACTION_PACKAGE_CHANGED.equals(action)) {
			Logger.i("app package changed");
			Logger.i(intent.getDataString());
		} else if (Intent.ACTION_PACKAGE_REPLACED.equals(action)) {
			Logger.i("app replaced");
			Logger.i(intent.getDataString());

		} else if (Intent.ACTION_PACKAGE_RESTARTED.equals(action)) {
			Logger.i("app restarted");
			Logger.i(intent.getDataString());
		}
	}
}
