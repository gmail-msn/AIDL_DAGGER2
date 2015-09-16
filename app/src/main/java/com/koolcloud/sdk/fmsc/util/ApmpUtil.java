package com.koolcloud.sdk.fmsc.util;

import android.content.Context;
import android.text.TextUtils;
import android.view.TextureView;

import com.koolcloud.sdk.fmsc.domain.Constant;
import com.koolyun.smartpos.sdk.service.ApmpService;
import com.koolyun.smartpos.sdk.service.POSPTransactionService;

/**
 * Created by admin on 2015/7/20.
 */
public class ApmpUtil {
    private static ApmpService mApmpService;
    public static ApmpService getApmpInstance(Context ctx) {
        ApmpService apmpService = ApmpService.getInstance(ctx);
        apmpService.setClientType(ApmpService.POS_TYPE_SMART);
        apmpService.setEnvironmentMode(ApmpService.ENV_PRODUCT);
//        apmpService.setEnvironmentMode(ApmpService.ENV_TEST);
        apmpService.setAndroidBuildSerial(android.os.Build.SERIAL);
        mApmpService = apmpService;
        return apmpService;
    }

    public static POSPTransactionService getTransactionService(Context context, String merchId, String termId, String keyIndex) {
        if (mApmpService == null) {
            getApmpInstance(context);
        }
        POSPTransactionService service = POSPTransactionService.getInstance(context, mApmpService, merchId, termId, keyIndex);
        return service;
    }

    public static String getActionByTransType(String transType) {
        String action = "";
        if (!TextUtils.isEmpty(transType) &&transType.equals(Constant.APMP_TRAN_BALANCE)) {
            action = "Balance";
        }
        return action;
    }
}
