// ITransactionCallBack.aidl
package com.koolcloud.sdk.fmsc.service;

interface ITransactionCallBack {
    void signInCallBack(String signInResult);
    void onTransactionCallBack(String transactionResult);
}
