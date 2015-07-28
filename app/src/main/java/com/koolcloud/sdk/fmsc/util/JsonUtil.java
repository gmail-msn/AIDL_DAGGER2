package com.koolcloud.sdk.fmsc.util;

import android.content.Context;
import android.text.TextUtils;

import com.koolcloud.sdk.fmsc.domain.entity.PaymentInfo;
import com.koolyun.smartpos.sdk.util.UtilForMoney;

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

    public static JSONObject makeTransactionParams(Context ctx, JSONObject paramsObj) {
        JSONObject transJsonObj = new JSONObject();
        try {
            String terminalId = PreferenceUtil.getTerminalID(ctx);

            transJsonObj.put("paymentId", paramsObj.optString("paymentId"));
            transJsonObj.put("F02", paramsObj.optString("pan"));
            transJsonObj.put("track3", paramsObj.optString("track3"));
            transJsonObj.put("F36", paramsObj.optString("track3"));
            transJsonObj.put("track2", paramsObj.optString("track2"));
            transJsonObj.put("F35", paramsObj.optString("track2"));
            transJsonObj.put("pwd", paramsObj.optString("pinblock"));
            transJsonObj.put("F52", paramsObj.optString("pinblock"));
            transJsonObj.put("transType", paramsObj.optString("transType"));
            transJsonObj.put("brhKeyIndex", paramsObj.optString("brhKeyIndex"));
            String transAmount = paramsObj.optString("transAmount");
            if (!TextUtils.isEmpty(transAmount)) {
                transJsonObj.put("F04", UtilForMoney.yuan2fen(transAmount));
                transJsonObj.put("transAmount", UtilForMoney.yuan2fen(transAmount));
            } else {
                transJsonObj.put("F04", "0");
                transJsonObj.put("transAmount", "0");
            }
            transJsonObj.put("F62", paramsObj.optString("toAccount"));
            transJsonObj.put("F60.6", paramsObj.optString("paymentId"));
            transJsonObj.put("F61", paramsObj.optString("idCard"));
            transJsonObj.put("idCard", paramsObj.optString("idCard"));
            transJsonObj.put("openBrh", paramsObj.optString("openBrh"));
            transJsonObj.put("F40_6F20", paramsObj.optString("openBrh"));
            transJsonObj.put("iposId", terminalId);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return  transJsonObj;
    }
}
