package com.koolcloud.sdk.fmsc.util;

import com.koolcloud.sdk.fmsc.domain.entity.PaymentInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class JsonUtil {

	public static JSONObject getResponseData(JSONObject dataJson) {
		JSONObject bodyJson = null;
		if (null != dataJson) {
			try {
				
				JSONArray bodyArray = dataJson.getJSONArray("body");
				if (null != bodyArray && bodyArray.length() > 0) {
					bodyJson = bodyArray.getJSONObject(0);
					
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return bodyJson;
	}

	public static List<PaymentInfo> parseJsonArray2AcquireInstitute(JSONArray jsonArray) {
		List<PaymentInfo> acquireList = null;
		HashSet<String> tmpSet = null;
		try {
			if (null != jsonArray && jsonArray.length() > 0) {
				acquireList = new ArrayList<PaymentInfo>();
				tmpSet = new HashSet<String>();
				for (int i = 0; i < jsonArray.length(); i++) {
					JSONObject jsonObj = jsonArray.getJSONObject(i);
					if (tmpSet.contains(jsonObj.optString("payment_id"))) {
						continue;
					} else {
						tmpSet.add(jsonObj.optString("payment_id"));
					}
					PaymentInfo paymentInfo = new PaymentInfo();
					paymentInfo.setBrhMchtId(jsonObj.optString("brh_mcht_cd"));
					paymentInfo.setBrhTermId(jsonObj.optString("brh_term_id"));
					paymentInfo.setDeviceNumOfMerch(jsonObj.optString("open_brh"));
					paymentInfo.setImgName(jsonObj.optString("payment_icon"));
					paymentInfo.setInstituteName(jsonObj.optString("open_brh_name"));
					paymentInfo.setMerchNumOfMerch(jsonObj.optString("mcht_no"));
					paymentInfo.setPaymentId(jsonObj.optString("payment_id"));
					paymentInfo.setPaymentName(jsonObj.optString("payment_name"));
					paymentInfo.setPrintType(jsonObj.optString("print_type"));
					paymentInfo.setProductDesc(jsonObj.optString("prdt_title"));
					paymentInfo.setProductNo(jsonObj.optString("prdt_no"));
					paymentInfo.setProductTitle(jsonObj.optString("prdt_title"));
					paymentInfo.setProductType(jsonObj.optString("print_type"));
					paymentInfo.setTypeId(jsonObj.optString("content"));
					paymentInfo.setTypeName(jsonObj.optString("content"));
					paymentInfo.setBrhKeyIndex(jsonObj.optString("brh_key_index"));
					paymentInfo.setBrhMsgType(jsonObj.optString("msg_type"));
					paymentInfo.setBrhMchtMcc(jsonObj.optString("brh_mcht_mcc"));
					paymentInfo.setMsgSendType(jsonObj.optString("msg_send_type"));

					paymentInfo.setJsonItem(jsonObj.toString());

					acquireList.add(paymentInfo);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return acquireList;
	}
}
