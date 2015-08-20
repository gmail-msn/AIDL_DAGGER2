package com.koolcloud.sdk.fmsc.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

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
		Log.i(TAG, intent.getDataString());
		if (Intent.ACTION_PACKAGE_ADDED.equals(action)) {
			Log.i(TAG, "app installed");
			Log.i(TAG, intent.getDataString());

		} else if (Intent.ACTION_PACKAGE_REMOVED.equals(action)) {
			Log.i(TAG, "app removed");
			Log.i(TAG, intent.getDataString());

		} else if (Intent.ACTION_PACKAGE_CHANGED.equals(action)) {
			Log.i(TAG, "app package changed");
			Log.i(TAG, intent.getDataString());
		} else if (Intent.ACTION_PACKAGE_REPLACED.equals(action)) {
			Log.i(TAG, "app replaced");
			Log.i(TAG, intent.getDataString());

		} else if (Intent.ACTION_PACKAGE_RESTARTED.equals(action)) {
			Log.i(TAG, "app restarted");
			Log.i(TAG, intent.getDataString());
		}
	}
}
