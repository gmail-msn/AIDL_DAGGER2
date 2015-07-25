package com.koolcloud.sdk.fmsc.service;

interface IApmpCallBack {
    void loginApmpCallBack(String loginResult);
    void onDownloadPaymentParamsCallBack(String paymentObj);
}