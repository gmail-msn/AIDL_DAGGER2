package com.koolcloud.sdk.fmsc.provider;


import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

import com.koolcloud.sdk.fmsc.domain.database.PaymentParamsDB;

public class PaymentProvider extends ContentProvider {

	// All URIs share these parts
	public static final String AUTHORITY = "com.koolcloud.sdk.fmsc.provider";
	public static final String SCHEME = "content://";

	// URIs
	// Used for all persons
	public static final String PAYMENTS = SCHEME + AUTHORITY + "/payment";
	public static final Uri URI_PAYMENTS = Uri.parse(PAYMENTS);
	// Used for a single person, just add the id to the end
	public static final String PAYMENT_BASE = PAYMENTS + "/";

	//colum
	public final static String PAYMENT_MERCHANT_ID = "brhMchtId";
	public final static String PAYMENT_TERMINAL_ID = "brhTermId";
	public final static String PAYMENT_DEVICE_NUM_OF_MERCH = "openBrh";
	public final static String PAYMENT_IMG_NAME = "imgName";
	public final static String PAYMENT_INSTITUTE_NAME = "openBrhName";
	public final static String PAYMENT_MERCH_NUM_OF_MERCH = "mch_no";
	public final static String PAYMENT_PAYMENT_ID = "paymentId";
	public final static String PAYMENT_PAYMENT_NAME = "paymentName";
	public final static String PAYMENT_PRINT_TYPE = "printType";
	public final static String PAYMENT_PRODUCT_DESC = "prdtDesc";
	public final static String PAYMENT_PRODUCT_NO = "prdtNo";
	public final static String PAYMENT_PRODUCT_TITLE = "prdtTitle";
	public final static String PAYMENT_PRODUCT_TYPE = "prdtType";
	public final static String PAYMENT_TYPE_ID = "typeId";
	public final static String PAYMENT_TYPE_NAME = "typeName";
	public final static String PAYMENT_BRH_KEY_INDEX = "brhKeyIndex";
	public final static String PAYMENT_BRH_MSG_TYPE = "brhMsgType";
	public final static String PAYMENT_BRH_MCHT_MCC = "brhMchtMcc";
	public final static String PAYMENT_TAB_TYPE_ID = "tabType";
	public final static String PAYMENT_MSG_SEND_TYPE = "msg_send_type";

	public final static String PAYMENT_ACTIVITY_JSON = "payment_json";

	public PaymentProvider() {
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		// Implement this to handle requests to delete one or more rows.
		throw new UnsupportedOperationException("Not yet implemented");
	}

	@Override
	public String getType(Uri uri) {
		// TODO: Implement this to handle requests for the MIME type of the data
		// at the given URI.
		throw new UnsupportedOperationException("Not yet implemented");
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		// TODO: Implement this to handle requests to insert a new row.
		throw new UnsupportedOperationException("Not yet implemented");
	}

	@Override
	public boolean onCreate() {
		return true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		Cursor result = null;
		if (URI_PAYMENTS.equals(uri)) {
			result = PaymentParamsDB.getInstance(getContext()).getPayments();
			result.setNotificationUri(getContext().getContentResolver(), URI_PAYMENTS);
		} else if (uri.toString().startsWith(PAYMENT_BASE)) {
			final String paymentId = String.valueOf(uri.getLastPathSegment());
			result = PaymentParamsDB.getInstance(getContext()).selectPaymentByPaymentId(paymentId);
			result.setNotificationUri(getContext().getContentResolver(), URI_PAYMENTS);
		} else {
			throw new UnsupportedOperationException("Not yet implemented");
		}

		return result;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		// TODO: Implement this to handle requests to update one or more rows.
		throw new UnsupportedOperationException("Not yet implemented");
	}
}
