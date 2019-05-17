package com.huican.pay.tools.http.bean;

import com.huican.pay.tools.UuidTools;
import com.huican.pay.tools.http.encrypt.RSAUtil;

/**
 * anther: created by zuochunsheng on 2019/5/9 11 : 36
 * description :
 */
public class ParamentBean {
    /* 请求共有的参数 */
    protected String encryptKey;

    public ParamentBean(){
        try {
            encryptKey = RSAUtil.encrypt(RSAUtil.getPublicKey(RSAUtil.devKey),RSAUtil.getBawoKey());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected String encryptId = "100002";

    protected String apiVersion = "2";

    protected String deviceId ;//= UuidTools.getUUid(this);


//protected String credentialsId = AppApplication.userInfo == null ? "267412": AppApplication.credentialsId;


    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }
}
