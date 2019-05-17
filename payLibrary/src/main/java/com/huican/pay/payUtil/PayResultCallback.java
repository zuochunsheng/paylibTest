package com.huican.pay.payUtil;

/**
 * anther: created by zsc on 2019/5/8 15 : 16
 * description :
 */
public interface PayResultCallback {

    void onSuccess(int payMethod);               //支付成功

    void onError(int payMethod,int errorCode,String errorMsg);   //支付失败

    void onCancel(int payMethod);                //支付取消

}
