package com.koolcloud.sdk.fmsc.util;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferenceUtil {

	public static final String PREFS_NAME 								= "preference_file";
	public static final String TERMINAL_ID 								= "terminal_id";
	public static final String MERCHANT_ID 								= "merchant_id";

	// load terminal id
	public static String getTerminalID(Context context) {
		SharedPreferences prefer = context.getSharedPreferences(PREFS_NAME,
				Context.MODE_MULTI_PROCESS);
		return prefer.getString(TERMINAL_ID, "");
	}

	// save terminal id
	public static void saveTerminaID(Context context, String terminalID) {
		SharedPreferences prefer = context.getSharedPreferences(PREFS_NAME,Context.MODE_MULTI_PROCESS);
		prefer.edit().putString(TERMINAL_ID, terminalID).commit();
	}
	
	// load merchant id
	public static String getMerchID(Context context) {
		SharedPreferences prefer = context.getSharedPreferences(PREFS_NAME,
				Context.MODE_PRIVATE);
		return prefer.getString(MERCHANT_ID, "");
	}
	
	// save merchant id
	public static void saveMerchID(Context context, String merchId) {
		SharedPreferences prefer = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
		prefer.edit().putString(MERCHANT_ID, merchId).commit();
	}
}
