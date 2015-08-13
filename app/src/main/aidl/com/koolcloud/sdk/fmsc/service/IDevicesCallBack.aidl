package com.koolcloud.sdk.fmsc.service;

interface IDevicesCallBack {
    void swipeCardCallBack(boolean swipeCardResult);
    void onSwipeCardErrorCallBack(String errorResult);
    void onGetCardDataCallBack(String cardData);
}