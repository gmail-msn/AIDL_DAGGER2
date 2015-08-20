// IApmpInterface.aidl
package com.koolcloud.sdk.fmsc.service;

import com.koolcloud.sdk.fmsc.service.IApmpCallBack;

interface IApmpInterface {
    void loginApmp(String merchId, String userName, String password, IApmpCallBack mIApmpCallBack);
    void logoutApmp(IApmpCallBack mIApmpCallBack);
    void downloadPaymentParams(String merchId, IApmpCallBack mIApmpCallBack);
}
