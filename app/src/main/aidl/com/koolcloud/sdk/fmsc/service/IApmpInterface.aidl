// IApmpInterface.aidl
package com.koolcloud.sdk.fmsc.service;

import com.koolcloud.sdk.fmsc.service.IApmpCallBack;

interface IApmpInterface {
    void loginApmp(String merchId, String userName, String password, IApmpCallBack mIApmpCallBack);
}
