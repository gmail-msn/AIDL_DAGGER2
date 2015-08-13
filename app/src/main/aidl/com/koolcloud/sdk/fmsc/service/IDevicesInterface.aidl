package com.koolcloud.sdk.fmsc.service;

import com.koolcloud.sdk.fmsc.service.IDevicesCallBack;
import com.koolcloud.sdk.fmsc.service.ITransactionCallBack;

interface IDevicesInterface {
    /**
     * String paymentId: payment number
     * String transAmount: transaction amount unit yuan (eg:1.00), it is null on Balance Query
     * int transType:
     *      1041 -- Balance Query
     *      1021 -- Consume
     *      3021 -- Consume Cancel
     *      3051 -- Refund
     *      1721 -- Super Transfer
     * String cardId: the identity card of people (last 6 int number), the parameter is null on Balance Query
     * String toAccout: Transferee card NO, the parameter is null on Balance Query
     * IDevicesCallBack devicesCallBack: devices response call back
     * ITransactionCallBack transactionCallBack: transaction call back
     **/
    void onStartSwipeCard(String paymentId, String transAmount, String transType, String cardId, String toAccount, IDevicesCallBack devicesCallBack, ITransactionCallBack transactionCallBack);
    void onStopSwipeCard();
    void startPinPad(boolean isEMVCard);
}
