package com.koolcloud.sdk.fmsc.service;

import com.koolcloud.sdk.fmsc.service.ITransactionCallBack;

interface ITransactionInterface {
    void signIn(String paymentId, String keyIndex, ITransactionCallBack transactionCallBack);
}
