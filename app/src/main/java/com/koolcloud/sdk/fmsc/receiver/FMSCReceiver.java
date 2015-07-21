package com.koolcloud.sdk.fmsc.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.koolcloud.sdk.fmsc.service.apmp.FapmpService;
import com.koolcloud.sdk.fmsc.service.device.DeviceService;
import com.koolcloud.sdk.fmsc.service.transaction.TransactionService;

/**
 * Created by admin on 2015/7/17.
 */
public class FMSCReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent deviceService = new Intent(context, DeviceService.class);
        context.startService(deviceService);

        Intent transactionService = new Intent(context, TransactionService.class);
        context.startService(transactionService);

        Intent fapmpService = new Intent(context, FapmpService.class);
        context.startService(fapmpService);
    }
}
