package com.koolcloud.sdk.fmsc.service;

interface IApmpCallBack {
    void loginApmpCallBack(String loginResult);
    void logoutCallBack(String logoutResult);
    void onDownloadPaymentParamsCallBack(String paymentObj);
}